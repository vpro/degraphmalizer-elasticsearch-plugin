package dgm.modules.fsmon;

import dgm.configuration.Configuration;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Singleton;

/**
 * Non-reloading javascript configuration
 */
public class StaticConfiguration extends AbstractConfigurationModule {
    public StaticConfiguration(String scriptFolder, String... libraries) {
        super(scriptFolder, libraries);
    }

    @Provides
    @Singleton
    @Inject
    final Configuration provideConfiguration(ObjectMapper om) throws IOException {
        return createConfiguration(om, scriptFolder, libraries);
    }

    @Override
    protected void configureModule() {
    }
}
