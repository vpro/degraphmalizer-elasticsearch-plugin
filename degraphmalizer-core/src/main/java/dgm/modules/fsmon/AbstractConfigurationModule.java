package dgm.modules.fsmon;

import dgm.configuration.Configuration;
import dgm.configuration.javascript.JavascriptConfiguration;
import dgm.exceptions.ConfigurationException;
import dgm.modules.ServiceModule;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

/**
 * Base class for all configuration providers
 */
abstract class AbstractConfigurationModule extends ServiceModule {
    final String scriptFolder;
    final List<URL> libraries;

    AbstractConfigurationModule(String scriptFolder, String... libraries) {
        this.scriptFolder = scriptFolder;
        this.libraries = Lists.newArrayList(toFiles(libraries));
    }

    @Override
    protected void configure() {
        // bind paths
        bind(String.class).annotatedWith(Names.named("scriptFolder")).toInstance(scriptFolder);
        bind(new TypeLiteral<List<URL>>() {
        }).annotatedWith(Names.named("libraryFiles")).toInstance(libraries);

        configureModule();
    }

    protected abstract void configureModule();

    static Configuration createConfiguration(ObjectMapper om, String scriptFolder, List<URL> libraries) throws IOException {
        return new JavascriptConfiguration(om, new File(scriptFolder), libraries.toArray(new URL[libraries.size()]));
    }

    private static URL[] toFiles(final String[] filenames) {
        final URL[] fs = new URL[filenames.length];
        int i = 0;
        for (final String fn : filenames) {
            try {

                URL f = AbstractConfigurationModule.class.getClassLoader().getResource(fn);
                if (f == null) {
                    f = new URL(fn);
                }

                if (!f.getFile().endsWith(".js")) {
                    throw new ConfigurationException("Will only load .js files");
                }
                fs[i] = f;
            } catch (MalformedURLException mfe) {

                throw new ConfigurationException(mfe.getMessage());
            }
            i++;
        }

        return fs;
    }
}
