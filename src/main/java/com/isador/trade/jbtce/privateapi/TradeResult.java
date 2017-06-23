package com.isador.trade.jbtce.privateapi;

import com.google.gson.annotations.SerializedName;

/**
 * Trade response holder
 *
 * @author isador
 * @since 2.0.1
 */
public final class TradeResult {

    @SerializedName("order_id")
    private final long orderId;

    private final double received;
    private final double remains;
    private final Funds funds;

    public TradeResult(double received, double remains, long orderId, Funds funds) {
        this.received = received;
        this.remains = remains;
        this.orderId = orderId;
        this.funds = funds;
    }

    /**
     * @return The amount of currency bought/sold
     */
    public double getReceived() {
        return received;
    }

    /**
     * @return The remaining amount of currency to be bought/sold (and the initial order amount)
     */
    public double getRemains() {
        return remains;
    }

    /**
     * @return Is equal to 0 if the request was fully “matched” by the opposite orders, otherwise the ID of the executed order will be returned
     */
    public long getOrderId() {
        return orderId;
    }

    /**
     * @return Balance after the request
     */
    public Funds getFunds() {
        return funds;
    }

    /**
     * Just help method.
     *
     * @return orderId == 0
     */
    public boolean isTradeComplete() {
        return orderId == 0;
    }

    @Override
    public String toString() {
        return "TradeResult{" +
                "received=" + received +
                ", remains=" + remains +
                ", orderId=" + orderId +
                ", funds=" + funds +
                '}';
    }
}
