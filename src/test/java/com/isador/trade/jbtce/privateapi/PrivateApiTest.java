package com.isador.trade.jbtce.privateapi;

import com.isador.trade.jbtce.BTCEException;
import com.isador.trade.jbtce.Connector;
import com.isador.trade.jbtce.DefaultConnector;
import com.isador.trade.jbtce.ServerProvider;
import com.isador.trade.jbtce.constants.Currency;
import com.isador.trade.jbtce.constants.Sort;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.isador.trade.jbtce.LocalDateTimeDeserializer.deserialize;
import static com.isador.trade.jbtce.TestUtils.getErrorJson;
import static com.isador.trade.jbtce.TestUtils.getJson;
import static com.isador.trade.jbtce.constants.Pair.BTC_USD;
import static com.isador.trade.jbtce.constants.TradeType.BUY;
import static com.isador.trade.jbtce.privateapi.OrderStatus.ACTIVE;
import static com.isador.trade.jbtce.privateapi.TransactionStatus.SUCCESSFUL;
import static com.isador.trade.jbtce.privateapi.TransactionType.CREDIT;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
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

    @Mock
    private ServerProvider serverProvider;

    private PrivateApi api;

    @Before
    public void setUp() throws Exception {
        when(serverProvider.getCurrentServer()).thenReturn("https://btc-e.com/");
        api = new PrivateApi("1", "1", serverProvider, connector);
    }

    @Test
    public void testCreateWithNullKey() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("Key must be specified");

        new PrivateApi(null, null, null, null);
    }

    @Test
    public void testCreateWithNullSecret() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("Secret must be specified");

        new PrivateApi("", null, null, null);
    }

    @Test
    public void testCreateWithEmptySecret() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Empty key");

        new PrivateApi("", "", serverProvider, connector);
    }

    @Test
    public void testCreate() {
        PrivateApi api = new PrivateApi("1", "1");

        assertNotNull("Default connector should be not null", api.getConnector());
        assertThat("Default connector should be DefaultConnector", api.getConnector(), instanceOf(DefaultConnector.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetUserInfoInvalidResponseNoSuccess() {
        thrown.expect(BTCEException.class);
        thrown.expectMessage("Invalid server response. \"success\" field missed.");
        when(connector.post(eq("https://btc-e.com/tapi"), anyString(), nullable(Map.class))).thenReturn("{}");

        api.getUserInfo();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetUserInfoInvalidResponseNoReturn() {
        thrown.expect(BTCEException.class);
        thrown.expectMessage("Invalid server response. \"return\" field missed.");
        when(connector.post(eq("https://btc-e.com/tapi"), anyString(), nullable(Map.class))).thenReturn("{\"success\": 1}");

        api.getUserInfo();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetUserInfoInvalidResponseNoError() {
        thrown.expect(BTCEException.class);
        thrown.expectMessage("Invalid server response. \"error\" field missed.");
        when(connector.post(eq("https://btc-e.com/tapi"), anyString(), nullable(Map.class))).thenReturn("{\"success\": 0}");

        api.getUserInfo();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetUserInfoError() {
        thrown.expect(BTCEException.class);
        thrown.expectMessage("Some error");
        when(connector.post(eq("https://btc-e.com/tapi"), anyString(), nullable(Map.class))).thenReturn(getErrorJson());

        api.getUserInfo();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetUserInfo() {
        UserInfo expected = new UserInfo(new UserInfo.Rights(1, 1, 0), getExpectedFunds(), 0, deserialize(1491468795), 0);
        when(connector.post(eq("https://btc-e.com/tapi"), anyString(), nullable(Map.class))).thenReturn(getJson("info.json"));

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

        api.trade(BTC_USD, null, -1, -1);
    }

    @Test
    public void testTradeInvalidRate() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Invalid trade rate: -1");

        api.trade(BTC_USD, BUY, -1, -1);
    }

    @Test
    public void testTradeInvalidAmount() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Invalid trade amount: -1");

        api.trade(BTC_USD, BUY, 1, -1);
    }

    @Test
    public void testTradeInvalidResponseNoSuccess() {
        thrown.expect(BTCEException.class);
        thrown.expectMessage("Invalid server response. \"success\" field missed.");
        when(connector.post(eq("https://btc-e.com/tapi"), anyString(), anyMap())).thenReturn("{}");

        api.trade(BTC_USD, BUY, 1, 1);
    }

    @Test
    public void testTradeInvalidResponseNoReturn() {
        thrown.expect(BTCEException.class);
        thrown.expectMessage("Invalid server response. \"return\" field missed.");
        when(connector.post(eq("https://btc-e.com/tapi"), anyString(), anyMap())).thenReturn("{\"success\": 1}");

        api.trade(BTC_USD, BUY, 1, 1);
    }

    @Test
    public void testTradeInvalidResponseNoError() {
        thrown.expect(BTCEException.class);
        thrown.expectMessage("Invalid server response. \"error\" field missed.");
        when(connector.post(eq("https://btc-e.com/tapi"), anyString(), anyMap())).thenReturn("{\"success\": 0}");

        api.trade(BTC_USD, BUY, 1, 1);
    }

    @Test
    public void testTradeError() {
        thrown.expect(BTCEException.class);
        thrown.expectMessage("Some error");
        when(connector.post(eq("https://btc-e.com/tapi"), anyString(), anyMap())).thenReturn(getErrorJson());

        api.trade(BTC_USD, BUY, 1, 1);
    }

    @Test
    public void testTrade() {
        double amount = 0.001;
        TradeResult expected = new TradeResult(amount, 0, 0, getExpectedFunds());
        when(connector.post(eq("https://btc-e.com/tapi"), anyString(), anyMap())).thenReturn(getJson("tradeResult.json"));

        TradeResult actual = api.trade(BTC_USD, BUY, amount, 1);

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
    public void testCancelOrderInvalidResponseNoSuccess() {
        thrown.expect(BTCEException.class);
        thrown.expectMessage("Invalid server response. \"success\" field missed.");
        when(connector.post(eq("https://btc-e.com/tapi"), anyString(), anyMap())).thenReturn("{}");

        api.cancelOrder(123);
    }

    @Test
    public void testCancelOrderInvalidResponseNoReturn() {
        thrown.expect(BTCEException.class);
        thrown.expectMessage("Invalid server response. \"return\" field missed.");
        when(connector.post(eq("https://btc-e.com/tapi"), anyString(), anyMap())).thenReturn("{\"success\": 1}");

        api.cancelOrder(123);
    }

    @Test
    public void testCancelOrderInvalidResponseNoError() {
        thrown.expect(BTCEException.class);
        thrown.expectMessage("Invalid server response. \"error\" field missed.");
        when(connector.post(eq("https://btc-e.com/tapi"), anyString(), anyMap())).thenReturn("{\"success\": 0}");

        api.cancelOrder(123);
    }

    @Test
    public void testCancelOrderError() {
        thrown.expect(BTCEException.class);
        thrown.expectMessage("Some error");
        when(connector.post(eq("https://btc-e.com/tapi"), anyString(), anyMap())).thenReturn(getErrorJson());

        api.cancelOrder(123);
    }

    @Test
    public void testCancelOrder() {
        CancelOrderResult expected = new CancelOrderResult(123, getExpectedFunds());
        when(connector.post(eq("https://btc-e.com/tapi"), anyString(), anyMap())).thenReturn(getJson("cancelOrder.json"));

        CancelOrderResult actual = api.cancelOrder(123);

        assertNotNull("Cancel order result must be not null", actual);
        assertEquals("Actual cancel order result doesn't match", expected, actual);
        assertEquals("Actual cancel order id doesn't match", expected.getId(), actual.getId());
        assertFunds(expected.getFunds(), actual.getFunds());
    }

    @Test
    public void testGetTransactionListInvalidResponseNoSuccess() {
        thrown.expect(BTCEException.class);
        thrown.expectMessage("Invalid server response. \"success\" field missed.");
        when(connector.post(eq("https://btc-e.com/tapi"), anyString(), anyMap())).thenReturn("{}");

        api.getTransactionsList(null, null, null, null, null, null, null);
    }

    @Test
    public void testGetTransactionListInvalidResponseNoReturn() {
        thrown.expect(BTCEException.class);
        thrown.expectMessage("Invalid server response. \"return\" field missed.");
        when(connector.post(eq("https://btc-e.com/tapi"), anyString(), anyMap())).thenReturn("{\"success\": 1}");

        api.getTransactionsList(null, null, null, null, null, null, null);
    }

    @Test
    public void testGetTransactionListInvalidResponseNoError() {
        thrown.expect(BTCEException.class);
        thrown.expectMessage("Invalid server response. \"error\" field missed.");
        when(connector.post(eq("https://btc-e.com/tapi"), anyString(), anyMap())).thenReturn("{\"success\": 0}");

        api.getTransactionsList(null, null, null, null, null, null, null);
    }

    @Test
    public void testGetTransactionListEmptyResponse() {
        when(connector.post(eq("https://btc-e.com/tapi"), anyString(), anyMap())).thenReturn("{\"success\": 1, \"return\": {}}");

        List<Transaction> list = api.getTransactionsList(null, null, null, null, null, null, null);

        assertTrue("Transactions list must be empty", list.isEmpty());
    }

    @Test
    public void testGetTransactionList() {
        Transaction expectedTransaction = new Transaction(CREDIT, 1.09920000, Currency.USD, SUCCESSFUL, deserialize(1491904521), 3574749223L, "Cancel order :order:1703917256:");
        when(connector.post(eq("https://btc-e.com/tapi"),
                anyString(),
//                eq("from_id=0&method=TradeHistory&count=5&end_id=5&from=0&end=1491555286&nonce=1495180901&order=ASC&since=1491555286"),
                anyMap())).thenReturn(getJson("transactionHistory.json"));

        List<Transaction> transactions = api.getTransactionsList(0L, 5, 0L, 5L, Sort.ASC, deserialize(1491555286), deserialize(1491555286));

        assertNotNull("Transactions list must be not null", transactions);
        assertFalse("Transactions list must be non empty", transactions.isEmpty());
        transactions
                .forEach(transaction -> assertNotNull("Transactions list should not contain null elements", transaction));

        Transaction actualTransaction = transactions.get(0);

        assertEquals("Actual transaction doesn't match", expectedTransaction, actualTransaction);
        assertEquals("Actual transaction id doesn't match", expectedTransaction.getId(), actualTransaction.getId());
        assertEquals("Actual transaction amount doesn't match", expectedTransaction.getAmount(), actualTransaction.getAmount(), 0.000001);
        assertEquals("Actual transaction currency doesn't match", expectedTransaction.getCurrency(), actualTransaction.getCurrency());
        assertEquals("Actual transaction description doesn't match", expectedTransaction.getDescription(), actualTransaction.getDescription());
        assertEquals("Actual transaction status doesn't match", expectedTransaction.getStatus(), actualTransaction.getStatus());
        assertEquals("Actual transaction timestamp doesn't match", expectedTransaction.getTimestamp(), actualTransaction.getTimestamp());
        assertEquals("Actual transaction type doesn't match", expectedTransaction.getType(), actualTransaction.getType());
    }

    @Test
    public void testGetOrderListInvalidResponseNoSuccess() {
        thrown.expect(BTCEException.class);
        thrown.expectMessage("Invalid server response. \"success\" field missed.");
        when(connector.post(eq("https://btc-e.com/tapi"), anyString(), anyMap())).thenReturn("{}");

        api.getOrderList(null, null, null, null, null, null, null, null, null);
    }

    @Test
    public void testGetOrderListInvalidResponseNoReturn() {
        thrown.expect(BTCEException.class);
        thrown.expectMessage("Invalid server response. \"return\" field missed.");
        when(connector.post(eq("https://btc-e.com/tapi"), anyString(), anyMap())).thenReturn("{\"success\": 1}");

        api.getOrderList(null, null, null, null, null, null, null, null, null);
    }

    @Test
    public void testGetOrderListInvalidResponseNoError() {
        thrown.expect(BTCEException.class);
        thrown.expectMessage("Invalid server response. \"error\" field missed.");
        when(connector.post(eq("https://btc-e.com/tapi"), anyString(), anyMap())).thenReturn("{\"success\": 0}");

        api.getOrderList(null, null, null, null, null, null, null, null, null);
    }

    @Test
    public void testGetOrderListEmptyResponse() {
        when(connector.post(eq("https://btc-e.com/tapi"), anyString(), anyMap())).thenReturn("{\"success\": 1, \"return\": {}}");

        List<Order> list = api.getOrderList(null, null, null, null, null, null, null, null, null);

        assertTrue("Order list must be empty", list.isEmpty());
    }

    @Test
    public void testGetOrderList() {
        Order expectedOrder = new Order(1696817430L, BTC_USD, BUY, 0.00100000, 1158.82900000, ACTIVE, deserialize(1491563567));
        when(connector.post(eq("https://btc-e.com/tapi"),
                anyString(),
//                eq("from_id=0&method=TradeHistory&count=5&end_id=5&from=0&end=1491555286&nonce=1495180901&pair=btc_usd&order=ASC&since=1491555286&active=1"),
                anyMap())).thenReturn(getJson("orders.json"));

        List<Order> orders = api.getOrderList(0L, 5, 0L, 5L, Sort.ASC, deserialize(1491555286), deserialize(1491555286), BTC_USD, true);

        assertNotNull("Order list must be not null", orders);
        assertFalse("Order list must be non empty", orders.isEmpty());
        orders.forEach(order -> assertNotNull("Order list should not contain null elements", order));

        Order actualOrder = orders.get(0);
        assertEquals("Actual order doesn't match", expectedOrder, actualOrder);
        assertEquals("Actual order id doesn't match", expectedOrder.getId(), actualOrder.getId());
        assertEquals("Actual order amount doesn't match", expectedOrder.getAmount(), actualOrder.getAmount(), 0.000001);
        assertEquals("Actual order status doesn't match", expectedOrder.getStatus(), actualOrder.getStatus());
        assertEquals("Actual order type doesn't match", expectedOrder.getType(), actualOrder.getType());
        assertEquals("Actual order pair doesn't match", expectedOrder.getPair(), actualOrder.getPair());
        assertEquals("Actual order rate doesn't match", expectedOrder.getRate(), actualOrder.getRate(), 0.000001);
        assertEquals("Actual order timestamp doesn't match", expectedOrder.getTimestampCreated(), actualOrder.getTimestampCreated());
    }

    @Test
    public void testGetTradeHistoryListInvalidResponseNoSuccess() {
        thrown.expect(BTCEException.class);
        thrown.expectMessage("Invalid server response. \"success\" field missed.");
        when(connector.post(eq("https://btc-e.com/tapi"), anyString(), anyMap())).thenReturn("{}");

        api.getTradesList(null, null, null, null, null, null, null, null);
    }

    @Test
    public void testGetTradeHistoryListInvalidResponseNoReturn() {
        thrown.expect(BTCEException.class);
        thrown.expectMessage("Invalid server response. \"return\" field missed.");
        when(connector.post(eq("https://btc-e.com/tapi"), anyString(), anyMap())).thenReturn("{\"success\": 1}");

        api.getTradesList(null, null, null, null, null, null, null, null);
    }

    @Test
    public void testGetTradeHistoryListInvalidResponseNoError() {
        thrown.expect(BTCEException.class);
        thrown.expectMessage("Invalid server response. \"error\" field missed.");
        when(connector.post(eq("https://btc-e.com/tapi"), anyString(), anyMap())).thenReturn("{\"success\": 0}");

        api.getTradesList(null, null, null, null, null, null, null, null);
    }

    @Test
    public void testGetTradeHistoryListEmptyResponse() {
        when(connector.post(eq("https://btc-e.com/tapi"), anyString(), anyMap())).thenReturn("{\"success\": 1, \"return\": {}}");

        List<TradeHistory> list = api.getTradesList(null, null, null, null, null, null, null, null);

        assertTrue("Trade history list must be empty", list.isEmpty());
    }

    @Test
    public void testGetTradeHistoryList() {
        TradeHistory expectedTh = new TradeHistory(BTC_USD, BUY,
                0.00100000, 1180.00000000, 1696641686,
                0, deserialize(1491555286), 97951082L);
        when(connector.post(eq("https://btc-e.com/tapi"),
                anyString(),
//                eq("from_id=0&method=TradeHistory&count=5&end_id=5&from=0&end=1491555286&nonce=1495180901&pair=btc_usd&order=ASC&since=1491555286"),
                anyMap()))
                .thenReturn(getJson("tradeHistory.json"));

        List<TradeHistory> trades = api.getTradesList(0L, 5, 0L, 5L, Sort.ASC, deserialize(1491555286), deserialize(1491555286), BTC_USD);

        assertNotNull("Trade history list must be not null", trades);
        assertFalse("Trade history list must be non empty", trades.isEmpty());
        trades.forEach(trade -> assertNotNull("Trade history list should not contain null elements", trade));

        TradeHistory actualTh = trades.get(0);
        assertEquals("Actual trade history doesn't match", expectedTh, actualTh);
        assertEquals("Actual trade history rate doesn't match", expectedTh.getRate(), actualTh.getRate(), 0.0000001);
        assertEquals("Actual trade history pair doesn't match", expectedTh.getPair(), actualTh.getPair());
        assertEquals("Actual trade history type doesn't match", expectedTh.getType(), actualTh.getType());
        assertEquals("Actual trade history amount doesn't match", expectedTh.getAmount(), actualTh.getAmount(), 0.0000001);
        assertEquals("Actual trade history orderId doesn't match", expectedTh.getOrderId(), actualTh.getOrderId());
        assertEquals("Actual trade history timestamp doesn't match", expectedTh.getTimestamp(), actualTh.getTimestamp());
        assertEquals("Actual trade history tradeId doesn't match", expectedTh.getId(), actualTh.getId());
        assertEquals("Actual trade history yourOrder doesn't match", expectedTh.isYourOrder(), actualTh.isYourOrder());

    }

    private void assertRightsEquals(UserInfo.Rights expected, UserInfo.Rights actual) {
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
