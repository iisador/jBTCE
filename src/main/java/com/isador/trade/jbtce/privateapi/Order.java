package com.isador.trade.jbtce.privateapi;

import com.google.gson.annotations.SerializedName;
import com.isador.trade.jbtce.constants.Pair;
import com.isador.trade.jbtce.constants.TradeType;

import java.time.LocalDateTime;

public final class Order {

    @SerializedName("timestamp_created")
    private final LocalDateTime timestampCreated;

    private final long id;
    private final Pair pair;
    private final TradeType type;
    private final double amount;
    private final double rate;
    private final OrderStatus status;

    public Order(long id, Pair pair, TradeType type, double amount, double rate, OrderStatus status, LocalDateTime timestampCreated) {
        this.id = id;
        this.pair = pair;
        this.type = type;
        this.amount = amount;
        this.rate = rate;
        this.status = status;
        this.timestampCreated = timestampCreated;
    }

    public long getId() {
        return id;
    }

    public Pair getPair() {
        return pair;
    }

    public TradeType getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public double getRate() {
        return rate;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public LocalDateTime getTimestampCreated() {
        return timestampCreated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Order order = (Order) o;

        return id == order.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", pair=" + pair +
                ", type=" + type +
                ", amount=" + amount +
                ", rate=" + rate +
                ", status=" + status +
                ", timestampCreated=" + timestampCreated +
                '}';
    }
}
