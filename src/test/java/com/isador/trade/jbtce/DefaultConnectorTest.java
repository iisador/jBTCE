package com.isador.trade.jbtce;

import com.google.common.collect.ImmutableMap;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockserver.integration.ClientAndServer;

import static org.junit.Assert.assertEquals;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

/**
 * Created by isador
 * Only public API tests
 * on 07.04.17
 */
public class DefaultConnectorTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private ClientAndServer server;

    private DefaultConnector connector = new DefaultConnector();

    @Before
    public void setUp() throws Exception {
        server = startClientAndServer(7071);
    }

    @After
    public void tearDown() throws Exception {
        server.stop();
    }

    @Test
    public void testGet() {
        server.when(request()
                            .withMethod("GET"))
                .respond(response()
                                 .withStatusCode(200)
                                 .withBody("ok"));

        String response = connector.get("http://localhost:7071");
        assertEquals("Invalid request was created", response, "ok");
    }

    @Test
    public void testPost() {
        server.when(request()
                            .withMethod("POST"))
                .respond(response()
                                 .withStatusCode(200)
                                 .withBody("ok"));

        String response = connector.post("http://localhost:7071", null, null);
        assertEquals("Invalid request was created", response, "ok");
    }

    @Test
    public void testPostWithHeaders() {
        server.when(request()
                            .withMethod("POST")
                            .withHeader("key1", "val1")
                            .withHeader("key2", "val2"))
                .respond(response()
                                 .withStatusCode(200)
                                 .withBody("ok"));

        String response = connector.post("http://localhost:7071", null, ImmutableMap.of("key1", "val1", "key2", "val2"));
        assertEquals("Invalid request was created", response, "ok");
    }

    @Test
    public void testPostWithBody() {
        server.when(request()
                            .withMethod("POST")
                            .withBody("someBody"))
                .respond(response()
                                 .withStatusCode(200)
                                 .withBody("ok"));

        String response = connector.post("http://localhost:7071", "someBody", null);
        assertEquals("Invalid request was created", response, "ok");
    }

    @Test(expected = BTCEException.class)
    public void testGetInvalidUrl() {
        connector.get("invalid bla bla bla");
    }

    @Test(expected = BTCEException.class)
    public void testPostInvalidUrl() {
        connector.post("invalid bla bla bla", null, null);
    }
}
