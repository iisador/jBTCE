package com.isador.btce.api;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonObject;

public class CancelOrderStatus implements Serializable {
	private static final long serialVersionUID = 6770446775025458795L;
	private Funds funds;
	private long orderId;

	public CancelOrderStatus() {
		super();
	}

	public CancelOrderStatus(JsonObject status) {
		this();
		setOrderId(status.get("order_id").getAsLong());
		setFunds(new Funds(status.get("funds").getAsJsonObject()));
	}

	public Map<String, Object> asMap() {
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("order_id", getOrderId());
		m.put("funds", getFunds().asMap());
		return m;
	}

	public Funds getFunds() {
		return funds;
	}

	public long getOrderId() {
		return orderId;
	}

	public void setFunds(Funds funds) {
		this.funds = funds;
	}

	public void setOrderId(long orderId) {
		this.orderId = orderId;
	}

	@Override
	public String toString() {
		return "CancelOrderStatus [orderId=" + orderId + ", funds=" + funds
				+ "]";
	}

}