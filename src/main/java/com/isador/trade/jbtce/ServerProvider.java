package com.isador.trade.jbtce;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

/**
 * Created by isador
 * on 12.05.17
 */
public class ServerProvider {

    public static final String DEFAULT_SERVER = "https://btc-e.com/";
    public static final String[] DEFAULT_MIRRORS = {"https://btc-e.nz/"};
    private static final Logger LOG = LoggerFactory.getLogger(ServerProvider.class);
    private static final String TEST_URL_TEMPLATE = "%sapi/3/fee/btc_usd";
    private static final String API_VALIDATION_TEMPLATE = "^\\{\\\"btc_usd\\\"\\:(\\d+|\\d+\\.\\d+)\\}$";
    private String currentServer;
    private String[] mirrors;

    public ServerProvider(String currentServer, String... mirrors) {
        this.currentServer = requireNonNull(currentServer, "Current server must be not null");
        this.mirrors = validateMirrors(mirrors);
    }

    public ServerProvider(String[] mirrors) {
        this.mirrors = validateMirrors(mirrors);
        nextMirror();
    }

    public ServerProvider() {
        this(DEFAULT_SERVER, DEFAULT_MIRRORS);
    }

    private String[] validateMirrors(String[] mirrors) {
        if (mirrors != null) {
            mirrors = Stream.of(mirrors)
                    .filter(Objects::nonNull)
                    .distinct()
                    .toArray(String[]::new);

            return (mirrors.length == 0) ? new String[0] : mirrors;
        }

        return new String[0];
    }

    public String getCurrentServer() {
        return currentServer;
    }

    public String[] getMirrors() {
        return mirrors;
    }

    public void nextMirror() {
        LOG.debug("Searching next mirror");
        String newServer = Stream.of(mirrors)
                .filter(mirror -> !mirror.equals(currentServer))
                .filter(this::isServerReachable)
                .findFirst()
                .orElseThrow(() -> new ServerProviderException("No valid server found"));
        mirrors = ArrayUtils.removeElement(mirrors, newServer);
        if (currentServer != null) {
            mirrors = ArrayUtils.add(mirrors, currentServer);
        }

        currentServer = newServer;
    }

    private boolean isServerReachable(String server) {
        try {
            HttpURLConnection urlConnection = (HttpURLConnection) new URL(String.format(TEST_URL_TEMPLATE, server)).openConnection();
            urlConnection.setRequestProperty("User-Agent", "jBTCEv2");
            urlConnection.connect();
            try (BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()))) {
                String response = in.lines().collect(Collectors.joining());
                return response.matches(API_VALIDATION_TEMPLATE);
            }
        } catch (IOException e) {
            LOG.warn("Server '{}' not available", server, e);
        }
        return false;
    }
}
