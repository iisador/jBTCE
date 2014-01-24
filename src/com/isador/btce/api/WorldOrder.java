package com.isador.btce.api;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.isador.btce.api.constants.Pair;

public class WorldOrder implements Serializable {
	private static final long serialVersionUID = 4910869980603495659L;
	private double amount;
	private Pair pair;
	private double price;
	private String type;

	public WorldOrder() {
	}

	public WorldOrder(double price, String type, double amount, Pair pair) {
		this.price = price;
		this.type = type;
		this.amount = amount;
		this.pair = pair;
	}

	public Map<String, Object> asMap() {
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("price", getPrice());
		m.put("type", getType().toString());
		m.put("amount", getAmount());
		m.put("pair", getPair().toString());
		return m;
	}

	public double getAmount() {
		return amount;
	}

	public Pair getPair() {
		return pair;
	}

	public double getPrice() {
		return price;
	}

	public String getType() {
		return type;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public void setPair(Pair pair) {
		this.pair = pair;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "WorldOrder [price=" + price + ", type=" + type + ", amount="
				+ amount + "]";
	}
}
