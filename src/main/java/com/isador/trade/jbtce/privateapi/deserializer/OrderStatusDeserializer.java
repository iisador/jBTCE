package com.isador.trade.jbtce.privateapi.deserializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.isador.trade.jbtce.privateapi.OrderStatus;

import java.lang.reflect.Type;

/**
 * Order status deserializer. Using Enum ordered conversion
 *
 * @author isador
 * @see OrderStatus
 * @see JsonDeserializer
 * @since 2.0.1
 */
public class OrderStatusDeserializer implements JsonDeserializer<OrderStatus> {

    @Override
    public OrderStatus deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return OrderStatus.values()[json.getAsInt()];
    }
}
