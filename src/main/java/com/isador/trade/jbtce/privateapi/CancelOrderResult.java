package com.isador.trade.jbtce.privateapi;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

/**
 * Cancel order response holder
 *
 * @author isador
 * @since 2.0.1
 */
public final class CancelOrderResult {

    @SerializedName("order_id")
    private final long id;

    private final Funds funds;

    public CancelOrderResult(long id, Funds funds) {
        this.id = id;
        this.funds = funds;
    }

    /**
     * @return ID of canceled order
     */
    public long getId() {
        return id;
    }

    /**
     * @return balance upon request
     */
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