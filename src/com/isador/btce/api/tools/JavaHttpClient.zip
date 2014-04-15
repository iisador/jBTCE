package com.isador.btce.api.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class JavaHttpsClient extends HttpClient {

	@Override
	public String sendPost(Map<String, String> headers, String data)
			throws IOException {
		return send(url, headers, data, "POST");
	}

	@Override
	public String sendGet(Map<String, String> headers, String data)
			throws IOException {
		return send(url, headers, data, "GET");
	}

	public String send(String url, Map<String, String> headers, String data,
			String method) throws IOException {
		StringBuilder sb = new StringBuilder();
		URL u = new URL(url);
		HttpsURLConnection uc = (HttpsURLConnection) u.openConnection();
		uc.setRequestMethod(method);
		uc.setInstanceFollowRedirects(true);
		uc.setDoOutput(true);
		if (headers != null)
			for (String key : headers.keySet()) {
				uc.addRequestProperty(key, headers.get(key));
			}

		if (data != null) {
			try (PrintWriter out = new PrintWriter(uc.getOutputStream())) {
				out.write(data);
				out.flush();
			}
		}

		try (BufferedReader in = new BufferedReader(new InputStreamReader(
				uc.getInputStream()))) {
			String line;
			while ((line = in.readLine()) != null) {
				sb.append(line);
			}
		}
		return sb.toString();
	}

	public String send(String url, Map<String, String> headers, String data,
			String method, long timeout) throws IOException {
		StringBuilder sb = new StringBuilder();
		URL u = new URL(url);
		HttpsURLConnection uc = (HttpsURLConnection) u.openConnection();
		uc.setRequestMethod(method);
		uc.setDoOutput(true);
		uc.setInstanceFollowRedirects(true);
		if (headers != null)
			for (String key : headers.keySet()) {
				uc.addRequestProperty(key, headers.get(key));
			}

		if (data != null) {
			try (PrintWriter out = new PrintWriter(uc.getOutputStream())) {
				out.write(data);
				out.flush();
			}
		}

		try {
			Thread.sleep(timeout);
		} catch (InterruptedException e) {
			;
		}

		try (BufferedReader in = new BufferedReader(new InputStreamReader(
				uc.getInputStream()))) {
			String line;
			while ((line = in.readLine()) != null) {
				sb.append(line);
			}
		}
		return sb.toString();
	}
}
