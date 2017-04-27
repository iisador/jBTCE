package com.isador.btce.api.publicapi;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

/**
 * Created by isador
 * on 27.04.17
 */
public class TradeTest {

    @Test
    public void testEqualsHashCode() {
        EqualsVerifier.forClass(Trade.class)
                .withOnlyTheseFields("id")
                .verify();
    }
}