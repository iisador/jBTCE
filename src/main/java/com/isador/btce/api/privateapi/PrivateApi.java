package com.isador.btce.api.privateapi;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.isador.btce.api.AbstractApi;
import com.isador.btce.api.BTCEException;
import com.isador.btce.api.Connector;
import com.isador.btce.api.LocalDateTimeDeserializer;
import com.isador.btce.api.constants.Currency;
import com.isador.btce.api.constants.Operation;
import com.isador.btce.api.constants.Pair;
import com.isador.btce.api.constants.Sort;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.isador.btce.api.LocalDateTimeDeserializer.deserialize;

public class PrivateApi extends AbstractApi {

    private final Connector connector;

    public PrivateApi(Connector connector) {
        super(ImmutableMap.of(LocalDateTime.class, new LocalDateTimeDeserializer(),
                              Funds.class, new FundsDeserializer()));
        this.connector = checkNotNull(connector, "Connector instance should be not null");
    }

    public UserInfo getUserInfo() throws BTCEException {
        String json = connector.signedPost("getInfo", null);
        JsonElement response = processResponse(json);
        return gson.fromJson(response, UserInfo.class);
    }

    public TradeResult trade(Pair pair, Operation operation, double rate, double amount) throws BTCEException {
        checkNotNull(pair, "Invalid trade pair");
        checkNotNull(operation, "Invalid trade type");
        checkArgument(rate > 0, "Invalid trade rate: %s", rate);
        checkArgument(amount > 0, "Invalid trade amount: %s", amount);

        Map<String, Object> map = ImmutableMap.of("pair", pair.getName(),
                                                  "type", operation.name().toLowerCase(),
                                                  "rate", rate,
                                                  "amount", amount);
        String json = connector.signedPost("Trade", map);
        JsonElement response = processResponse(json);
        return gson.fromJson(response, TradeResult.class);
    }

    public List<Order> getOrderList(Long fromNum, Integer count, Long fromId,
                                    Long endId, Sort sort, LocalDateTime since,
                                    LocalDateTime end, Pair pair, Boolean active) throws BTCEException {

        Map<String, Object> map = new ParametersBuiilder()
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

        String json = connector.signedPost("OrderList", map);
        JsonObject response = (JsonObject) processResponse(json);
        return response.entrySet().stream()
                .map(this::toOrder)
                .collect(Collectors.toList());
    }

    public List<Transaction> getTransactionsList(Long fromNum, Integer count, Long fromId,
                                                 Long endId, Sort sort, LocalDateTime since,
                                                 LocalDateTime end) throws BTCEException {
        Map<String, Object> map = new ParametersBuiilder()
                .from(fromNum)
                .count(count)
                .fromId(fromId)
                .endId(endId)
                .order(sort)
                .since(since)
                .end(end)
                .build();

        String json = connector.signedPost("TransHistory", map);
        JsonObject response = (JsonObject) processResponse(json);
        return response.entrySet().stream()
                .map(this::toTransaction)
                .collect(Collectors.toList());
    }

    public List<TradeHistory> getTradesList(Long fromNum, Integer count, Long fromId,
                                            Long endId, Sort sort, LocalDateTime since,
                                            LocalDateTime end, Pair pair) throws BTCEException {
        Map<String, Object> map = new ParametersBuiilder()
                .from(fromNum)
                .count(count)
                .fromId(fromId)
                .endId(endId)
                .order(sort)
                .since(since)
                .end(end)
                .pair(pair)
                .build();

        String json = connector.signedPost("TradeHistory", map);
        JsonObject response = (JsonObject) processResponse(json);
        return response.entrySet().stream()
                .map(this::toTradeHistory)
                .collect(Collectors.toList());
    }

    public CancelOrderResult cancelOrder(long orderId) throws BTCEException {
        checkArgument(orderId > 0, "Invalid oderId: %s", orderId);

        Map<String, Object> map = ImmutableMap.of("order_id", orderId);
        String json = connector.signedPost("CancelOrder", map);
        JsonElement response = processResponse(json);
        return gson.fromJson(response, CancelOrderResult.class);
    }

