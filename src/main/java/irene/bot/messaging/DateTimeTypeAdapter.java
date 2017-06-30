package irene.bot.messaging;

import com.google.gson.*;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.lang.reflect.Type;


public class DateTimeTypeAdapter implements JsonSerializer<DateTime>, JsonDeserializer<DateTime> {
    private static final String ISO_8601_FORMAT_STRING = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSS'Z'";

    private DateTimeFormatter formatter;

    public DateTimeTypeAdapter() {
        formatter = DateTimeFormat.forPattern(ISO_8601_FORMAT_STRING);
    }

    @Override
    public synchronized JsonElement serialize(DateTime date, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(formatter.print(date));
    }

    @Override
    public synchronized DateTime deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
        try {
            return formatter.parseDateTime(jsonElement.getAsString());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}