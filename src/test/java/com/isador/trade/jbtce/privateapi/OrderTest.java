package com.isador.trade.jbtce.privateapi;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

/**
 * Created by isador
 * on 27.04.17
 */
public class OrderTest {

    @Test
    public void testEqualsHashCode() {
        EqualsVerifier.forClass(Order.class)
                .withOnlyTheseFields("id")
                .verify();
    }
}