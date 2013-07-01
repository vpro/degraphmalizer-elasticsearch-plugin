package dgm.modules.fsmon;

import dgm.configuration.Configuration;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Provides;

/**
 * Non-reloading javascript configuration
 */
public class StaticConfiguration extends AbstractConfigurationModule {
    private Configuration configuration;

    public StaticConfiguration(String scriptFolder, String... libraries) {
        super(scriptFolder, libraries);
        try {
            configuration = createConfiguration(new ObjectMapper(), scriptFolder, this.libraries);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //bind(Configuration.class).toProvider(ConfigurationReloader.class);
    }

    @Provides
    final Configuration provideConfiguration() throws IOException {
        return configuration;
    }

    @Override
    protected void configureModule() {

    }
}
