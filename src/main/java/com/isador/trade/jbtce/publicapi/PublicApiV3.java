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
 * Created by isador
 * on 20.04.17
 */
public class PublicApiV3 extends AbstractApi {

    private static final String PUBLIC_API_TEMPLATE = "api/3/%s/%s";

    public PublicApiV3() {
        this(new ServerProvider(), new DefaultConnector());
    }

    public PublicApiV3(ServerProvider serverProvider, Connector connector) {
        super(serverProvider, connector, ImmutableMap.of(LocalDateTime.class, new LocalDateTimeDeserializer(),
                SimpleOrder.class, new SimpleOrderDeserializer(),
                BTCEInfo.class, new BtceInfoDeserilizer()));
    }

    public BTCEInfo getInfo() throws BTCEException {
        JsonObject response = call("info", null);
        return gson.fromJson(response, BTCEInfo.class);
    }

    public Map<Pair, Double> getFees(Pair... pairs) throws BTCEException {
        JsonObject json = call("fee", null, pairs);

        return Stream.of(pairs)
                .map(pair -> ImmutablePair.of(pair, json.get(pair.getName()).getAsDouble()))
                .collect(toMap(ImmutablePair::getLeft, ImmutablePair::getRight));
    }

    public Map<Pair, Tick> getTicks(Pair... pairs) throws BTCEException {
        JsonObject json = call("ticker", null, pairs);

        return Stream.of(pairs)
                .map(pair -> ImmutablePair.of(pair, gson.fromJson(json.get(pair.getName()), Tick.class)))
                .collect(toMap(ImmutablePair::getLeft, ImmutablePair::getRight));
    }

    public Map<Pair, Depth> getDepths(Pair... pairs) throws BTCEException {
        return getDepths(null, pairs);
    }

    public Map<Pair, Depth> getDepths(Integer limit, Pair... pairs) throws BTCEException {
        JsonObject json = call("depth", limit, pairs);

        return Stream.of(pairs)
                .map(pair -> ImmutablePair.of(pair, gson.fromJson(json.get(pair.getName()), Depth.class)))
                .collect(toMap(ImmutablePair::getLeft, ImmutablePair::getRight));
    }

    public Map<Pair, List<Trade>> getTrades(Pair... pairs) throws BTCEException {
        return getTrades(null, pairs);
    }

    public Map<Pair, List<Trade>> getTrades(Integer limit, Pair... pairs) throws BTCEException {
        JsonObject json = call("trades", limit, pairs);

        return Stream.of(pairs)
                .map(pair -> ImmutablePair.of(pair, toTradeList(pair, json.get(pair.getName()).getAsJsonArray())))
                .collect(toMap(ImmutablePair::getLeft, ImmutablePair::getRight));
    }

    private List<Trade> toTradeList(Pair pair, JsonElement element) {
        JsonArray array = element.getAsJsonArray();

        return StreamSupport.stream(array.spliterator(), false)
                .map(e -> toTrade(pair, e.getAsJsonObject()))
                .collect(toList());
    }

    private Trade toTrade(Pair pair, JsonObject jsonTrade) {
        TradeType type = TradeType.valueOf(jsonTrade.get("type").getAsString().toUpperCase());
        double price = jsonTrade.get("price").getAsDouble();
        double amount = jsonTrade.get("amount").getAsDouble();
        long id = jsonTrade.get("tid").getAsLong();
        LocalDateTime timestamp = LocalDateTimeDeserializer.deserialize(jsonTrade.get("timestamp").getAsLong());
        Currency item = pair.getPrim();
        Currency priceCurrency = pair.getSec();

        return new Trade(timestamp, price, amount, id, priceCurrency, item, type);
    }

    private JsonObject call(String method, Integer limit, Pair... pairs) throws BTCEException {
        String preparedUrlPath = prepareUrl(method, limit, pairs);
        JsonObject response = processServerResponse(connector -> connector.get(createUrl(preparedUrlPath), headers))
                .getAsJsonObject();

        if (response.has("success") && response.get("success").getAsByte() == 0) {
            throw new BTCEException(get(response, "error").getAsString());
        }

        return response;
    }

    private String prepareUrl(String method, Integer limit, Pair... pairs) {
        String url = String.format(PUBLIC_API_TEMPLATE, method, Pair.toUrlString(pairs));
        if (limit != null) {
            url += "?limit=" + limit;
        }
        return url;
    }
}
