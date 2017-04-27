package com.isador.btce.api.publicapi;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.isador.btce.api.AbstractApi;
import com.isador.btce.api.BTCEException;
import com.isador.btce.api.Connector;
import com.isador.btce.api.LocalDateTimeDeserializer;
import com.isador.btce.api.constants.Pair;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

/**
 * Created by isador
 * on 20.04.17
 */
public class PublicV3Api extends AbstractApi {

    private static final String PUBLIC_API_TMPL = "https://btc-e.com/api/3/%s/%s";

    private final Connector connector;

    public PublicV3Api(Connector connector) {
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

    public Map<Pair, List<Trade>> getTrades(Pair... pairs) throws BTCEException {
        Pair[] validPairs = checkPairs(false, pairs);
//        JsonObject obj = (JsonObject) processResponse(connector.call(String.format(PUBLIC_API_TMPL, pair.getName(), "ticker")));
        return null;
    }

    private Pair[] checkPairs(boolean removeDuplicates, Pair... pairs) {
        requireNonNull(pairs, "Pairs must be not null");
        Preconditions.checkArgument(pairs.length > 0, "Pairs must be defined");

        if (removeDuplicates) {
            // todo: remove duplicates from array
        }

        return pairs;
    }
}
