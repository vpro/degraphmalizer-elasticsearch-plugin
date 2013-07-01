package dgm.fixtures;

import dgm.Degraphmalizr;
import dgm.ID;
import dgm.configuration.Configuration;
import dgm.configuration.ConfigurationMonitor;
import dgm.configuration.IndexConfig;
import dgm.configuration.TypeConfig;
import dgm.trees.Pair;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.elasticsearch.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Iterables;
import com.google.inject.Provider;

/**
 * This class loads fixture data into elasticsearch. The procedure is as follows:
 * For each index you want to bootstrap fixture data into:
 * - remove the index.
 * - gather index mapping for al types you have for the index.
 * - build an index configuration with composite type configuration (use default index shard/backup values for testing purposes)
 * - create the index
 * - insert the documents.
 * - write the result documents
 * - verify the result documents
 * <p/>
 *
 * @author Ernst Bunders
 */
public class FixturesDevelopmentRunner implements ConfigurationMonitor, FixturesRunner {
    protected final Client client;
    protected final Provider<Configuration> cfgProvider;

    final DeleteIndexesCommand deleteIndexesCommand;
    final DeleteTargetIndexesCommand deleteTargetIndexesCommand;
    final CreateIndexesCommand createIndexesCommand;
    final CreateTargetIndexesCommand createTargetIndexesCommand;
    final InsertDocumentsCommand insertDocumentsCommand;
    final RedegraphmalizeCommand redegraphmalizeCommand;
    final WriteResultDocumentsCommand writeResultDocumentsCommand;
    final VerifyResultDocumentsCommand verifyResultDocumentsCommand;

    private static final Logger LOG = LoggerFactory.getLogger(FixturesDevelopmentRunner.class);

    @Inject
    public FixturesDevelopmentRunner(Client client, Provider<Configuration> cfgProvider, Degraphmalizr degraphmalizr) {
        this.client = client;
        this.cfgProvider = cfgProvider;

        deleteIndexesCommand = new DeleteIndexesCommand(client, cfgProvider);
        deleteTargetIndexesCommand = new DeleteTargetIndexesCommand(client, cfgProvider);
        createIndexesCommand = new CreateIndexesCommand(client, cfgProvider);
        createTargetIndexesCommand = new CreateTargetIndexesCommand(client, cfgProvider);
        insertDocumentsCommand = new InsertDocumentsCommand(client, cfgProvider);
        redegraphmalizeCommand = new RedegraphmalizeCommand(client, cfgProvider, degraphmalizr);
        writeResultDocumentsCommand = new WriteResultDocumentsCommand(client, cfgProvider);
        verifyResultDocumentsCommand = new VerifyResultDocumentsCommand(client, cfgProvider);
    }

    @Override
    public void runFixtures() {
        List<ID> ids;
        List<String> indexes;
        List<Pair<ID, Boolean>> verifyResults;

        try {
            indexes = deleteIndexesCommand.execute();
            LOG.info("Deleted indexes: {}", indexes);
            indexes = deleteTargetIndexesCommand.execute();
            LOG.info("Deleted target indexes: {}", indexes);
            indexes = createIndexesCommand.execute();
            LOG.info("Created indexes: {}", indexes);
            indexes = createTargetIndexesCommand.execute();
            LOG.info("Created target indexes: {}", indexes);
            ids = insertDocumentsCommand.execute();
            LOG.info("Inserted {} documents", ids.size());
            ids = redegraphmalizeCommand.execute();
            LOG.info("Degraphmalized {} documents", ids.size());
            ids = writeResultDocumentsCommand.execute();
            LOG.info("Written {} result documents", ids.size());
            verifyResults = verifyResultDocumentsCommand.execute();
            LOG.info("Checked {} result documents", verifyResults.size());
            int success = 0, failed = 0;
            for (Pair<ID, Boolean> result : verifyResults) {
                if (result.b) {
                    success++;
                } else {
                    failed++;
                }
            }
            LOG.info("Verify results {} good, {} bad ", success, failed);
        } catch (Exception e) {
            LOG.error("Fixture run failed: {} ", e.getMessage(), e);
        }
    }


    @Override
    public void configurationChanged(String change) {
        //when the index that was just reloaded is the source index for one of the index configurations, we reinsert the
        //fixtures.
        final Configuration cfg = cfgProvider.get();

        // for quick lookup of the index name
        final Set<String> names = new HashSet<String>();
        Iterables.addAll(names, cfg.getFixtureConfiguration().getIndexNames());

        boolean needRun = false;
        for (IndexConfig indexConfig : cfg.indices().values())
            for (TypeConfig typeConfig : indexConfig.types().values()) {
                if (names.contains(typeConfig.sourceIndex())) {
                    needRun = true;

                    break;
                }

            }

        if (needRun)
            try {
                runFixtures();
            } catch (Exception e) {
                LOG.error("Something went wrong inserting the fixtures after a configurationChanged event.", e);
            }
    }
}
