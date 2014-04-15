package com.isador.btce.api.tools;

import java.util.concurrent.atomic.AtomicLong;

public class Nonce {
    private static final AtomicLong nonce = new AtomicLong(
	    System.currentTimeMillis() / 1000L);

    public static long get() {
	return nonce.getAndIncrement();
    }
}
