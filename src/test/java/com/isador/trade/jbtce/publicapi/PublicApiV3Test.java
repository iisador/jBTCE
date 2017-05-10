package com.isador.trade.jbtce.publicapi;

import com.google.common.collect.ImmutableMap;
import com.isador.trade.jbtce.BTCEException;
import com.isador.trade.jbtce.Connector;
import com.isador.trade.jbtce.DefaultConnector;
import com.isador.trade.jbtce.TestUtils;
import com.isador.trade.jbtce.constants.Pair;
import com.isador.trade.jbtce.publicapi.Depth.SimpleOrder;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import static com.isador.trade.jbtce.LocalDateTimeDeserializer.deserialize;
import static com.isador.trade.jbtce.TestUtils.*;
import static com.isador.trade.jbtce.constants.Pair.*;
import static com.isador.trade.jbtce.constants.TradeType.ASK;
import static com.isador.trade.jbtce.constants.TradeType.BID;
import static com.isador.trade.jbtce.publicapi.Asserts.*;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
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
        assertThat("Invalid connector class", api.getConnector(), instanceOf(DefaultConnector.class));
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
                BTC_USD, new Trade(deserialize(1493365526), 1304.679, 0.905, 99673985, BTC_USD.getSec(), BTC_USD.getPrim(), BID), BTC_RUR, new Trade(deserialize(1493365545), 72850, 0.00350568, 99673997, BTC_RUR.getSec(), BTC_RUR.getPrim(), ASK));
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
                BTC_USD, new Tick(1364.8815, 1424, 1425, 1424.544, 1304.763, 1418.001, null, deserialize(1493724687), 11600857.10276, 8495.85241), BTC_RUR, new Tick(75851, 78614.60518, 78600, 78600, 73102, 78500, null, deserialize(1493724687), 38167773.12839, 505.17796));
        when(connector.get("https://btc-e.com/api/3/ticker/btc_usd-btc_rur")).thenReturn(TestUtils.getJson("v3/ticker.json"));

        Map<Pair, Tick> actual = api.getTicks(expected.keySet().toArray(new Pair[2]));

        assertNotNull("Ticks map should be not null", actual);
        assertEquals("Actual map size doesn't match", 2, actual.size());
        actual.forEach((pair, actualTick) -> {
            assertThat("Actual pair is invalid", pair, isIn(expected.keySet()));
            assertTicksEquals(expected.get(pair), actualTick);
        });
    }

    @Test
    public void testGetDepthsNoPairs() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Pairs must be defined");

        api.getDepths();
    }

    @Test
    public void testGetDepthsInvalidResponseNull() {
        thrown.expect(BTCEException.class);
        thrown.expectMessage("Invalid server response. Null or empty response");
        when(connector.get("https://btc-e.com/api/3/depth/btc_usd")).thenReturn(null);

        api.getDepths(BTC_USD);
    }

    @Test
    public void testGetDepthsInvalidResponseEmpty() {
        thrown.expect(BTCEException.class);
        thrown.expectMessage("Invalid server response. Null or empty response");
        when(connector.get("https://btc-e.com/api/3/depth/btc_usd")).thenReturn("");

        api.getDepths(BTC_USD);
    }

    @Test
    public void testGetDepthsInvalidJson() {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Not a JSON Object: \"" + ahalaiMahalai() + "\"");
        when(connector.get("https://btc-e.com/api/3/depth/btc_usd")).thenReturn(ahalaiMahalai());

        api.getDepths(BTC_USD);
    }

    @Test
    public void testGetDepthsInvalidResponseNoError() {
        thrown.expect(BTCEException.class);
        thrown.expectMessage("Invalid server response. \"error\" field missed.");
        when(connector.get("https://btc-e.com/api/3/depth/btc_usd")).thenReturn("{\"success\": 0}");

        api.getDepths(BTC_USD);
    }

    @Test
    public void testGetDepthsError() {
        thrown.expect(BTCEException.class);
        thrown.expectMessage("Some error");
        when(connector.get("https://btc-e.com/api/3/depth/btc_usd")).thenReturn(getErrorJson());

        api.getDepths(BTC_USD);
    }

    @Test
    public void testGetDepths() {
        Map<Pair, Depth> expected = getExpectedDepths();
        when(connector.get("https://btc-e.com/api/3/depth/btc_usd-btc_rur")).thenReturn(TestUtils.getJson("v3/depth.json"));

        Map<Pair, Depth> actual = api.getDepths(expected.keySet().toArray(new Pair[2]));

        assertNotNull("Depths map should be not null", actual);
        assertEquals("Actual map size doesn't match", 2, actual.size());
        actual.forEach((pair, actualDepth) -> {
            assertThat("Actual depth is invalid", pair, isIn(expected.keySet()));
            assertDepthsEquals(expected.get(pair), actualDepth);
        });
    }


    @Test
    public void testGetFeesNoPairs() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Pairs must be defined");

        api.getFees();
    }

    @Test
    public void testGetFeesInvalidResponseNull() {
        thrown.expect(BTCEException.class);
        thrown.expectMessage("Invalid server response. Null or empty response");
        when(connector.get("https://btc-e.com/api/3/fee/btc_usd")).thenReturn(null);

        api.getFees(BTC_USD);
    }

    @Test
    public void testGetFeesInvalidResponseEmpty() {
        thrown.expect(BTCEException.class);
        thrown.expectMessage("Invalid server response. Null or empty response");
        when(connector.get("https://btc-e.com/api/3/fee/btc_usd")).thenReturn("");

        api.getFees(BTC_USD);
    }

    @Test
    public void testGetFeesInvalidJson() {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Not a JSON Object: \"" + ahalaiMahalai() + "\"");
        when(connector.get("https://btc-e.com/api/3/fee/btc_usd")).thenReturn(ahalaiMahalai());

        api.getFees(BTC_USD);
    }

    @Test
    public void testGetFeesInvalidResponseNoError() {
        thrown.expect(BTCEException.class);
        thrown.expectMessage("Invalid server response. \"error\" field missed.");
        when(connector.get("https://btc-e.com/api/3/fee/btc_usd")).thenReturn("{\"success\": 0}");

        api.getFees(BTC_USD);
    }

    @Test
    public void testGetFeesError() {
        thrown.expect(BTCEException.class);
        thrown.expectMessage("Some error");
        when(connector.get("https://btc-e.com/api/3/fee/btc_usd")).thenReturn(getErrorJson());

        api.getFees(BTC_USD);
    }

    @Test
    public void testGetFees() {
        Map<Pair, Double> expected = ImmutableMap.of(BTC_USD, 0.2,
                BTC_RUR, 0.2);
        when(connector.get("https://btc-e.com/api/3/fee/btc_usd-btc_rur")).thenReturn(TestUtils.getJson("v3/fee.json"));

        Map<Pair, Double> actual = api.getFees(expected.keySet().toArray(new Pair[2]));

        assertNotNull("Fees map should be not null", actual);
        assertEquals("Actual map size doesn't match", 2, actual.size());
        actual.forEach((pair, actualFee) -> {
            assertThat("Actual depth is invalid", pair, isIn(expected.keySet()));
            assertEquals("Actual fee doesn't match", expected.get(pair), actualFee, 0.000001);
        });
    }

    @Test
    public void testGetInfoInvalidResponseNull() {
        thrown.expect(BTCEException.class);
        thrown.expectMessage("Invalid server response. Null or empty response");
        when(connector.get("https://btc-e.com/api/3/info")).thenReturn(null);

        api.getInfo();
    }

    @Test
    public void testGetInfoInvalidResponseEmpty() {
        thrown.expect(BTCEException.class);
        thrown.expectMessage("Invalid server response. Null or empty response");
        when(connector.get("https://btc-e.com/api/3/info")).thenReturn("");

        api.getInfo();
    }

    @Test
    public void testGetInfoInvalidJson() {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Not a JSON Object: \"" + ahalaiMahalai() + "\"");
        when(connector.get("https://btc-e.com/api/3/info")).thenReturn(ahalaiMahalai());

        api.getInfo();
    }

    @Test
    public void testGetInfo() {
        BTCEInfo expected = new BTCEInfo(deserialize(1494399104), getExpectedPairsInfo());
        when(connector.get("https://btc-e.com/api/3/info")).thenReturn(getJson("v3/info.json"));

        BTCEInfo actual = api.getInfo();

        assertNotNull("Actual btceinfo must be not null", actual);
        assertEquals("Actual btceinfo doesn't equals", expected, actual);
        assertEquals("Actual btceinfo server time doesn't equals", expected.getServerTime(), actual.getServerTime());
        assertEquals("Actual btceinfo has more pairs that expected", expected.getPairInfoList().size(), actual.getPairInfoList().size());
        Map<Pair, PairInfo> actualPairsInfoMap = actual.getPairInfoList().stream()
                .collect(toMap(PairInfo::getPair, identity()));
        expected.getPairInfoList().forEach(expectedPi -> {
            PairInfo actualPi = actualPairsInfoMap.get(expectedPi.getPair());
            assertPairsInfoEquals(expectedPi, actualPi);
        });
    }

    private void assertPairsInfoEquals(PairInfo expected, PairInfo actual) {
        basicAssert(PairInfo.class, expected, actual);

        assertEquals(String.format("Actual %s pair info decimalPlaces doesn't match", actual.getPair()), expected.getDecimalPlaces(), actual.getDecimalPlaces());
        assertEquals(String.format("Actual %s pair info fee doesn't match", actual.getPair()), expected.getFee(), actual.getFee(), 0.0000001);
        assertEquals(String.format("Actual %s pair info hidden doesn't match", actual.getPair()), expected.isHidden(), actual.isHidden());
        assertEquals(String.format("Actual %s pair info maxPrice doesn't match", actual.getPair()), expected.getMaxPrice(), actual.getMaxPrice(), 0.0000001);
        assertEquals(String.format("Actual %s pair info minPrica doesn't match", actual.getPair()), expected.getMinPrice(), actual.getMinPrice(), 0.0000001);
        assertEquals(String.format("Actual %s pair info minAmount doesn't match", actual.getPair()), expected.getMinAmount(), actual.getMinAmount(), 0.0000001);
        assertEquals(String.format("Actual %s pair info pair doesn't match", actual.getPair()), expected.getPair(), actual.getPair());


        assertEquals(expected.getDecimalPlaces(), actual.getDecimalPlaces());
    }

    private List<PairInfo> getExpectedPairsInfo() {
        return Arrays.asList(
                new PairInfo(3, 0.1, 10_000, 0.001, 0, 0.2, BTC_USD),
                new PairInfo(5, 1, 1_000_000, 0.001, 0, 0.2, BTC_RUR),
                new PairInfo(5, 0.1, 10000, 0.001, 0, 0.2, BTC_EUR),
                new PairInfo(5, 0.0001, 10, 0.01, 0, 0.2, LTC_BTC),
                new PairInfo(6, 0.0001, 1000, 0.1, 0, 0.2, LTC_USD),
                new PairInfo(5, 0.01, 100_000, 0.01, 0, 0.2, LTC_RUR),
                new PairInfo(3, 0.0001, 1000, 0.01, 0, 0.2, LTC_EUR),
                new PairInfo(5, 0.0001, 10, 0.1, 0, 0.2, NMC_BTC),
                new PairInfo(3, 0.001, 100, 0.1, 0, 0.2, NMC_USD),
                new PairInfo(5, 0.0001, 10, 0.1, 0, 0.2, NVC_BTC),
                new PairInfo(3, 0.001, 1000, 0.1, 0, 0.2, NVC_USD),
                new PairInfo(5, 25, 150, 0.1, 0, 0.2, USD_RUR),
                new PairInfo(5, 0.5, 2, 0.1, 0, 0.2, EUR_USD),
                new PairInfo(5, 30, 200, 0.1, 0, 0.2, EUR_RUR),
                new PairInfo(5, 0.0001, 10, 0.1, 0, 0.2, PPC_BTC),
                new PairInfo(3, 0.001, 100, 0.1, 0, 0.2, PPC_USD),
                new PairInfo(5, 0.0001, 10, 0.1, 0, 0.2, DSH_BTC),
                new PairInfo(5, 0.1, 1000, 0.1, 0, 0.2, DSH_USD),
                new PairInfo(3, 1, 100_000, 0.1, 0, 0.2, DSH_RUR),
                new PairInfo(3, 0.1, 1000, 0.1, 0, 0.2, DSH_EUR),
                new PairInfo(3, 0.1, 600, 0.1, 0, 0.2, DSH_LTC),
                new PairInfo(3, 0.1, 600, 0.1, 0, 0.2, DSH_ETH),
                new PairInfo(5, 0.0001, 10, 0.1, 0, 0.2, ETH_BTC),
                new PairInfo(5, 0.0001, 1000, 0.1, 0, 0.2, ETH_USD),
                new PairInfo(5, 0.0001, 1000, 0.1, 0, 0.2, ETH_EUR),
                new PairInfo(5, 0.0001, 1000, 0.1, 0, 0.2, ETH_LTC),
                new PairInfo(5, 0.0001, 100_000, 0.1, 0, 0.2, ETH_RUR)
        );
    }

    // pfff, govnische
    private Map<Pair, Depth> getExpectedDepths() {
        SimpleOrder[] btcUsdAsks = new SimpleOrder[]{
                new SimpleOrder(1389.896, 0.37335142),
                new SimpleOrder(1389.897, 0.82695949),
                new SimpleOrder(1389.9, 17.99590647),
                new SimpleOrder(1390, 0.74878118),
                new SimpleOrder(1390.098, 0.05),
                new SimpleOrder(1390.348, 0.1),
                new SimpleOrder(1390.598, 0.15),
                new SimpleOrder(1390.899, 0.01),
                new SimpleOrder(1391, 0.47099255),
                new SimpleOrder(1391.301, 0.00586038),
                new SimpleOrder(1391.98, 0.47835841),
                new SimpleOrder(1391.989, 0.1983),
                new SimpleOrder(1391.999, 0.01342319),
                new SimpleOrder(1392, 2.12522433),
                new SimpleOrder(1392.477, 0.02546396),
                new SimpleOrder(1392.478, 0.00192714),
                new SimpleOrder(1392.479, 0.0147993),
                new SimpleOrder(1392.641, 0.0104),
                new SimpleOrder(1392.79, 0.001),
                new SimpleOrder(1392.931, 0.001),
                new SimpleOrder(1392.979, 0.01492066),
                new SimpleOrder(1393, 0.7053),
                new SimpleOrder(1393.153, 0.0104),
                new SimpleOrder(1393.193, 0.01),
                new SimpleOrder(1393.479, 0.01504301),
                new SimpleOrder(1393.5, 0.005),
                new SimpleOrder(1393.6, 0.04318458),
                new SimpleOrder(1393.601, 0.01),
                new SimpleOrder(1393.828, 0.02878608),
                new SimpleOrder(1393.829, 0.2214805),
                new SimpleOrder(1393.83, 0.24131119),
                new SimpleOrder(1393.969, 0.04317228),
                new SimpleOrder(1393.97, 0.349),
                new SimpleOrder(1393.979, 0.01516636),
                new SimpleOrder(1393.99, 1.28603975),
                new SimpleOrder(1394, 0.005),
                new SimpleOrder(1394.106, 0.005),
                new SimpleOrder(1394.373, 0.03049164),
                new SimpleOrder(1394.374, 0.01),
                new SimpleOrder(1394.479, 0.01529072),
                new SimpleOrder(1394.5, 0.005),
                new SimpleOrder(1394.599, 0.22459303),
                new SimpleOrder(1394.608, 0.001),
                new SimpleOrder(1394.92, 9.92),
                new SimpleOrder(1394.921, 0.02876792),
                new SimpleOrder(1394.922, 0.0073395),
                new SimpleOrder(1394.978, 0.14380375),
                new SimpleOrder(1394.979, 0.01541611),
                new SimpleOrder(1395, 6.34206363),
                new SimpleOrder(1395.14, 0.32157345),
                new SimpleOrder(1395.153, 0.001),
                new SimpleOrder(1395.173, 0.01099624),
                new SimpleOrder(1395.313, 0.0104),
                new SimpleOrder(1395.479, 0.01554252),
                new SimpleOrder(1395.552, 0.02408819),
                new SimpleOrder(1395.555, 0.01),
                new SimpleOrder(1395.59, 0.06),
                new SimpleOrder(1395.611, 0.9889186),
                new SimpleOrder(1395.612, 0.0104),
                new SimpleOrder(1395.68, 0.734),
                new SimpleOrder(1395.701, 0.01),
                new SimpleOrder(1395.716, 0.0104),
                new SimpleOrder(1395.779, 1.005437),
                new SimpleOrder(1395.82, 0.001),
                new SimpleOrder(1395.971, 0.05029508),
                new SimpleOrder(1395.978, 0.05748008),
                new SimpleOrder(1395.979, 0.01566997),
                new SimpleOrder(1396, 12.852919),
                new SimpleOrder(1396.479, 0.01579846),
                new SimpleOrder(1396.627, 0.001),
                new SimpleOrder(1396.734, 0.0359069),
                new SimpleOrder(1396.736, 0.01),
                new SimpleOrder(1396.978, 0.01435985),
                new SimpleOrder(1396.979, 0.01592801),
                new SimpleOrder(1396.999, 0.01435958),
                new SimpleOrder(1397, 3.26919898),
                new SimpleOrder(1397.309, 0.15793916),
                new SimpleOrder(1397.31, 0.05),
                new SimpleOrder(1397.479, 0.01605862),
                new SimpleOrder(1397.801, 0.01),
                new SimpleOrder(1397.917, 0.01),
                new SimpleOrder(1397.979, 0.0161903),
                new SimpleOrder(1397.999, 1.42161408),
                new SimpleOrder(1398, 11.61771759),
                new SimpleOrder(1398.016, 0.75939788),
                new SimpleOrder(1398.03, 0.02781204),
                new SimpleOrder(1398.037, 11.26645),
                new SimpleOrder(1398.3, 0.1),
                new SimpleOrder(1398.429, 0.16031964),
                new SimpleOrder(1398.479, 0.01632306),
                new SimpleOrder(1398.48, 0.1),
                new SimpleOrder(1398.576, 18.82),
                new SimpleOrder(1398.578, 0.0104),
                new SimpleOrder(1398.723, 0.06242424),
                new SimpleOrder(1398.765, 4.63591855),
                new SimpleOrder(1398.888, 13.85200535),
                new SimpleOrder(1398.9, 1.1),
                new SimpleOrder(1398.979, 0.61525693),
                new SimpleOrder(1399, 157.15094788),
                new SimpleOrder(1399.241, 0.274926),
                new SimpleOrder(1399.323, 6),
                new SimpleOrder(1399.421, 7),
                new SimpleOrder(1399.479, 0.01659186),
                new SimpleOrder(1399.499, 0.25784413),
                new SimpleOrder(1399.828, 0.471231),
                new SimpleOrder(1399.876, 0.1),
                new SimpleOrder(1399.9, 0.18127628),
                new SimpleOrder(1399.901, 0.01),
                new SimpleOrder(1399.961, 1.54057261),
                new SimpleOrder(1399.979, 0.01672791),
                new SimpleOrder(1399.99, 0.11498388),
                new SimpleOrder(1399.999, 3.59738544),
                new SimpleOrder(1400, 91.64227892),
                new SimpleOrder(1400.003, 0.00411142),
                new SimpleOrder(1400.009, 0.28173711),
                new SimpleOrder(1400.056, 0.01097422),
                new SimpleOrder(1400.204, 1.54057261),
                new SimpleOrder(1400.218, 8.13053567),
                new SimpleOrder(1400.479, 0.01686508),
                new SimpleOrder(1400.52, 6.90901494),
                new SimpleOrder(1400.947, 0.087138),
                new SimpleOrder(1400.979, 0.01700337),
                new SimpleOrder(1401, 1.5),
                new SimpleOrder(1401.22, 10),
                new SimpleOrder(1401.222, 3.69818418),
                new SimpleOrder(1401.235, 0.1),
                new SimpleOrder(1401.479, 0.0171428),
                new SimpleOrder(1401.562, 0.786669),
                new SimpleOrder(1401.674, 3.84652907),
                new SimpleOrder(1401.841, 0.0011976),
                new SimpleOrder(1401.979, 0.01728337),
                new SimpleOrder(1402, 1.00897801),
                new SimpleOrder(1402.001, 0.01),
                new SimpleOrder(1402.01, 3.84652907),
                new SimpleOrder(1402.051, 3),
                new SimpleOrder(1402.479, 0.01742509),
                new SimpleOrder(1402.52, 0.1),
                new SimpleOrder(1402.979, 0.01756798),
                new SimpleOrder(1403, 0.75215689),
                new SimpleOrder(1403.007, 0.4995),
                new SimpleOrder(1403.043, 0.01029214),
                new SimpleOrder(1403.105, 0.07086378),
                new SimpleOrder(1403.207, 0.0017964),
                new SimpleOrder(1403.25, 0.935),
                new SimpleOrder(1403.284, 0.001),
                new SimpleOrder(1403.479, 0.01771204),
                new SimpleOrder(1403.979, 0.01785728),
                new SimpleOrder(1404, 0.015),
                new SimpleOrder(1404.101, 0.01),
                new SimpleOrder(1404.112, 0.0142867)
        };

        SimpleOrder[] btcUsdBids = new SimpleOrder[]{
                new SimpleOrder(1386.5, 0.58352816),
                new SimpleOrder(1386.407, 0.05),
                new SimpleOrder(1386.157, 0.1),
                new SimpleOrder(1385.907, 0.15),
                new SimpleOrder(1385.001, 0.01033644),
                new SimpleOrder(1385, 2.10719749),
                new SimpleOrder(1384.833, 0.0278),
                new SimpleOrder(1383.552, 0.00699),
                new SimpleOrder(1383.057, 0.0104),
                new SimpleOrder(1383.023, 0.92712),
                new SimpleOrder(1382.678, 0.01997545),
                new SimpleOrder(1381.901, 0.0194432),
                new SimpleOrder(1381.773, 0.0104),
                new SimpleOrder(1381.5, 0.0104),
                new SimpleOrder(1381.408, 0.01116019),
                new SimpleOrder(1381.165, 0.0100529),
                new SimpleOrder(1381.124, 0.00724047),
                new SimpleOrder(1380.734, 1.733),
                new SimpleOrder(1380.7, 0.011),
                new SimpleOrder(1380.61, 8.4),
                new SimpleOrder(1380.559, 0.359),
                new SimpleOrder(1380.284, 0.0104),
                new SimpleOrder(1380.1, 0.95),
                new SimpleOrder(1380, 0.02964546),
                new SimpleOrder(1379.945, 0.0104),
                new SimpleOrder(1379.39, 0.0104),
                new SimpleOrder(1379.277, 0.06422494),
                new SimpleOrder(1378.791, 0.01041),
                new SimpleOrder(1378.788, 0.01041),
                new SimpleOrder(1378.422, 0.20375531),
                new SimpleOrder(1378.42, 1.5),
                new SimpleOrder(1378.333, 0.0012024),
                new SimpleOrder(1378.082, 0.1),
                new SimpleOrder(1378, 0.02316),
                new SimpleOrder(1376.59, 0.01119272),
                new SimpleOrder(1376.1, 10),
                new SimpleOrder(1376, 0.015),
                new SimpleOrder(1375.777, 1),
                new SimpleOrder(1375.309, 0.58651256),
                new SimpleOrder(1375.11, 2),
                new SimpleOrder(1375.01, 0.0104),
                new SimpleOrder(1375, 0.08865),
                new SimpleOrder(1374, 0.5),
                new SimpleOrder(1373.905, 0.52405374),
                new SimpleOrder(1373.5, 0.00505721),
                new SimpleOrder(1373.414, 3.38233084),
                new SimpleOrder(1373.319, 0.9745),
                new SimpleOrder(1373.31, 0.9675),
                new SimpleOrder(1373.261, 0.0104),
                new SimpleOrder(1373.2, 0.00231556),
                new SimpleOrder(1373.102, 0.9784),
                new SimpleOrder(1373.011, 0.9784),
                new SimpleOrder(1373, 1.6258414),
                new SimpleOrder(1372.5, 0.00505362),
                new SimpleOrder(1372.003, 1.7735),
                new SimpleOrder(1372, 7.58955202),
                new SimpleOrder(1371.902, 0.98741),
                new SimpleOrder(1371.788, 0.01123118),
                new SimpleOrder(1371.5, 0.005),
                new SimpleOrder(1371.372, 0.2),
                new SimpleOrder(1371.183, 3.64648747),
                new SimpleOrder(1371.159, 0.0104),
                new SimpleOrder(1371, 0.107184),
                new SimpleOrder(1370.5, 0.00505008),
                new SimpleOrder(1370.47, 0.04231),
                new SimpleOrder(1370.104, 0.001),
                new SimpleOrder(1370.1, 0.07294842),
                new SimpleOrder(1370.01, 0.00584313),
                new SimpleOrder(1370.001, 0.01208742),
                new SimpleOrder(1370, 2.9483281),
                new SimpleOrder(1369.646, 0.218743),
                new SimpleOrder(1369.501, 1),
                new SimpleOrder(1369.5, 0.00509295),
                new SimpleOrder(1369, 0.00502645),
                new SimpleOrder(1368.5, 0.00505381),
                new SimpleOrder(1368.131, 0.0104),
                new SimpleOrder(1368.076, 0.025643),
                new SimpleOrder(1368, 0.00503377),
                new SimpleOrder(1367.5, 0.00506115),
                new SimpleOrder(1367.4, 0.02111355),
                new SimpleOrder(1367.359, 4.49453008),
                new SimpleOrder(1367.2, 5),
                new SimpleOrder(1367.111, 1),
                new SimpleOrder(1367.02, 5.60528298),
                new SimpleOrder(1367.004, 0.01126005),
                new SimpleOrder(1367, 6.35600938),
                new SimpleOrder(1366.15, 0.997),
                new SimpleOrder(1366, 20.00504293),
                new SimpleOrder(1365.55, 0.13932115),
                new SimpleOrder(1365.455, 0.002),
                new SimpleOrder(1365.01, 5),
                new SimpleOrder(1365.002, 0.1718),
                new SimpleOrder(1365.001, 0.1),
                new SimpleOrder(1365, 51.5233998),
                new SimpleOrder(1364.8, 0.5),
                new SimpleOrder(1364.396, 0.0010022),
                new SimpleOrder(1364, 0.0060636),
                new SimpleOrder(1363.999, 2.15551727),
                new SimpleOrder(1363.878, 0.0104),
                new SimpleOrder(1363.201, 1.32457374),
                new SimpleOrder(1363.142, 0.0104),
                new SimpleOrder(1363.141, 0.0208),
                new SimpleOrder(1363.009, 0.00836822),
                new SimpleOrder(1363, 2.26331195),
                new SimpleOrder(1362.5, 0.5),
                new SimpleOrder(1362.236, 0.01129538),
                new SimpleOrder(1362.172, 0.0116098),
                new SimpleOrder(1362.121, 0.009),
                new SimpleOrder(1362.102, 0.002),
                new SimpleOrder(1362.003, 0.005),
                new SimpleOrder(1362, 0.78655872),
                new SimpleOrder(1361.783, 0.0306),
                new SimpleOrder(1361.777, 1),
                new SimpleOrder(1361.501, 0.01),
                new SimpleOrder(1361.32, 0.25),
                new SimpleOrder(1361.22, 1.59160114),
                new SimpleOrder(1361.201, 0.001),
                new SimpleOrder(1361.1, 0.01838622),
                new SimpleOrder(1361.06, 0.09),
                new SimpleOrder(1361.045, 0.01),
                new SimpleOrder(1361.009, 0.005),
                new SimpleOrder(1361.001, 0.32509876),
                new SimpleOrder(1361, 10.21291171),
                new SimpleOrder(1360.901, 0.01),
                new SimpleOrder(1360.757, 0.15005117),
                new SimpleOrder(1360.721, 0.002144),
                new SimpleOrder(1360.496, 0.014206),
                new SimpleOrder(1360.332, 0.00443),
                new SimpleOrder(1360.116, 0.022272),
                new SimpleOrder(1360.111, 0.01),
                new SimpleOrder(1360.1, 0.1),
                new SimpleOrder(1360.045, 0.01),
                new SimpleOrder(1360.005, 0.06708089),
                new SimpleOrder(1360.002, 0.02269957),
                new SimpleOrder(1360.001, 0.1766906),
                new SimpleOrder(1360, 11.74436428),
                new SimpleOrder(1359.72, 0.01),
                new SimpleOrder(1359.712, 0.01944),
                new SimpleOrder(1359.5, 0.02480693),
                new SimpleOrder(1359.458, 0.00171143),
                new SimpleOrder(1359.409, 0.0104),
                new SimpleOrder(1359.401, 0.01),
                new SimpleOrder(1359.372, 3.480059),
                new SimpleOrder(1359.33, 0.295302),
                new SimpleOrder(1359.295, 0.120902),
                new SimpleOrder(1359.1, 0.005),
                new SimpleOrder(1359.06, 0.062382),
                new SimpleOrder(1359.045, 0.01),
                new SimpleOrder(1359, 0.11458401),
                new SimpleOrder(1358.727, 0.091164)
        };

        SimpleOrder[] btcRurAsks = new SimpleOrder[]{
                new SimpleOrder(77794.89647, 0.01112746),
                new SimpleOrder(77881.99417, 0.02472452),
                new SimpleOrder(77913.08792, 0.0122516),
                new SimpleOrder(77997.82015, 0.52099099),
                new SimpleOrder(77998.99787, 0.005),
                new SimpleOrder(77998.99887, 0.00868614),
                new SimpleOrder(77999, 0.17126298),
                new SimpleOrder(78000, 4.68030235),
                new SimpleOrder(78009.269, 0.0011),
                new SimpleOrder(78019.39159, 0.01),
                new SimpleOrder(78066.2652, 0.001),
                new SimpleOrder(78067.17861, 0.01107008),
                new SimpleOrder(78104.6214, 0.01),
                new SimpleOrder(78104.62142, 0.005591),
                new SimpleOrder(78143.2, 0.06902681),
                new SimpleOrder(78143.7, 0.0011),
                new SimpleOrder(78200, 8.22749832),
                new SimpleOrder(78281.50006, 0.0415276),
                new SimpleOrder(78292.17, 0.0011),
                new SimpleOrder(78300.60201, 0.01),
                new SimpleOrder(78317.411, 0.001),
                new SimpleOrder(78317.5261, 0.02419811),
                new SimpleOrder(78340.41373, 0.01103825),
                new SimpleOrder(78489.72187, 0.007655),
                new SimpleOrder(78500, 0.355),
                new SimpleOrder(78545.99997, 3),
                new SimpleOrder(78546, 0.0021),
                new SimpleOrder(78550.326, 0.0011),
                new SimpleOrder(78581.81243, 0.01),
                new SimpleOrder(78614.60518, 0.01100095),
                new SimpleOrder(78640.62462, 0.7),
                new SimpleOrder(78666, 0.02416452),
                new SimpleOrder(78757.49894, 0.00844571),
                new SimpleOrder(78757.49995, 0.02298539),
                new SimpleOrder(78778.99, 0.0011),
                new SimpleOrder(78780, 0.01428),
                new SimpleOrder(78781.01, 0.0011),
                new SimpleOrder(78782.645, 0.001),
                new SimpleOrder(78800, 1),
                new SimpleOrder(78814.47909, 0.04285073),
                new SimpleOrder(78831.005, 0.0011),
                new SimpleOrder(78847.872, 0.01098),
                new SimpleOrder(78848.1, 0.001),
                new SimpleOrder(78853.11556, 0.001),
                new SimpleOrder(78863.02285, 0.01),
                new SimpleOrder(78864, 0.1986),
                new SimpleOrder(78881, 0.0022),
                new SimpleOrder(78881.01, 0.0011),
                new SimpleOrder(78889.7563, 0.01104521),
                new SimpleOrder(78906.57, 0.01935314),
                new SimpleOrder(78925.2362, 0.00299),
                new SimpleOrder(78931.5, 0.01208),
                new SimpleOrder(78948.8, 0.001),
                new SimpleOrder(78948.981, 0.0111),
                new SimpleOrder(78954.16887, 4.8636),
                new SimpleOrder(78963, 0.0022),
                new SimpleOrder(78982, 0.0011),
                new SimpleOrder(78994.115, 0.001),
                new SimpleOrder(79000, 0.15599886),
                new SimpleOrder(79039.43, 0.001),
                new SimpleOrder(79048.493, 0.00299),
                new SimpleOrder(79049.5, 0.001),
                new SimpleOrder(79054.816, 0.0011),
                new SimpleOrder(79128.9523, 0.001),
                new SimpleOrder(79130.00259, 0.239607),
                new SimpleOrder(79144.23327, 0.01),
                new SimpleOrder(79147.07899, 0.01),
                new SimpleOrder(79160.366, 0.01098),
                new SimpleOrder(79164.9022, 0.00299),
                new SimpleOrder(79165.87045, 0.01104524),
                new SimpleOrder(79221.37, 0.0011),
                new SimpleOrder(79253.6189, 0.001),
                new SimpleOrder(79274.9, 0.0011),
                new SimpleOrder(79276.075, 0.003),
                new SimpleOrder(79283.485, 0.0011),
                new SimpleOrder(79283.99, 0.0011),
                new SimpleOrder(79285, 0.0022),
                new SimpleOrder(79286.6485, 0.001),
                new SimpleOrder(79308.9032, 0.004),
                new SimpleOrder(79364.689, 0.0011),
                new SimpleOrder(79400.3, 0.005),
                new SimpleOrder(79400.746, 0.0011),
                new SimpleOrder(79425.44369, 0.01),
                new SimpleOrder(79442.95099, 0.01103991),
                new SimpleOrder(79489.727, 0.0011),
                new SimpleOrder(79500, 0.37679027),
                new SimpleOrder(79512.25, 0.0022),
                new SimpleOrder(79537.5, 0.01208),
                new SimpleOrder(79545.075, 0.0022),
                new SimpleOrder(79545.149, 0.0022),
                new SimpleOrder(79545.176, 0.0022),
                new SimpleOrder(79550.631, 0.0011),
                new SimpleOrder(79555, 0.01884217),
                new SimpleOrder(79600, 0.005),
                new SimpleOrder(79629.107, 0.01208),
                new SimpleOrder(79653.549, 0.0011),
                new SimpleOrder(79700, 0.03055847),
                new SimpleOrder(79706.65411, 0.01),
                new SimpleOrder(79721.00132, 0.01119247),
                new SimpleOrder(79790, 0.0011),
                new SimpleOrder(79800, 0.005),
                new SimpleOrder(79838.28941, 0.01),
                new SimpleOrder(79867, 0.104),
                new SimpleOrder(79889.99, 0.0011),
                new SimpleOrder(79925, 0.1),
                new SimpleOrder(79930, 0.01),
                new SimpleOrder(79987.86453, 0.01),
                new SimpleOrder(79990, 0.385),
                new SimpleOrder(79999.999, 0.05659332),
                new SimpleOrder(79999.9999, 0.01),
                new SimpleOrder(80000, 5.92219572),
                new SimpleOrder(80000.02482, 0.01117606),
                new SimpleOrder(80017.181, 0.0011),
                new SimpleOrder(80049, 0.001),
                new SimpleOrder(80050, 0.005),
                new SimpleOrder(80070, 0.0011),
                new SimpleOrder(80087.305, 0.0011),
                new SimpleOrder(80099, 0.001),
                new SimpleOrder(80121, 0.05),
                new SimpleOrder(80149, 0.001),
                new SimpleOrder(80153.6, 0.01208),
                new SimpleOrder(80162.67, 0.0011),
                new SimpleOrder(80188, 0.77756653),
                new SimpleOrder(80190, 0.1),
                new SimpleOrder(80199, 0.001),
                new SimpleOrder(80219.51, 0.0011),
                new SimpleOrder(80245.872, 0.01098),
                new SimpleOrder(80246, 0.0780536),
                new SimpleOrder(80247.48, 0.0011),
                new SimpleOrder(80249, 0.001),
                new SimpleOrder(80249.85, 0.0011),
                new SimpleOrder(80269.07495, 0.01),
                new SimpleOrder(80280.02491, 0.01112418),
                new SimpleOrder(80299, 0.001),
                new SimpleOrder(80342, 0.02),
                new SimpleOrder(80349, 0.001),
                new SimpleOrder(80399, 0.001),
                new SimpleOrder(80405.09, 0.01497),
                new SimpleOrder(80444, 0.011),
                new SimpleOrder(80449, 0.001),
                new SimpleOrder(80490, 0.03),
                new SimpleOrder(80499, 0.001),
                new SimpleOrder(80500, 0.086),
                new SimpleOrder(80529.49983, 0.01),
                new SimpleOrder(80549, 0.001),
                new SimpleOrder(80555, 0.00555),
                new SimpleOrder(80599, 0.001),
                new SimpleOrder(80614.68, 0.0011),
                new SimpleOrder(80649, 0.001),
                new SimpleOrder(80662.465, 0.001996)
        };

        SimpleOrder[] btcRurBids = new SimpleOrder[]{
                new SimpleOrder(77271.29401, 0.005),
                new SimpleOrder(77271.29338, 0.7),
                new SimpleOrder(77270, 0.626),
                new SimpleOrder(77268.436, 0.07787221),
                new SimpleOrder(77231.95154, 0.0157816),
                new SimpleOrder(77184.19631, 0.02649159),
                new SimpleOrder(77126.36258, 7),
                new SimpleOrder(77116, 0.01967632),
                new SimpleOrder(77056.8, 0.04561772),
                new SimpleOrder(77027.34204, 0.01118644),
                new SimpleOrder(76985, 0.19484314),
                new SimpleOrder(76907.3315, 0.04338013),
                new SimpleOrder(76759, 0.02),
                new SimpleOrder(76758.68664, 0.01121081),
                new SimpleOrder(76752.602, 0.02152939),
                new SimpleOrder(76750, 0.007),
                new SimpleOrder(76701.01, 0.04979168),
                new SimpleOrder(76701, 0.05066),
                new SimpleOrder(76700, 0.255506),
                new SimpleOrder(76638.84715, 0.0018865),
                new SimpleOrder(76520, 0.027385),
                new SimpleOrder(76500, 0.05417763),
                new SimpleOrder(76490.96825, 0.01124405),
                new SimpleOrder(76405, 0.25158206),
                new SimpleOrder(76402.0001, 0.2545105),
                new SimpleOrder(76402.00009, 0.0268775),
                new SimpleOrder(76402, 0.03511974),
                new SimpleOrder(76401, 0.03523),
                new SimpleOrder(76400, 9.57725004),
                new SimpleOrder(76324, 0.02),
                new SimpleOrder(76321.13279, 0.00172964),
                new SimpleOrder(76318.18117, 0.01),
                new SimpleOrder(76300, 0.01044804),
                new SimpleOrder(76224.18361, 0.01122485),
                new SimpleOrder(76199.26896, 0.00177091),
                new SimpleOrder(76132, 0.02),
                new SimpleOrder(76077.40512, 0.0018123),
                new SimpleOrder(76052.03947, 0.001315),
                new SimpleOrder(76036.97075, 0.01),
                new SimpleOrder(76035.86857, 0.01),
                new SimpleOrder(76020, 0.03984395),
                new SimpleOrder(76001, 0.021),
                new SimpleOrder(76000.01, 0.5),
                new SimpleOrder(76000.001, 0.0101),
                new SimpleOrder(76000.0001, 0.0101),
                new SimpleOrder(76000, 0.13136022),
                new SimpleOrder(75998, 0.00104997),
                new SimpleOrder(75958.32946, 0.01138131),
                new SimpleOrder(75902.86331, 0.090739),
                new SimpleOrder(75857.75263, 0.002878),
                new SimpleOrder(75801, 0.001),
                new SimpleOrder(75755.76036, 2.01707101),
                new SimpleOrder(75755.76033, 0.01),
                new SimpleOrder(75693.40255, 0.0114155),
                new SimpleOrder(75640.94736, 0.001388),
                new SimpleOrder(75601, 0.001),
                new SimpleOrder(75555, 0.01948857),
                new SimpleOrder(75520, 0.03122133),
                new SimpleOrder(75505, 0.37261532),
                new SimpleOrder(75501.01, 0.04934991),
                new SimpleOrder(75500.0001, 0.0101),
                new SimpleOrder(75500, 2.16913242),
                new SimpleOrder(75474.54991, 0.01),
                new SimpleOrder(75429.39965, 0.01140965),
                new SimpleOrder(75401, 0.001),
                new SimpleOrder(75344.65815, 0.01),
                new SimpleOrder(75229.85526, 0.001465),
                new SimpleOrder(75201, 0.001),
                new SimpleOrder(75193.33949, 0.01),
                new SimpleOrder(75166.31754, 0.01145382),
                new SimpleOrder(75111, 0.00111),
                new SimpleOrder(75070.16612, 0.05),
                new SimpleOrder(75064, 0.0063),
                new SimpleOrder(75033.21184, 0.003055),
                new SimpleOrder(75022, 0.1),
                new SimpleOrder(75020, 0.027),
                new SimpleOrder(75008, 0.3),
                new SimpleOrder(75001, 0.001),
                new SimpleOrder(75000.001, 0.0452),
                new SimpleOrder(75000.0001, 0.1001),
                new SimpleOrder(75000, 0.23793838),
                new SimpleOrder(74912.1291, 0.001),
                new SimpleOrder(74912.12907, 0.01),
                new SimpleOrder(74904.153, 0.01148133),
                new SimpleOrder(74818.76315, 0.001547),
                new SimpleOrder(74801, 0.001),
                new SimpleOrder(74769.98476, 0.096719),
                new SimpleOrder(74730.01, 0.001),
                new SimpleOrder(74730, 1.83032021),
                new SimpleOrder(74653.44773, 0.01),
                new SimpleOrder(74642.90284, 0.01158735),
                new SimpleOrder(74630.91865, 0.01),
                new SimpleOrder(74601, 0.001),
                new SimpleOrder(74520, 0.02698068),
                new SimpleOrder(74507, 0.002),
                new SimpleOrder(74500.0001, 0.0101),
                new SimpleOrder(74500, 0.13),
                new SimpleOrder(74407.67105, 0.001633),
                new SimpleOrder(74401, 0.001),
                new SimpleOrder(74400, 0.39784079),
                new SimpleOrder(74350.804, 0.09214895),
                new SimpleOrder(74349.70823, 0.01),
                new SimpleOrder(74250, 0.01),
                new SimpleOrder(74208.67105, 0.003243),
                new SimpleOrder(74201, 0.001),
                new SimpleOrder(74147.6, 0.52676146),
                new SimpleOrder(74111, 0.00111),
                new SimpleOrder(74068.49781, 0.01),
                new SimpleOrder(74022, 0.1),
                new SimpleOrder(74020, 0.02696028),
                new SimpleOrder(74001, 0.001),
                new SimpleOrder(74000.001, 0.0101),
                new SimpleOrder(74000.0001, 0.0101),
                new SimpleOrder(74000, 0.6037358),
                new SimpleOrder(73996.57894, 0.001725),
                new SimpleOrder(73962.23731, 0.01),
                new SimpleOrder(73801, 0.001),
                new SimpleOrder(73787.28739, 0.01),
                new SimpleOrder(73750, 0.04401119),
                new SimpleOrder(73700, 0.30334784),
                new SimpleOrder(73637.1062, 0.103118),
                new SimpleOrder(73601, 0.001),
                new SimpleOrder(73585.48684, 0.001821),
                new SimpleOrder(73510, 0.01401881),
                new SimpleOrder(73506.07697, 0.01),
                new SimpleOrder(73500.0001, 0.0202),
                new SimpleOrder(73500, 0.72815237),
                new SimpleOrder(73401, 0.001),
                new SimpleOrder(73384.13026, 0.003443),
                new SimpleOrder(73271.02689, 0.01),
                new SimpleOrder(73260, 0.03),
                new SimpleOrder(73224.86655, 0.01),
                new SimpleOrder(73201, 0.001),
                new SimpleOrder(73111, 0.00111),
                new SimpleOrder(73046, 0.002),
                new SimpleOrder(73022, 0.1),
                new SimpleOrder(73020, 0.0141585),
                new SimpleOrder(73001, 0.001),
                new SimpleOrder(73000.001, 0.0101),
                new SimpleOrder(73000.0001, 0.0202),
                new SimpleOrder(73000, 1.10665653),
                new SimpleOrder(72999.4199, 0.00195501),
                new SimpleOrder(72960.187, 0.00677453),
                new SimpleOrder(72943.65613, 0.01),
                new SimpleOrder(72901, 0.001),
                new SimpleOrder(72900, 0.1),
                new SimpleOrder(72806, 0.06497673),
                new SimpleOrder(72801, 0.001),
                new SimpleOrder(72800.007, 2.01008681),
                new SimpleOrder(72750, 0.03)
        };
        return ImmutableMap.of(BTC_USD, new Depth(btcUsdAsks, btcUsdBids), BTC_RUR, new Depth(btcRurAsks, btcRurBids));
    }
}