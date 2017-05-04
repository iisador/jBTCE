package com.isador.trade.jbtce.privateapi;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public final class CancelOrderResult {

    @SerializedName("order_id")
    private final long id;

    private final Funds funds;

    CancelOrderResult(long id, Funds funds) {
        this.id = id;
        this.funds = funds;
    }

    public long getId() {
        return id;
    }

    public Funds getFunds() {
        return funds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CancelOrderResult that = (CancelOrderResult) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "CancelOrderResult{" +
                "id=" + id +
                ", funds=" + funds +
                '}';
    }
}