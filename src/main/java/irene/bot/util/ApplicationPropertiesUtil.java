package irene.bot.util;

import java.io.InputStream;
import java.util.Properties;

public class ApplicationPropertiesUtil {

    private static final String APPLICATION_PROPERTIES = "application.properties";
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(ApplicationPropertiesUtil.class);


    public static <T> String getProperty(String name, Class<T> clazz) {
        try {
            final Properties properties = new Properties();
            properties.load(getFile(APPLICATION_PROPERTIES, clazz));
            return properties.getProperty(name);
        } catch (Exception e) {
            log.error(String.format("Property %s not found", name));
            return null;
        }
    }

    public static <T> InputStream getFile(final String fileName, Class<T> clazz){
        return clazz.getClassLoader().getResourceAsStream(fileName);
    }
}
