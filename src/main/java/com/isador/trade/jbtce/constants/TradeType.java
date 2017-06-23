package com.isador.trade.jbtce.constants;

/**
 * Trade type aggregator. Contains operation name BUY -&gt; bid, SELL -&gt; ask
 *
 * @author isador
 * @since 2.0.1
 */
public enum TradeType {
    BUY("bid"),
    SELL("ask");

    private String operationName;

    TradeType(String operationName) {
        this.operationName = operationName;
    }

    /**
     * Get tradeType object from string.
     *
     * @param s tradeType or operationName. Case insensitive.
     * @return tradeType object
     * @throws IllegalArgumentException if input string is invalid.
     */
    public static TradeType parse(String s) {
        if ("bid".equalsIgnoreCase(s) || "buy".equalsIgnoreCase(s)) {
            return BUY;
        }
        if ("ask".equalsIgnoreCase(s) || "sell".equalsIgnoreCase(s)) {
            return SELL;
        }
        throw new IllegalArgumentException("Invalid arg of Operation");
    }

    public String getOperationName() {
        return operationName;
    }
}
