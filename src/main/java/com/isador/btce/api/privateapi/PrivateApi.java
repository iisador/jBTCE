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
import static com.isador.btce.api.LocalDateTimeDeserializer.deserialize;
import static java.util.Objects.requireNonNull;

public class PrivateApi extends AbstractApi {

    private final Connector connector;

    public PrivateApi(Connector connector) {
        super(ImmutableMap.of(LocalDateTime.class, new LocalDateTimeDeserializer(),
                              Funds.class, new FundsDeserializer()));
        this.connector = requireNonNull(connector, "Connector instance should be not null");
    }

    public UserInfo getUserInfo() throws BTCEException {
        String json = connector.signedPost("getInfo", null);
        JsonElement response = processResponse(json);
        return gson.fromJson(response, UserInfo.class);
    }

    public TradeResult trade(Pair pair, Operation operation, double rate, double amount) throws BTCEException {
        requireNonNull(pair, "Invalid trade pair");
        requireNonNull(operation, "Invalid trade type");
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

        String json = connector.signedPost("OrderList", map);
        JsonObject response = (JsonObject) processResponse(json);
        return response.entrySet().stream()
                .map(this::toOrder)
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

        String json = connector.signedPost("TransHistory", map);
        JsonObject response = (JsonObject) processResponse(json);
        return response.entrySet().stream()
                .map(this::toTransaction)
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
        processServerResponse(json);
        JsonObject obj = parser.parse(json).getAsJsonObject();
        if (get(obj, "success").getAsByte() == 0) {
            throw new BTCEException(get(obj, "error").getAsString());
        }

        return get(obj, "return");
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
