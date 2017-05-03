package com.isador.btce.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by isador
 * on 05.04.17
 */
public class DefaultConnector implements Connector {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultConnector.class);

    @Override
    public String post(String url, String body, Map<String, String> headers) throws BTCEException {
        LOG.debug("POST: '{}'", url);
        try {
            HttpURLConnection uc = (HttpURLConnection) new URL(url).openConnection();
            uc.setRequestMethod("POST");

            if (headers != null && !headers.isEmpty()) {
                LOG.debug("HEADERS:");
                headers.entrySet().forEach(e -> {
                    LOG.debug("'{}' - '{}'", e.getKey(), e.getValue());
                    uc.addRequestProperty(e.getKey(), e.getValue());
                });
            }

            if (body != null) {
                LOG.debug("BODY: '{}'", body);
                uc.setDoOutput(true);
                try (PrintWriter out = new PrintWriter(uc.getOutputStream())) {
                    out.write(body);
                    out.flush();
                    out.close();
                }
            }

            return getResponse(uc);
        } catch (IOException e) {
            throw new BTCEException(e);
        }
    }

    @Override
    public String get(String url) throws BTCEException {
        LOG.debug("GET: '{}'", url);
        try {
            return getResponse(new URL(url).openConnection());
        } catch (IOException e) {
            throw new BTCEException(e);
        }
    }

    private String getResponse(URLConnection urlConnection) throws IOException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()))) {
            String response = in.lines().collect(Collectors.joining());

            LOG.debug("RESPONSE: '{}'", response);
            return response;
        }
    }
}
