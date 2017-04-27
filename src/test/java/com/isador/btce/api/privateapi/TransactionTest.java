package com.isador.btce.api.privateapi;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

/**
 * Created by isador
 * on 27.04.17
 */
public class TransactionTest {

    @Test
    public void testEqualsHashCode() {
        EqualsVerifier.forClass(Transaction.class)
                .withOnlyTheseFields("id")
                .verify();
    }
}