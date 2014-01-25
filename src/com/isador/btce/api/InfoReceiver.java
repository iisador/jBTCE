package com.isador.btce.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.isador.btce.api.constants.Pair;
import com.isador.btce.api.constants.TradeType;
import com.isador.btce.api.tools.HttpClient;
import com.isador.btce.api.tools.HttpClient.ClientType;

/**
 * @author Isador Class to receive pair depth, trades, orders and fee
 */
public class InfoReceiver {
    private HttpClient httpClient;

    private JsonParser p = new JsonParser();
    private Pair pair;
    private String urlDepth = "https://btc-e.com/api/2/%s/depth";
    private String urlTrades = "https://btc-e.com/api/2/%s/trades";
    private String urlTicks = "https://btc-e.com/api/2/%s/ticker";
    private String urlFee = "https://btc-e.com/api/2/%s/fee";

    public InfoReceiver(Pair pair, ClientType httpClient) {
	urlTrades = String.format(urlTrades, pair.toString().toLowerCase());
	urlDepth = String.format(urlDepth, pair.toString().toLowerCase());
	this.pair = pair;

	this.httpClient = HttpClient.getClient(httpClient);
	if (this.httpClient == null)
	    throw new RuntimeException("Wrong http client");
    }

    public List<WorldOrder> getAskOrders() throws IOException {
	List<WorldOrder> l = new ArrayList<WorldOrder>();
	String url = String.format(urlDepth, pair.toString().toLowerCase());
	String data = httpClient.send(url, null, null, "GET");
	JsonArray e = p.parse(data).getAsJsonObject().get("asks")
		.getAsJsonArray();
	Iterator<JsonElement> it = e.iterator();
	while (it.hasNext()) {
	    JsonArray order = it.next().getAsJsonArray();
	    l.add(new WorldOrder(order.get(0).getAsDouble(), TradeType.ASK
		    .toString(), order.get(1).getAsDouble(), pair));
	}
	return l;
    }

    public List<WorldOrder> getBidOrders() throws IOException {
	List<WorldOrder> l = new ArrayList<WorldOrder>();
	String url = String.format(urlDepth, pair.toString().toLowerCase());
	String data = httpClient.send(url, null, null, "GET");
	JsonArray e = p.parse(data).getAsJsonObject().get("bids")
		.getAsJsonArray();
	Iterator<JsonElement> it = e.iterator();
	while (it.hasNext()) {
	    JsonArray order = it.next().getAsJsonArray();
	    l.add(new WorldOrder(order.get(0).getAsDouble(), TradeType.BID
		    .toString(), order.get(1).getAsDouble(), pair));
	}
	return l;
    }

    /**
     * Returns pair fee.
     * 
     * @param pair
     *            trade pair
     * @return
     * @throws IOException
     */
    public double getFee(Pair pair) throws IOException {
	double fee = -1.0;
	if (pair == null)
	    return fee;

	String url = String.format(urlFee, pair.toString().toLowerCase());
	String serverAnswer = httpClient.send(url, null, null, "POST");
	if (serverAnswer != null) {
	    try {
		JsonObject jo = p.parse(serverAnswer).getAsJsonObject();
		fee = jo.get("trade").getAsDouble();
	    } catch (JsonParseException e) {
		;
	    }
	}
	return fee;
    }

    public Tick getTick() throws IOException {
	String url = String.format(urlTicks, pair.toString().toLowerCase());
	String data = httpClient.send(url, null, null, "GET");
	Tick t = new Tick(p.parse(data).getAsJsonObject().get("ticker")
		.getAsJsonObject());
	t.setPair(pair);
	return t;
    }

    public List<WorldTrade> getTrades() throws IOException {
	List<WorldTrade> l = new ArrayList<WorldTrade>();
	String url = String.format(urlTrades, pair.toString().toLowerCase());
	String data = httpClient.send(url, null, null, "GET");
	JsonArray e = p.parse(data).getAsJsonArray();
	Iterator<JsonElement> it = e.iterator();
	while (it.hasNext()) {
	    l.add(new WorldTrade(it.next().getAsJsonObject()));
	}
	return l;
    }
}
