package com.isador.trade.jbtce.privateapi;

import com.google.gson.annotations.SerializedName;
import com.isador.trade.jbtce.constants.Currency;

import java.time.LocalDateTime;
import java.util.Objects;

public final class Transaction {

    @SerializedName("desc")
    private final String description;

    private final int type;
    private final double amount;
    private final Currency currency;
    private final int status;
    private final LocalDateTime timestamp;
    private final long id;

    Transaction(int type, double amount, Currency currency, int status, LocalDateTime timestamp, long id, String description) {
        this.type = type;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
        this.timestamp = timestamp;
        this.id = id;
        this.description = description;
    }

    public int getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public int getStatus() {
        return status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Transaction that = (Transaction) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "type=" + type +
                ", amount=" + amount +
                ", currency=" + currency +
                ", status=" + status +
                ", timestamp=" + timestamp +
                ", id=" + id +
                ", description='" + description + '\'' +
                '}';
    }
}
