package com.isador.trade.jbtce.privateapi;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

/**
 * Created by isador
 * on 26.04.17
 */
public class CancelOrderResultTest {

    @Test
    public void testEqualsHashCode() {
        EqualsVerifier.forClass(CancelOrderResult.class)
                .withOnlyTheseFields("id")
                .verify();
    }
}