    private Order toOrder(Entry<String, JsonElement> entry) {
        JsonObject jsonOrder = entry.getValue().getAsJsonObject();

        long id = Long.parseLong(entry.getKey());
        Pair pair = Pair.valueOf(jsonOrder.get("pair").getAsString().toUpperCase());
        Operation type = Operation.valueOf(jsonOrder.get("type").getAsString().toUpperCase());
        double amount = jsonOrder.get("amount").getAsDouble();
        double rate = jsonOrder.get("rate").getAsDouble();
        int status = jsonOrder.get("status").getAsInt();
        LocalDateTime timestampCreated = deserialize(jsonOrder.get("timestamp_created").getAsLong());

        return new Order(id, pair, type, amount, rate, status, timestampCreated);
    }

    private Transaction toTransaction(Entry<String, JsonElement> entry) {
        JsonObject jsonTransaction = entry.getValue().getAsJsonObject();

        long id = Long.parseLong(entry.getKey());
        int type = jsonTransaction.get("type").getAsInt();
        double amount = jsonTransaction.get("amount").getAsDouble();
        Currency currency = Currency.valueOf(jsonTransaction.get("currency").getAsString().toUpperCase());
        String desc = jsonTransaction.get("desc").getAsString();
        int status = jsonTransaction.get("status").getAsInt();
        LocalDateTime timestamp = deserialize(jsonTransaction.get("timestamp").getAsLong());

        return new Transaction(type, amount, currency, status, timestamp, id, desc);
    }

    private TradeHistory toTradeHistory(Entry<String, JsonElement> entry) {
        JsonObject jsonTh = entry.getValue().getAsJsonObject();

        long id = Long.parseLong(entry.getKey());
        Pair pair = Pair.valueOf(jsonTh.get("pair").getAsString().toUpperCase());
        Operation type = Operation.valueOf(jsonTh.get("type").getAsString().toUpperCase());
        double amount = jsonTh.get("amount").getAsDouble();
        double rate = jsonTh.get("rate").getAsDouble();
        long orderId = jsonTh.get("order_id").getAsLong();
        boolean yourOrder = jsonTh.get("is_your_order").getAsInt() == 1;
        LocalDateTime timestamp = deserialize(jsonTh.get("timestamp").getAsLong());

        return new TradeHistory(pair, type, amount, rate, orderId, yourOrder, timestamp, id);
    }

    private JsonElement processResponse(String json) throws BTCEException {
        JsonObject obj = parser.parse(json).getAsJsonObject();
        if (obj.get("success").getAsByte() == 0) {
            throw new BTCEException(obj.get("error").getAsString());
        }

        return obj.get("return");
    }

    private class ParametersBuiilder {

        private final Map<String, Object> parameters;

        public ParametersBuiilder() {
            this.parameters = new HashMap<>();
        }

        public ParametersBuiilder from(Long from) {
            if (from != null) {
                parameters.put("from", from);
            }
            return this;
        }

        public ParametersBuiilder count(Integer count) {
            if (count != null) {
                parameters.put("count", count);
            }
            return this;
        }

        public ParametersBuiilder fromId(Long fromId) {
            if (fromId != null) {
                parameters.put("from_id", fromId);
            }
            return this;
        }

        public ParametersBuiilder endId(Long endId) {
            if (endId != null) {
                parameters.put("end_id", endId);
            }
            return this;
        }

        public ParametersBuiilder order(Sort sort) {
            if (sort != null) {
                parameters.put("order", sort);
            }
            return this;
        }

        public ParametersBuiilder since(LocalDateTime since) {
            if (since != null) {
                parameters.put("since", since.toEpochSecond(ZoneOffset.UTC));
            }
            return this;
        }

        public ParametersBuiilder end(LocalDateTime end) {
            if (end != null) {
                parameters.put("end", end.toEpochSecond(ZoneOffset.UTC));
            }
            return this;
        }

        public ParametersBuiilder pair(Pair pair) {
            if (pair != null) {
                parameters.put("pair", pair.getName());
            }
            return this;
        }

        public ParametersBuiilder active(Boolean active) {
            if (active != null) {
                parameters.put("active", active ? 1 : 0);
            }
            return this;
        }

