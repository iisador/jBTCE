package com.isador.trade.jbtce.privateapi.deserializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.isador.trade.jbtce.privateapi.TransactionType;

import java.lang.reflect.Type;

/**
 * Transaction type deserializer. Using Enum ordered conversion
 *
 * @author isador
 * @see TransactionType
 * @see JsonDeserializer
 * @since 2.0.1
 */
public class TransactionTypeDeserializer implements JsonDeserializer<TransactionType> {

    @Override
    public TransactionType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return TransactionType.values()[json.getAsInt()];
    }
}
