package com.isador.btce.api;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonObject;
import com.isador.btce.api.constants.Currency;
import com.isador.btce.api.constants.Pair;
import com.isador.btce.api.constants.TradeType;

public class WorldTrade implements Serializable {
	private static final long serialVersionUID = -3008879935991330250L;
	private double amount;
	private long date;
	private Currency item;
	private Pair pair;
	private double price;
	private Currency priceCurrency;
	private long tid;
	private TradeType type;

	public WorldTrade(JsonObject trade) {
		setDate(trade.get("date").getAsLong() * 1000L);
		setPrice(trade.get("price").getAsDouble());
		setAmount(trade.get("amount").getAsDouble());
		setType(TradeType.valueOf(trade.get("trade_type").getAsString()
				.toUpperCase()));
		setPriceCurrency(Currency.valueOf(trade.get("price_currency")
				.getAsString().toUpperCase()));
		setItem(Currency.valueOf(trade.get("item").getAsString().toUpperCase()));
		setTid(trade.get("tid").getAsLong());
		setPair(Pair.valueOf(new StringBuilder(getItem().toString())
				.append("_").append(getPriceCurrency()).toString()));
	}

	public Map<String, Object> asMap() {
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("date", getDate());
		m.put("price", getPrice());
		m.put("amount", getAmount());
		m.put("tid", getTid());
		m.put("price_currency", getPriceCurrency().toString());
		m.put("item", getItem().toString());
		m.put("trade_type", getType().toString());
		m.put("pair", getPair().toString());
		return m;
	}

	public double getAmount() {
		return amount;
	}

	public long getDate() {
		return date;
	}

	public Currency getItem() {
		return item;
	}

	public Pair getPair() {
		return pair;
	}

	public double getPrice() {
		return price;
	}

	public Currency getPriceCurrency() {
		return priceCurrency;
	}

	public long getTid() {
		return tid;
	}

	public TradeType getType() {
		return type;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public void setItem(Currency item) {
		this.item = item;
	}

	public void setPair(Pair pair) {
		this.pair = pair;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public void setPriceCurrency(Currency priceCurrency) {
		this.priceCurrency = priceCurrency;
	}

	public void setTid(long tid) {
		this.tid = tid;
	}

	public void setType(TradeType type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "WorldTrade [amount=" + amount + ", date=" + date + ", item="
				+ item + ", pair=" + pair + ", price=" + price
				+ ", priceCurrency=" + priceCurrency + ", tid=" + tid
				+ ", type=" + type + "]";
	}

}
