package dgm.modules.fsmon;

import dgm.configuration.Configuration;
import dgm.configuration.Configurations;
import dgm.configuration.javascript.JavascriptConfiguration;
import dgm.modules.ServiceModule;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

/**
 * Base class for all configuration providers
 */
abstract class AbstractConfigurationModule extends ServiceModule {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractConfigurationModule.class);

    final String scriptFolder;
    final List<URL> libraries;

    AbstractConfigurationModule(String scriptFolder, String... libraries) throws IOException {
        this.scriptFolder = scriptFolder;
        LOG.info("Loading script {} libraries {}", scriptFolder, Arrays.asList(libraries));
        if (libraries.length == 0) {
            this.libraries = Configurations.list("classpath:lib", Configurations.JS);
        } else {
            this.libraries = Configurations.load(libraries);
        }
        LOG.info("Configuration module {}, {}/{}", new Object[] {this, scriptFolder, Arrays.asList(libraries)});
    }

    @Override
    protected void configure() {
        // bind paths
        bind(String.class).annotatedWith(Names.named("scriptFolder")).toInstance(scriptFolder);
        bind(new TypeLiteral<List<URL>>(){}).annotatedWith(Names.named("libraryFiles")).toInstance(libraries);

        configureModule();
    }

    protected abstract void configureModule();

    static Configuration createConfiguration(ObjectMapper om, String scriptFolder, List<URL> libraries) throws IOException {
        return new JavascriptConfiguration(om, scriptFolder, libraries.toArray(new URL[libraries.size()]));
    }

}
