package com.isador.btce.api;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.isador.btce.api.constants.Pair;
import com.isador.btce.api.constants.Sort;
import com.isador.btce.api.constants.TradeType;
import com.isador.btce.api.tools.HexEncoder;
import com.isador.btce.api.tools.HttpClient;
import com.isador.btce.api.tools.Nonce;

/**
 * @author Isador BTC-E API main class. Generates mac key when instance created.
 */
public class BtcApi {
	public static final String FEE_URL = "https://btc-e.com/api/2/%s/fee";
	// Mac key
	private static Mac mac = null;

	// Api key
	private String key;

	// Json parser to parse incoming messages
	private JsonParser p = new JsonParser();

	// Http client to send\receive messages
	private HttpClient httpClient;

	/**
	 * Create new btce api instance. Generates new mac and new http client
	 * instance.
	 * 
	 * @param key
	 *            api key
	 * @param secret
	 *            api secret key
	 * @param httpClient
	 *            http client type
	 * @throws NullPointerException
	 *             when key or secret is null
	 * @throws InvalidKeyException
	 *             when secret is invalid
	 * @throws RuntimeException
	 *             when http client type is incorrect
	 */
	public BtcApi(String key, String secret, String httpClient)
			throws InvalidKeyException {
		if (key == null || key.isEmpty())
			throw new NullPointerException("Key is null or empty");

		if (secret == null || secret.isEmpty())
			throw new NullPointerException("Secret is null or empty");

		this.key = key;
		String encrAlgo = "HmacSHA512";
		try {
			mac = Mac.getInstance(encrAlgo);
		} catch (NoSuchAlgorithmException e) {
			// Should never happen
			e.printStackTrace();
		}
		mac.init(new SecretKeySpec(secret.getBytes(Charset.forName("UTF-8")),
				encrAlgo));

		this.httpClient = HttpClient.getClient(httpClient);
		if (this.httpClient == null)
			throw new RuntimeException("Wrong http client");
		this.httpClient.setUrl("https://btc-e.com/tapi");
	}

	/**
	 * Private method to send request and receive server answer.
	 * 
	 * @param method
	 * @param parameters
	 * @return
	 * @throws IOException
	 */
	private JsonObject apiCall(String method, Map<String, String> parameters)
			throws IOException {
		JsonObject result = sendSigned(method, parameters);
		int success = result.get("success").getAsInt();
		if (success != 0)
			return result.getAsJsonObject("return").getAsJsonObject();
		else
			return result.get("error").getAsJsonObject();
	}

	/**
	 * Returns your open orders.
	 * 
	 * @param pair
	 *            Trade pair or null
	 * @return orders array
	 * @throws IOException
	 */
	public Order[] getActiveOrders(Pair pair) throws IOException {
		Map<String, String> params = new HashMap<String, String>();
		if (pair != null)
			params.put("pair", pair.toString().toLowerCase());

		Order[] orders = new Order[0];
		JsonObject result = apiCall("ActiveOrders", params);
		if (result != null) {
			Set<Entry<String, JsonElement>> set = result.entrySet();
			Iterator<Entry<String, JsonElement>> it = set.iterator();
			List<Order> l = new ArrayList<Order>();
			while (it.hasNext()) {
				Entry<String, JsonElement> entry = it.next();
				l.add(new Order(Long.parseLong(entry.getKey()), entry
						.getValue().getAsJsonObject()));
			}
			orders = l.toArray(orders);
		}
		return orders;
	}

	/**
	 * Cancel opened order.
	 * 
	 * @param orderId
	 *            should be >= 0 or -1
	 * @return CancelOrderStatus
	 * @throws IllegalArgumentException
	 *             when orderId is invalid
	 * @throws IOException
	 *             on any errors with server communication
	 */
	public CancelOrderStatus cancelOrder(long orderId) throws IOException {
		if (orderId != -1 && orderId <= 0)
			throw new IllegalArgumentException("Wrong order id value: "
					+ orderId);

		Map<String, String> params = new HashMap<String, String>();
		if (orderId != -1)
			params.put("order_id", String.valueOf(orderId));

		JsonObject result = apiCall("CancelOrder", params);
		if (result != null)
			return new CancelOrderStatus(result);
		return null;
	}

