package com.isador.btce.api.publicapi;

import com.google.gson.*;
import com.isador.btce.api.publicapi.Depth;

import java.lang.reflect.Type;

/**
 * Created by isador
 * on 07.04.17
 */
public class SimpleOrderDeserializer implements JsonDeserializer<Depth.SimpleOrder> {

    @Override
    public Depth.SimpleOrder deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonArray arr = (JsonArray) json;
        return new Depth.SimpleOrder(arr.get(0).getAsDouble(), arr.get(1).getAsDouble());
    }
}
