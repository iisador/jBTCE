package com.isador.btce.api.publicapi;

import com.isador.btce.api.BTCEException;
import com.isador.btce.api.Connector;
import com.isador.btce.api.JavaConnector;
import com.isador.btce.api.constants.Currency;
import com.isador.btce.api.constants.Pair;
import com.isador.btce.api.constants.TradeType;
import com.isador.btce.api.publicapi.Depth.SimpleOrder;
import nl.jqno.equalsverifier.EqualsVerifier;
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
import static com.isador.btce.api.TestUtils.getErrorJson;
import static com.isador.btce.api.TestUtils.getJson;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
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
    public void testGetTickInvalidResponseNull() {
        thrown.expect(BTCEException.class);
        thrown.expectMessage("Invalid server response. Null or empty response");
        when(connector.get(anyString())).thenReturn("");
        api.getTick(Pair.BTC_USD);
    }

    @Test
    public void testGetTickInvalidResponseEmpty() {
        thrown.expect(BTCEException.class);
        thrown.expectMessage("Invalid server response. Null or empty response");
        when(connector.get(anyString())).thenReturn("");
        api.getTick(Pair.BTC_USD);
    }

    @Test
    public void testGetTickInvalidResponseNoError() {
        thrown.expect(BTCEException.class);
        thrown.expectMessage("Invalid server response. \"error\" field missed.");
        when(connector.get(anyString())).thenReturn("{\"success\": 0}");
        api.getTick(Pair.BTC_USD);
    }

    @Test
    public void testGetTickError() {
        thrown.expect(BTCEException.class);
        thrown.expectMessage("Some error");

        when(connector.get(anyString())).thenReturn(getErrorJson());
        api.getTick(Pair.BTC_USD);
    }

    @Test
    public void testGetTick() {
        Tick expected = new Tick(1128.0015, 1154.849, 1155.003,
                1150.504, 1101, 1150.504,
                deserialize(1491478858), deserialize(1491478857),
                10573719.72477, 9390.70116, null);
        when(connector.get(anyString())).thenReturn(getJson("ticker.json"));

        Tick actual = api.getTick(Pair.BTC_USD);

        assertNotNull("Tick must be not null", actual);
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
        assertNull("Actual tick pair must be null", actual.getPair());

        EqualsVerifier.forClass(Tick.class)
                .withOnlyTheseFields("updated", "pair")
                .verify();
    }

    @Test
    public void testGetTradesNullPair() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("Pair must be specified");
        api.getTrades(null);
    }

    @Test
    public void testGetTradesInvalidResponseNull() {
        thrown.expect(BTCEException.class);
        thrown.expectMessage("Invalid server response. Null or empty response");
        when(connector.get(anyString())).thenReturn("");
        api.getTrades(Pair.BTC_USD);
    }

    @Test
    public void testGetTradesInvalidResponseEmpty() {
        thrown.expect(BTCEException.class);
        thrown.expectMessage("Invalid server response. Null or empty response");
        when(connector.get(anyString())).thenReturn("");
        api.getTrades(Pair.BTC_USD);
    }

    @Test
    public void testGetTradesInvalidResponseNoError() {
        thrown.expect(BTCEException.class);
        thrown.expectMessage("Invalid server response. \"error\" field missed.");
        when(connector.get(anyString())).thenReturn("{\"success\": 0}");
        api.getTrades(Pair.BTC_USD);
    }

    @Test
    public void testGetTradesError() {
        thrown.expect(BTCEException.class);
        thrown.expectMessage("Some error");

        when(connector.get(anyString())).thenReturn(getErrorJson());
        api.getTrades(Pair.BTC_USD);
    }

    @Test
    public void testGetTrades() {
        Trade expectedTrade = new Trade(deserialize(1491542177), 1178, 1.78, 97938795, Currency.USD, Currency.BTC, TradeType.BID);
        when(connector.get(anyString())).thenReturn(getJson("trades.json"));

        Trade[] actualTrades = api.getTrades(Pair.BTC_USD);

        assertNotNull("Trades array should be not null", actualTrades);
        assertEquals("Trades size doesn't match", 150, actualTrades.length);
        assertFalse("Trades must not contain null elements", Stream.of(actualTrades).anyMatch(Objects::isNull));
        Trade actualTrade = actualTrades[0];
        assertEquals("Actual trade doesn't match", expectedTrade, actualTrade);
        assertEquals("Trade.price is invalid", expectedTrade.getPrice(), actualTrade.getPrice(), 0.0000001);
        assertEquals("Trade.amount is invalid", expectedTrade.getAmount(), actualTrade.getAmount(), 0.0000001);
        assertEquals("Trade.tradeId is invalid", expectedTrade.getId(), actualTrade.getId(), 0.0000001);
        assertEquals("Trade.item is invalid", expectedTrade.getItem(), actualTrade.getItem());
        assertEquals("Trade.priceCurrency is invalid", expectedTrade.getPriceCurrency(), actualTrade.getPriceCurrency());
        assertEquals("Trade.type is invalid", expectedTrade.getType(), actualTrade.getType());
        assertEquals("Update time doesn't match", expectedTrade.getDate(), actualTrade.getDate());
    }

    @Test
    public void testGetDepthNullPair() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("Pair must be specified");
        api.getTick(null);
    }

    @Test
    public void testGetDepthInvalidResponseNull() {
        thrown.expect(BTCEException.class);
        thrown.expectMessage("Invalid server response. Null or empty response");
        when(connector.get(anyString())).thenReturn("");
        api.getDepth(Pair.BTC_USD);
    }

    @Test
    public void testGetDepthInvalidResponseEmpty() {
        thrown.expect(BTCEException.class);
        thrown.expectMessage("Invalid server response. Null or empty response");
        when(connector.get(anyString())).thenReturn("");
        api.getDepth(Pair.BTC_USD);
    }

    @Test
    public void testGetDepthInvalidResponseNoError() {
        thrown.expect(BTCEException.class);
        thrown.expectMessage("Invalid server response. \"error\" field missed.");
        when(connector.get(anyString())).thenReturn("{\"success\": 0}");
        api.getDepth(Pair.BTC_USD);
    }

    @Test
    public void testGetDepthError() {
        thrown.expect(BTCEException.class);
        thrown.expectMessage("Some error");

        when(connector.get(anyString())).thenReturn(getErrorJson());
        api.getDepth(Pair.BTC_USD);
    }

    @Test
    public void testGetDepth() {
        Depth expected = getExpectedDepth();
        when(connector.get(anyString())).thenReturn(getJson("depth.json"));

        Depth actual = api.getDepth(Pair.BTC_USD);

        assertNotNull("Depth must be not null", actual);
        assertNotNull("Depth.asks must be not null", actual.getAsks());
        assertNotNull("Depth.bids must be not null", actual.getBids());
        assertArrayEquals("Actual asks doesn't match", expected.getAsks(), actual.getAsks());
        assertArrayEquals("Actual bids doesn't match", expected.getBids(), actual.getBids());

        Depth.SimpleOrder expectedOrder = expected.getAsks()[0];
        Depth.SimpleOrder actualOrder = actual.getAsks()[0];

        assertEquals("Actual order doesn't match", expectedOrder, actualOrder);
        assertEquals("Actual order amount doesn't match", expectedOrder.getAmount(), actualOrder.getAmount(), 0.000001);
        assertEquals("Actual order price doesn't match", expectedOrder.getPrice(), actualOrder.getPrice(), 0.000001);
    }

    @Test
    public void testGetFeeNullPair() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("Pair must be specified");
        api.getFee(null);
    }

    @Test
    public void testGetFeeInvalidResponseNull() {
        thrown.expect(BTCEException.class);
        thrown.expectMessage("Invalid server response. Null or empty response");
        when(connector.get(anyString())).thenReturn("");
        api.getFee(Pair.BTC_USD);
    }

    @Test
    public void testGetFeeInvalidResponseEmpty() {
        thrown.expect(BTCEException.class);
        thrown.expectMessage("Invalid server response. Null or empty response");
        when(connector.get(anyString())).thenReturn("");
        api.getFee(Pair.BTC_USD);
    }

    @Test
    public void testGetFeeInvalidResponseNoError() {
        thrown.expect(BTCEException.class);
        thrown.expectMessage("Invalid server response. \"error\" field missed.");
        when(connector.get(anyString())).thenReturn("{\"success\": 0}");
        api.getFee(Pair.BTC_USD);
    }

    @Test
    public void testGetFeeError() {
        thrown.expect(BTCEException.class);
        thrown.expectMessage("Some error");

        when(connector.get(anyString())).thenReturn(getErrorJson());
        api.getFee(Pair.BTC_USD);
    }

    @Test
    public void testGetFee() {
        when(connector.get(anyString())).thenReturn("{\"trade\":0.2}");

        double fee = api.getFee(Pair.BTC_USD);

        assertThat("Fee invalid", fee, greaterThan(0.0));
    }

    @Test
    public void testCreate() {
        PublicApi api = new PublicApi();

        assertNotNull("Connector must be not null", api.getConnector());
        assertThat("Invalid connector class", api.getConnector(), instanceOf(JavaConnector.class));
    }

    private Depth getExpectedDepth() {
        SimpleOrder[] asks = new SimpleOrder[]{
                new SimpleOrder(1177.999, 0.5),
                new SimpleOrder(1178, 4.01039939),
                new SimpleOrder(1178.172, 0.0051885),
                new SimpleOrder(1178.449, 0.05369222),
                new SimpleOrder(1178.699, 0.17809008),
                new SimpleOrder(1178.999, 0.11060449),
                new SimpleOrder(1179, 9.31860979),
                new SimpleOrder(1179.109, 0.00850653),
                new SimpleOrder(1179.11, 0.3),
                new SimpleOrder(1179.148, 0.0090223),
                new SimpleOrder(1179.493, 0.01701136),
                new SimpleOrder(1179.494, 0.0049724),
                new SimpleOrder(1179.669, 10),
                new SimpleOrder(1179.721, 0.011),
                new SimpleOrder(1179.756, 0.01700406),
                new SimpleOrder(1179.763, 0.00850203),
                new SimpleOrder(1179.764, 6.11277682),
                new SimpleOrder(1179.791, 0.9),
                new SimpleOrder(1179.8, 0.23776854),
                new SimpleOrder(1179.899, 0.03400336),
                new SimpleOrder(1179.9, 9.273),
                new SimpleOrder(1179.95, 0.24447435),
                new SimpleOrder(1179.989, 0.05950184),
                new SimpleOrder(1179.99, 0.01),
                new SimpleOrder(1179.998, 3.141592),
                new SimpleOrder(1180, 12.86686846),
                new SimpleOrder(1180.011, 0.07367513),
                new SimpleOrder(1180.015, 0.0104),
                new SimpleOrder(1180.437, 0.05947935),
                new SimpleOrder(1180.438, 0.0233),
                new SimpleOrder(1180.513, 0.01699286),
                new SimpleOrder(1180.514, 0.11093449),
                new SimpleOrder(1180.558, 0.93066779),
                new SimpleOrder(1180.598, 0.0104),
                new SimpleOrder(1180.617, 1.30992505),
                new SimpleOrder(1180.674, 0.77107737),
                new SimpleOrder(1180.754, 1.14),
                new SimpleOrder(1180.764, 0.00849462),
                new SimpleOrder(1180.765, 0.001),
                new SimpleOrder(1180.82, 0.0104),
                new SimpleOrder(1180.915, 1.30992505),
                new SimpleOrder(1180.94, 0.05945512),
                new SimpleOrder(1180.941, 0.6),
                new SimpleOrder(1180.981, 2.45858775),
                new SimpleOrder(1180.982, 0.1741362),
                new SimpleOrder(1180.983, 2.65166415),
                new SimpleOrder(1181, 0.1),
                new SimpleOrder(1181.055, 0.07163912),
                new SimpleOrder(1181.21, 0.16133656),
                new SimpleOrder(1181.211, 0.011),
                new SimpleOrder(1181.444, 0.01151692),
                new SimpleOrder(1181.546, 2.6346065),
                new SimpleOrder(1181.55, 0.55270304),
                new SimpleOrder(1181.721, 0.01825696),
                new SimpleOrder(1181.726, 3.45116711),
                new SimpleOrder(1181.825, 1.017),
                new SimpleOrder(1181.961, 5.54280267),
                new SimpleOrder(1181.999, 0.17821608),
                new SimpleOrder(1182, 0.874087),
                new SimpleOrder(1182.735, 1),
                new SimpleOrder(1182.929, 0.3),
                new SimpleOrder(1182.989, 0.03),
                new SimpleOrder(1182.999, 0.81475769),
                new SimpleOrder(1183.005, 0.00103494),
                new SimpleOrder(1183.119, 0.074878),
                new SimpleOrder(1183.275, 0.00889407),
                new SimpleOrder(1183.312, 0.00847642),
                new SimpleOrder(1183.313, 0.5080671),
                new SimpleOrder(1183.377, 0.02542863),
                new SimpleOrder(1183.378, 0.02),
                new SimpleOrder(1183.401, 2.38396324),
                new SimpleOrder(1183.478, 0.011),
                new SimpleOrder(1183.5, 0.0104),
                new SimpleOrder(1183.509, 0.0169497),
                new SimpleOrder(1183.558, 0.0508491),
                new SimpleOrder(1183.559, 0.0104),
                new SimpleOrder(1183.651, 0.0104),
                new SimpleOrder(1183.756, 1.045481),
                new SimpleOrder(1183.817, 0.0104),
                new SimpleOrder(1183.89, 0.011),
                new SimpleOrder(1183.9, 3.37),
                new SimpleOrder(1183.91, 0.00847198),
                new SimpleOrder(1183.913, 0.01694392),
                new SimpleOrder(1184, 0.29862402),
                new SimpleOrder(1184.001, 0.03223504),
                new SimpleOrder(1184.013, 0.06854),
                new SimpleOrder(1184.017, 0.05929854),
                new SimpleOrder(1184.029, 0.11859706),
                new SimpleOrder(1184.03, 0.3),
                new SimpleOrder(1184.04, 0.01),
                new SimpleOrder(1184.162, 0.011),
                new SimpleOrder(1184.188, 0.09),
                new SimpleOrder(1184.232, 0.011),
                new SimpleOrder(1184.282, 0.011),
                new SimpleOrder(1184.414, 0.62858533),
                new SimpleOrder(1184.764, 1.01998),
                new SimpleOrder(1184.978, 10.56),
                new SimpleOrder(1184.979, 0.03),
                new SimpleOrder(1184.999, 1.75413296),
                new SimpleOrder(1185, 13.77628408),
                new SimpleOrder(1185.01, 0.40784598),
                new SimpleOrder(1185.021, 0.010001),
                new SimpleOrder(1185.085, 0.1269537),
                new SimpleOrder(1185.087, 0.00846357),
                new SimpleOrder(1185.239, 0.011),
                new SimpleOrder(1185.405, 0.00434619),
                new SimpleOrder(1185.574, 0.35547017),
                new SimpleOrder(1185.74, 0.01),
                new SimpleOrder(1185.821, 1.039),
                new SimpleOrder(1185.833, 0.011),
                new SimpleOrder(1185.88, 0.011),
                new SimpleOrder(1185.883, 0.05),
                new SimpleOrder(1185.9, 0.011),
                new SimpleOrder(1185.92, 0.00845762),
                new SimpleOrder(1185.922, 0.00845761),
                new SimpleOrder(1185.969, 1.06716086),
                new SimpleOrder(1185.978, 0.278451),
                new SimpleOrder(1185.992, 0.00845711),
                new SimpleOrder(1186, 0.45154603),
                new SimpleOrder(1186.037, 0.03164),
                new SimpleOrder(1186.054, 0.01691334),
                new SimpleOrder(1186.156, 0.07610346),
                new SimpleOrder(1186.232, 0.011),
                new SimpleOrder(1186.259, 0.0104),
                new SimpleOrder(1186.263, 0.0104),
                new SimpleOrder(1186.57, 4.68218448),
                new SimpleOrder(1186.579, 0.0026946),
                new SimpleOrder(1186.603, 8.024465),
                new SimpleOrder(1186.606, 0.02535819),
                new SimpleOrder(1186.608, 0.05071632),
                new SimpleOrder(1186.651, 0.2),
                new SimpleOrder(1186.786, 1.1),
                new SimpleOrder(1186.977, 0.01690018),
                new SimpleOrder(1187, 0.175),
                new SimpleOrder(1187.032, 0.0084497),
                new SimpleOrder(1187.08, 3.289),
                new SimpleOrder(1187.099, 0.02534766),
                new SimpleOrder(1187.115, 0.00844911),
                new SimpleOrder(1187.192, 0.1),
                new SimpleOrder(1187.196, 0.00844853),
                new SimpleOrder(1187.217, 0.356968),
                new SimpleOrder(1187.3, 0.01),
                new SimpleOrder(1187.377, 0.242875),
                new SimpleOrder(1187.417, 0.00879002),
                new SimpleOrder(1187.428, 0.00844688),
                new SimpleOrder(1187.43, 0.04223435),
                new SimpleOrder(1187.441, 0.0104),
                new SimpleOrder(1187.468, 0.05),
                new SimpleOrder(1187.534, 0.595573),
                new SimpleOrder(1187.571, 3.31962953)
        };

        SimpleOrder[] bids = new SimpleOrder[]{
                new SimpleOrder(1173.501, 2.033331),
                new SimpleOrder(1173.5, 0.001),
                new SimpleOrder(1173.254, 0.13903309),
                new SimpleOrder(1173.25, 0.001),
                new SimpleOrder(1173.11, 0.02665654),
                new SimpleOrder(1173.109, 1.2),
                new SimpleOrder(1173.1, 0.34861154),
                new SimpleOrder(1173.05, 0.06),
                new SimpleOrder(1172.8, 0.11),
                new SimpleOrder(1172.5, 0.001),
                new SimpleOrder(1172.405, 0.00852947),
                new SimpleOrder(1172.392, 0.00852957),
                new SimpleOrder(1172.203, 0.0047014),
                new SimpleOrder(1172.202, 0.00853095),
                new SimpleOrder(1172.201, 0.28560847),
                new SimpleOrder(1172.2, 0.01353096),
                new SimpleOrder(1172.075, 1.113),
                new SimpleOrder(1172.002, 0.0085324),
                new SimpleOrder(1172.001, 0.00470221),
                new SimpleOrder(1172, 26.00853242),
                new SimpleOrder(1171.803, 0.014109),
                new SimpleOrder(1171.759, 0.00853417),
                new SimpleOrder(1171.509, 0.04267995),
                new SimpleOrder(1171.113, 1.018),
                new SimpleOrder(1171.097, 0.059773),
                new SimpleOrder(1171.096, 5),
                new SimpleOrder(1170.603, 6.23782735),
                new SimpleOrder(1170.602, 5.01708522),
                new SimpleOrder(1170.601, 5.51255722),
                new SimpleOrder(1170.6, 20.02854262),
                new SimpleOrder(1170.514, 0.00854325),
                new SimpleOrder(1170.51, 0.8984107),
                new SimpleOrder(1170.504, 0.1),
                new SimpleOrder(1170.5, 0.55487948),
                new SimpleOrder(1170.177, 0.05127426),
                new SimpleOrder(1170.176, 0.938),
                new SimpleOrder(1170.124, 0.0085461),
                new SimpleOrder(1170.001, 0.017094),
                new SimpleOrder(1170, 0.76127856),
                new SimpleOrder(1169.662, 0.04603718),
                new SimpleOrder(1169.661, 0.01004),
                new SimpleOrder(1169.658, 0.0104),
                new SimpleOrder(1169.565, 0.0104),
                new SimpleOrder(1169.564, 0.0208),
                new SimpleOrder(1169.549, 1.072),
                new SimpleOrder(1169.537, 0.02565117),
                new SimpleOrder(1169.536, 0.0208),
                new SimpleOrder(1169.297, 0.00855214),
                new SimpleOrder(1169.187, 0.00855295),
                new SimpleOrder(1169.186, 0.04276475),
                new SimpleOrder(1169.185, 0.6040412),
                new SimpleOrder(1168.998, 0.1),
                new SimpleOrder(1168.255, 0.14551609),
                new SimpleOrder(1168.254, 0.964897),
                new SimpleOrder(1168.175, 0.01712072),
                new SimpleOrder(1168.174, 0.6),
                new SimpleOrder(1168.172, 0.51362321),
                new SimpleOrder(1167.641, 0.09420697),
                new SimpleOrder(1167.64, 0.0069643),
                new SimpleOrder(1167.515, 0.0171304),
                new SimpleOrder(1167.514, 0.00914199),
                new SimpleOrder(1167.269, 0.042835),
                new SimpleOrder(1167.268, 0.9214921),
                new SimpleOrder(1167.011, 0.04284445),
                new SimpleOrder(1167.01, 0.13346451),
                new SimpleOrder(1167, 0.04012228),
                new SimpleOrder(1166.815, 0.02571099),
                new SimpleOrder(1166.571, 0.04286065),
                new SimpleOrder(1166.57, 0.011),
                new SimpleOrder(1166.519, 0.00857251),
                new SimpleOrder(1166.253, 0.02572338),
                new SimpleOrder(1166.249, 0.02572347),
                new SimpleOrder(1166.248, 0.4301556),
                new SimpleOrder(1166.022, 0.00857616),
                new SimpleOrder(1166.021, 0.02572851),
                new SimpleOrder(1166.02, 1),
                new SimpleOrder(1166.003, 0.1),
                new SimpleOrder(1166.001, 1),
                new SimpleOrder(1166, 3.11047826),
                new SimpleOrder(1165.989, 0.01003),
                new SimpleOrder(1165.929, 0.00857685),
                new SimpleOrder(1165.928, 0.00121012),
                new SimpleOrder(1165.893, 0.00857711),
                new SimpleOrder(1165.892, 0.025259),
                new SimpleOrder(1165.794, 0.01715568),
                new SimpleOrder(1165.793, 0.0104),
                new SimpleOrder(1165.66, 0.01715766),
                new SimpleOrder(1165.581, 0.00857941),
                new SimpleOrder(1165.58, 0.00857941),
                new SimpleOrder(1165.579, 0.01235212),
                new SimpleOrder(1165.537, 0.00857973),
                new SimpleOrder(1165.467, 0.00858025),
                new SimpleOrder(1165.466, 0.03498902),
                new SimpleOrder(1165.457, 0.05),
                new SimpleOrder(1165.389, 0.01029),
                new SimpleOrder(1165.306, 0.01716286),
                new SimpleOrder(1165.304, 0.4371391),
                new SimpleOrder(1165.129, 0.0104),
                new SimpleOrder(1165.049, 0.49367571),
                new SimpleOrder(1165.011, 6),
                new SimpleOrder(1165.001, 0.00858368),
                new SimpleOrder(1165, 2.37766953),
                new SimpleOrder(1164.947, 0.00858408),
                new SimpleOrder(1164.946, 0.0104),
                new SimpleOrder(1164.751, 0.02575656),
                new SimpleOrder(1164.75, 0.082818),
                new SimpleOrder(1164.636, 0.02172039),
                new SimpleOrder(1164.344, 0.0104),
                new SimpleOrder(1164.332, 0.011728),
                new SimpleOrder(1164.271, 1.1),
                new SimpleOrder(1164.222, 0.0602577),
                new SimpleOrder(1164.184, 0.05),
                new SimpleOrder(1164, 0.02),
                new SimpleOrder(1163.989, 0.06),
                new SimpleOrder(1163.986, 0.101),
                new SimpleOrder(1163.818, 0.029391),
                new SimpleOrder(1163.782, 0.24099038),
                new SimpleOrder(1163.731, 0.01057),
                new SimpleOrder(1163.625, 0.00125915),
                new SimpleOrder(1163.442, 0.00892965),
                new SimpleOrder(1163.256, 0.7634227),
                new SimpleOrder(1163, 0.01),
                new SimpleOrder(1162.864, 0.03039),
                new SimpleOrder(1162.859, 0.41305744),
                new SimpleOrder(1162.648, 4.30052891),
                new SimpleOrder(1162.438, 6.23),
                new SimpleOrder(1162.437, 0.00102547),
                new SimpleOrder(1162.359, 0.00926563),
                new SimpleOrder(1162.346, 0.103205),
                new SimpleOrder(1162.345, 2.9859),
                new SimpleOrder(1162.312, 1.61325175),
                new SimpleOrder(1162.165, 0.010566),
                new SimpleOrder(1162, 0.01),
                new SimpleOrder(1161.899, 5.43705113),
                new SimpleOrder(1161.865, 0.41341081),
                new SimpleOrder(1161.822, 0.0104),
                new SimpleOrder(1161.649, 1.63540788),
                new SimpleOrder(1161.579, 0.0045605),
                new SimpleOrder(1161.475, 0.013687),
                new SimpleOrder(1161.472, 0.069348),
                new SimpleOrder(1161.403, 1.0200875),
                new SimpleOrder(1161.385, 2.54864619),
                new SimpleOrder(1161.321, 0.00130837),
                new SimpleOrder(1161.3, 0.03),
                new SimpleOrder(1161, 0.01),
                new SimpleOrder(1160.98, 3),
                new SimpleOrder(1160.667, 0.001),
                new SimpleOrder(1160.416, 0.570029),
                new SimpleOrder(1160.405, 1.72353618),
                new SimpleOrder(1160.4, 0.035)
        };

        return new Depth(asks, bids);
    }

}