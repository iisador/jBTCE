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
 * Implement mirror switching
 *
 * @author isador
 * @since 2.0.1
 */
public class ServerProvider {
    public static final String DEFAULT_SERVER = "https://btc-e.com/";
    public static final String[] DEFAULT_MIRRORS = {"https://btc-e.nz/"};
    /**
     * Server provider for only btc-e.com
     */
    public static final ServerProvider BTCE_COM = new ServerProvider(DEFAULT_SERVER);
    /**
     * Server provider for only btc-e.nz
     */
    public static final ServerProvider BTCE_NZ = new ServerProvider(DEFAULT_MIRRORS[0]);
    private static final Logger LOG = LoggerFactory.getLogger(ServerProvider.class);
    private static final String TEST_URL_TEMPLATE = "%sapi/3/fee/btc_usd";
    private static final String API_VALIDATION_TEMPLATE = "^\\{\\\"btc_usd\\\"\\:(\\d+|\\d+\\.\\d+)\\}$";
    private String currentServer;
    private String[] mirrors;

    /**
     * Create new server provider with current server and mirrors
     *
     * @param currentServer current server
     * @param mirrors       mirrors
     */
    public ServerProvider(String currentServer, String... mirrors) {
        this.currentServer = requireNonNull(currentServer, "Current server must be not null");
        this.mirrors = validateMirrors(mirrors);
    }

    /**
     * Create new server provider with list of servers.
     * The first valid server will be used as current server, others will be mirrors
     *
     * @param mirrors server list
     */
    public ServerProvider(String[] mirrors) {
        this.mirrors = validateMirrors(mirrors);
        nextMirror();
    }

    /**
     * Default server provider with btc-e.com as current server,
     * btc-e.nz as mirror
     */
    public ServerProvider() {
        this(DEFAULT_SERVER, DEFAULT_MIRRORS);
    }

    /**
     * Validate and distinct mirrors list
     *
     * @param mirrors server array
     * @return prepared server list
     */
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

    /**
     * Get last server where api method was executed successfully
     *
     * @return server
     */
    public String getCurrentServer() {
        return currentServer;
    }

    /**
     * @return mirrors array
     */
    public String[] getMirrors() {
        return mirrors;
    }

    /**
     * Get next mirror from mirror list.
     * If found one - becomes current server. Old current server becomes mirror
     *
     * @throws ServerProviderException if no valid server found
     */
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

    /**
     * Test server availability.
     * Tries to execute {$server_name}api/3/fee/btc_usd
     *
     * @param server server
     * @return false if server is not available, or response doesn't match expected
     */
    private boolean isServerReachable(String server) {
        try {
            HttpURLConnection urlConnection = prepareConnection(new URL(String.format(TEST_URL_TEMPLATE, server)));
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

    /**
     * Prepare connection
     *
     * @param url source url
     * @return prepared url connection
     * @throws IOException ioe
     */
    private HttpURLConnection prepareConnection(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        AbstractApi.DEFAULT_HEADERS.forEach(urlConnection::setRequestProperty);
        return urlConnection;
    }
}
