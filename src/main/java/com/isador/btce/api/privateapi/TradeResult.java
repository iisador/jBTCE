package com.isador.btce.api.privateapi;

import com.google.gson.annotations.SerializedName;

public final class TradeResult {

    @SerializedName("order_id")
    private final long orderId;

    private final double received;
    private final double remains;
    private final Funds funds;

    TradeResult(double received, double remains, long orderId, Funds funds) {
        this.received = received;
        this.remains = remains;
        this.orderId = orderId;
        this.funds = funds;
    }

    public double getReceived() {
        return received;
    }

    public double getRemains() {
        return remains;
    }

    public long getOrderId() {
        return orderId;
    }

    public Funds getFunds() {
        return funds;
    }

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
