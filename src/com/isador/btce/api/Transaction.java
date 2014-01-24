package com.isador.btce.api;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonObject;
import com.isador.btce.api.constants.Currency;

public class Transaction implements Serializable {
	private static final long serialVersionUID = 7652725445175449590L;
	private double amount;
	private Currency currency;
	private String description;
	private int status;
	private long timestamp;
	private long transactionNumber;
	private int type;

	public Transaction() {
		super();
	}

	public Transaction(long transactionNumber, JsonObject transaction) {
		setTransactionNumber(transactionNumber);
		setAmount(transaction.get("amount").getAsDouble());
		setCurrency(Currency.valueOf(transaction.get("currency").getAsString()));
		setDescription(transaction.get("desc").getAsString());
		setStatus(transaction.get("status").getAsInt());
		setTimestamp(transaction.get("timestamp").getAsLong());
		setType(transaction.get("type").getAsInt());
	}

	public Map<String, Object> asMap() {
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("transactionNumber", transactionNumber);
		m.put("type", getType());
		m.put("amount", getAmount());
		m.put("currency", getCurrency().toString());
		m.put("status", getStatus());
		m.put("desc", getDescription());
		m.put("timestamp", getTimestamp());
		return m;
	}

	public double getAmount() {
		return amount;
	}

	public Currency getCurrency() {
		return currency;
	}

	public String getDescription() {
		return description;
	}

	public int getStatus() {
		return status;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public long getTransactionNumber() {
		return transactionNumber;
	}

	public int getType() {
		return type;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public void setTransactionNumber(long transactionNumber) {
		this.transactionNumber = transactionNumber;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "Transaction [transactionNumber=" + transactionNumber
				+ ", type=" + type + ", amount=" + amount + ", currency="
				+ currency + ", status=" + status + ", timestamp=" + timestamp
				+ ", description=" + description + "]";
	}

}
