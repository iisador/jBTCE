package com.isador.btce.api;

import java.util.Map;

/**
 * Created by isador
 * on 04.04.17
 */
public interface Connector {

    String PRIVATE_API_URL = "https://btc-e.com/tapi";
    String PUBLIC_V3_API_TMPL = "https://btc-e.com/api/3/%s/%s";

    void init(String key, String secret);

    String call(String url) throws BTCEException;

    String signedPost(String method, Map<String, Object> additionalParameters) throws BTCEException;
}
