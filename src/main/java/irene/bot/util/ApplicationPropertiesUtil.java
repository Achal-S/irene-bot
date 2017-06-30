package irene.bot.util;

import java.util.Properties;

public class ApplicationPropertiesUtil {

    private static final String APPLICATION_PROPERTIES = "application.properties";
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(ApplicationPropertiesUtil.class);


    public static <T> String getProperty(String name, Class<T> clazz) {
        try {
            final Properties properties = new Properties();
            properties.load(clazz.getClassLoader().getResourceAsStream(APPLICATION_PROPERTIES));
            return properties.getProperty(name);
        } catch (Exception e) {
            log.error(String.format("Property %s not found", name));
            return null;
        }
    }
}
