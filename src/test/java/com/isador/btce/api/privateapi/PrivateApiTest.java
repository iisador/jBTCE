package com.isador.btce.api.privateapi;

import com.isador.btce.api.BTCEException;
import com.isador.btce.api.Connector;
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
import java.util.List;

import static com.isador.btce.api.LocalDateTimeDeserializer.deserialize;
import static com.isador.btce.api.TestUtils.getErrorJson;
import static com.isador.btce.api.TestUtils.getJson;
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
        when(connector.signedPost(eq("getInfo"), any())).thenReturn(getJson("info.json"));

        UserInfo info = api.getUserInfo();
        assertNotNull("User info must be not null", info);
        assertEquals("Open orders count doesn't match", 0, info.getOpenOrdersCount());
        assertEquals("Transaction count doesn't match", 0, info.getTransactionCount());
        assertEquals("Server time doesn't match", deserialize(1491468795), info.getServerTime());

        assertFunds(info.getFunds());

        Rights rights = info.getRights();
        assertNotNull("Rights must be not null", rights);
        assertEquals("Rights.info doesn't match", 1, rights.getInfo());
        assertEquals("Rights.trade doesn't match", 1, rights.getTrade());
        assertEquals("Rights.withdraw doesn't match", 0, rights.getWithdraw());
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
        when(connector.signedPost(eq("Trade"), any())).thenReturn(getJson("tradeResult.json"));

        com.isador.btce.api.privateapi.TradeResult tResult = api.trade(Pair.BTC_USD, Operation.BUY, amount, 1);

        assertNotNull("Trade result must be not null", tResult);
        assertEquals("TradeResult.orderId doesn't match", 0, tResult.getOrderId());
        assertEquals("TradeResult.received doesn't match with amount requested", amount, tResult.getReceived(), 0.0000001);
        assertEquals("TradeResult.remains doesn't match", 0, tResult.getRemains(), 0.0000001);

        assertFunds(tResult.getFunds());
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
        when(connector.signedPost(eq("CancelOrder"), any())).thenReturn(getJson("cancelOrder.json"));

        CancelOrderResult result = api.cancelOrder(123);

        assertNotNull("Cancel order result must be not null", result);
        assertEquals("Order id doesn't match", 123, result.getOrderId());

        assertFunds(result.getFunds());
    }

    @Test
    public void testGetTransactionList() {
        when(connector.signedPost(eq("TransHistory"), any())).thenReturn(getJson("transactionHistory.json"));

        List<Transaction> transactions = api.getTransactionsList(null, null, null, null, null, null, null);

        assertNotNull("Order list must be not null", transactions);
        assertFalse("Order list must be non empty", transactions.isEmpty());

        System.out.println(transactions.get(0));
    }

    @Test
    public void testGetOrderList() {
        when(connector.signedPost(eq("OrderList"), any())).thenReturn(getJson("orders.json"));

        List<Order> orders = api.getOrderList(1L, 1, 1L, 1L, null, LocalDateTime.now(), LocalDateTime.MAX, Pair.BTC_USD, null);

        assertNotNull("Order list must be not null", orders);
        assertFalse("Order list must be non empty", orders.isEmpty());

        System.out.println(orders.get(0));
    }

    @Test
    public void testGetTradeHistoryList() {
        when(connector.signedPost(eq("TradeHistory"), any())).thenReturn(getJson("tradeHistory.json"));

        List<TradeHistory> trades = api.getTradesList(null, null, null, null, null, null, null, null);

        assertNotNull("Trade list must be not null", trades);
        assertFalse("Trade list must be non empty", trades.isEmpty());

        System.out.println(trades.get(0));
    }

    private void assertFunds(Funds funds) {
        assertNotNull("Funds must be not null", funds);
        assertEquals("Funds.btc doesn't match expected", 0, funds.getBtc(), 0.0000001);
        assertEquals("Funds.usd doesn't match expected", 16.5607577, funds.getUsd(), 0.0000001);
        assertEquals("Funds.rur doesn't match expected", 0.00006105, funds.getRur(), 0.0000001);
        assertEquals("Funds.eur doesn't match expected", 0, funds.getEur(), 0.0000001);
        assertEquals("Funds.ltc doesn't match expected", 0.00000999, funds.getLtc(), 0.0000001);
        assertEquals("Funds.nmc doesn't match expected", 0, funds.getNmc(), 0.0000001);
        assertEquals("Funds.nvc doesn't match expected", 0, funds.getNvc(), 0.0000001);
        assertEquals("Funds.ppc doesn't match expected", 0, funds.getPpc(), 0.0000001);
        assertEquals("Funds.dsh doesn't match expected", 0, funds.getDsh(), 0.0000001);
        assertEquals("Funds.eth doesn't match expected", 0, funds.getEth(), 0.0000001);
    }
}
