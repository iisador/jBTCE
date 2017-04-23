package com.isador.btce.api;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertNotNull;

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

        connector.init(null, null);
    }

    @Test
    public void testInitWithNullSecret() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("Secret must be specified");

        connector.init("", null);
    }

    @Test
    public void testCall() {
        String response = connector.call("https://httpbin.org/user-agent");
        assertNotNull("Some response must be", response);
    }

    @Test
    public void testSignedCallNotInitialized() {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Connector is not initialized");

        connector.signedPost(null, null);
    }
}
