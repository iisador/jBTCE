package com.isador.trade.jbtce.publicapi;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.isador.trade.jbtce.*;
import com.isador.trade.jbtce.constants.Pair;
import com.isador.trade.jbtce.constants.TradeType;
import com.isador.trade.jbtce.publicapi.Depth.SimpleOrder;

import java.time.LocalDateTime;

import static java.util.Objects.requireNonNull;

/**
 * Created by isador
 * on 03.04.17
 */
public class PublicApi extends AbstractApi {

    private static final String PUBLIC_API_URL_TEMPLATE = "api/2/%s/%s";

    public PublicApi() {
        this(new ServerProvider(), new DefaultConnector());
    }

    public PublicApi(ServerProvider serverProvider, Connector connector) {
        super(serverProvider, connector, ImmutableMap.of(LocalDateTime.class, new LocalDateTimeDeserializer(),
                SimpleOrder.class, new SimpleOrderDeserializer(),
                TradeType.class, new TradeTypeDeserializer()));
    }

    public Tick getTick(Pair pair) throws BTCEException {
        checkPair(pair);
        JsonObject obj = (JsonObject) call(pair, "ticker");
        return gson.fromJson(obj.get("ticker"), Tick.class);
    }

    public Trade[] getTrades(Pair pair) throws BTCEException {
        checkPair(pair);
        JsonArray obj = (JsonArray) call(pair, "trades");

        return gson.fromJson(obj, Trade[].class);
    }

    public Depth getDepth(Pair pair) throws BTCEException {
        checkPair(pair);
        JsonObject obj = (JsonObject) call(pair, "depth");
        return gson.fromJson(obj, Depth.class);
    }

    public double getFee(Pair pair) throws BTCEException {
        checkPair(pair);
        JsonObject obj = (JsonObject) call(pair, "fee");
        return obj.get("trade").getAsDouble();
    }

    private JsonElement call(Pair pair, String methodName) throws BTCEException {
        String preparedUrlPath = String.format(PUBLIC_API_URL_TEMPLATE, pair.getName(), methodName);
        JsonElement response = processServerResponse(connector -> connector.get(createUrl(preparedUrlPath), headers));

        if (response.isJsonArray()) {
            return response.getAsJsonArray();
        }

        JsonObject obj = response.getAsJsonObject();
        if (obj.has("success") && obj.get("success").getAsByte() == 0) {
            throw new BTCEException(get(obj, "error").getAsString());
        }

        return obj;
    }

    private void checkPair(Pair pair) {
        requireNonNull(pair, "Pair must be specified");
    }
}
