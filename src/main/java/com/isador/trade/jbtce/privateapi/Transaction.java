package com.isador.trade.jbtce.privateapi;

import com.google.gson.annotations.SerializedName;
import com.isador.trade.jbtce.constants.Currency;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Transaction holder
 *
 * @author isador
 * @since 2.0.1
 */
public final class Transaction {

    @SerializedName("desc")
    private final String description;

    private final TransactionType type;
    private final double amount;
    private final Currency currency;
    private final TransactionStatus status;
    private final LocalDateTime timestamp;
    private final long id;

    public Transaction(TransactionType type, double amount, Currency currency, TransactionStatus status, LocalDateTime timestamp, long id, String description) {
        this.type = type;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
        this.timestamp = timestamp;
        this.id = id;
        this.description = description;
    }

    /**
     *
     * @return Transaction type
     */
    public TransactionType getType() {
        return type;
    }

    /**
     * @return Transaction amount
     */
    public double getAmount() {
        return amount;
    }

    /**
     * @return Transaction currency
     */
    public Currency getCurrency() {
        return currency;
    }

    /**
     * @return Transaction status
     */
    public TransactionStatus getStatus() {
        return status;
    }

    /**
     * @return Transaction time
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * @return Transaction ID
     */
    public long getId() {
        return id;
    }

    /**
     * @return Transaction description
     */
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
