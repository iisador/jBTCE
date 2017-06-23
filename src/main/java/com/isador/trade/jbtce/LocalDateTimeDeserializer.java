package com.isador.trade.jbtce;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * UNIX time -&gt; LocalDateTime converter
 * UTC zone offset is used by default
 *
 * @author isador
 * @since 2.0.1
 */
public class LocalDateTimeDeserializer implements JsonDeserializer<LocalDateTime> {

    /**
     * Convert epoch seconds to LocalDateTime object
     *
     * @param epochSeconds epoch seconds
     * @return localDateTime
     */
    public static LocalDateTime deserialize(long epochSeconds) {
        return LocalDateTime.ofEpochSecond(epochSeconds, 0, ZoneOffset.UTC);
    }

    @Override
    public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return deserialize(json.getAsLong());
    }
}
