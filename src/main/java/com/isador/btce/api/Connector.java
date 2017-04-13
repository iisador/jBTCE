package com.isador.btce.api;

import com.isador.btce.api.constants.Pair;
import com.isador.btce.api.privateapi.Nonce;

import java.util.HashMap;
import java.util.Map;

import static java.util.stream.Collectors.joining;

/**
 * Created by isador
 * on 04.04.17
 */
public interface Connector {

    String PRIVATE_API_URL = "https://btc-e.com/tapi";
    String PUBLIC_API_TMPL = "https://btc-e.com/api/2/%s/%s";

    void init(String key, String secret);

    String signedPost(String method, Map<String, Object> additionalParameters) throws BTCEException;
    String getTick(Pair pair) throws BTCEException;
    String getTrades(Pair pair) throws BTCEException;
    String getFee(Pair pair) throws BTCEException;
    String getDepth(Pair pair) throws BTCEException;

    default String getBody(String method, Map<String, Object> additionalParameters) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("nonce", String.valueOf(Nonce.get()));
        parameters.put("method", method);

        if (additionalParameters != null) {
            parameters.putAll(additionalParameters);
        }

        return parameters.entrySet().stream()
                .map(e -> String.format("%s=%s", e.getKey(), e.getValue()))
                .collect(joining("&"));
    }
}
