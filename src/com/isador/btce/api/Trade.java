package com.isador.btce.api;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonObject;
import com.isador.btce.api.constants.Pair;
import com.isador.btce.api.constants.TradeType;

public class Trade implements Serializable {
	private static final long serialVersionUID = 1708993231126558690L;
	private double amount;
	private long orderId;
	private Pair pair;
	private double rate;
	private long timestamp;
	private long tradeNumber;
	private TradeType type;
	private boolean yourOrder;

	public Trade() {
		super();
	}

	public Trade(long tradeNumber, JsonObject trade) {
		setTradeNumber(tradeNumber);
		setPair(Pair.valueOf(trade.get("pair").getAsString().toUpperCase()));
		setType(TradeType
				.valueOf(trade.get("type").getAsString().toUpperCase()));
		setAmount(trade.get("amount").getAsDouble());
		setRate(trade.get("rate").getAsDouble());
		setOrderId(trade.get("order_id").getAsLong());
		setYourOrder(trade.get("is_your_order").getAsInt() == 0);
		setTimestamp(trade.get("timestamp").getAsLong());
	}

	public Map<String, Object> asMap() {
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("tradeNumber", tradeNumber);
		m.put("pair", getPair().toString());
		m.put("type", getType().toString());
		m.put("amount", getAmount());
		m.put("rate", getRate());
		m.put("order_id", getOrderId());
		m.put("is_your_order", isYourOrder());
		m.put("timestamp", getTimestamp());
		return m;
	}

	public double getAmount() {
		return amount;
	}

	public long getOrderId() {
		return orderId;
	}

	public Pair getPair() {
		return pair;
	}

	public double getRate() {
		return rate;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public long getTradeNumber() {
		return tradeNumber;
	}

	public TradeType getType() {
		return type;
	}

	public boolean isYourOrder() {
		return yourOrder;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public void setOrderId(long orderId) {
		this.orderId = orderId;
	}

	public void setPair(Pair pair) {
		this.pair = pair;
	}

	public void setRate(double rate) {
		this.rate = rate;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public void setTradeNumber(long tradeNumber) {
		this.tradeNumber = tradeNumber;
	}

	public void setType(TradeType type) {
		this.type = type;
	}

	public void setYourOrder(boolean yourOrder) {
		this.yourOrder = yourOrder;
	}

	@Override
	public String toString() {
		return "Trade [tradeNumber=" + tradeNumber + ", pair=" + pair
				+ ", type=" + type + ", amount=" + amount + ", rate=" + rate
				+ ", orderId=" + orderId + ", timestamp=" + timestamp
				+ ", yourOrder=" + yourOrder + "]";
	}
}
