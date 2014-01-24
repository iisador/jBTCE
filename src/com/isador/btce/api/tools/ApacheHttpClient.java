package com.isador.btce.api.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.SystemDefaultHttpClient;

public class ApacheHttpClient extends HttpClient {
	private DefaultHttpClient httpClient = new SystemDefaultHttpClient();
	private URI uri;
	private HttpPost reqPost = new HttpPost() {
		{
			addHeader("useragent", "Isador BTC-E API V 0.0.1");
			addHeader("Content-Type", "application/x-www-form-urlencoded");
			addHeader("accept", "application/json");
		}
	};

	private HttpGet reqGet = new HttpGet() {
		{
			addHeader("useragent", "Isador BTC-E API V 0.0.1");
			addHeader("Content-Type", "application/x-www-form-urlencoded");
			addHeader("accept", "application/json");
		}
	};

	@Override
	public void setUrl(String url) {
		super.setUrl(url);
		if (url == null)
			uri = null;
		else
			try {
				uri = new URL(url).toURI();
			} catch (MalformedURLException | URISyntaxException e) {
				throw new RuntimeException(e);
			}

	}

	public String sendPost(Map<String, String> headerLines, String data)
			throws IOException {
		reqPost.reset();
		reqPost.setURI(uri);

		if (headerLines != null)
			for (String key : headerLines.keySet()) {
				reqPost.addHeader(key, headerLines.get(key));
			}

		if (data != null)
			reqPost.setEntity(new StringEntity(data, Charset.forName("UTF-8")));
		return sendRequest(reqPost);
	}

	public String sendGet(Map<String, String> headerLines, String data)
			throws IOException {
		reqGet.reset();
		reqGet.setURI(uri);

		if (headerLines != null)
			for (String key : headerLines.keySet()) {
				reqGet.addHeader(key, headerLines.get(key));
			}

		if (data != null)
			reqPost.setEntity(new StringEntity(data, Charset.forName("UTF-8")));
		return sendRequest(reqGet);
	}

	private String sendRequest(HttpUriRequest req) throws IOException {
		StringBuilder sb = new StringBuilder();

		HttpResponse resp = httpClient.execute(req);
		HttpEntity entity = resp.getEntity();

		try (BufferedReader br = new BufferedReader(new InputStreamReader(
				entity.getContent()))) {

			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
		}
		return sb.toString();
	}

	@Override
	public String send(String url, Map<String, String> headers, String data,
			String type) throws IOException {
		URI uri;
		try {
			uri = new URL(url).toURI();
		} catch (URISyntaxException e) {
			throw new RuntimeException("Wrong url format: " + url);
		}
		HttpUriRequest req = null;
		if (type.equalsIgnoreCase("get")) {
			reqGet.reset();
			reqGet.setURI(uri);
			req = reqGet;
		}
		if (type.equalsIgnoreCase("post")) {
			reqPost.reset();
			reqPost.setURI(uri);
			req = reqPost;
		}

		if (req == null)
			throw new IllegalArgumentException("Wrong http reqeust type: "
					+ type);

		if (headers != null)
			for (String key : headers.keySet()) {
				reqGet.addHeader(key, headers.get(key));
			}

		if (data != null)
			reqPost.setEntity(new StringEntity(data, Charset.forName("UTF-8")));
		return sendRequest(req);
	}
}
