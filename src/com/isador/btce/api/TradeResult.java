package com.isador.btce.api;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonObject;

public class TradeResult implements Serializable {
	private static final long serialVersionUID = 9018895666227562803L;
	private Funds funds;
	private long orderId;
	private double received;
	private double remains;

	public TradeResult() {
		super();
	}

	public TradeResult(JsonObject trade) {
		setFunds(new Funds(trade.get("funds").getAsJsonObject()));
		setOrderId(trade.get("order_id").getAsLong());
		setReceived(trade.get("received").getAsDouble());
		setRemains(trade.get("remains").getAsDouble());
	}

	public Map<String, Object> asMap() {
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("received", getReceived());
		m.put("remains", getRemains());
		m.put("funds", getFunds().asMap());
		m.put("order_id", getOrderId());
		return m;
	}

	public Funds getFunds() {
		return funds;
	}

	public long getOrderId() {
		return orderId;
	}

	public double getReceived() {
		return received;
	}

	public double getRemains() {
		return remains;
	}

	public void setFunds(Funds funds) {
		this.funds = funds;
	}

	public void setOrderId(long orderId) {
		this.orderId = orderId;
	}

	public void setReceived(double received) {
		this.received = received;
	}

	public void setRemains(double remains) {
		this.remains = remains;
	}

	@Override
	public String toString() {
		return "TradeResult [funds=" + funds + ", orderId=" + orderId
				+ ", received=" + received + ", remains=" + remains + "]";
	}

}
