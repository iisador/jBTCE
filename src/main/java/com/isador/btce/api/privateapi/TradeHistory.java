package com.isador.btce.api.privateapi;

import com.isador.btce.api.constants.Operation;
import com.isador.btce.api.constants.Pair;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Created by isador
 * on 12.04.17
 */
public class TradeHistory {
    private final Pair pair;
    private final Operation type;
    private final double amount;
    private final double rate;
    private final long orderId;
    private final boolean yourOrder;
    private final LocalDateTime timestamp;
    private final long tradeId;

    TradeHistory(Pair pair, Operation type, double amount, double rate, long orderId, boolean yourOrder, LocalDateTime timestamp, long tradeId) {
        this.pair = pair;
        this.type = type;
        this.amount = amount;
        this.rate = rate;
        this.orderId = orderId;
        this.yourOrder = yourOrder;
        this.timestamp = timestamp;
        this.tradeId = tradeId;
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

    public long getOrderId() {
        return orderId;
    }

    public boolean isYourOrder() {
        return yourOrder;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public long getTradeId() {
        return tradeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TradeHistory that = (TradeHistory) o;
        return Objects.equals(tradeId, that.tradeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tradeId);
    }

    @Override
    public String toString() {
        return "TradeHistory{" +
                "pair=" + pair +
                ", type=" + type +
                ", amount=" + amount +
                ", rate=" + rate +
                ", orderId=" + orderId +
                ", yourOrder=" + yourOrder +
                ", timestamp=" + timestamp +
                ", tradeId=" + tradeId +
                '}';
    }
}
