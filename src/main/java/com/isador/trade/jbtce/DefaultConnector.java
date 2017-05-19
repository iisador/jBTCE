package com.isador.trade.jbtce;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by isador
 * on 05.04.17
 */
public class DefaultConnector implements Connector {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultConnector.class);

    @Override
    public String post(String url, String body, Map<String, String> headers) {
        try{
            return call(url, "POST", headers, body);
        } catch (IOException e) {
            throw new ConnectorException(e);
        }
    }

    @Override
    public String get(String url, Map<String, String> headers) throws ConnectorException {
        try{
            return call(url, "GET", headers, null);
        } catch (IOException e) {
            throw new ConnectorException(e);
        }
    }

    private String call(String url, String method, Map<String, String> headers, String body) throws IOException {
        LOG.debug("{}: '{}'", method, url);
        HttpURLConnection uc = (HttpURLConnection) new URL(url).openConnection();
        uc.setRequestMethod(method);

        if (headers != null && !headers.isEmpty()) {
            LOG.debug("HEADERS:");
            headers.forEach((key, value) -> {
                LOG.debug("'{}' - '{}'", key, value);
                uc.addRequestProperty(key, value);
            });
        }

        if (body != null && !body.isEmpty()) {
            LOG.debug("BODY: '{}'", body);
            uc.setDoOutput(true);
            try (PrintWriter out = new PrintWriter(uc.getOutputStream())) {
                out.write(body);
                out.flush();
                out.close();
            }
        }

        try (BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()))) {
            String response = in.lines().collect(Collectors.joining());

            LOG.debug("RESPONSE: '{}'", response);
            return response;
        }
    }
}
