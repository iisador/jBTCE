package com.isador.trade.jbtce.publicapi;

import com.google.gson.annotations.SerializedName;
import com.isador.trade.jbtce.constants.Currency;
import com.isador.trade.jbtce.constants.TradeType;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Trade holder
 *
 * @author isador
 * @since 2.0.1
 */
public final class Trade {

    @SerializedName("tid")
    private final long id;

    @SerializedName("price_currency")
    private final Currency priceCurrency;

    @SerializedName(value = "trade_type")
    private final TradeType type;

    private final LocalDateTime date;

    private final double price;
    private final double amount;
    private final Currency item;

    public Trade(LocalDateTime date, double price, double amount, long id, Currency priceCurrency, Currency item, TradeType type) {
        this.date = date;
        this.price = price;
        this.amount = amount;
        this.id = id;
        this.priceCurrency = priceCurrency;
        this.item = item;
        this.type = type;
    }

    /**
     * @return trade date
     */
    public LocalDateTime getDate() {
        return date;
    }

    /**
     * @return Buy price/Sell price
     */
    public double getPrice() {
        return price;
    }

    /**
     * @return the amount of asset bought/sold
     */
    public double getAmount() {
        return amount;
    }

    /**
     * @return trade ID
     */
    public long getId() {
        return id;
    }

    /**
     * @return trade price currency
     */
    public Currency getPriceCurrency() {
        return priceCurrency;
    }

    /**
     * @return trade item
     */
    public Currency getItem() {
        return item;
    }

    /**
     * @return trade type
     */
    public TradeType getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Trade trade = (Trade) o;
        return Objects.equals(id, trade.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Trade{" +
                "date=" + date +
                ", price=" + price +
                ", amount=" + amount +
                ", id=" + id +
                ", priceCurrency=" + priceCurrency +
                ", item=" + item +
                ", type=" + type +
                '}';
    }
}
