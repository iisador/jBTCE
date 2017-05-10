package com.isador.trade.jbtce.publicapi;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.isador.trade.jbtce.*;
import com.isador.trade.jbtce.constants.Currency;
import com.isador.trade.jbtce.constants.Pair;
import com.isador.trade.jbtce.constants.TradeType;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

/**
 * Created by isador
 * on 20.04.17
 */
public class PublicApiV3 extends AbstractApi {

    private static final String PUBLIC_API_TEMPLATE = "https://btc-e.com/api/3/%s/%s";

    private final Connector connector;

    public PublicApiV3() {
        this(new DefaultConnector());
    }

    public PublicApiV3(Connector connector) {
        super(ImmutableMap.of(LocalDateTime.class, new LocalDateTimeDeserializer(),
                Depth.SimpleOrder.class, new SimpleOrderDeserializer()));
        this.connector = requireNonNull(connector, "Connector instance should be not null");
    }

    public Map<Pair, Double> getFees(Pair... pairs) throws BTCEException {
        Pair[] validPairs = checkPairs(false, pairs);
        String response = connector.get(prepareUrl("fee", validPairs));

        JsonObject json = processResponse(response);

        return Stream.of(validPairs)
                .map(pair -> ImmutablePair.of(pair, json.get(pair.getName()).getAsDouble()))
                .collect(toMap(ImmutablePair::getLeft, ImmutablePair::getRight));
    }

    public Map<Pair, Tick> getTicks(Pair... pairs) throws BTCEException {
        Pair[] validPairs = checkPairs(false, pairs);
        String response = connector.get(prepareUrl("ticker", validPairs));

        JsonObject json = processResponse(response);

        return Stream.of(validPairs)
                .map(pair -> ImmutablePair.of(pair, gson.fromJson(json.get(pair.getName()), Tick.class)))
                .collect(toMap(ImmutablePair::getLeft, ImmutablePair::getRight));
    }

    public Map<Pair, Depth> getDepths(Pair... pairs) throws BTCEException {
        return getDepths(null, pairs);
    }

    public Map<Pair, Depth> getDepths(Integer limit, Pair... pairs) throws BTCEException {
        Pair[] validPairs = checkPairs(false, pairs);
        String response = connector.get(prepareUrl("depth", limit, validPairs));
        JsonObject json = processResponse(response);

        return Stream.of(validPairs)
                .map(pair -> ImmutablePair.of(pair, gson.fromJson(json.get(pair.getName()), Depth.class)))
                .collect(toMap(ImmutablePair::getLeft, ImmutablePair::getRight));
    }

    public Map<Pair, List<Trade>> getTrades(Pair... pairs) throws BTCEException {
        return getTrades(null, pairs);
    }

    public Map<Pair, List<Trade>> getTrades(Integer limit, Pair... pairs) throws BTCEException {
        Pair[] validPairs = checkPairs(false, pairs);
        String response = connector.get(prepareUrl("trades", limit, validPairs));

        JsonObject json = processResponse(response);

        return Stream.of(validPairs)
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

    private String prepareUrl(String method, Pair... pairs) {
        String pairsString = Stream.of(pairs)
                .map(Pair::getName)
                .collect(Collectors.joining("-"));
        return String.format(PUBLIC_API_TEMPLATE, method, pairsString);
    }

    private String prepareUrl(String method, Integer limit, Pair... pairs) {
        String url = prepareUrl(method, pairs);

        if (limit != null) {
            url += "?limit=" + limit;
        }

        return url;
    }

    private Pair[] checkPairs(boolean removeDuplicates, Pair... pairs) {
        requireNonNull(pairs, "Pairs must be specified");
        checkArgument(pairs.length > 0, "Pairs must be defined");

        if (removeDuplicates) {
            pairs = Stream.of(pairs).distinct().toArray(Pair[]::new);
        }

        return pairs;
    }

    private JsonObject processResponse(String json) throws BTCEException {
        processServerResponse(json);
        JsonObject obj = parser.parse(json).getAsJsonObject();
        if (obj.has("success") && obj.get("success").getAsByte() == 0) {
            throw new BTCEException(get(obj, "error").getAsString());
        }

        return obj;
    }

    public Connector getConnector() {
        return connector;
    }
}
