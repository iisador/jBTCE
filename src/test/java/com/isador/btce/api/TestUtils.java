package com.isador.btce.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static java.util.stream.Collectors.joining;

/**
 * Created by isador
 * on 06.04.2017.
 */
public class TestUtils {

    public static String getErrorJson() {
        return "{\"success\":0,\"error\":\"Some error\"}";
    }

    @SuppressWarnings("ConstantConditions")
    public static String getJson(String name) {
        try {
            // Leading '/' issue. Path is /D:/test/test.json instead of D:/test/test.json
//            return Files.lines(Paths.get(TestUtils.class.getClassLoader().getResource(name).getPath())).collect(Collectors.joining());

            try (BufferedReader in = new BufferedReader(new InputStreamReader(TestUtils.class.getClassLoader().getResourceAsStream(name)))) {
                return in.lines().collect(joining());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String ahalaiMahalai() {
        return "abracadabra";
    }
}
