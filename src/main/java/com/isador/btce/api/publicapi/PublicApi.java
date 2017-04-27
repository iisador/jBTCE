package com.isador.btce.api.publicapi;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.isador.btce.api.*;
import com.isador.btce.api.constants.Pair;

import java.time.LocalDateTime;

import static java.util.Objects.requireNonNull;

/**
 * Created by isador
 * on 03.04.17
 */
public class PublicApi extends AbstractApi {

    private static final String PUBLIC_API_URL_TEMPLATE = "https://btc-e.com/api/2/%s/%s";

    private Connector connector;

    public PublicApi() {
        this(new JavaConnector());
    }

    public PublicApi(Connector connector) {
        super(ImmutableMap.of(LocalDateTime.class, new LocalDateTimeDeserializer(),
                              Depth.SimpleOrder.class, new SimpleOrderDeserializer()));
        this.connector = requireNonNull(connector, "Connector instance should be not null");
    }

    public Connector getConnector() {
        return connector;
    }

    public void setConnector(Connector connector) {
        this.connector = connector;
    }

    public Tick getTick(Pair pair) throws BTCEException {
        checkPair(pair);
        JsonObject obj = (JsonObject) processResponse(connector.get(String.format(PUBLIC_API_URL_TEMPLATE, pair.getName(), "ticker")));
        return gson.fromJson(obj.get("ticker"), Tick.class);
    }

    public Trade[] getTrades(Pair pair) throws BTCEException {
        checkPair(pair);
        JsonArray obj = (JsonArray) processResponse(connector.get(String.format(PUBLIC_API_URL_TEMPLATE, pair.getName(), "trades")));

        return gson.fromJson(obj, Trade[].class);
    }

    public Depth getDepth(Pair pair) throws BTCEException {
        checkPair(pair);
        JsonObject obj = (JsonObject) processResponse(connector.get(String.format(PUBLIC_API_URL_TEMPLATE, pair.getName(), "depth")));
        return gson.fromJson(obj, Depth.class);
    }

    public double getFee(Pair pair) throws BTCEException {
        checkPair(pair);
        JsonObject obj = (JsonObject) processResponse(connector.get(String.format(PUBLIC_API_URL_TEMPLATE, pair.getName(), "fee")));
        return obj.get("trade").getAsDouble();
    }

    private JsonElement processResponse(String json) throws BTCEException {
        processServerResponse(json);
        JsonElement el = parser.parse(json);
        if (el.isJsonArray()) {
            return el.getAsJsonArray();
        }

        JsonObject obj = el.getAsJsonObject();
        if (obj.has("success") && obj.get("success").getAsByte() == 0) {
            throw new BTCEException(get(obj, "error").getAsString());
        }

        return obj;
    }

    private void checkPair(Pair pair) {
        requireNonNull(pair, "Pair must be specified");
    }
}
