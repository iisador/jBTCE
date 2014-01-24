package com.isador.btce.api;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonObject;
import com.isador.btce.api.constants.Pair;
import com.isador.btce.api.constants.TradeType;

public class Order implements Serializable {
	private static final long serialVersionUID = 2713429332764386513L;
	private double amount;
	private long orderNumber;
	private Pair pair;
	private double rate;
	private int status;
	private long timestampCreated;
	private TradeType type;

	public Order() {
		super();
	}

	public Order(long orderNumber, JsonObject order) {
		setOrderNumber(orderNumber);
		setPair(Pair.valueOf(order.get("pair").getAsString().toUpperCase()));
		setType(TradeType
				.valueOf(order.get("type").getAsString().toUpperCase()));
		setAmount(order.get("amount").getAsDouble());
		setRate(order.get("rate").getAsDouble());
		setTimestampCreated(order.get("timestamp_created").getAsLong() * 1000L);
		setStatus(order.get("status").getAsInt());
	}

	public Map<String, Object> asMap() {
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("orderNumber", orderNumber);
		m.put("pair", getPair().toString());
		m.put("type", getType().toString());
		m.put("amount", getAmount());
		m.put("rate", getRate());
		m.put("status", getStatus());
		m.put("timestamp_created", getTimestampCreated());
		return m;
	}

	public double getAmount() {
		return amount;
	}

	public long getOrderNumber() {
		return orderNumber;
	}

	public Pair getPair() {
		return pair;
	}

	public double getRate() {
		return rate;
	}

	public int getStatus() {
		return status;
	}

	public long getTimestampCreated() {
		return timestampCreated;
	}

	public TradeType getType() {
		return type;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public void setOrderNumber(long orderNumber) {
		this.orderNumber = orderNumber;
	}

	public void setPair(Pair pair) {
		this.pair = pair;
	}

	public void setRate(double rate) {
		this.rate = rate;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public void setTimestampCreated(long timestampCreated) {
		this.timestampCreated = timestampCreated;
	}

	public void setType(TradeType type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "Order [orderNumber=" + orderNumber + ", pair=" + pair
				+ ", type=" + type + ", amount=" + amount + ", rate=" + rate
				+ ", timestampCreated=" + timestampCreated + ", status="
				+ status + "]";
	}
}
