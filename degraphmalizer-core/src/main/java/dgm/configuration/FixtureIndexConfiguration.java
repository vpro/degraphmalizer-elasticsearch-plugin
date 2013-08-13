package dgm.configuration;

import org.elasticsearch.common.settings.Settings;

/**
 * @author Ernst Bunders
 */
public interface FixtureIndexConfiguration {
    Iterable<String> getTypeNames();
    FixtureTypeConfiguration getTypeConfig(String name);
    Iterable<FixtureTypeConfiguration> getTypeConfigurations();
    Settings getSettingsConfig();

}
