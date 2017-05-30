package com.isador.trade.jbtce.publicapi;

import com.google.gson.annotations.SerializedName;
import com.isador.trade.jbtce.constants.Pair;

import java.util.Objects;

/**
 * Created by isador
 * on 10.05.17
 */
public final class PairInfo {

    @SerializedName("decimal_places")
    private final int decimalPlaces;

    @SerializedName("min_price")
    private final double minPrice;

    @SerializedName("max_price")
    private final double maxPrice;

    @SerializedName("min_amount")
    private final double minAmount;

    private final int hidden;
    private final double fee;
    private final Pair pair;

    public PairInfo(int decimalPlaces, double minPrice, double maxPrice, double minAmount, int hidden, double fee, Pair pair) {
        this.decimalPlaces = decimalPlaces;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.minAmount = minAmount;
        this.hidden = hidden;
        this.fee = fee;
        this.pair = pair;
    }

    public int getDecimalPlaces() {
        return decimalPlaces;
    }

    public double getMinPrice() {
        return minPrice;
    }

    public double getMaxPrice() {
        return maxPrice;
    }

    public double getMinAmount() {
        return minAmount;
    }

    public boolean isHidden() {
        return hidden == 1;
    }

    public double getFee() {
        return fee;
    }

    public Pair getPair() {
        return pair;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PairInfo pairInfo = (PairInfo) o;
        return Objects.equals(pair, pairInfo.pair);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pair);
    }

    @Override
    public String toString() {
        return "PairInfo{" +
                "decimalPlaces=" + decimalPlaces +
                ", minPrice=" + minPrice +
                ", maxPrice=" + maxPrice +
                ", minAmount=" + minAmount +
                ", hidden=" + hidden +
                ", fee=" + fee +
                ", pair=" + pair +
                '}';
    }
}
