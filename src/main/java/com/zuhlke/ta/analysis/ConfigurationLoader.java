package com.zuhlke.ta.analysis;

import java.io.IOException;
import java.util.Properties;

public class ConfigurationLoader {

    private Properties props;

    ConfigurationLoader() throws IOException {
        // default to the development environment
        String environment = System.getenv("ENV"    ) == null ? "development" : System.getenv("ENV").toLowerCase();
        props = new Properties();
        props.load(ConfigurationLoader.class.getClassLoader().getResourceAsStream("configuration/" + environment + ".properties"));
    }

    public String getConfigItem(String key) {
        return props.getProperty(key);
    }
}
