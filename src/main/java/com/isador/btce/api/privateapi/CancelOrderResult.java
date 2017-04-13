package com.isador.btce.api.privateapi;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class CancelOrderResult {

    @SerializedName("order_id")
    private final long orderId;

    private final Funds funds;

    private CancelOrderResult(long orderId, Funds funds) {
        this.orderId = orderId;
        this.funds = funds;
    }

    public long getOrderId() {
        return orderId;
    }

    public Funds getFunds() {
        return funds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CancelOrderResult that = (CancelOrderResult) o;
        return Objects.equals(orderId, that.orderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId);
    }

    @Override
    public String toString() {
        return "CancelOrderStatus{" +
                "orderId=" + orderId +
                ", funds=" + funds +
                '}';
    }
}