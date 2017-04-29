package com.isador.btce.api.publicapi;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.isador.btce.api.*;
import com.isador.btce.api.constants.Pair;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toMap;

/**
 * Created by isador
 * on 20.04.17
 */
public class PublicApiV3 extends AbstractApi {

    private static final String PUBLIC_API_TMPL = "https://btc-e.com/api/3/%s/%s";

    private final Connector connector;

    public PublicApiV3() {
        this(new JavaConnector());
    }

    public PublicApiV3(Connector connector) {
        super(ImmutableMap.of(LocalDateTime.class, new LocalDateTimeDeserializer(),
                Depth.SimpleOrder.class, new SimpleOrderDeserializer()));
        this.connector = requireNonNull(connector, "Connector instance should be not null");
    }

    private String prepareUrl(String method, Pair... pairs) {
        String pairsString = Stream.of(pairs)
                .map(Pair::getName)
                .collect(Collectors.joining("-"));
        return String.format(PUBLIC_API_TMPL, method, pairsString);
    }

    public Map<Pair, Trade[]> getTrades(Pair... pairs) throws BTCEException {
        Pair[] validPairs = checkPairs(false, pairs);
        String response = connector.get(prepareUrl("trades", validPairs));
        processResponse(response);
        JsonObject json = (JsonObject) parser.parse(response);

        return Stream.of(validPairs)
                .map(pair -> ImmutablePair.of(pair, gson.fromJson(json.get(pair.getName()), Trade[].class)))
                .collect(toMap(ImmutablePair::getLeft, ImmutablePair::getRight));
    }

    private Pair[] checkPairs(boolean removeDuplicates, Pair... pairs) {
        requireNonNull(pairs, "Pairs must be specified");
        checkArgument(pairs.length > 0, "Pairs must be defined");

        if (removeDuplicates) {
            // todo: remove duplicates from array
        }

        return pairs;
    }

    private JsonElement processResponse(String json) throws BTCEException {
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
