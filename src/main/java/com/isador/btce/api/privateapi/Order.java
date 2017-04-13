package com.isador.btce.api.privateapi;

import com.isador.btce.api.constants.Operation;
import com.isador.btce.api.constants.Pair;

import java.time.LocalDateTime;

public class Order {

    private final long id;
    private final Pair pair;
    private final Operation type;
    private final double amount;
    private final double rate;
    private final int status;
    private final LocalDateTime timestampCreated;

    Order(long id, Pair pair, Operation type, double amount, double rate, int status, LocalDateTime timestampCreated) {
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

    public Operation getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public double getRate() {
        return rate;
    }

    public int getStatus() {
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
