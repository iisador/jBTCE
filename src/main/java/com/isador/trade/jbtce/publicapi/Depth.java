package com.isador.trade.jbtce.publicapi;

import java.util.Arrays;
import java.util.Objects;

/**
 * Depth holder
 *
 * @author isador
 * @since 2.0.1
 */
public final class Depth {

    private final SimpleOrder[] asks;
    private final SimpleOrder[] bids;

    public Depth(SimpleOrder[] asks,
                 SimpleOrder[] bids) {
        this.asks = asks;
        this.bids = bids;
    }

    /**
     * @return Sell orders
     */
    public SimpleOrder[] getAsks() {
        return asks;
    }

    /**
     * @return Buy orders
     */
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

    /**
     * Price/amount holder
     */
    public static final class SimpleOrder {

        private final double price;
        private final double amount;

        public SimpleOrder(double price, double amount) {
            this.price = price;
            this.amount = amount;
        }

        /**
         * @return order price
         */
        public double getPrice() {
            return price;
        }

        /**
         * @return order amount
         */
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

        @Override
        public String toString() {
            return "SimpleOrder{" +
                    "price=" + price +
                    ", amount=" + amount +
                    '}';
        }
    }
}
