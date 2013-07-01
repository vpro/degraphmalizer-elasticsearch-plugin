package dgm.modules.fsmon;

import dgm.configuration.Configuration;
import dgm.configuration.javascript.JavascriptConfiguration;
import dgm.exceptions.ConfigurationException;
import dgm.modules.ServiceModule;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

/**
 * Base class for all configuration providers
 */
abstract class AbstractConfigurationModule extends ServiceModule {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractConfigurationModule.class);

    final String scriptFolder;
    final List<URL> libraries;

    AbstractConfigurationModule(String scriptFolder, String... libraries) {
        this.scriptFolder = scriptFolder;
        if (libraries.length == 0) {
            this.libraries = Lists.newArrayList(toFiles("lib", "INDEX"));
        } else {
            this.libraries = Lists.newArrayList(toFiles("lib", libraries));
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

    private static URL[] toFiles(String relative, String... filenames) {
        List<URL> result = new ArrayList<URL>();
        for (final String fn : filenames) {
            try {
                URL f = AbstractConfigurationModule.class.getClassLoader().getResource(relative + "/" + fn);
                if (f == null) {
                    if (fn.equals("INDEX")) { // no index file can be found, so nothing
                        break;
                    }
                    f = new URL(fn);
                }
                if (f.getPath().replaceAll(".*/", "").equals("INDEX")) {
                    LOG.info("Reading index file {}", f);
                    BufferedReader reader = null;
                    try {
                        reader = new BufferedReader(new InputStreamReader(f.openStream()));
                        String line = reader.readLine();
                        while (line != null) {
                            line = line.trim();
                            if (line.length() > 0 && !line.startsWith("#")) {
                                result.addAll(Arrays.asList(toFiles(relative, line)));
                            }
                            line = reader.readLine();
                        }
                    } catch(IOException e){
                        throw new ConfigurationException(e.getMessage());
                    } finally {
                        IOUtils.closeQuietly(reader);
                    }
                } else {
                    if (!f.getFile().endsWith(".js")) {
                        throw new ConfigurationException("Will only load .js files");
                    }
                    result.add(f);
                }
            } catch (MalformedURLException mfe) {
                throw new ConfigurationException(mfe.getMessage(), mfe);
            }
        }

        return result.toArray(new URL[result.size()]);
    }
}
