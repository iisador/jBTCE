package com.isador.btce.api.privateapi;

import com.isador.btce.api.BTCEException;
import com.isador.btce.api.Connector;
import com.isador.btce.api.constants.Currency;
import com.isador.btce.api.constants.Operation;
import com.isador.btce.api.constants.Pair;
import com.isador.btce.api.privateapi.UserInfo.Rights;
import com.isador.btce.api.publicapi.PublicApi;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.isador.btce.api.LocalDateTimeDeserializer.deserialize;
import static com.isador.btce.api.TestUtils.getErrorJson;
import static com.isador.btce.api.TestUtils.getJson;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Created by isador
 * on 06.04.17
 */
@RunWith(MockitoJUnitRunner.class)
public class PrivateApiTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    private Connector connector;

    private PrivateApi api;

    @Before
    public void setUp() throws Exception {
        api = new PrivateApi(connector);
    }

    @Test
    public void testCreateNullConnector() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("Connector instance should be not null");
        new PublicApi(null);
    }

    @Test
    public void testGetUserInfoError() {
        thrown.expect(BTCEException.class);
        thrown.expectMessage("Some error");
        when(connector.signedPost(eq("getInfo"), any())).thenReturn(getErrorJson());

        api.getUserInfo();
    }

    @Test
    public void testGetUserInfo() {
        UserInfo expected = new UserInfo(new Rights(1, 1, 0), getExpectedFunds(), 0, deserialize(1491468795), 0);
        when(connector.signedPost(eq("getInfo"), any())).thenReturn(getJson("info.json"));

        UserInfo actual = api.getUserInfo();

        assertNotNull("User info must be not null", actual);
        assertEquals("Open orders count doesn't match", expected.getOpenOrdersCount(), actual.getOpenOrdersCount());
        assertEquals("Transaction count doesn't match", expected.getTransactionCount(), actual.getTransactionCount());
        assertEquals("Server time doesn't match", expected.getServerTime(), actual.getServerTime());
        assertRightsEquals(expected.getRights(), actual.getRights());
        assertFunds(expected.getFunds(), actual.getFunds());
    }

    @Test
    public void testTradeNullPair() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("Invalid trade pair");

        api.trade(null, null, -1, -1);
    }

    @Test
    public void testTradeNullType() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("Invalid trade type");

        api.trade(Pair.BTC_USD, null, -1, -1);
    }

    @Test
    public void testTradeInvalidRate() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Invalid trade rate: -1");

        api.trade(Pair.BTC_USD, Operation.BUY, -1, -1);
    }

    @Test
    public void testTradeInvalidAmount() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Invalid trade amount: -1");

        api.trade(Pair.BTC_USD, Operation.BUY, 1, -1);
    }

    @Test
    public void testTradeError() {
        thrown.expect(BTCEException.class);
        thrown.expectMessage("Some error");
        when(connector.signedPost(eq("Trade"), any())).thenReturn(getErrorJson());

        api.trade(Pair.BTC_USD, Operation.BUY, 1, 1);
    }

    @Test
    public void testTrade() {
        double amount = 0.001;
        TradeResult expected = new TradeResult(amount, 0, 0, getExpectedFunds());
        when(connector.signedPost(eq("Trade"), any())).thenReturn(getJson("tradeResult.json"));

        TradeResult actual = api.trade(Pair.BTC_USD, Operation.BUY, amount, 1);

        assertNotNull("Trade result must be not null", actual);
        assertEquals("TradeResult.orderId doesn't match", expected.getOrderId(), actual.getOrderId());
        assertTrue("OrderId is not null - so trade scheduled", actual.isTradeComplete());
        assertEquals("TradeResult.received doesn't match with amount requested", expected.getReceived(), actual.getReceived(), 0.0000001);
        assertEquals("TradeResult.remains doesn't match", expected.getRemains(), actual.getRemains(), 0.0000001);

        assertFunds(expected.getFunds(), actual.getFunds());
    }

    @Test
    public void testCancelOrderInvalidId() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Invalid oderId: -1");
        api.cancelOrder(-1);
    }

    @Test
    public void testCancelOrderError() {
        thrown.expect(BTCEException.class);
        thrown.expectMessage("Some error");
        when(connector.signedPost(eq("CancelOrder"), any())).thenReturn(getErrorJson());

        api.cancelOrder(123);
    }

    @Test
    public void testCancelOrder() {
        CancelOrderResult expected = new CancelOrderResult(123, getExpectedFunds());
        when(connector.signedPost(eq("CancelOrder"), any())).thenReturn(getJson("cancelOrder.json"));

        CancelOrderResult actual = api.cancelOrder(123);

        assertNotNull("Cancel order result must be not null", actual);
        assertEquals("Actual cancel order result doesn't match", expected, actual);
        assertFunds(expected.getFunds(), actual.getFunds());
    }

    @Test
    public void testGetTransactionList() {
        when(connector.signedPost(eq("TransHistory"), any())).thenReturn(getJson("transactionHistory.json"));

        List<Transaction> transactions = api.getTransactionsList(null, null, null, null, null, null, null);

        assertNotNull("Order list must be not null", transactions);
        assertFalse("Order list must be non empty", transactions.isEmpty());
    }

    @Test
    public void testGetOrderList() {
        when(connector.signedPost(eq("OrderList"), any())).thenReturn(getJson("orders.json"));

        List<Order> orders = api.getOrderList(1L, 1, 1L, 1L, null, LocalDateTime.now(), LocalDateTime.MAX, Pair.BTC_USD, null);

        assertNotNull("Order list must be not null", orders);
        assertFalse("Order list must be non empty", orders.isEmpty());
    }

    @Test
    public void testGetTradeHistoryList() {
        when(connector.signedPost(eq("TradeHistory"), any())).thenReturn(getJson("tradeHistory.json"));

        List<TradeHistory> trades = api.getTradesList(null, null, null, null, null, null, null, null);

        assertNotNull("Trade list must be not null", trades);
        assertFalse("Trade list must be non empty", trades.isEmpty());
    }

    private void assertRightsEquals(Rights expected, Rights actual) {
        if (expected == null) {
            assertNull("Actual rights is not null", actual);
            return;
        }

        assertNotNull("Actual user rights must be not null", actual);
        assertEquals("Rights.info doesn't match", expected.getInfo(), actual.getInfo());
        assertEquals("Rights.trade doesn't match", expected.getTrade(), actual.getTrade());
        assertEquals("Rights.withdraw doesn't match", expected.getWithdraw(), actual.getWithdraw());
    }

    private void assertFunds(Funds expected, Funds actual) {
        if (expected == null) {
            assertNull("Actual funds must be null", actual);
            return;
        }

        assertNotNull("Funds must be not null", actual);
        assertEquals("Actual btc value doesn't match expected", expected.getBtc(), actual.getBtc(), 0.0000001);
        assertEquals("Actual usd value doesn't match expected", expected.getUsd(), actual.getUsd(), 0.0000001);
        assertEquals("Actual rur value doesn't match expected", expected.getRur(), actual.getRur(), 0.0000001);
        assertEquals("Actual eur value doesn't match expected", expected.getEur(), actual.getEur(), 0.0000001);
        assertEquals("Actual ltc value doesn't match expected", expected.getLtc(), actual.getLtc(), 0.0000001);
        assertEquals("Actual nmc value doesn't match expected", expected.getNmc(), actual.getNmc(), 0.0000001);
        assertEquals("Actual nvc value doesn't match expected", expected.getNvc(), actual.getNvc(), 0.0000001);
        assertEquals("Actual ppc value doesn't match expected", expected.getPpc(), actual.getPpc(), 0.0000001);
        assertEquals("Actual dsh value doesn't match expected", expected.getDsh(), actual.getDsh(), 0.0000001);
        assertEquals("Actual eth value doesn't match expected", expected.getEth(), actual.getEth(), 0.0000001);
    }

    private Funds getExpectedFunds() {
        Map<Currency, Double> map = new HashMap<>();
        map.put(Currency.USD, 16.5607577);
        map.put(Currency.RUR, 0.00006105);
        map.put(Currency.LTC, 0.00000999);
        return new Funds(map);
    }
}
