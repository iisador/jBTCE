package com.isador.trade.jbtce;

import com.google.common.collect.ImmutableMap;
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
 * Abstract class for creating api.
 * Contains gson serializer to deserialize responses. Default headers, etc.
 *
 * @author isador
 * @since 2.0.1
 */
public abstract class AbstractApi {

    /**
     * Default headers that will be included to request
     */
    public static final Map<String, String> DEFAULT_HEADERS = ImmutableMap.of(
            "User-Agent", "jBTCEv2"
    );
    private static final Logger LOG = LoggerFactory.getLogger(DefaultConnector.class);
    protected final Gson gson;
    protected final JsonParser parser;
    protected Connector connector;
    protected Map<String, String> headers;
    private ServerProvider serverProvider;

    /**
     * Create new abstract api instance with default gson
     */
    public AbstractApi() {
        headers = new HashMap<>(DEFAULT_HEADERS);

        gson = new GsonBuilder().create();
        parser = new JsonParser();
    }

    /**
     * Create new abstract api instance and provide additional serializers\deserializers
     * to gson
     *
     * @param deserializersMap gson adapters
     */
    public AbstractApi(Map<? extends Type, ? extends JsonDeserializer> deserializersMap) {
        headers = new HashMap<>(DEFAULT_HEADERS);

        GsonBuilder builder = new GsonBuilder();
        if (deserializersMap != null && deserializersMap.size() > 0) {
            deserializersMap.entrySet().stream()
                    .filter(e -> Objects.nonNull(e.getValue()))
                    .forEach(e -> builder.registerTypeAdapter(e.getKey(), e.getValue()));
        }

        gson = builder.create();
        parser = new JsonParser();
    }

    /**
     * New abstract api instance with cusom gson adapters, server provider and connector
     *
     * @param serverProvider   server provider
     * @param connector        connector
     * @param deserializersMap gson adapters
     */
    public AbstractApi(ServerProvider serverProvider, Connector connector, Map<? extends Type, ? extends JsonDeserializer> deserializersMap) {
        this(deserializersMap);

        this.connector = connector;
        this.serverProvider = serverProvider;
    }

    /**
     * @return connector
     */
    public Connector getConnector() {
        return connector;
    }

    /**
     * Set new connector
     *
     * @param connector connector
     */
    public void setConnector(Connector connector) {
        this.connector = connector;
    }

    /**
     * @return server provider
     */
    public ServerProvider getServerProvider() {
        return serverProvider;
    }

    /**
     * Set new server provider
     *
     * @param server server provider
     */
    public void setServer(ServerProvider server) {
        this.serverProvider = server;
    }

    /**
     * Calls remote server and validates response.
     * Retrieves remote server from server provider. If call was failed - tries to execute call on the next server.
     *
     * @param connectorCall function to apply to connector
     * @return json parsed response
     * @throws BTCEException            response is null, empty or not valid json
     * @throws ServerProviderException  if no valid server found to execute request
     * @throws IllegalArgumentException if connector is null
     * @see ServerProvider
     * @see Connector
     */
    protected JsonElement processServerResponse(Function<Connector, String> connectorCall) throws BTCEException {
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

    /**
     * Validate response function
     *
     * @return parsed json response
     */
    private Function<String, JsonElement> validateResponse() {
        return response -> {
            if (response == null || response.isEmpty()) {
                throw new BTCEException("Invalid server response. Null or empty response");
            }

            try {
                return parser.parse(response);
            } catch (JsonSyntaxException e) {
                throw new BTCEException(String.format("Not a JSON Object: \"%s\"", response));
            }
        };
    }

    /**
     * Help method to crete valid url string, with valid server,
     * using server provider
     *
     * @param urlPath url suffix
     * @return {@code serverProvider.getCurrentServer() + urlPath}
     */
    protected String createUrl(String urlPath) {
        checkArgument(serverProvider != null, "Server provider must be not null");
        return serverProvider.getCurrentServer() + urlPath;
    }

    /**
     * Retrieve field from json object.
     *
     * @param obj   json object to retrieve from
     * @param field field name
     * @return field value as json element
     * @throws BTCEException if object doesn't contain that field
     */
    protected JsonElement get(JsonObject obj, String field) throws BTCEException {
        if (!obj.has(field)) {
            throw new BTCEException(String.format("Invalid server response. \"%s\" field missed.", field));
        }
        return obj.get(field);
    }
}
