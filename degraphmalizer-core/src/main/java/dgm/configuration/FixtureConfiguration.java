package dgm.configuration;

import java.io.File;

/**
 * @author Ernst Bunders
 */
public interface FixtureConfiguration {
    Iterable<String> getIndexNames();
    FixtureIndexConfiguration getIndexConfig(String name);

    Iterable<String> getExpectedIndexNames();
    FixtureIndexConfiguration getExpectedIndexConfig(String name);

    File getResultsDirectory();
}
