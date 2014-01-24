package com.isador.btce.api;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonObject;
import com.isador.btce.api.constants.Currency;

public class Funds extends HashMap<Currency, Double> implements Serializable {

	private static final long serialVersionUID = 7136448479370634538L;

	public Funds() {
		super(Currency.values().length);
	}

	public Funds(JsonObject funds) {
		this();
		for (Currency c : Currency.values()) {
			put(c, funds.get(c.toString().toLowerCase()).getAsDouble());
		}
	}

	public Funds(Map<Currency, Double> m) {
		super(Currency.values().length);
		putAll(m);
	}

	public Map<String, Object> asMap() {
		Map<String, Object> m = new HashMap<String, Object>(this.size());
		for (Currency c : keySet()) {
			m.put(c.toString(), get(c));
		}
		return m;
	}

	public double getValue(Currency currency) {
		Double val = get(currency);
		return val == null ? 0.0 : val;
	}

	public void setValue(Currency currency, Double value) {
		put(currency, value);
	}
}
