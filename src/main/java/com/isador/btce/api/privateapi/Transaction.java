package com.isador.btce.api.privateapi;

import com.isador.btce.api.constants.Currency;

import java.time.LocalDateTime;
import java.util.Objects;

public class Transaction {

    private final int type;
    private final double amount;
    private final Currency currency;
    private final int status;
    private final LocalDateTime timestamp;
    private final long transactionId;
    private final String description;

    public Transaction(int type, double amount, Currency currency, int status, LocalDateTime timestamp, long transactionId, String description) {
        this.type = type;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
        this.timestamp = timestamp;
        this.transactionId = transactionId;
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

    public long getTransactionId() {
        return transactionId;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(transactionId, that.transactionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionId);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "type=" + type +
                ", amount=" + amount +
                ", currency=" + currency +
                ", status=" + status +
                ", timestamp=" + timestamp +
                ", transactionId=" + transactionId +
                ", description='" + description + '\'' +
                '}';
    }
}
