package com.isador.btce.api.tools;

import java.io.IOException;
import java.util.Map;

public abstract class HttpClient {
	protected String url;

	public void setUrl(String url) {
		this.url = url;
	}

	public abstract String sendPost(Map<String, String> headers, String data)
			throws IOException;

	public abstract String sendGet(Map<String, String> headers, String data)
			throws IOException;

	public abstract String send(String url, Map<String, String> headers,
			String data, String type) throws IOException;

	public static HttpClient getClient(String clientType) {
		HttpClient client = null;
		if (clientType.equalsIgnoreCase("apache"))
			client = new ApacheHttpClient();
		else if (clientType.equalsIgnoreCase("java"))
			client = new JavaHttpsClient();
		return client;
	}
}
