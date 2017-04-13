package com.isador.btce.api;

/**
 * Created by isador
 * on 29.03.17
 */
public class BTCEException extends RuntimeException {

    public BTCEException(String message, Throwable cause) {
        super(message, cause, false, false);
    }

    public BTCEException(String message) {
        this(message, null);
    }

    public BTCEException(Throwable cause) {
        this(null, cause);
    }

}
