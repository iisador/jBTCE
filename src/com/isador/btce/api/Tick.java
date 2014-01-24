package com.isador.btce.api;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonObject;
import com.isador.btce.api.constants.Pair;

public class Tick implements Serializable {
	private static final long serialVersionUID = 5789001941119065049L;
	private double avg;
	private double buy;
	private double high;
	private double last;
	private double low;
	private Pair pair;
	private double sell;
	private long serverTime;
	private long updated;
	private double vol;
	private double volCur;

	public Tick() {
		super();
	}

	public Tick(JsonObject ticker) {
		setHigh(ticker.get("high").getAsDouble());
		setLow(ticker.get("low").getAsDouble());
		setAvg(ticker.get("avg").getAsDouble());
		setVolCur(ticker.get("vol_cur").getAsDouble());
		setLast(ticker.get("last").getAsDouble());
		setBuy(ticker.get("buy").getAsDouble());
		setSell(ticker.get("sell").getAsDouble());
		setServerTime(ticker.get("server_time").getAsLong() * 1000L);
		setUpdated(ticker.get("updated").getAsLong() * 1000L);
		setVol(ticker.get("vol").getAsDouble());
	}

	public Map<String, Object> asMap() {
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("high", getHigh());
		m.put("low", getLow());
		m.put("avg", getAvg());
		m.put("vol_cur", getVolCur());
		m.put("last", getLast());
		m.put("buy", getBuy());
		m.put("sell", getSell());
		m.put("server_time", getServerTime());
		m.put("updated", getUpdated());
		m.put("vol", getVol());
		m.put("pair", getPair());
		return m;
	}

	public double getAvg() {
		return avg;
	}

	public double getBuy() {
		return buy;
	}

	public double getHigh() {
		return high;
	}

	public double getLast() {
		return last;
	}

	public double getLow() {
		return low;
	}

	public Pair getPair() {
		return pair;
	}

	public double getSell() {
		return sell;
	}

	public long getServerTime() {
		return serverTime;
	}

	public long getUpdated() {
		return updated;
	}

	public double getVol() {
		return vol;
	}

	public double getVolCur() {
		return volCur;
	}

	public void setAvg(double avg) {
		this.avg = avg;
	}

	public void setBuy(double buy) {
		this.buy = buy;
	}

	public void setHigh(double high) {
		this.high = high;
	}

	public void setLast(double last) {
		this.last = last;
	}

	public void setLow(double low) {
		this.low = low;
	}

	public void setPair(Pair pair) {
		this.pair = pair;
	}

	public void setSell(double sell) {
		this.sell = sell;
	}

	public void setServerTime(long serverTime) {
		this.serverTime = serverTime;
	}

	public void setUpdated(long updated) {
		this.updated = updated;
	}

	public void setVol(double vol) {
		this.vol = vol;
	}

	public void setVolCur(double volCur) {
		this.volCur = volCur;
	}

	@Override
	public String toString() {
		return "Tick [avg=" + avg + ", buy=" + buy + ", high=" + high
				+ ", last=" + last + ", low=" + low + ", pair=" + pair
				+ ", sell=" + sell + ", serverTime=" + serverTime
				+ ", updated=" + updated + ", vol=" + vol + ", volCur="
				+ volCur + "]";
	}
}
