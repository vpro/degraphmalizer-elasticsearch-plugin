package dgm.configuration;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.junit.Test;

import com.google.common.base.Predicate;

import static org.junit.Assert.assertEquals;


/**
 * @author Michiel Meeuwissen
 * @since 0.1
 */
public class ConfigurationsTest {


    @Test
    public void testList2() throws IOException {

        // non recursively load all configuration files
        final Predicate<URL> filenameFilter = new Predicate<URL>() {
            @Override
            public boolean apply(URL url) {
                return url.getFile().endsWith(".conf.js");

            }
        };

        final List<URL> configFiles = Configurations.list("classpath:conf/test-merge-multiple-src", filenameFilter);
        assertEquals(2, configFiles.size());


    }

    @Test
    public void testListDir() throws IOException {


        final List<String> dirs = Configurations.listDirectories("classpath:org/apache");
        System.out.println(dirs);
        assertEquals(2, dirs.size());

    }
}
