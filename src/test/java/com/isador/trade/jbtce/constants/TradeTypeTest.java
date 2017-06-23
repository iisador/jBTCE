package com.isador.trade.jbtce.constants;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by isador
 * on 23.06.17
 */
public class TradeTypeTest {

    @Test
    public void testParse() {
        TradeType expectedBuy = TradeType.BUY;
        TradeType expectedSell = TradeType.SELL;

        TradeType actualBuy = TradeType.parse("buy");
        TradeType actualSell = TradeType.parse("sell");
        TradeType actualBid = TradeType.parse("bid");
        TradeType actualAsk = TradeType.parse("ask");

        assertEquals("Actual type doesn't match", expectedBuy, actualBuy);
        assertEquals("Actual type doesn't match", expectedBuy, actualBid);
        assertEquals("Actual type doesn't match", expectedSell, actualSell);
        assertEquals("Actual type doesn't match", expectedSell, actualAsk);
    }

    @Test(expected = IllegalArgumentException.class)
    public void assertParseNull() {
        TradeType.parse(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void assertParseEmpty() {
        TradeType.parse("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void assertParseInvalid() {
        TradeType.parse("blablabla");
    }
}