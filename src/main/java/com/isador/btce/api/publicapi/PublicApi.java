package com.isador.btce.api.publicapi;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.isador.btce.api.AbstractApi;
import com.isador.btce.api.BTCEException;
import com.isador.btce.api.Connector;
import com.isador.btce.api.LocalDateTimeDeserializer;
import com.isador.btce.api.constants.Pair;

import java.time.LocalDateTime;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by isador
 * on 03.04.17
 */
public class PublicApi extends AbstractApi {

    private final Connector connector;

    public PublicApi(Connector connector) {
        super(ImmutableMap.of(LocalDateTime.class, new LocalDateTimeDeserializer(),
                              Depth.SimpleOrder.class, new SimpleOrderDeserializer()));
        this.connector = checkNotNull(connector, "Connector instance should be not null");
    }

    public Tick getTick(Pair pair) throws BTCEException {
        checkPair(pair);
        JsonObject obj = (JsonObject) processResponse(connector.getTick(pair));
        return gson.fromJson(obj.get("ticker"), Tick.class);
    }

    public Trade[] getTrades(Pair pair) throws BTCEException {
        checkPair(pair);
        JsonArray obj = (JsonArray) processResponse(connector.getTrades(pair));

        return gson.fromJson(obj, Trade[].class);
    }

    public Depth getDepth(Pair pair) throws BTCEException {
        checkPair(pair);
        JsonObject obj = (JsonObject) processResponse(connector.getDepth(pair));
        return gson.fromJson(obj, Depth.class);
    }

    public double getFee(Pair pair) throws BTCEException {
        checkPair(pair);
        JsonObject obj = (JsonObject) processResponse(connector.getFee(pair));
        return obj.get("trade").getAsDouble();
    }

    private JsonElement processResponse(String json) throws BTCEException {
        JsonElement el = parser.parse(json);
        if (el.isJsonArray()) {
            return el.getAsJsonArray();
        }
        JsonObject obj = el.getAsJsonObject();
        if (obj.has("success") && obj.get("success").getAsByte() == 0) {
            throw new BTCEException(obj.get("error").getAsString());
        }

        return obj;
    }

    private void checkPair(Pair pair) {
        checkNotNull(pair, "Pair must be specified");
    }
}
