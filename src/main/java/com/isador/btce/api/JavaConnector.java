package com.isador.btce.api;

import com.google.common.collect.ImmutableMap;
import com.isador.btce.api.constants.Pair;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by isador
 * on 05.04.17
 */
public class JavaConnector implements Connector {

    private Mac mac;
    private Map<String, String> headers;

    public JavaConnector() {
    }

    @Override
    public String signedPost(String method, Map<String, Object> additionalParameters) throws BTCEException {
        try {
            HttpsURLConnection uc = (HttpsURLConnection) new URL(PRIVATE_API_URL).openConnection();
            uc.setRequestMethod("POST");
            uc.setDoOutput(true);

            String body = getBody(method, additionalParameters);
            System.out.println(body);
            mac.update(body.getBytes(Charset.forName("UTF-8")));
            Map<String, String> headers = new HashMap<>(this.headers);
            headers.put("Sign", Hex.encodeHexString(mac.doFinal()));
            for (String key : headers.keySet()) {
                uc.addRequestProperty(key, headers.get(key));
            }

            try (PrintWriter out = new PrintWriter(uc.getOutputStream())) {
                out.write(body);
                out.flush();
            }

            try (BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()))) {
                return in.lines().collect(Collectors.joining());
            }
        } catch (IOException e) {
            throw new BTCEException(e);
        }
    }

    @Override
    public String getTick(Pair pair) throws BTCEException {
        try {
            return get(String.format(PUBLIC_API_TMPL, pair.getName(), "ticker"));
        } catch (IOException e) {
            throw new BTCEException(e);
        }
    }

    @Override
    public String getTrades(Pair pair) throws BTCEException {
        try {
            return get(String.format(PUBLIC_API_TMPL, pair.getName(), "trades"));
        } catch (IOException e) {
            throw new BTCEException(e);
        }
    }

    @Override
    public String getFee(Pair pair) throws BTCEException {
        try {
            return get(String.format(PUBLIC_API_TMPL, pair.getName(), "fee"));
        } catch (IOException e) {
            throw new BTCEException(e);
        }
    }

    @Override
    public String getDepth(Pair pair) throws BTCEException {
        try {
            return get(String.format(PUBLIC_API_TMPL, pair.getName(), "depth"));
        } catch (IOException e) {
            throw new BTCEException(e);
        }
    }

    private String get(String url) throws IOException {
        HttpsURLConnection uc = (HttpsURLConnection) new URL(url).openConnection();

        try (BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()))) {
            return in.lines().collect(Collectors.joining());
        }
    }

    @Override
    public void init(String key, String secret) {
        checkNotNull(key, "Key must be specified");
        checkNotNull(secret, "Secret must be specified");

        headers = ImmutableMap.of(
                "Key", key,
                "User-Agent", "jBTCEv2"
        );

        try {
            String alg = "HmacSHA512";
            mac = Mac.getInstance(alg);
            mac.init(new SecretKeySpec(secret.getBytes(Charset.forName("UTF-8")), alg));
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }
}
