package com.isador.trade.jbtce.privateapi.deserializer;

import com.google.gson.*;
import com.isador.trade.jbtce.constants.Currency;
import com.isador.trade.jbtce.privateapi.Funds;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Funds deserializer.
 *
 * @see Funds
 * @see JsonDeserializer
 * @since 2.0.1
 * @author isador
 */
public class FundsDeserializer implements JsonDeserializer<Funds> {

    @Override
    public Funds deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Map<Currency, Double> funds = Stream.of(Currency.values())
                .map(currency -> ImmutablePair.of(currency, ((JsonObject) json).get(currency.name().toLowerCase()).getAsDouble()))
                .collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
        return new Funds(funds);
    }
}
