package org.elasticsearch.plugin.degraphmalizer;

import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthStatus;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.Loggers;
import org.elasticsearch.common.network.NetworkUtils;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.testng.annotations.*;

import java.io.IOException;

import static org.elasticsearch.client.Requests.*;
import static org.elasticsearch.common.settings.ImmutableSettings.settingsBuilder;
import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.elasticsearch.node.NodeBuilder.nodeBuilder;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;


/**
 * What does this actually test?
 */
@Test
public class DegraphmalizerTest {
    private final static ESLogger LOG = Loggers.getLogger(DegraphmalizerTest.class);

    private Node node;

    @BeforeMethod
    public void createIndex() {
        node = nodeBuilder().local(true).settings(settingsBuilder()
                .put("path.data", "target/data")
                .put("cluster.name", "test-cluster-" + NetworkUtils.getLocalAddress())
                .put("gateway.type", "none")
                .put("plugin.degraphmalizer.DegraphmalizerPlugin.degraphmalizerHost", "127.0.0.1")).node();

        LOG.info("creating index [test]");
        node.client().admin().indices().create(createIndexRequest("test").settings(settingsBuilder().put("index.numberOfReplicas", 0))).actionGet();
		node.client().admin().indices().aliases(new IndicesAliasesRequest().addAlias("test", "test-alias")).actionGet();
        LOG.info("Running Cluster Health");
        ClusterHealthResponse clusterHealth = node.client().admin().cluster().health(clusterHealthRequest().waitForGreenStatus()).actionGet();
        LOG.info("Done Cluster Health, status " + clusterHealth.status());
        assertThat(clusterHealth.timedOut(), equalTo(false));
        assertThat(clusterHealth.status(), equalTo(ClusterHealthStatus.GREEN));
    }

    @AfterMethod
    public void deleteIndex() {
        LOG.info("deleting index [test]");
        node.client().admin().indices().delete(deleteIndexRequest("test")).actionGet();
        LOG.info("stopping ES");
        node.stop();
        LOG.info("closing ES");
        node.close();
    }

    @Test
    public void testCreateDocument() throws IOException {
        final IndexResponse indexResponse = node.client().index(indexRequest("test").type("person").id("foo/bar").source(
                jsonBuilder().startObject().field("jelle", "was here").endObject())).actionGet();

        final String id = indexResponse.getId();

		System.out.println("Found id: " + id);
        node.client().admin().indices().refresh(refreshRequest()).actionGet();

        final GetResponse getResponse = node.client().get(getRequest("test").id(id)).actionGet();

        assertThat(getResponse.isExists(), is(true));
    }

    @Test
    public void testPluginSettings() {
        final Settings pluginSettings = node.settings().getComponentSettings(DegraphmalizerPlugin.class);
        final String host = pluginSettings.get("DegraphmalizerPlugin.degraphmalizerHost");
        assertThat(host, equalTo("127.0.0.1")); // As set in setupServer()
    }
}