        public Map<String, Object> build() {
            return parameters;
        }
    }

//    /**
//     * Returns your open orders.
//     *
//     * @param pair
//     *            Trade pair or null
//     * @return orders array
//     * @throws IOException
//     */
//    public Order[] getActiveOrders(Pair pair) throws IOException {
//        Map<String, String> params = new HashMap<String, String>();
//        if (pair != null) {
//            params.put("pair", pair.toString().toLowerCase());
//        }
//
//        Order[] orders = new Order[0];
//        JsonObject result = apiCall("ActiveOrders", params);
//        if (result != null) {
//            Set<Entry<String, JsonElement>> set = result.entrySet();
//            Iterator<Entry<String, JsonElement>> it = set.iterator();
//            List<Order> l = new ArrayList<Order>();
//            while (it.hasNext()) {
//                Entry<String, JsonElement> entry = it.next();
//                l.add(new Order(Long.parseLong(entry.getKey()), entry
//                        .getValue().getAsJsonObject()));
//            }
//            orders = l.toArray(orders);
//        }
//        return orders;
//    }

//    /**
//     * Cancel opened order.
//     *
//     * @param orderId
//     *            should be >= 0 or -1
//     * @return CancelOrderStatus
//     * @throws IllegalArgumentException
//     *             when orderId is invalid
//     * @throws IOException
//     *             on any errors with server communication
//     */
//    public CancelOrderStatus cancelOrder(long orderId) throws IOException {
//        if (orderId != -1 && orderId <= 0) {
//            throw new IllegalArgumentException("Wrong order id value: "
//                                                       + orderId);
//        }
//
//        Map<String, String> params = new HashMap<String, String>();
//        if (orderId != -1) {
//            params.put("order_id", String.valueOf(orderId));
//        }
//
//        JsonObject result = apiCall("CancelOrder", params);
//        if (result != null) {
//            return new CancelOrderStatus(result);
//        }
//        return null;
//    }

//    /**
//     * It returns your open orders/the orders history. Calls
//     * {@code getOrderList(-1, -1, -1, -1, null, -1, -1, null, -1)}
//     *
//     * @return last account orders
//     * @throws IOException
//     *             on any errors with server communication
//     */
//    @Deprecated
//    public Order[] getOrderList() throws IOException {
//        return getOrderList(-1, -1, -1, -1, null, -1, -1, null, -1);
//    }

//    /**
//     * It returns your open orders/the orders history. <br>
//     * <i>Note: while using since or end parameters, order parameter
//     * automatically takes up ASC value.</i>
//     *
//     * @deprecated use getActiveOrders
//     *
//     * @param from
//     *            the number of the order to start displaying with
//     * @param count
//     *            The number of orders for displaying
//     * @param fromId
//     *            id of the order to start displaying with
//     * @param endId
//     *            id of the order to finish displaying
//     * @param order
//     *            sorting
//     * @param since
//     *            when to start displaying
//     * @param end
//     *            when to finish displaying
//     * @param pair
//     *            the pair to display the orders
//     * @param active
//     *            is it displaying of active orders only?
//     * @return orders array
//     * @throws IOException
//     */
//    @Deprecated
//    public Order[] getOrderList(long from, int count, long fromId, long endId,
//                                Sort order, long since, long end, Pair pair, int active)
//            throws IOException {
//        Map<String, String> params = new HashMap<String, String>();
//        if (from != -1) {
//            params.put("from", String.valueOf(from));
//        }
//        if (count != -1) {
//            params.put("count", String.valueOf(count));
//        }
//        if (fromId != -1) {
//            params.put("from_id", String.valueOf(fromId));
//        }
//        if (endId != -1) {
//            params.put("end_id", String.valueOf(endId));
//        }
//        if (order != null) {
//            params.put("order", order.toString());
//        }
//        if (since != -1) {
//            params.put("since", String.valueOf(since));
//        }
//        if (end != -1) {
//            params.put("end", String.valueOf(end));
//        }
//        if (pair != null) {
//            params.put("pair", pair.toString().toLowerCase());
//        }
//        if (active != -1) {
//            if (active > 1) {
//                params.put("active", "1");
//            } else {
//                params.put("active", "0");
//            }
//        }
//
//        Order[] orders = new Order[0];
//        JsonObject result = apiCall("OrderList", params);
//        if (result != null) {
//            Set<Entry<String, JsonElement>> set = result.entrySet();
//            Iterator<Entry<String, JsonElement>> it = set.iterator();
//            List<Order> l = new ArrayList<Order>();
//            while (it.hasNext()) {
//                Entry<String, JsonElement> entry = it.next();
//                l.add(new Order(Long.parseLong(entry.getKey()), entry
//                        .getValue().getAsJsonObject()));
//            }
//            orders = l.toArray(orders);
//        }
//        return orders;
//    }

//    /**
//     * It returns the trade history. Calls
//     * {@code getTradeHistory(-1, -1, -1, -1, null, -1, -1, null)}
//     *
//     * @return trade array
//     * @throws IOException
//     */
//    public Trade[] getTradeHistory() throws IOException {
//        return getTradeHistory(-1, -1, -1, -1, null, -1, -1, null);
//    }

//    /**
//     * Returns your last trade. Calls
//     * {@code getTradeHistory(-1, 1, -1, -1, null, -1, -1, pair)}
//     *
//     * @param pair
//     *            pair of lsat trade
//     * @return Trade instance
//     * @throws IOException
//     */
//    public Trade getLastTrade(Pair pair) throws IOException {
//        Trade[] list = getTradeHistory(-1, 1, -1, -1, null, -1, -1, pair);
//        return (list.length > 0) ? list[0] : null;
//    }

//    /**
//     * It returns the trade history. <br>
//     * <i>Note: while using since or end parameters, order parameter
//     * automatically takes up ASC value.</i>
//     *
//     * @param from
//     *            the number of the transaction to start displaying with
//     * @param count
//     *            the number of transactions for displaying
//     * @param fromId
//     *            the ID of the transaction to start displaying with
//     * @param endId
//     *            the ID of the transaction to finish displaying with
//     * @param order
//     *            sorting
//     * @param since
//     *            when to start the displaying
//     * @param end
//     *            when to finish the displaying
//     * @param pair
//     *            the pair to show the transactions
//     * @return trades array
//     * @throws IOException
//     */
//    public Trade[] getTradeHistory(long from, int count, long fromId,
//                                   long endId, Sort order, long since, long end, Pair pair)
//            throws IOException {
//        Map<String, String> params = new HashMap<String, String>();
//        if (from != -1) {
//            params.put("from", String.valueOf(from));
//        }
//        if (count != -1) {
//            params.put("count", String.valueOf(count));
//        }
//        if (fromId != -1) {
//            params.put("from_id", String.valueOf(fromId));
//        }
//        if (endId != -1) {
//            params.put("end_id", String.valueOf(endId));
//        }
//        if (order != null) {
//            params.put("order", order.toString());
//        }
//        if (since != -1) {
//            params.put("since", String.valueOf(since));
//        }
//        if (end != -1) {
//            params.put("end", String.valueOf(end));
//        }
//        if (pair != null) {
//            params.put("pair", pair.toString().toLowerCase());
//        }
//
//        Trade[] trades = new Trade[0];
//        JsonObject result = apiCall("TradeHistory", params);
//        if (result != null) {
//            Set<Entry<String, JsonElement>> set = result.entrySet();
//            Iterator<Entry<String, JsonElement>> it = set.iterator();
//            List<Trade> l = new ArrayList<Trade>();
//            while (it.hasNext()) {
//                Entry<String, JsonElement> entry = it.next();
//                l.add(new Trade(Long.parseLong(entry.getKey()), entry
//                        .getValue().getAsJsonObject()));
//            }
//            trades = l.toArray(trades);
//        }
//        return trades;
//    }

//    /**
//     * It returns the transactions history. Calls
//     * {@code getTransHistrory(-1, -1, -1, -1, null, -1, -1)}
//     *
//     * @return transaction array
//     * @throws IOException
//     */
//    public Transaction[] getTransactionHistory() throws IOException {
//        return getTransHistrory(-1, -1, -1, -1, null, -1, -1);
//    }

//    /**
//     * Returns your last transaction. Calls
//     * {@code getTransHistrory(-1, 1, -1, -1, null, -1, -1)}.
//     *
//     * @return transaction instance
//     * @throws IOException
//     */
//    public Transaction getLastTransaction() throws IOException {
//        Transaction[] list = getTransHistrory(-1, 1, -1, -1, null, -1, -1);
//        return (list.length > 0) ? list[0] : null;
//    }

//    /**
//     * It returns the transactions history. <br>
//     * <i>Note: while using since or end parameters, the order parameter
//     * automatically take up ASC value.</i>
//     *
//     * @param from
//     *            The ID of the transaction to start displaying with
//     * @param count
//     *            The number of transactions for displaying
//     * @param fromId
//     *            The ID of the transaction to start displaying with
//     * @param endId
//     *            The ID of the transaction to finish displaying with
//     * @param order
//     *            sorting
//     * @param since
//     *            When to start displaying?
//     * @param end
//     *            When to finish displaying?
//     * @return
//     * @throws IOException
//     */
//    public Transaction[] getTransHistrory(long from, int count, long fromId,
//                                          long endId, Sort order, long since, long end) throws IOException {
//        Map<String, String> params = new HashMap<String, String>();
//        if (from != -1) {
//            params.put("from", String.valueOf(from));
//        }
//        if (count != -1) {
//            params.put("count", String.valueOf(count));
//        }
//        if (fromId != -1) {
//            params.put("from_id", String.valueOf(fromId));
//        }
//        if (endId != -1) {
//            params.put("end_id", String.valueOf(endId));
//        }
//        if (order != null) {
//            params.put("order", order.toString());
//        }
//        if (since != -1) {
//            params.put("since", String.valueOf(since));
//        }
//        if (end != -1) {
//            params.put("end", String.valueOf(end));
//        }
//
//        Transaction[] transactions = new Transaction[0];
//        JsonObject result = apiCall("TransHistory", params);
//        if (result != null) {
//            Set<Entry<String, JsonElement>> set = result.entrySet();
//            Iterator<Entry<String, JsonElement>> it = set.iterator();
//            List<Transaction> l = new ArrayList<Transaction>();
//            while (it.hasNext()) {
//                Entry<String, JsonElement> entry = it.next();
//                l.add(new Transaction(Long.parseLong(entry.getKey()), entry
//                        .getValue().getAsJsonObject()));
//            }
//            transactions = l.toArray(transactions);
//        }
//        return transactions;
//    }

//    public UserInfo getUserInfo() throws BTCEException {
//        return mapper.readValue(connector.signedPost("getInfo", null).toString(), UserInfo.class);
//    }

//    /**
//     * Trading is done according to this method.
//     *
//     * @param pair
//     *            Trade pair
//     * @param type
//     *            The transaction type
//     * @param rate
//     *            The rate to buy/sell
//     * @param amount
//     *            The amount which is necessary to buy/sell
//     * @return trade result instance
//     * @throws IOException
//     * @throws IllegalArgumentException
//     *             when any of parameters is invalid
//     */
//    public TradeResult trade(Pair pair, TradeType type, Double rate,
//                             Double amount) throws IOException {
//        if (pair == null) {
//            throw new IllegalArgumentException("Pair is null");
//        }
//        if (type == null) {
//            throw new IllegalArgumentException("Type is null");
//        }
//        if (rate != -1 && rate <= 0) {
//            throw new IllegalArgumentException("Rate value is illegal");
//        }
//        if (amount != -1 && amount <= 0) {
//            throw new IllegalArgumentException("Amount value is illegal");
//        }
//
//        Map<String, String> params = new HashMap<String, String>();
//        params.put("pair", pair.toString().toLowerCase());
//        params.put("type", type.toString().toLowerCase());
//        params.put("rate", String.valueOf(rate));
//        params.put("amount", String.format("%.7f", amount).replace(',', '.'));
//
//        JsonObject result = apiCall("Trade", params);
//        return (result != null) ? new TradeResult(result) : null;
//    }
}
