package com.isador.btce.api;

import com.isador.btce.api.constants.Pair;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

/**
 * Created by isador
 * Only public API tests
 * on 07.04.17
 */
public class JavaConnectorTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private Connector connector;

    @Before
    public void setUp() throws Exception {
        connector = new JavaConnector();
    }

    @Test
    public void testInitWithNullKey() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("Key must be specified");

        Connector connector = new JavaConnector();
        connector.init(null, null);
    }

    @Test
    public void testInitWithNullSecret() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("Secret must be specified");

        Connector connector = new JavaConnector();
        connector.init("", null);
    }

    @Test
    public void testGetTick() {
        String response = connector.getTick(Pair.BTC_USD);

        assertThat(response, hasJsonPath("ticker", notNullValue()));
        assertThat(response, hasJsonPath("ticker.high", notNullValue()));
        assertThat(response, hasJsonPath("ticker.low", notNullValue()));
        assertThat(response, hasJsonPath("ticker.avg", notNullValue()));
        assertThat(response, hasJsonPath("ticker.vol", notNullValue()));
        assertThat(response, hasJsonPath("ticker.vol_cur", notNullValue()));
        assertThat(response, hasJsonPath("ticker.last", notNullValue()));
        assertThat(response, hasJsonPath("ticker.buy", notNullValue()));
        assertThat(response, hasJsonPath("ticker.sell", notNullValue()));
        assertThat(response, hasJsonPath("ticker.updated", notNullValue()));
        assertThat(response, hasJsonPath("ticker.server_time", notNullValue()));
    }

    @Test
    public void testGetFee() {
        String response = connector.getFee(Pair.BTC_USD);

        assertThat(response, hasJsonPath("trade", notNullValue()));
    }

    @Test
    public void testGetDepth() {
        String response = connector.getDepth(Pair.BTC_USD);

        assertThat(response, hasJsonPath("asks", notNullValue()));
        assertThat(response, hasJsonPath("$.asks.length()", equalTo(150)));
        assertThat(response, hasJsonPath("$.asks[*].length()", everyItem(equalTo(2))));
        assertThat(response, hasJsonPath("bids", notNullValue()));
        assertThat(response, hasJsonPath("$.bids.length()", equalTo(150)));
        assertThat(response, hasJsonPath("$.bids[*].length()", everyItem(equalTo(2))));
    }

    @Test
    public void testGetTrade() {
        String response = connector.getTrades(Pair.BTC_USD);

        assertThat(response, hasJsonPath("$", notNullValue()));
        assertThat(response, hasJsonPath("$.length()", equalTo(150)));
        assertThat(response, hasJsonPath("$[*].date", everyItem(notNullValue())));
        assertThat(response, hasJsonPath("$[*].price", everyItem(notNullValue())));
        assertThat(response, hasJsonPath("$[*].amount", everyItem(notNullValue())));
        assertThat(response, hasJsonPath("$[*].tid", everyItem(notNullValue())));
        assertThat(response, hasJsonPath("$[*].price_currency", everyItem(notNullValue())));
        assertThat(response, hasJsonPath("$[*].item", everyItem(notNullValue())));
        assertThat(response, hasJsonPath("$[*].trade_type", everyItem(notNullValue())));
    }
}
