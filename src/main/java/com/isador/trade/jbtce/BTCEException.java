package com.isador.trade.jbtce;

/**
 * Created by isador
 * on 29.03.17
 */
public class BTCEException extends RuntimeException {

    public BTCEException(String message, Throwable cause) {
        super(message, cause);
    }

    public BTCEException(String message) {
        this(message, null);
    }

    public BTCEException(Throwable cause) {
        this(null, cause);
    }

}