	/**
	 * It returns your open orders/the orders history. Calls
	 * {@code getOrderList(-1, -1, -1, -1, null, -1, -1, null, -1)}
	 * 
	 * @return last account orders
	 * @throws IOException
	 *             on any errors with server communication
	 */
	@Deprecated
	public Order[] getOrderList() throws IOException {
		return getOrderList(-1, -1, -1, -1, null, -1, -1, null, -1);
	}

	/**
	 * It returns your open orders/the orders history. <br>
	 * <i>Note: while using since or end parameters, order parameter
	 * automatically takes up ASC value.</i>
	 * 
	 * @deprecated use getActiveOrders
	 * 
	 * @param from
	 *            the number of the order to start displaying with
	 * @param count
	 *            The number of orders for displaying
	 * @param fromId
	 *            id of the order to start displaying with
	 * @param endId
	 *            id of the order to finish displaying
	 * @param order
	 *            sorting
	 * @param since
	 *            when to start displaying
	 * @param end
	 *            when to finish displaying
	 * @param pair
	 *            the pair to display the orders
	 * @param active
	 *            is it displaying of active orders only?
	 * @return orders array
	 * @throws IOException
	 */
	@Deprecated
	public Order[] getOrderList(long from, int count, long fromId, long endId,
			Sort order, long since, long end, Pair pair, int active)
			throws IOException {
		Map<String, String> params = new HashMap<String, String>();
		if (from != -1)
			params.put("from", String.valueOf(from));
		if (count != -1)
			params.put("count", String.valueOf(count));
		if (fromId != -1)
			params.put("from_id", String.valueOf(fromId));
		if (endId != -1)
			params.put("end_id", String.valueOf(endId));
		if (order != null)
			params.put("order", order.toString());
		if (since != -1)
			params.put("since", String.valueOf(since));
		if (end != -1)
			params.put("end", String.valueOf(end));
		if (pair != null)
			params.put("pair", pair.toString().toLowerCase());
		if (active != -1)
			if (active > 1)
				params.put("active", "1");
			else
				params.put("active", "0");

		Order[] orders = new Order[0];
		JsonObject result = apiCall("OrderList", params);
		if (result != null) {
			Set<Entry<String, JsonElement>> set = result.entrySet();
			Iterator<Entry<String, JsonElement>> it = set.iterator();
			List<Order> l = new ArrayList<Order>();
			while (it.hasNext()) {
				Entry<String, JsonElement> entry = it.next();
				l.add(new Order(Long.parseLong(entry.getKey()), entry
						.getValue().getAsJsonObject()));
			}
			orders = l.toArray(orders);
		}
		return orders;
	}

	/**
	 * It returns the trade history. Calls
	 * {@code getTradeHistory(-1, -1, -1, -1, null, -1, -1, null)}
	 * 
	 * @return trade array
	 * @throws IOException
	 */
	public Trade[] getTradeHistory() throws IOException {
		return getTradeHistory(-1, -1, -1, -1, null, -1, -1, null);
	}

	/**
	 * Returns your last trade. Calls
	 * {@code getTradeHistory(-1, 1, -1, -1, null, -1, -1, pair)}
	 * 
	 * @param pair
	 *            pair of lsat trade
	 * @return Trade instance
	 * @throws IOException
	 */
	public Trade getLastTrade(Pair pair) throws IOException {
		Trade[] list = getTradeHistory(-1, 1, -1, -1, null, -1, -1, pair);
		return (list.length > 0) ? list[0] : null;
	}

