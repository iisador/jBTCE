package com.isador.trade.jbtce.publicapi;

import com.google.gson.*;
import com.isador.trade.jbtce.publicapi.Depth.SimpleOrder;

import java.lang.reflect.Type;

/**
 * Created by isador
 * on 07.04.17
 */
public class SimpleOrderDeserializer implements JsonDeserializer<SimpleOrder> {

    @Override
    public SimpleOrder deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonArray arr = (JsonArray) json;
        return new SimpleOrder(arr.get(0).getAsDouble(), arr.get(1).getAsDouble());
    }
}
