package com.isador.btce.api.publicapi;

import com.isador.btce.api.BTCEException;
import com.isador.btce.api.Connector;
import com.isador.btce.api.constants.Currency;
import com.isador.btce.api.constants.Pair;
import com.isador.btce.api.constants.TradeType;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Objects;
import java.util.stream.Stream;

import static com.isador.btce.api.LocalDateTimeDeserializer.deserialize;
import static com.isador.btce.api.TestUtils.*;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.isOneOf;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Created by isador
 * on 06.04.2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class PublicApiTest {


    @Rule
    public ExpectedException thrown = ExpectedException.none();
    @Mock
    private Connector connector;
    private PublicApi api;

    @Before
    public void setUp() throws Exception {
        api = new PublicApi(connector);
    }

    @Test
    public void testCreateWithNullConnector() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("Connector instance should be not null");

        new PublicApi(null);
    }

    @Test
    public void testGetTickNullPair() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("Pair must be specified");
        api.getTick(null);
    }

    @Test
    public void testGetTickError() {
        thrown.expect(BTCEException.class);
        thrown.expectMessage("Some error");

        when(connector.getTick(Pair.BTC_USD)).thenReturn(getErrorJson());
        api.getTick(Pair.BTC_USD);
    }

    @Test
    public void testGetTick() {
        when(connector.getTick(Pair.BTC_USD)).thenReturn(getJson("ticker.json"));

        Tick tick = api.getTick(Pair.BTC_USD);

        assertNotNull("Tick must be not null", tick);
        assertThat("Tick.avg value is invalid", tick.getAvg(), greaterThan(0.0));
        assertThat("Tick.buy value is invalid", tick.getBuy(), greaterThan(0.0));
        assertThat("Tick.high value is invalid", tick.getHigh(), greaterThan(0.0));
        assertThat("Tick.last value is invalid", tick.getLast(), greaterThan(0.0));
        assertThat("Tick.low value is invalid", tick.getLow(), greaterThan(0.0));
        assertThat("Tick.sell value is invalid", tick.getSell(), greaterThan(0.0));
        assertThat("Tick.vol value is invalid", tick.getVol(), greaterThan(0.0));
        assertThat("Tick.volCur value is invalid", tick.getVolCur(), greaterThan(0.0));
        assertEquals("Update time doesn't match", deserialize(1491478857), tick.getUpdated());
        assertEquals("Server time doesn't match", deserialize(1491478858), tick.getServerTime());
    }

    @Test
    public void testGetTradesNullPair() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("Pair must be specified");
        api.getTick(null);
    }

    @Test
    public void testGetTradesError() {
        thrown.expect(BTCEException.class);
        thrown.expectMessage("Some error");

        when(connector.getTrades(Pair.BTC_USD)).thenReturn(getErrorJson());
        api.getTrades(Pair.BTC_USD);
    }

    @Test
    public void testGetTrades() {
        when(connector.getTrades(Pair.BTC_USD)).thenReturn(getJson("trades.json"));

        Trade[] trades = api.getTrades(Pair.BTC_USD);

        assertNotNull("Trades array should be not null", trades);
        assertEquals("Trades size doesn't match", 150, trades.length);
        assertFalse("Trades must not contain null elements", Stream.of(trades).anyMatch(Objects::isNull));
        Trade trade = trades[0];
        assertThat("Trade.price is invalid", trade.getPrice(), greaterThan(0.0));
        assertThat("Trade.amount is invalid", trade.getAmount(), greaterThan(0.0));
        assertThat("Trade.tradeId is invalid", trade.getTradeId(), greaterThan(0L));
        assertThat("Trade.item is invalid", trade.getItem(), isOneOf(Currency.values()));
        assertThat("Trade.priceCurrency is invalid", trade.getPriceCurrency(), isOneOf(Currency.values()));
        assertThat("Trade.type is invalid", trade.getType(), isOneOf(TradeType.values()));
        assertEquals("Update time doesn't match", deserialize(1491542177), trade.getDate());
    }

    @Test
    public void testGetDepthNullPair() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("Pair must be specified");
        api.getTick(null);
    }

    @Test
    public void testGetDepthError() {
        thrown.expect(BTCEException.class);
        thrown.expectMessage("Some error");

        when(connector.getDepth(Pair.BTC_USD)).thenReturn(getErrorJson());
        api.getDepth(Pair.BTC_USD);
    }

    @Test
    public void testGetDepth() {
        when(connector.getDepth(Pair.BTC_USD)).thenReturn(getJson("depth.json"));

        Depth depth = api.getDepth(Pair.BTC_USD);

        assertNotNull("Depth must be not null", depth);
        assertNotNull("Depth.asks must be not null", depth.getAsks());
        assertThat("Depth.asks length invalid", depth.getAsks().length, greaterThan(0));
        assertFalse("Depth.asks must not contain null elements", Stream.of(depth.getAsks()).anyMatch(Objects::isNull));
        assertSimpleOrder(depth.getAsks()[0]);

        assertNotNull("Depth.bids must be not null", depth.getBids());
        assertThat("Depth.bids length invalid", depth.getBids().length, greaterThan(0));
        assertFalse("Depth.bids must not contain null elements", Stream.of(depth.getBids()).anyMatch(Objects::isNull));
        assertSimpleOrder(depth.getBids()[0]);
    }

    @Test
    public void testGetFeehNullPair() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("Pair must be specified");
        api.getFee(null);
    }

    @Test
    public void testGetFeeError() {
        thrown.expect(BTCEException.class);
        thrown.expectMessage("Some error");

        when(connector.getFee(Pair.BTC_USD)).thenReturn(getErrorJson());
        api.getFee(Pair.BTC_USD);
    }

    @Test
    public void testGetFee() {
        when(connector.getFee(Pair.BTC_USD)).thenReturn("{\"trade\":0.2}");

        double fee = api.getFee(Pair.BTC_USD);

        assertThat("Fee invalid", fee, greaterThan(0.0));
    }

    private void assertSimpleOrder(Depth.SimpleOrder order) {
        assertThat("Order.amount invalid", order.getAmount(), greaterThan(0.0));
        assertThat("Order.price invalid", order.getPrice(), greaterThan(0.0));
    }
}