	/**
	 * It returns the trade history. <br>
	 * <i>Note: while using since or end parameters, order parameter
	 * automatically takes up ASC value.</i>
	 * 
	 * @param from
	 *            the number of the transaction to start displaying with
	 * @param count
	 *            the number of transactions for displaying
	 * @param fromId
	 *            the ID of the transaction to start displaying with
	 * @param endId
	 *            the ID of the transaction to finish displaying with
	 * @param order
	 *            sorting
	 * @param since
	 *            when to start the displaying
	 * @param end
	 *            when to finish the displaying
	 * @param pair
	 *            the pair to show the transactions
	 * @return trades array
	 * @throws IOException
	 */
	public Trade[] getTradeHistory(long from, int count, long fromId,
			long endId, Sort order, long since, long end, Pair pair)
			throws IOException {
		Map<String, String> params = new HashMap<String, String>();
		if (from != -1)
			params.put("from", String.valueOf(from));
		if (count != -1)
			params.put("count", String.valueOf(count));
		if (fromId != -1)
			params.put("from_id", String.valueOf(fromId));
		if (endId != -1)
			params.put("end_id", String.valueOf(endId));
		if (order != null)
			params.put("order", order.toString());
		if (since != -1)
			params.put("since", String.valueOf(since));
		if (end != -1)
			params.put("end", String.valueOf(end));
		if (pair != null)
			params.put("pair", pair.toString().toLowerCase());

		Trade[] trades = new Trade[0];
		JsonObject result = apiCall("TradeHistory", params);
		if (result != null) {
			Set<Entry<String, JsonElement>> set = result.entrySet();
			Iterator<Entry<String, JsonElement>> it = set.iterator();
			List<Trade> l = new ArrayList<Trade>();
			while (it.hasNext()) {
				Entry<String, JsonElement> entry = it.next();
				l.add(new Trade(Long.parseLong(entry.getKey()), entry
						.getValue().getAsJsonObject()));
			}
			trades = l.toArray(trades);
		}
		return trades;
	}

	/**
	 * It returns the transactions history. Calls
	 * {@code getTransHistrory(-1, -1, -1, -1, null, -1, -1)}
	 * 
	 * @return transaction array
	 * @throws IOException
	 */
	public Transaction[] getTransactionHistory() throws IOException {
		return getTransHistrory(-1, -1, -1, -1, null, -1, -1);
	}

	/**
	 * Returns your last transaction. Calls
	 * {@code getTransHistrory(-1, 1, -1, -1, null, -1, -1)}.
	 * 
	 * @return transaction instance
	 * @throws IOException
	 */
	public Transaction getLastTransaction() throws IOException {
		Transaction[] list = getTransHistrory(-1, 1, -1, -1, null, -1, -1);
		return (list.length > 0) ? list[0] : null;
	}

	/**
	 * It returns the transactions history. <br>
	 * <i>Note: while using since or end parameters, the order parameter
	 * automatically take up ASC value.</i>
	 * 
	 * @param from
	 *            The ID of the transaction to start displaying with
	 * @param count
	 *            The number of transactions for displaying
	 * @param fromId
	 *            The ID of the transaction to start displaying with
	 * @param endId
	 *            The ID of the transaction to finish displaying with
	 * @param order
	 *            sorting
	 * @param since
	 *            When to start displaying?
	 * @param end
	 *            When to finish displaying?
	 * @return
	 * @throws IOException
	 */
	public Transaction[] getTransHistrory(long from, int count, long fromId,
			long endId, Sort order, long since, long end) throws IOException {
		Map<String, String> params = new HashMap<String, String>();
		if (from != -1)
			params.put("from", String.valueOf(from));
		if (count != -1)
			params.put("count", String.valueOf(count));
		if (fromId != -1)
			params.put("from_id", String.valueOf(fromId));
		if (endId != -1)
			params.put("end_id", String.valueOf(endId));
		if (order != null)
			params.put("order", order.toString());
		if (since != -1)
			params.put("since", String.valueOf(since));
		if (end != -1)
			params.put("end", String.valueOf(end));

		Transaction[] transactions = new Transaction[0];
		JsonObject result = apiCall("TransHistory", params);
		if (result != null) {
			Set<Entry<String, JsonElement>> set = result.entrySet();
			Iterator<Entry<String, JsonElement>> it = set.iterator();
			List<Transaction> l = new ArrayList<Transaction>();
			while (it.hasNext()) {
				Entry<String, JsonElement> entry = it.next();
				l.add(new Transaction(Long.parseLong(entry.getKey()), entry
						.getValue().getAsJsonObject()));
			}
			transactions = l.toArray(transactions);
		}
		return transactions;
	}

