package dgm.configuration;

import java.net.URL;
import java.util.Arrays;

import org.junit.Test;

import com.google.common.base.Predicates;

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
}
