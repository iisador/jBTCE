package com.isador.btce.api.publicapi;

import java.util.Arrays;
import java.util.Objects;

/**
 * Created by isador
 * on 31.03.17
 */
public final class Depth {

    private final SimpleOrder[] asks;
    private final SimpleOrder[] bids;

    Depth(SimpleOrder[] asks,
          SimpleOrder[] bids) {
        this.asks = asks;
        this.bids = bids;
    }

    public SimpleOrder[] getAsks() {
        return asks;
    }

    public SimpleOrder[] getBids() {
        return bids;
    }

    @Override
    public String toString() {
        return "Depth{" +
                "asks=" + Arrays.toString(asks) +
                ", bids=" + Arrays.toString(bids) +
                '}';
    }

    public static final class SimpleOrder {

        private final double price;
        private final double amount;

        SimpleOrder(double price, double amount) {
            this.price = price;
            this.amount = amount;
        }

        public double getPrice() {
            return price;
        }

        public double getAmount() {
            return amount;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            SimpleOrder item = (SimpleOrder) o;
            return Double.compare(price, item.price) == 0 &&
                    Double.compare(amount, item.amount) == 0;
        }

        @Override
        public int hashCode() {
            return Objects.hash(price, amount);
        }

        @Override public String toString() {
            return "SimpleOrder{" +
                    "price=" + price +
                    ", amount=" + amount +
                    '}';
        }
    }
}
