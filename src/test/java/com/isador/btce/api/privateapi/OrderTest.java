package com.isador.btce.api.privateapi;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

import static org.junit.Assert.*;

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