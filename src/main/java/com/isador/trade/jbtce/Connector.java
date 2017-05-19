package com.isador.trade.jbtce;

import java.util.Map;

/**
 * Created by isador
 * on 04.04.17
 */
public interface Connector {

    String get(String url, Map<String, String> headers) throws ConnectorException;

    String post(String url, String body, Map<String, String> headers) throws ConnectorException;
}