	/**
	 * It returns the information about the user's current balance, API key
	 * privileges,the number of transactions, the number of open orders and the
	 * server time.
	 * 
	 * @return user info instance
	 * @throws IOException
	 */
	public UserInfo getUserInfo() throws IOException {
		JsonObject result = apiCall("getInfo", null);
		return result != null ? new UserInfo(result) : null;
	}

	/**
	 * Sends signed post request to server.
	 * 
	 * @param method
	 *            api method
	 * @param parameters
	 *            parameters map
	 * @return json object
	 * @throws IOException
	 */
	public JsonObject sendSigned(String method, Map<String, String> parameters)
			throws IOException {
		// Header lines map
		HashMap<String, String> headerLines = new HashMap<String, String>();
		headerLines.put("Key", key);

		// Data to be sent
		StringBuilder postData = new StringBuilder();

		// Adding default parameters
		parameters = (parameters == null) ? new HashMap<String, String>()
				: parameters;

		parameters.put("nonce", "" + Nonce.get());
		parameters.put("method", method);

		// write parameters to post data
		for (String key : parameters.keySet()) {
			postData.append(key).append("=").append(parameters.get(key))
					.append("&");
		}
		postData.deleteCharAt(postData.length() - 1);

		// update mac and put it to header lines
		mac.update(postData.toString().getBytes(Charset.forName("UTF-8")));
		headerLines
				.put("Sign", new String(HexEncoder.encodeHex(mac.doFinal())));

		// send request
		String serverAnswer = httpClient.sendPost(headerLines,
				postData.toString());
		// if server answer not null
		if (serverAnswer != null) {
			JsonObject jsonAnswer = null;
			// try to create new json object from server answer
			try {
				jsonAnswer = p.parse(serverAnswer).getAsJsonObject();
			} catch (JsonSyntaxException e) {
				System.err.println("Bad json syntax: " + serverAnswer);
			}
			return jsonAnswer;
		} else
			System.err.println("Communication error.");
		return null;
	}

	/**
	 * Trading is done according to this method.
	 * 
	 * @param pair
	 *            Trade pair
	 * @param type
	 *            The transaction type
	 * @param rate
	 *            The rate to buy/sell
	 * @param amount
	 *            The amount which is necessary to buy/sell
	 * @return trade result instance
	 * @throws IOException
	 * @throws IllegalArgumentException
	 *             when any of parameters is invalid
	 */
	public TradeResult trade(Pair pair, TradeType type, Double rate,
			Double amount) throws IOException {
		if (pair == null)
			throw new IllegalArgumentException("Pair is null");
		if (type == null)
			throw new IllegalArgumentException("Type is null");
		if (rate != -1 && rate <= 0)
			throw new IllegalArgumentException("Rate value is illegal");
		if (amount != -1 && amount <= 0)
			throw new IllegalArgumentException("Amount value is illegal");

		Map<String, String> params = new HashMap<String, String>();
		params.put("pair", pair.toString().toLowerCase());
		params.put("type", type.toString().toLowerCase());
		params.put("rate", String.valueOf(rate));
		params.put("amount", String.format("%.7f", amount).replace(',', '.'));

		JsonObject result = apiCall("Trade", params);
		return (result != null) ? new TradeResult(result) : null;
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

		String url = String.format(FEE_URL, pair.toString().toLowerCase());
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

	public static void main(String[] args) {
		try {
			Pair p = Pair.LTC_USD;
			BtcApi ba = new BtcApi(
					"V2Y98IIQ-FAIW701D-AR4OU5NW-06HDLD5T-OIU6B633",
					"3872afabab005dbab13a14dd093573cb06390972c96962f7a2bf43e0a891785b",
					"java");
			System.out.println(p + " fee: " + ba.getFee(p));
			System.out.println(Arrays.toString(ba.getActiveOrders(p)));
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
