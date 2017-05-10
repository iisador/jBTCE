package com.isador.trade.jbtce.publicapi;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

/**
 * Created by isador
 * on 27.04.17
 */
public class BTCEInfoTest {

    @Test
    public void testEqualsHashCode() {
        EqualsVerifier.forClass(BTCEInfo.class)
                .withOnlyTheseFields("serverTime")
                .verify();
    }
}