package com.isador.btce.api.publicapi;

import com.isador.btce.api.publicapi.Depth.SimpleOrder;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

/**
 * Created by isador
 * on 27.04.17
 */
public class SimpleOrderTest {

    @Test
    public void testEqualsHashCode() {
        EqualsVerifier.forClass(SimpleOrder.class)
                .verify();
    }
}