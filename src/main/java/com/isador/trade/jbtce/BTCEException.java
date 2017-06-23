package com.isador.trade.jbtce;

/**
 * Common exception.
 *
 * @author isador
 * @since 2.0.1
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
