package com.isador.trade.jbtce.privateapi;

import com.google.gson.annotations.SerializedName;
import com.isador.trade.jbtce.constants.Pair;
import com.isador.trade.jbtce.constants.TradeType;

import java.time.LocalDateTime;

/**
 * Order holder
 *
 * @author isador
 * @since 2.0.1
 */
public final class Order {

    @SerializedName("timestamp_created")
    private final LocalDateTime timestampCreated;

    private final long id;
    private final Pair pair;
    private final TradeType type;
    private final double amount;

    @SerializedName("start_amount")
    private final double startAmount;
    private final double rate;
    private final OrderStatus status;

    public Order(long id, Pair pair, TradeType type, double startAmount, double amount, double rate, OrderStatus status, LocalDateTime timestampCreated) {
        this.id = id;
        this.pair = pair;
        this.type = type;
        this.startAmount = startAmount;
        this.amount = amount;
        this.rate = rate;
        this.status = status;
        this.timestampCreated = timestampCreated;
    }
//    timestamp_created: .
//
//            status:

    /**
     * @return order ID
     */
    public long getId() {
        return id;
    }

    /**
     * @return The pair on which the order was created
     */
    public Pair getPair() {
        return pair;
    }

    /**
     * @return Order type, buy/sell
     */
    public TradeType getType() {
        return type;
    }

    /**
     * @return The initial amount at the time of order creation
     */
    public double getStartAmount() {
        return startAmount;
    }

    /**
     * @return The remaining amount of currency to be bought/sold
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
     * @return order status
     * @see OrderStatus
     */
    public OrderStatus getStatus() {
        return status;
    }

    /**
     * @return The time when the order was created
     */
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
