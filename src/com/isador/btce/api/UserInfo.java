package com.isador.btce.api;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonObject;

public class UserInfo implements Serializable {
	private static final long serialVersionUID = 5612074346509175176L;

	public class Rights implements Serializable {
		private static final long serialVersionUID = -5711305460672771812L;
		private int info;
		private int trade;
		private int withdraw;

		public Rights(JsonObject rights) {
			setInfo(rights.get("info").getAsInt());
			setTrade(rights.get("trade").getAsInt());
			setWithdraw(rights.get("withdraw").getAsInt());
		}

		public Map<String, Object> asMap() {
			Map<String, Object> m = new HashMap<String, Object>();
			m.put("info", getInfo());
			m.put("trade", getTrade());
			m.put("withdraw", getWithdraw());
			return m;
		}

		public int getInfo() {
			return info;
		}

		public int getTrade() {
			return trade;
		}

		public int getWithdraw() {
			return withdraw;
		}

		public boolean isInfo() {
			return getInfo() == 1;
		}

		public boolean isTrade() {
			return getTrade() == 1;
		}

		public boolean isWithdraw() {
			return getWithdraw() == 1;
		}

		public void setInfo(int info) {
			this.info = info;
		}

		public void setTrade(int trade) {
			this.trade = trade;
		}

		public void setWithdraw(int withdraw) {
			this.withdraw = withdraw;
		}

		@Override
		public String toString() {
			return "Rights [info=" + info + ", trade=" + trade + ", withdraw="
					+ withdraw + "]";
		}
	}

	private Funds funds;
	private int openOrdersCount;
	private Rights rights;
	private long serverTime;
	private int transactionCount;

	public UserInfo(JsonObject result) {
		setFunds(new Funds(result.get("funds").getAsJsonObject()));
		setOpenOrdersCount(result.get("open_orders").getAsInt());
		setServerTime(result.get("server_time").getAsLong());
		setTransactionCount(result.get("transaction_count").getAsInt());
		setRights(new Rights(result.get("rights").getAsJsonObject()));
	}

	public Map<String, Object> asMap() {
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("funds", getFunds().asMap());
		m.put("open_orders", getOpenOrdersCount());
		m.put("server_time", getServerTime());
		m.put("transaction_count", getTransactionCount());
		m.put("rights", getRights().asMap());
		return m;
	}

	public Funds getFunds() {
		return funds;
	}

	public int getOpenOrdersCount() {
		return openOrdersCount;
	}

	public Rights getRights() {
		return rights;
	}

	public long getServerTime() {
		return serverTime;
	}

	public int getTransactionCount() {
		return transactionCount;
	}

	public void setFunds(Funds funds) {
		this.funds = funds;
	}

	public void setOpenOrdersCount(int openOrdersCount) {
		this.openOrdersCount = openOrdersCount;
	}

	public void setRights(Rights rights) {
		this.rights = rights;
	}

	public void setServerTime(long serverTime) {
		this.serverTime = serverTime;
	}

	public void setTransactionCount(int transactionCount) {
		this.transactionCount = transactionCount;
	}

	@Override
	public String toString() {
		return "UserInfo [funds=" + funds + ", openOrdersCount="
				+ openOrdersCount + ", serverTime=" + serverTime
				+ ", transactionCount=" + transactionCount + ", rights="
				+ rights + "]";
	}
}
