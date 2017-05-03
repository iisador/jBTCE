package com.isador.btce.api.publicapi;

import com.google.common.collect.ImmutableMap;
import com.isador.btce.api.BTCEException;
import com.isador.btce.api.Connector;
import com.isador.btce.api.JavaConnector;
import com.isador.btce.api.TestUtils;
import com.isador.btce.api.constants.Pair;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import static com.isador.btce.api.LocalDateTimeDeserializer.deserialize;
import static com.isador.btce.api.TestUtils.ahalaiMahalai;
import static com.isador.btce.api.TestUtils.getErrorJson;
import static com.isador.btce.api.constants.Pair.BTC_RUR;
import static com.isador.btce.api.constants.Pair.BTC_USD;
import static com.isador.btce.api.constants.TradeType.ASK;
import static com.isador.btce.api.constants.TradeType.BID;
import static com.isador.btce.api.publicapi.Asserts.assertTicksEquals;
import static com.isador.btce.api.publicapi.Asserts.assertTradesEquals;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.Matchers.isIn;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Created by isador
 * on 29.04.2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class PublicApiV3Test {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    private Connector connector;

    private PublicApiV3 api;

    @Before
    public void setUp() throws Exception {
        api = new PublicApiV3(connector);
    }

    @Test
    public void testCreate() {
        PublicApiV3 api = new PublicApiV3();

        assertNotNull("Connector must be not null", api.getConnector());
        assertThat("Invalid connector class", api.getConnector(), instanceOf(JavaConnector.class));
    }

    @Test
    public void testCreateWithNullConnector() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("Connector instance should be not null");

        new PublicApiV3(null);
    }

    @Test
    public void testGetTradesNullPairs() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("Pairs must be specified");

        api.getTrades(null);
    }


    @Test
    public void testGetTradesNoPairs() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Pairs must be defined");

        api.getTrades();
    }

    @Test
    public void testGetTradesInvalidResponseNull() {
        thrown.expect(BTCEException.class);
        thrown.expectMessage("Invalid server response. Null or empty response");
        when(connector.get("https://btc-e.com/api/3/trades/btc_usd")).thenReturn(null);

        api.getTrades(BTC_USD);
    }

    @Test
    public void testGetTradesInvalidResponseEmpty() {
        thrown.expect(BTCEException.class);
        thrown.expectMessage("Invalid server response. Null or empty response");
        when(connector.get("https://btc-e.com/api/3/trades/btc_usd")).thenReturn("");

        api.getTrades(BTC_USD);
    }

    @Test
    public void testGetTradesInvalidJson() {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Not a JSON Object: \"" + ahalaiMahalai() + "\"");
        when(connector.get("https://btc-e.com/api/3/trades/btc_usd")).thenReturn(ahalaiMahalai());

        api.getTrades(BTC_USD);
    }

    @Test
    public void testGetTradesInvalidResponseNoError() {
        thrown.expect(BTCEException.class);
        thrown.expectMessage("Invalid server response. \"error\" field missed.");
        when(connector.get("https://btc-e.com/api/3/trades/btc_usd")).thenReturn("{\"success\": 0}");

        api.getTrades(BTC_USD);
    }

    @Test
    public void testGetTradesError() {
        thrown.expect(BTCEException.class);
        thrown.expectMessage("Some error");
        when(connector.get("https://btc-e.com/api/3/trades/btc_usd")).thenReturn(getErrorJson());

        api.getTrades(BTC_USD);
    }

    @Test
    public void testGetTrades() {
        Map<Pair, Trade> expected = ImmutableMap.of(
                BTC_USD, new Trade(deserialize(1493365526), 1304.679, 0.905, 99673985, BTC_USD.getSec(), BTC_USD.getPrim(), BID),
                BTC_RUR, new Trade(deserialize(1493365545), 72850, 0.00350568, 99673997, BTC_RUR.getSec(), BTC_RUR.getPrim(), ASK));
        when(connector.get("https://btc-e.com/api/3/trades/btc_usd-btc_rur")).thenReturn(TestUtils.getJson("v3/trades.json"));

        Map<Pair, List<Trade>> actual = api.getTrades(expected.keySet().toArray(new Pair[2]));

        assertNotNull("Trades map should be not null", actual);
        assertEquals("Actual map size doesn't match", 2, actual.size());
        actual.forEach((pair, trades) -> {
            assertThat("Actual pair is invalid", pair, isIn(expected.keySet()));
            assertEquals("Trades size doesn't match", 150, trades.size());
            assertFalse("Trades must not contain null elements", Stream.of(trades).anyMatch(Objects::isNull));
            assertTradesEquals(expected.get(pair), trades.get(0));
        });
    }

    @Test
    public void testGetTicksNoPairs() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Pairs must be defined");

        api.getTicks();
    }

    @Test
    public void testGetTicksInvalidResponseNull() {
        thrown.expect(BTCEException.class);
        thrown.expectMessage("Invalid server response. Null or empty response");
        when(connector.get("https://btc-e.com/api/3/ticker/btc_usd")).thenReturn(null);

        api.getTicks(BTC_USD);
    }

    @Test
    public void testGetTicksInvalidResponseEmpty() {
        thrown.expect(BTCEException.class);
        thrown.expectMessage("Invalid server response. Null or empty response");
        when(connector.get("https://btc-e.com/api/3/ticker/btc_usd")).thenReturn("");

        api.getTicks(BTC_USD);
    }

    @Test
    public void testGetTicksInvalidJson() {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Not a JSON Object: \"" + ahalaiMahalai() + "\"");
        when(connector.get("https://btc-e.com/api/3/ticker/btc_usd")).thenReturn(ahalaiMahalai());

        api.getTicks(BTC_USD);
    }

    @Test
    public void testGetTicksInvalidResponseNoError() {
        thrown.expect(BTCEException.class);
        thrown.expectMessage("Invalid server response. \"error\" field missed.");
        when(connector.get("https://btc-e.com/api/3/ticker/btc_usd")).thenReturn("{\"success\": 0}");

        api.getTicks(BTC_USD);
    }

    @Test
    public void testGetTicksError() {
        thrown.expect(BTCEException.class);
        thrown.expectMessage("Some error");
        when(connector.get("https://btc-e.com/api/3/ticker/btc_usd")).thenReturn(getErrorJson());

        api.getTicks(BTC_USD);
    }

    @Test
    public void testGetTicks() {
        Map<Pair, Tick> expected = ImmutableMap.of(
                BTC_USD, new Tick(1364.8815, 1424, 1425, 1424.544, 1304.763, 1418.001, null, deserialize(1493724687), 11600857.10276, 8495.85241),
                BTC_RUR, new Tick(75851, 78614.60518, 78600, 78600, 73102, 78500, null, deserialize(1493724687), 38167773.12839, 505.17796));
        when(connector.get("https://btc-e.com/api/3/ticker/btc_usd-btc_rur")).thenReturn(TestUtils.getJson("v3/ticker.json"));

        Map<Pair, Tick> actual = api.getTicks(expected.keySet().toArray(new Pair[2]));

        assertNotNull("Ticks map should be not null", actual);
        assertEquals("Actual map size doesn't match", 2, actual.size());
        actual.forEach((pair, actualTick) -> {
            assertThat("Actual pair is invalid", pair, isIn(expected.keySet()));
            assertTicksEquals(expected.get(pair), actualTick);
        });
    }
}