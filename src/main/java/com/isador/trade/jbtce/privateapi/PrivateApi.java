package com.isador.trade.jbtce.privateapi;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.isador.trade.jbtce.*;
import com.isador.trade.jbtce.constants.Pair;
import com.isador.trade.jbtce.constants.Sort;
import com.isador.trade.jbtce.constants.TradeType;
import com.isador.trade.jbtce.privateapi.deserializer.FundsDeserializer;
import com.isador.trade.jbtce.privateapi.deserializer.OrderStatusDeserializer;
import com.isador.trade.jbtce.privateapi.deserializer.TransactionStatusDeserializer;
import com.isador.trade.jbtce.privateapi.deserializer.TransactionTypeDeserializer;
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

/**
 * TAPI implementation.
 *
 * @author isador
 * @since 2.0.1
 */
public class PrivateApi extends AbstractApi {

    private static final String PRIVATE_API_URL = "tapi";
    private static final AtomicLong nonce = new AtomicLong(System.currentTimeMillis() / 1000);
    private final Mac mac;

    /**
     * Create new private api instance using default serverProvider and connector
     *
     * @param key    api key
     * @param secret api secret
     * @throws NullPointerException if key or secret is null
     * @throws RuntimeException     if there is any exception during mac init.
     */
    public PrivateApi(String key, String secret) {
        this(key, secret, new ServerProvider(), new DefaultConnector());
    }

    /**
     * Create new private api instance
     *
     * @param key            api key
     * @param secret         api secret
     * @param serverProvider server provider implementation
     * @param connector      connector implementation
     * @throws NullPointerException if key or secret is null
     * @throws RuntimeException     if there is any exception during mac init.
     */
    @SuppressWarnings("unchecked")
    public PrivateApi(String key, String secret, ServerProvider serverProvider, Connector connector) {
        super(serverProvider, connector, new Builder()
                .put(LocalDateTime.class, new LocalDateTimeDeserializer())
                .put(Funds.class, new FundsDeserializer())
                .put(TradeType.class, new TradeTypeDeserializer())
                .put(OrderStatus.class, new OrderStatusDeserializer())
                .put(TransactionType.class, new TransactionTypeDeserializer())
                .put(TransactionStatus.class, new TransactionStatusDeserializer())
                .build());

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

    /**
     * Returns information about the user’s current balance, API-key privileges, the number of open orders and Server Time.<br>
     * To use this method you need a privilege of the key info.<br>
     *
     * @return userInfo
     * @throws BTCEException if was any error during execution
     * @see UserInfo
     */
    public UserInfo getUserInfo() throws BTCEException {
        JsonElement response = call("getInfo", null);
        return gson.fromJson(response, UserInfo.class);
    }

    /**
     * The basic method that can be used for creating orders and trading on the exchange.<br>
     * To use this method you need an API key privilege to trade.<br>
     * You can only create limit orders using this method, but you can emulate market orders using rate parameters. E.g. using rate=0.1 you can sell at the best market price.<br>
     * Each pair has a different limit on the minimum / maximum amounts, the minimum amount and the number of digits after the decimal point. All limitations can be obtained using the info method in PublicAPI v3.<br>
     *
     * @param pair   pair
     * @param type   order type
     * @param rate   the rate at which you need to buy/sell
     * @param amount the amount you need to buy / sell
     * @return tradeResult
     * @throws BTCEException if was any error during execution
     * @see TradeResult
     */
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

    /**
     * Returns the list of your active orders.
     * To use this method you need a privilege of the info key.
     * If the order disappears from the list, it was either executed or canceled
     *
     * @param pair orders pair
     * @return list of active orders
     * @throws BTCEException if was any error during execution
     */
    public List<Order> getActiveOrders(Pair pair) throws BTCEException {
        Map<String, Object> map = new ParametersBuilder().pair(pair).build();
        JsonObject response = (JsonObject) call("ActiveOrders", map);

        // stupid orders return format
        return response.entrySet().stream()
                .peek(e -> e.getValue().getAsJsonObject().addProperty("id", e.getKey()))
                .map(e -> gson.fromJson(e.getValue(), Order.class))
                .collect(Collectors.toList());
    }

    /**
     * Returns orders list
     *
     * @param fromNum transaction number from which to read
     * @param count   transactions count
     * @param fromId  from <code>transactionId</code> (inclusive)
     * @param endId   end <code>transactionId</code> (inclusive)
     * @param sort    <code>ASC</code> when using <code>since</code> or <code>end</code>
     * @param since   list transactions after timestamp
     * @param end     list transactions before this timestamp
     * @param pair    order pair
     * @param active  return only active orders
     * @return orders list
     * @throws BTCEException if was any error during execution
     * @deprecated Now returns only active orders. Use getActiveOrders instead.
     */
    @Deprecated
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

    /**
     * Returns the history of transactions.<br>
     * To use this method you need a privilege of the info key.<br>
     *
     * @param fromNum transaction number from which to read
     * @param count   transactions count
     * @param fromId  from <code>transactionId</code> (inclusive)
     * @param endId   end <code>transactionId</code> (inclusive)
     * @param sort    <code>ASC</code> when using <code>since</code> or <code>end</code>
     * @param since   list transactions after timestamp
     * @param end     list transactions before this timestamp
     * @return list of user transactions
     * @throws BTCEException if no such fromId\endId\since, if was any error during execution
     */
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
        LocalDateTime.parse("2007-12-03T00:00:00");
        return response.entrySet().stream()
                .peek(e -> e.getValue().getAsJsonObject().addProperty("id", e.getKey()))
                .map(e -> gson.fromJson(e.getValue(), Transaction.class))
                .collect(Collectors.toList());
    }

