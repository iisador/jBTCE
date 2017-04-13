package com.isador.btce.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonParser;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;

/**
 * Created by isador
 * on 06.04.2017.
 */
public abstract class AbstractApi {

    protected final Gson gson;
    protected final JsonParser parser;

    public AbstractApi(Map<Type, JsonDeserializer> deserializersMap) {
        GsonBuilder builder = new GsonBuilder();
        if (deserializersMap != null && deserializersMap.size() > 0) {
            deserializersMap.entrySet().stream()
                    .filter(e -> Objects.nonNull(e.getValue()))
                    .forEach(e -> builder.registerTypeAdapter(e.getKey(), e.getValue()));
        }

        gson = builder.create();
        parser = new JsonParser();
    }
}
