package com.isador.btce.api;

import java.util.Map;

/**
 * Created by isador
 * on 04.04.17
 */
public interface Connector {

    String get(String url) throws BTCEException;

    String post(String url, String body, Map<String, String> headers) throws BTCEException;
}
