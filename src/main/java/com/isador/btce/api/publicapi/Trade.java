package com.isador.btce.api.publicapi;

import com.google.gson.annotations.SerializedName;
import com.isador.btce.api.constants.Currency;
import com.isador.btce.api.constants.TradeType;

import java.time.LocalDateTime;
import java.util.Objects;

public final class Trade {

    @SerializedName("tid")
    private final long id;

    @SerializedName("price_currency")
    private final Currency priceCurrency;

    @SerializedName(value = "trade_type", alternate = "type")
    private final TradeType type;

    @SerializedName(value = "date", alternate = "timestamp")
    private final LocalDateTime date;

    private final double price;
    private final double amount;
    private final Currency item;

    Trade(LocalDateTime date, double price, double amount, long id, Currency priceCurrency, Currency item, TradeType type) {
        this.date = date;
        this.price = price;
        this.amount = amount;
        this.id = id;
        this.priceCurrency = priceCurrency;
        this.item = item;
        this.type = type;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public double getPrice() {
        return price;
    }

    public double getAmount() {
        return amount;
    }

    public long getId() {
        return id;
    }

    public Currency getPriceCurrency() {
        return priceCurrency;
    }

    public Currency getItem() {
        return item;
    }

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
