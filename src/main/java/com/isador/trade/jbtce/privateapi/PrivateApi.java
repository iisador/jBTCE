package com.isador.trade.jbtce.privateapi;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.isador.trade.jbtce.*;
import com.isador.trade.jbtce.constants.Pair;
import com.isador.trade.jbtce.constants.Sort;
import com.isador.trade.jbtce.constants.TradeType;
import com.isador.trade.jbtce.TradeTypeDeserializer;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;

public class PrivateApi extends AbstractApi {

    private static final String PRIVATE_API_URL = "tapi";
    private static final AtomicLong nonce = new AtomicLong(System.currentTimeMillis() / 1000);
    private final Mac mac;

    public PrivateApi(String key, String secret) {
        this(key, secret, new ServerProvider(), new DefaultConnector());
    }

    public PrivateApi(String key, String secret, ServerProvider serverProvider, Connector connector) {
        super(serverProvider, connector, ImmutableMap.of(LocalDateTime.class, new LocalDateTimeDeserializer(),
                Funds.class, new FundsDeserializer(),
                TradeType.class, new TradeTypeDeserializer()));
        requireNonNull(key, "Key must be specified");
        requireNonNull(secret, "Secret must be specified");

        // Init mac
        try {
            String alg = "HmacSHA512";
            mac = Mac.getInstance(alg);
            mac.init(new SecretKeySpec(secret.getBytes(Charset.forName("UTF-8")), alg));
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }

        // Init headers
        headers.put("Key", key);
    }

    public UserInfo getUserInfo() throws BTCEException {
        JsonElement response = call("getInfo", null);
        return gson.fromJson(response, UserInfo.class);
    }

    public TradeResult trade(Pair pair, TradeType type, double rate, double amount) throws BTCEException {
        requireNonNull(pair, "Invalid trade pair");
        requireNonNull(type, "Invalid trade type");
        checkArgument(rate > 0, "Invalid trade rate: %s", rate);
        checkArgument(amount > 0, "Invalid trade amount: %s", amount);

        Map<String, Object> map = ImmutableMap.of("pair", pair.getName(),
                "type", type.name().toLowerCase(),
                "rate", rate,
                "amount", amount);

        JsonElement response = call("Trade", map);
        return gson.fromJson(response, TradeResult.class);
    }

    public List<Order> getOrderList(Long fromNum, Integer count, Long fromId,
                                    Long endId, Sort sort, LocalDateTime since,
                                    LocalDateTime end, Pair pair, Boolean active) throws BTCEException {

        Map<String, Object> map = new ParametersBuilder()
                .from(fromNum)
                .count(count)
                .fromId(fromId)
                .endId(endId)
                .order(sort)
                .since(since)
                .end(end)
                .pair(pair)
                .active(active)
                .build();

        JsonObject response = (JsonObject) call("OrderList", map);

        // stupid orders return format
        return response.entrySet().stream()
                .peek(e -> e.getValue().getAsJsonObject().addProperty("id", e.getKey()))
                .map(e -> gson.fromJson(e.getValue(), Order.class))
                .collect(Collectors.toList());
    }

    public List<Transaction> getTransactionsList(Long fromNum, Integer count, Long fromId,
                                                 Long endId, Sort sort, LocalDateTime since,
                                                 LocalDateTime end) throws BTCEException {
        Map<String, Object> map = new ParametersBuilder()
                .from(fromNum)
                .count(count)
                .fromId(fromId)
                .endId(endId)
                .order(sort)
                .since(since)
                .end(end)
                .build();

        JsonObject response = (JsonObject) call("TransHistory", map);
        return response.entrySet().stream()
                .peek(e -> e.getValue().getAsJsonObject().addProperty("id", e.getKey()))
                .map(e -> gson.fromJson(e.getValue(), Transaction.class))
                .collect(Collectors.toList());
    }

    public List<TradeHistory> getTradesList(Long fromNum, Integer count, Long fromId,
                                            Long endId, Sort sort, LocalDateTime since,
                                            LocalDateTime end, Pair pair) throws BTCEException {
        Map<String, Object> map = new ParametersBuilder()
                .from(fromNum)
                .count(count)
                .fromId(fromId)
                .endId(endId)
                .order(sort)
                .since(since)
                .end(end)
                .pair(pair)
                .build();

        JsonObject response = (JsonObject) call("TradeHistory", map);
        return response.entrySet().stream()
                .peek(e -> e.getValue().getAsJsonObject().addProperty("id", e.getKey()))
                .map(e -> gson.fromJson(e.getValue(), TradeHistory.class))
                .collect(Collectors.toList());
    }

    public CancelOrderResult cancelOrder(long orderId) throws BTCEException {
        checkArgument(orderId > 0, "Invalid oderId: %s", orderId);

        Map<String, Object> map = ImmutableMap.of("order_id", orderId);
        JsonElement response = call("CancelOrder", map);
        return gson.fromJson(response, CancelOrderResult.class);
    }

    private JsonElement call(String method, Map<String, Object> additionalParameters) throws BTCEException {
        String body = getBody(method, additionalParameters);
        Map<String, String> headers = getHeaders(body);

        JsonObject response = processServerResponse(connector -> connector.post(createUrl(PRIVATE_API_URL), body, headers))
                .getAsJsonObject();

        if (get(response, "success").getAsByte() == 0) {
            throw new BTCEException(get(response, "error").getAsString());
        }

        return get(response, "return");
    }

    private String getBody(String method, Map<String, Object> additionalParameters) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("nonce", nonce.getAndIncrement());
        parameters.put("method", method);

        if (additionalParameters != null) {
            parameters.putAll(additionalParameters);
        }

        return parameters.entrySet().stream()
                .map(e -> String.format("%s=%s", e.getKey(), e.getValue()))
                .collect(joining("&"));
    }

    private Map<String, String> getHeaders(String body) {
        mac.update(body.getBytes(Charset.forName("UTF-8")));
        Map<String, String> headers = new HashMap<>(this.headers);
        headers.put("Sign", Hex.encodeHexString(mac.doFinal()));

        return headers;
    }

    private class ParametersBuilder {

        private final Map<String, Object> parameters;

        public ParametersBuilder() {
            this.parameters = new HashMap<>();
        }

        public ParametersBuilder from(Long from) {
            if (from != null) {
                parameters.put("from", from);
            }
            return this;
        }

        public ParametersBuilder count(Integer count) {
            if (count != null) {
                parameters.put("count", count);
            }
            return this;
        }

        public ParametersBuilder fromId(Long fromId) {
            if (fromId != null) {
                parameters.put("from_id", fromId);
            }
            return this;
        }

        public ParametersBuilder endId(Long endId) {
            if (endId != null) {
                parameters.put("end_id", endId);
            }
            return this;
        }

        public ParametersBuilder order(Sort sort) {
            if (sort != null) {
                parameters.put("order", sort);
            }
            return this;
        }

        public ParametersBuilder since(LocalDateTime since) {
            if (since != null) {
                parameters.put("since", since.toEpochSecond(ZoneOffset.UTC));
            }
            return this;
        }

        public ParametersBuilder end(LocalDateTime end) {
            if (end != null) {
                parameters.put("end", end.toEpochSecond(ZoneOffset.UTC));
            }
            return this;
        }

        public ParametersBuilder pair(Pair pair) {
            if (pair != null) {
                parameters.put("pair", pair.getName());
            }
            return this;
        }

        public ParametersBuilder active(Boolean active) {
            if (active != null) {
                parameters.put("active", active ? 1 : 0);
            }
            return this;
        }

        public Map<String, Object> build() {
            return parameters;
        }
    }
}
