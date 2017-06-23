package com.isador.trade.jbtce.privateapi;

import com.google.gson.annotations.SerializedName;
import com.isador.trade.jbtce.constants.Pair;
import com.isador.trade.jbtce.constants.TradeType;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Trade history holder
 *
 * @author isador
 * @since 2.0.1
 */
public final class TradeHistory {

    @SerializedName("order_id")
    private final long orderId;

    @SerializedName("is_your_order")
    private final int yourOrder;

    private final Pair pair;
    private final TradeType type;
    private final double amount;
    private final double rate;
    private final LocalDateTime timestamp;
    private final long id;

    public TradeHistory(Pair pair, TradeType type, double amount, double rate, long orderId, int yourOrder, LocalDateTime timestamp, long id) {
        this.pair = pair;
        this.type = type;
        this.amount = amount;
        this.rate = rate;
        this.orderId = orderId;
        this.yourOrder = yourOrder;
        this.timestamp = timestamp;
        this.id = id;
    }

    /**
     * @return The pair on which the trade was executed
     */
    public Pair getPair() {
        return pair;
    }

    /**
     * @return Trade type, buy/sell
     */
    public TradeType getType() {
        return type;
    }

    /**
     * @return The amount of currency was bought/sold
     */
    public double getAmount() {
        return amount;
    }

    /**
     * @return Sell/Buy price
     */
    public double getRate() {
        return rate;
    }

    /**
     * @return order ID
     */
    public long getOrderId() {
        return orderId;
    }

    /**
     * @return True if order_id is your order, otherwise is false
     */
    public boolean isYourOrder() {
        return yourOrder == 1;
    }

    /**
     * @return Trade execution time
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * @return trade ID
     */
    public long getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TradeHistory that = (TradeHistory) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
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
                ", id=" + id +
                '}';
    }
}
