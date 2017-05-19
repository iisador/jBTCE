package com.isador.trade.jbtce;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockserver.integration.ClientAndServer;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

/**
 * Created by isador
 * on 15.05.17
 */
public class ServerProviderTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testCreateDefaultServerNull() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("Current server must be not null");

        new ServerProvider(null, null);
    }

    @Test
    public void testCreateMirrorsNull() {
        ServerProvider server = new ServerProvider("someServer", null);

        assertArrayEquals("Invalid server mirror list", new String[0], server.getMirrors());
    }

    @Test
    public void testCreateMirrorsEmpty() {
        ServerProvider server = new ServerProvider("someServer");

        assertArrayEquals("Invalid server mirror list", new String[0], server.getMirrors());
    }


    @Test
    public void testCreateMirrorsNullElements() {
        ServerProvider actual = new ServerProvider("someServer", "1", "2", null);

        assertServerEquals("someServer", new String[]{"1", "2"}, actual);
    }

    @Test
    public void testCreateMirrorsDuplicates() {
        ServerProvider actual = new ServerProvider("someServer", "1", "2", "2");

        assertServerEquals("someServer", new String[]{"1", "2"}, actual);
    }

    @Test
    public void testDefaultConstructor() {
        ServerProvider actual = new ServerProvider();

        assertServerEquals("https://btc-e.com/", new String[]{"https://btc-e.nz/"}, actual);
    }

    @Test
    public void testServerListConstructorNoValidMirrors() {
        thrown.expect(ServerProviderException.class);
        thrown.expectMessage("No valid server found");

        new ServerProvider(new String[]{"http://localhost:7072/", "http://localhost:7073/"});
    }

    @Test
    public void testServerListConstructor() {
        ClientAndServer remoteServer = startClientAndServer(7071);
        remoteServer.when(request()
                .withMethod("GET"))
                .respond(response()
                        .withStatusCode(200)
                        .withBody("{\"btc_usd\":0.2}"));

        try {
            ServerProvider actual = new ServerProvider(new String[]{"http://localhost:7071/", "http://localhost:7072/"});

            assertServerEquals("http://localhost:7071/", new String[]{"http://localhost:7072/"}, actual);
        } finally {
            remoteServer.stop();
        }
    }

    @Test
    public void testNextMirror() {
        ClientAndServer remoteServer1 = startClientAndServer(7071);
        ClientAndServer remoteServer2 = startClientAndServer(7072);
        remoteServer1.when(request()
                .withMethod("GET"))
                .respond(response()
                        .withStatusCode(200)
                        .withBody("{\"btc_usd\":2}"));
        remoteServer2.when(request()
                .withMethod("GET"))
                .respond(response()
                        .withStatusCode(200)
                        .withBody("{\"btc_usd\":0.2}"));

        try{
            ServerProvider actual = new ServerProvider(new String[]{"http://localhost:7071/", "http://localhost:7072/"});
            assertServerEquals("http://localhost:7071/", new String[]{"http://localhost:7072/"}, actual);

            remoteServer1.stop();
            actual.nextMirror();

            assertServerEquals("http://localhost:7072/", new String[]{"http://localhost:7071/"}, actual);
        }finally {
            remoteServer1.stop();
            remoteServer2.stop();
        }
    }

    private void assertServerEquals(String expectedCurrentServer, String[] expectedMirrors, ServerProvider actual) {
        assertEquals("Invalid current server", expectedCurrentServer, actual.getCurrentServer());
        assertArrayEquals("Invalid server mirror list", expectedMirrors, actual.getMirrors());
    }
}