package carmon.generator;

import java.io.*;
import java.util.Properties;

public class GeneratorParameters {
    private static final GeneratorParameters INSTANCE = new GeneratorParameters();

    private static final String DEFAULT_CONFIG_FILE_NAME = "generator.properties";
    private static final String CONFIG_FILE_NAME_PROPERTY = "configFile";
    private static final String API_URL_PROP_NAME = "api.url";
    private static final String SEND_PERIOD_PROP_NAME = "send.period";

    private final Properties properties = new Properties();

    private GeneratorParameters() {
        try {
            InputStream configInputStream;
            String configFileName = System.getProperty(CONFIG_FILE_NAME_PROPERTY);
            if (configFileName == null) {
                configInputStream = getClass().getClassLoader().getResourceAsStream(DEFAULT_CONFIG_FILE_NAME);
            } else {
                configInputStream = new FileInputStream(configFileName);
            }
            properties.load(configInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getApiUrl() {
        return INSTANCE.properties.getProperty(API_URL_PROP_NAME);
    }

    public static int getSendPeriod() {
        return Integer.parseInt(INSTANCE.properties.getProperty(SEND_PERIOD_PROP_NAME));
    }
}
