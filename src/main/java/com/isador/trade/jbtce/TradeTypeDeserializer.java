package com.isador.trade.jbtce;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.isador.trade.jbtce.constants.TradeType;

import java.lang.reflect.Type;

/**
 * Trade type deserializer
 *
 * @author isador
 * @since 2.0.1
 */
public class TradeTypeDeserializer implements JsonDeserializer<TradeType> {

    @Override
    public TradeType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return TradeType.parse(json.getAsString());
    }
}
