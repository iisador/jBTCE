package com.isador.trade.jbtce;

import java.util.Map;

/**
 * Connector interface. Only two methods to implement
 * to call api.
 *
 * @author isador
 * @since 2.0.1
 */
public interface Connector {

    /**
     * GET request.
     *
     * @param url     url
     * @param headers headers to include in request
     * @return response as string
     * @throws ConnectorException on any connection exceptions
     */
    String get(String url, Map<String, String> headers) throws ConnectorException;

    /**
     * POST request
     *
     * @param url     url
     * @param body    request body to include
     * @param headers headers to include
     * @return response as string
     * @throws ConnectorException on any connection exceptions
     */
    String post(String url, String body, Map<String, String> headers) throws ConnectorException;
}
