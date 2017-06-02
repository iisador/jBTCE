package com.isador.trade.jbtce.constants;

/**
 * Created by isador
 * on 07.04.17
 */
public enum TradeType {
    BUY("bid"),
    SELL("ask");

    private String operationName;

    TradeType(String operationName) {
        this.operationName = operationName;
    }

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
