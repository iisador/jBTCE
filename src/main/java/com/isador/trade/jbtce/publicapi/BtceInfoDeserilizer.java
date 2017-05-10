package com.isador.trade.jbtce.publicapi;

import com.google.gson.*;
import com.isador.trade.jbtce.LocalDateTimeDeserializer;
import com.isador.trade.jbtce.constants.Pair;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * Created by isador
 * on 10.05.17
 */
public class BtceInfoDeserilizer implements JsonDeserializer<BTCEInfo> {

    @Override
    public BTCEInfo deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = (JsonObject) json;
        // fixme: fix this govno
        List<PairInfo> pairsInfo = Stream.of(Pair.values())
                .map(pair -> ImmutablePair.of(pair.getName(), obj.get("pairs").getAsJsonObject().get(pair.getName()).getAsJsonObject()))
                .peek(pair -> pair.getRight().addProperty("pair", pair.getLeft()))
                .map(pair -> (PairInfo) context.deserialize(pair.getRight(), PairInfo.class))
                .collect(toList());
        return new BTCEInfo(LocalDateTimeDeserializer.deserialize(obj.get("server_time").getAsLong()), pairsInfo);
    }
}
