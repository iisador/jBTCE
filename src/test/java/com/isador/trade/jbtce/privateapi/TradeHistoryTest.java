package com.isador.trade.jbtce.privateapi;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

/**
 * Created by isador
 * on 27.04.17
 */
public class TradeHistoryTest {

    @Test
    public void testEqualsHashCode() {
        EqualsVerifier.forClass(TradeHistory.class)
                .withOnlyTheseFields("id")
                .verify();
    }
}