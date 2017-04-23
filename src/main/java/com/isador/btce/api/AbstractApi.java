package com.isador.btce.api;

import com.google.gson.*;

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

    protected void processServerResponse(String response) throws BTCEException {
        if (response == null || response.isEmpty()) {
            throw new BTCEException("Invalid server response. Null or empty response");
        }
    }

    protected JsonElement get(JsonObject obj, String field) throws BTCEException {
        if (!obj.has(field)) {
            throw new BTCEException(String.format("Invalid server response. \"%s\" field missed.", field));
        }
        return obj.get(field);
    }
}
