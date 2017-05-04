package com.isador.trade.jbtce.publicapi;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

/**
 * Created by isador
 * on 27.04.17
 */
public class SimpleOrderTest {

    @Test
    public void testEqualsHashCode() {
        EqualsVerifier.forClass(Depth.SimpleOrder.class)
                .verify();
    }
}