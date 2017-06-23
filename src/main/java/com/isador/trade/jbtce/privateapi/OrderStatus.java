package com.isador.trade.jbtce.privateapi;

/**
 * Order status enum implementation
 *
 * @author isador
 * @since 2.0.1
 */
public enum OrderStatus {
    /**
     * active
     */
    ACTIVE,

    /**
     * executed order
     */
    EXECUTED,

    /**
     * canceled
     */
    CANCELED,

    /**
     * canceled, but was partially executed
     */
    PARTIALLY_EXECUTED
}
