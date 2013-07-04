package dgm.fixtures;

import dgm.configuration.ConfigurationMonitor;
import dgm.configuration.FixtureConfiguration;
import dgm.configuration.javascript.JavascriptFixtureConfiguration;
import dgm.driver.RunMode;
import dgm.modules.ServiceModule;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import com.google.inject.Provides;

/**
 * The module what DegraphmalizeFixturesCommand will be injected into the FixtureLoader.
 *
 * @author Ernst Bunders
 */
public class FixturesModule extends ServiceModule {


    private final FixtureConfiguration configuration;

    final RunMode runMode;

    public FixturesModule(String fixturesDir, boolean development, boolean reloading) throws IOException {
        configuration = new JavascriptFixtureConfiguration(new File(fixturesDir));
        runMode = createRunMode(fixturesDir, development, reloading);
    }
    private static RunMode createRunMode(String fixtures, boolean development, boolean reloading) {
        if (development && reloading && StringUtils.isNotEmpty(fixtures)) {
            return RunMode.DEVELOPMENT;
        } else if (reloading && StringUtils.isNotEmpty(fixtures)) {
            return RunMode.TEST;
        }
        return RunMode.PRODUCTION;
    }

    @Provides
    final FixtureConfiguration provideConfiguration() throws IOException {
        return configuration;
    }

    @Override
    protected void configure() {


        switch (runMode) {
            case DEVELOPMENT:
                multiBind(ConfigurationMonitor.class).to(FixturesDevelopmentRunner.class);
                bind(FixturesRunner.class).to(FixturesDevelopmentRunner.class);
                break;
            case TEST:
                multiBind(ConfigurationMonitor.class).to(FixturesTestRunner.class);
                bind(FixturesRunner.class).to(FixturesDevelopmentRunner.class);
                break;
            default:
                break;
        }

    }
}
