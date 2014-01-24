package com.isador.btce.api.constants;

public enum Pair {
    USD_RUR(Currency.USD, Currency.RUR), BTC_USD(Currency.BTC, Currency.USD), BTC_RUR(
	    Currency.BTC, Currency.RUR), BTC_EUR(Currency.BTC, Currency.EUR), LTC_BTC(
	    Currency.LTC, Currency.BTC), LTC_USD(Currency.LTC, Currency.USD), LTC_RUR(
	    Currency.LTC, Currency.RUR), NMC_BTC(Currency.NMC, Currency.BTC), EUR_USD(
	    Currency.EUR, Currency.USD);

    private Currency prim;
    private Currency sec;

    Pair(Currency prim, Currency sec) {
	this.prim = prim;
	this.sec = sec;
    }

    public Currency getPrim() {
	return prim;
    }

    public Currency getSec() {
	return sec;
    }
}
