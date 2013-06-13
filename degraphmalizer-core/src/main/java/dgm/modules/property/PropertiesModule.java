/**
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package dgm.modules.property;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

/**
 * @author Roelof Jan Koekoek
 * @since 0.1
 */
public class PropertiesModule extends AbstractModule {

    private final String config;

    public PropertiesModule() {
        this.config = "config.properties";
    }

    public PropertiesModule(String config) {
        this.config = config;
    }

    @Override
    protected void configure() {
        Names.bindProperties(binder(), loadProperties());
    }

    private Properties loadProperties() {
        Properties properties = new Properties();
        try {
            FileInputStream fis = new FileInputStream(config);
            properties.load(fis);
            return properties;
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
}
