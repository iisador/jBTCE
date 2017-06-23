package com.isador.trade.jbtce.privateapi;

import com.isador.trade.jbtce.constants.Currency;

import java.util.Map;

/**
 * User funds implementation
 *
 * @author isador
 * @since 2.0.1
 */
public final class Funds {

    private final Map<Currency, Double> fundsMap;

    public Funds(Map<Currency, Double> fundsMap) {
        this.fundsMap = fundsMap;
    }

    /**
     * Return current balance
     *
     * @param currency currency to return
     * @return 0.0 if no info about <code>currency</code>
     */
    public double getFund(Currency currency) {
        return fundsMap.getOrDefault(currency, 0.0);
    }

    public double getBtc() {
        return getFund(Currency.BTC);
    }

    public double getUsd() {
        return getFund(Currency.USD);
    }

    public double getRur() {
        return getFund(Currency.RUR);
    }

    public double getEur() {
        return getFund(Currency.EUR);
    }

    public double getLtc() {
        return getFund(Currency.LTC);
    }

    public double getNmc() {
        return getFund(Currency.NMC);
    }

    public double getNvc() {
        return getFund(Currency.NVC);
    }

    public double getPpc() {
        return getFund(Currency.PPC);
    }

    public double getDsh() {
        return getFund(Currency.DSH);
    }

    public double getEth() {
        return getFund(Currency.ETH);
    }

    @Override
    public String toString() {
        return "Funds{" +
                "fundsMap=" + fundsMap +
                '}';
    }
}