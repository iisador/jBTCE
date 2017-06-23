package com.isador.trade.jbtce.publicapi;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.isador.trade.jbtce.*;
import com.isador.trade.jbtce.constants.Currency;
import com.isador.trade.jbtce.constants.Pair;
import com.isador.trade.jbtce.constants.TradeType;
import com.isador.trade.jbtce.publicapi.Depth.SimpleOrder;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

/**
 * Public V2 API implementation
 *
 * @author isador
 * @since 2.0.1
 */
public class PublicApiV3 extends AbstractApi {

    private static final String PUBLIC_API_TEMPLATE = "api/3/%s/%s";

    /**
     * Create new public v3 api using default server provider and connector
     */
    public PublicApiV3() {
        this(new ServerProvider(), new DefaultConnector());
    }

    /**
     * Create new public v3 api
     *
     * @param serverProvider server provider implementation
     * @param connector      connector implementation
     */
    public PublicApiV3(ServerProvider serverProvider, Connector connector) {
        super(serverProvider, connector, ImmutableMap.of(LocalDateTime.class, new LocalDateTimeDeserializer(),
                SimpleOrder.class, new SimpleOrderDeserializer(),
                BTCEInfo.class, new BtceInfoDeserializer()));
    }

    /**
     * This method provides all the information about currently active pairs, whether the pair is hidden
     *
     * @return info holder
     * @throws BTCEException if was any error during execution
     */
    public BTCEInfo getInfo() throws BTCEException {
        JsonObject response = call("info", null);
        return gson.fromJson(response, BTCEInfo.class);
    }

    /**
     * Provides commission for each pair<br/>
     * The Commission is displayed for all users, it will not change even if it was reduced on your account in case of promotional pricing.
     *
     * @param pairs pair
     * @return commission mapper on pair
     * @throws BTCEException if was any error during execution
     */
    public Map<Pair, Double> getFees(Pair... pairs) throws BTCEException {
        JsonObject json = call("fee", null, pairs);

        return Stream.of(pairs)
                .map(pair -> ImmutablePair.of(pair, json.get(pair.getName()).getAsDouble())) // todo: remove unused map
                .collect(toMap(ImmutablePair::getLeft, ImmutablePair::getRight));
    }

    /**
     * Provides all the information about currently active pairs
     *
     * @param pairs pairs
     * @return tick mapped to pair
     * @throws BTCEException if was any error during execution
     */
    public Map<Pair, Tick> getTicks(Pair... pairs) throws BTCEException {
        JsonObject json = call("ticker", null, pairs);

        return Stream.of(pairs)
                .map(pair -> ImmutablePair.of(pair, gson.fromJson(json.get(pair.getName()), Tick.class))) // todo: remove unused map
                .collect(toMap(ImmutablePair::getLeft, ImmutablePair::getRight));
    }

    /**
     * Provides the information about active orders on the pair.
     * Depth retrieve count defined by server (150 by default)
     *
     * @param pairs pairs
     * @return depth mapped to pair
     * @throws BTCEException if was any error during execution
     */
    public Map<Pair, Depth> getDepths(Pair... pairs) throws BTCEException {
        return getDepths(null, pairs);
    }

    /**
     * Provides the information about active orders on the pair.
     *
     * @param limit retrieve count
     * @param pairs pairs
     * @return depth mapped to pair
     * @throws BTCEException if was any error during execution
     */
    public Map<Pair, Depth> getDepths(Integer limit, Pair... pairs) throws BTCEException {
        JsonObject json = call("depth", limit, pairs);

        return Stream.of(pairs)
                .map(pair -> ImmutablePair.of(pair, gson.fromJson(json.get(pair.getName()), Depth.class)))
                .collect(toMap(ImmutablePair::getLeft, ImmutablePair::getRight));
    }

    /**
     * Provides the information about the last trades
     * Trades retrieve count defined by server (150 by default)
     *
     * @param pairs pairs
     * @return trades collection mapped to pair
     * @throws BTCEException if was any error during execution
     */
    public Map<Pair, List<Trade>> getTrades(Pair... pairs) throws BTCEException {
        return getTrades(null, pairs);
    }

    /**
     * Provides the information about the last trades
     *
     * @param limit retrieve count
     * @param pairs pairs
     * @return trades collection mapped to pair
     * @throws BTCEException if was any error during execution
     */
    public Map<Pair, List<Trade>> getTrades(Integer limit, Pair... pairs) throws BTCEException {
        JsonObject json = call("trades", limit, pairs);

        return Stream.of(pairs)
                .map(pair -> ImmutablePair.of(pair, toTradeList(pair, json.get(pair.getName()).getAsJsonArray())))
                .collect(toMap(ImmutablePair::getLeft, ImmutablePair::getRight));
    }

    /**
     * Maintenance method. Converts json element to list of trades
     *
     * @param pair    pair
     * @param element response json element
     * @return list of trades
     */
    private List<Trade> toTradeList(Pair pair, JsonElement element) {
        JsonArray array = element.getAsJsonArray();

        return StreamSupport.stream(array.spliterator(), false)
                .map(e -> toTrade(pair, e.getAsJsonObject()))
                .collect(toList());
    }

    /**
     * Maintenance method. Deserialize json trade element
     *
     * @param pair      pair
     * @param jsonTrade json
     * @return converted trade element
     */
    private Trade toTrade(Pair pair, JsonObject jsonTrade) {
        TradeType type = TradeType.parse(jsonTrade.get("type").getAsString().toUpperCase());
        double price = jsonTrade.get("price").getAsDouble();
        double amount = jsonTrade.get("amount").getAsDouble();
        long id = jsonTrade.get("tid").getAsLong();
        LocalDateTime timestamp = LocalDateTimeDeserializer.deserialize(jsonTrade.get("timestamp").getAsLong());
        Currency item = pair.getPrim();
        Currency priceCurrency = pair.getSec();

        return new Trade(timestamp, price, amount, id, priceCurrency, item, type);
    }

    /**
     * Call api method with specified pairs and limit.
     *
     * @param method api method
     * @param limit  limit (if supported)
     * @param pairs  pairs array
     * @return json response
     * @throws BTCEException if was any error during execution
     */
    private JsonObject call(String method, Integer limit, Pair... pairs) throws BTCEException {
        String preparedUrlPath = prepareUrl(method, limit, pairs);
        JsonObject response = processServerResponse(connector -> connector.get(createUrl(preparedUrlPath), headers))
                .getAsJsonObject();

        if (response.has("success") && response.get("success").getAsByte() == 0) {
            throw new BTCEException(get(response, "error").getAsString());
        }

        return response;
    }

    /**
     * Prepare url to call to.
     *
     * @param method api method
     * @param limit  limit (if supported)
     * @param pairs  pairs array
     * @return prepared url
     */
    private String prepareUrl(String method, Integer limit, Pair... pairs) {
        String url = String.format(PUBLIC_API_TEMPLATE, method, Pair.toUrlString(pairs));
        if (limit != null) {
            url += "?limit=" + limit;
        }
        return url;
    }
}
