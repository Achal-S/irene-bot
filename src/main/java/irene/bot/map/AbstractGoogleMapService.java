package irene.bot.map;

import java.io.IOException;
import java.util.Properties;

public class AbstractGoogleMapService {

    private static final String APPLICATION_PROPERTIES = "application.properties";
    private static final String API_KEY = "api.key";

    protected String getApiKey() throws IOException {
        final Properties properties = new Properties();
        properties.load(this.getClass().getClassLoader().getResourceAsStream(APPLICATION_PROPERTIES));
        return properties.getProperty(API_KEY);
    }
}
