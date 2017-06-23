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
 * Public V2 api implementation
 *
 * @author isador
 * @since 2.0.1
 */
public class PublicApiV2 extends AbstractApi {

    private static final String PUBLIC_API_URL_TEMPLATE = "api/2/%s/%s";

    /**
     * Create new public v2 api using default server provider and connector
     */
    public PublicApiV2() {
        this(new ServerProvider(), new DefaultConnector());
    }

    /**
     * Create new public v2 api
     *
     * @param serverProvider server provider implementation
     * @param connector      connector implementation
     */
    public PublicApiV2(ServerProvider serverProvider, Connector connector) {
        super(serverProvider, connector, ImmutableMap.of(LocalDateTime.class, new LocalDateTimeDeserializer(),
                SimpleOrder.class, new SimpleOrderDeserializer(),
                TradeType.class, new TradeTypeDeserializer()));
    }

    /**
     * Provides all the information about pair.<br>
     * All information is provided over the past 24 hours.
     *
     * @param pair pair
     * @return pair info
     * @throws BTCEException        if was any error during execution
     * @throws NullPointerException is pair is null
     */
    public Tick getTick(Pair pair) throws BTCEException {
        checkPair(pair);
        JsonObject obj = (JsonObject) call(pair, "ticker");
        return gson.fromJson(obj.get("ticker"), Tick.class);
    }

    /**
     * Provides the information about the last 150 trades<br>
     *
     * @param pair pair
     * @return trades array
     * @throws BTCEException        if was any error during execution
     * @throws NullPointerException is pair is null
     */
    public Trade[] getTrades(Pair pair) throws BTCEException {
        checkPair(pair);
        JsonArray obj = (JsonArray) call(pair, "trades");

        return gson.fromJson(obj, Trade[].class);
    }

    /**
     * Provides the information about active orders on the pair
     * Returns last 150 orders
     *
     * @param pair pair
     * @return depth implementation
     * @throws BTCEException        if was any error during execution
     * @throws NullPointerException is pair is null
     */
    public Depth getDepth(Pair pair) throws BTCEException {
        checkPair(pair);
        JsonObject obj = (JsonObject) call(pair, "depth");
        return gson.fromJson(obj, Depth.class);
    }

    /**
     * Provides pair commission<br>
     * The Commission is displayed for all users, it will not change even if it was reduced on your account in case of promotional pricing
     *
     * @param pair pair
     * @return pair commission
     * @throws BTCEException        if was any error during execution
     * @throws NullPointerException is pair is null
     */
    public double getFee(Pair pair) throws BTCEException {
        checkPair(pair);
        JsonObject obj = (JsonObject) call(pair, "fee");
        return obj.get("trade").getAsDouble();
    }

    /**
     * Call api method with pair specified
     *
     * @param pair       pair
     * @param methodName api v2 method
     * @return parsed response
     * @throws BTCEException if there is error in response(json contains 'error' field), or response was invalid
     */
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

    /**
     * Maintenance method to check pair before calling api
     *
     * @param pair pair
     * @throws NullPointerException if {@code pair} is null
     */
    private void checkPair(Pair pair) {
        requireNonNull(pair, "Pair must be specified");
    }
}