    /**
     * Returns trade history.<br>
     * To use this method you need a privilege of the info key.<br>
     * This method doesn't do any operations upon retrieved trades list. List returned just as API  returns is.<br>
     * <pre>
     * {@code
     * // getLast 5 transactions
     * getTradeHistory(null, 5, null, null, null, null, null, null);
     * // get only btc_usd transactions for one day
     * getTradeHistory(null, null, null, null, null, LocalDateTime.parse("2007-12-03T00:00:00"), LocalDateTime.parse("2007-12-03T23:59:59"), BTC_USD);
     * }
     * </pre>
     *
     * @param fromNum trade number, from which the display starts (default: 0)
     * @param count   the number of trades for display (default: 1000)
     * @param fromId  trade ID, from which the display starts (default: 0)
     * @param endId   trade ID on which the display ends (default: ∞)
     * @param sort    Sorting (default: DESC)
     * @param since   the time to start the display (default: 0)
     * @param end     the time to end the display (default: ∞)
     * @param pair    pair to be displayed (default: all pairs)
     * @return list of trades
     * @throws BTCEException if was any error during execution
     */
    public List<TradeHistory> getTradeHistory(Long fromNum, Integer count, Long fromId,
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

    /**
     * This method is used for order cancelation.
     * To use this method you need a privilege of the trade key.
     *
     * @param orderId id of cancelled order
     * @return cancel order result
     * @throws BTCEException if was any error during execution
     */
    public CancelOrderResult cancelOrder(long orderId) throws BTCEException {
        checkArgument(orderId > 0, "Invalid oderId: %s", orderId);

        Map<String, Object> map = ImmutableMap.of("order_id", orderId);
        JsonElement response = call("CancelOrder", map);
        return gson.fromJson(response, CancelOrderResult.class);
    }

    /**
     * Call tapi method with specified parameters
     *
     * @param method               tapi method
     * @param additionalParameters method parameters
     * @return parsed json as JsonElement
     * @throws BTCEException if there was an error executing method, invalid json returned, or smth else
     */
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

    /**
     * Prepare request body
     *
     * @param method               tapi method
     * @param additionalParameters filters/etc.
     * @return prepared request body
     */
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

    /**
     * Prepare request headers map.
     * Singing request body with key\secret pair
     *
     * @param body request body
     * @return headers for request
     */
    private Map<String, String> getHeaders(String body) {
        mac.update(body.getBytes(Charset.forName("UTF-8")));
        Map<String, String> headers = new HashMap<>(this.headers);
        headers.put("Sign", Hex.encodeHexString(mac.doFinal()));

        return headers;
    }

    /**
     * Parameters builder. Simply aggregate and construct additional parameters map for tapi requests
     */
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
