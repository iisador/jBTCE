package com.isador.trade.jbtce;

import com.google.gson.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Created by isador
 * on 18.05.17
 */
@RunWith(MockitoJUnitRunner.class)
public class AbstractApiTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    private Connector connector;

    private SimpleApi api;
    private Map<String, String> headers = Collections.singletonMap("User-Agent", "jBTCEv2");

    @Before
    public void setUp() throws Exception {
        api = new SimpleApi(Collections.singletonMap(Boolean.class, new CustomDeserializer()));
        api.setConnector(connector);
        api.setServer(new ServerProvider());
    }

    @Test
    public void testConstructorDefault() {
        SimpleApi api = new SimpleApi();

        assertNull("Server provider must be null", api.getServerProvider());
        assertNull("Connector must be null", api.getConnector());
        assertNotNull("Api headers must be not null", api.getHeaders());
        assertThat("Api headers invalid", api.getHeaders().values(), hasSize(1));
    }

    @Test
    public void testProcessResponseNull() {
        thrown.expect(BTCEException.class);
        thrown.expectMessage("Invalid server response. Null or empty response");
        when(connector.get("https://btc-e.com/", headers)).thenReturn(null);

        api.processServerResponse(connector -> connector.get("https://btc-e.com/", headers));
    }

    @Test
    public void testProcessResponseEmpty() {
        thrown.expect(BTCEException.class);
        thrown.expectMessage("Invalid server response. Null or empty response");
        when(connector.get("https://btc-e.com/", headers)).thenReturn("");

        api.processServerResponse(connector -> connector.get("https://btc-e.com/", headers));
    }

    @Test
    public void testProcessResponseInvalidJson() {
        thrown.expect(BTCEException.class);
        thrown.expectMessage("Not a JSON Object: \"bla bla bla\"");
        when(connector.get("https://btc-e.com/", headers)).thenReturn("bla bla bla");

        api.processServerResponse(connector -> connector.get("https://btc-e.com/", headers));
    }

    @Test
    public void testServerProvider() {
        when(connector.get("https://btc-e.com/", headers)).thenThrow(new ConnectorException(new IOException()));
        when(connector.get("https://btc-e.nz/", headers)).thenReturn("1");
        JsonPrimitive expected = new JsonPrimitive(1);

        JsonElement actual = api.processServerResponse(connector -> connector.get(api.createUrl(""), headers));

        assertEquals("Actual json doesn't match", expected, actual);
    }

    private static class SimpleApi extends AbstractApi {
        public SimpleApi() {
        }

        public SimpleApi(Map<Type, JsonDeserializer> deserializersMap) {
            super(deserializersMap);
        }

        public Map<String, String> getHeaders() {
            return headers;
        }
    }

    private static class CustomDeserializer implements JsonDeserializer<Boolean> {
        @Override
        public Boolean deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return json.getAsInt() == 1;
        }
    }
}