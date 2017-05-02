package com.isador.btce.api.publicapi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by isador
 * on 02.05.2017.
 */
public class Asserts {

    public static void assertTicksEquals(Tick expected, Tick actual) {
        basicAssert(Tick.class, expected, actual);
        assertEquals("Actual avg value is invalid", expected.getAvg(), actual.getAvg(), 0.0000001);
        assertEquals("Actual buy value is invalid", expected.getBuy(), actual.getBuy(), 0.0000001);
        assertEquals("Actual high value is invalid", expected.getHigh(), actual.getHigh(), 0.0000001);
        assertEquals("Actual last value is invalid", expected.getLast(), actual.getLast(), 0.0000001);
        assertEquals("Actual low value is invalid", expected.getLow(), actual.getLow(), 0.0000001);
        assertEquals("Actual sell value is invalid", expected.getSell(), actual.getSell(), 0.0000001);
        assertEquals("Actual vol value is invalid", expected.getVol(), actual.getVol(), 0.0000001);
        assertEquals("Actual volCur value is invalid", expected.getVolCur(), actual.getVolCur(), 0.0000001);
        assertEquals("Actual Update time doesn't match", expected.getUpdated(), actual.getUpdated());
        assertEquals("Actual Server time doesn't match", expected.getServerTime(), actual.getServerTime());
        assertEquals("Actual tick pair doesn't equals", expected.getPair(), actual.getPair());
    }

    public static void assertTradesEquals(Trade expected, Trade actual) {
        basicAssert(Trade.class, expected, actual);
        assertEquals("Trade.price is invalid", expected.getPrice(), actual.getPrice(), 0.0000001);
        assertEquals("Trade.amount is invalid", expected.getAmount(), actual.getAmount(), 0.0000001);
        assertEquals("Trade.tradeId is invalid", expected.getId(), actual.getId(), 0.0000001);
        assertEquals("Trade.type is invalid", expected.getType(), actual.getType());
        assertEquals("Update time doesn't match", expected.getDate(), actual.getDate());
        assertEquals("Trade.item is invalid", expected.getItem(), actual.getItem());
        assertEquals("Trade.priceCurrency is invalid", expected.getPriceCurrency(), actual.getPriceCurrency());
    }

    public static <T> void basicAssert(Class<T> typeClass, T expected, T actual) {
        if (expected == null) {
            assertNull(String.format("Actual %s should be null", typeClass.getSimpleName()), actual);
            return;
        }

        assertEquals(String.format("Actual %s doesn't equals", typeClass.getSimpleName()), expected, actual);
    }
}
