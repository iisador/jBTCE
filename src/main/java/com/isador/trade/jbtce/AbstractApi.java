package com.isador.trade.jbtce;

import com.google.gson.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created by isador
 * on 06.04.2017.
 */
public abstract class AbstractApi {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultConnector.class);
    protected final Gson gson;
    protected final JsonParser parser;
    protected Connector connector;
    protected Map<String, String> headers;
    private ServerProvider serverProvider;

    public AbstractApi() {
        headers = new HashMap<>();
        headers.put("User-Agent", "jBTCEv2");

        gson = new GsonBuilder().create();
        parser = new JsonParser();
    }

    public AbstractApi(Map<Type, JsonDeserializer> deserializersMap) {
        headers = new HashMap<>();
        headers.put("User-Agent", "jBTCEv2");

        GsonBuilder builder = new GsonBuilder();
        if (deserializersMap != null && deserializersMap.size() > 0) {
            deserializersMap.entrySet().stream()
                    .filter(e -> Objects.nonNull(e.getValue()))
                    .forEach(e -> builder.registerTypeAdapter(e.getKey(), e.getValue()));
        }

        gson = builder.create();
        parser = new JsonParser();
    }

    public AbstractApi(ServerProvider serverProvider, Connector connector, Map<Type, JsonDeserializer> deserializersMap) {
        this(deserializersMap);

        this.connector = connector;
        this.serverProvider = serverProvider;
    }

    public Connector getConnector() {
        return connector;
    }

    public void setConnector(Connector connector) {
        this.connector = connector;
    }

    public ServerProvider getServerProvider() {
        return serverProvider;
    }

    public void setServer(ServerProvider server) {
        this.serverProvider = server;
    }

    protected JsonElement processServerResponse(Function<Connector, String> connectorCall) throws ServerProviderException {
        checkArgument(connector != null, "Connector must be not null");
        while (true) { // todo: not sure
            try {
                return connectorCall
                        .andThen(validateResponse())
                        .apply(connector);
            } catch (ConnectorException e) {
                LOG.warn("Error processing request", e);
                serverProvider.nextMirror();
            }
        }
    }

    private Function<String, JsonElement> validateResponse() {
        return response -> {
            if (response == null || response.isEmpty()) {
                throw new BTCEException("Invalid server response. Null or empty response");
            }

            try {
                return parser.parse(response);
            } catch (JsonSyntaxException e) {
                throw new IllegalStateException(String.format("Not a JSON Object: \"%s\"", response));
            }
        };
    }

    protected String createUrl(String urlPath) {
        checkArgument(serverProvider != null, "Server provider must be not null");
        return serverProvider.getCurrentServer() + urlPath;
    }

    protected JsonElement get(JsonObject obj, String field) throws BTCEException {
        if (!obj.has(field)) {
            throw new BTCEException(String.format("Invalid server response. \"%s\" field missed.", field));
        }
        return obj.get(field);
    }
}
