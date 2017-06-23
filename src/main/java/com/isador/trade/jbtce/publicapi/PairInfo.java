package com.isador.trade.jbtce.publicapi;

import com.google.gson.annotations.SerializedName;
import com.isador.trade.jbtce.constants.Pair;

import java.util.Objects;

/**
 * Pair info holder
 *
 * @author isador
 * @see BTCEInfo
 * @since 2.0.1
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

    /**
     * @return number of decimals allowed during trading
     */
    public int getDecimalPlaces() {
        return decimalPlaces;
    }

    /**
     * @return minimum price allowed during trading
     */
    public double getMinPrice() {
        return minPrice;
    }

    /**
     * @return maximum price allowed during trading
     */
    public double getMaxPrice() {
        return maxPrice;
    }

    /**
     * @return minimum sell / buy transaction size
     */
    public double getMinAmount() {
        return minAmount;
    }

    /**
     * A hidden pair remains active but is not displayed in the list of pairs on the main page
     *
     * @return whether the pair is hidden
     */
    public boolean isHidden() {
        return hidden == 1;
    }

    /**
     * The Commission is displayed for all users, it will not change even if it was reduced on your account in case of promotional pricing
     *
     * @return commission for this pair
     */
    public double getFee() {
        return fee;
    }

    /**
     * @return pair
     */
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
