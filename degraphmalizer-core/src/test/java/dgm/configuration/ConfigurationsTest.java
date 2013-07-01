package dgm.configuration;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

import org.junit.Test;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import static org.junit.Assert.assertEquals;


/**
 * @author Michiel Meeuwissen
 * @since 0.1
 */
public class ConfigurationsTest {

    @Test
    public void testList() throws Exception {

        URL[] result = Configurations.list(getClass().getResource("/conf/test-merge-multiple-src"), Predicates.<URL>alwaysTrue());
        System.out.println(Arrays.asList(result));

    }

    @Test
    public void testList2() throws IOException {

        // non recursively load all configuration files
        final Predicate<URL> filenameFilter = new Predicate<URL>() {
            @Override
            public boolean apply(URL url) {
                return url.getFile().endsWith(".conf.js");

            }
        };

        final URL[] configFiles = Configurations.list(getClass().getResource("/conf/test-merge-multiple-src"), filenameFilter);
        assertEquals(2, configFiles.length);
    }
}
