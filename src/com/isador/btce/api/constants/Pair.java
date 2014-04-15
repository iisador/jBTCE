package com.isador.btce.api.constants;

public enum Pair {
    BTC_USD(Currency.BTC, Currency.USD), BTC_RUR(Currency.BTC, Currency.RUR), BTC_EUR(
	    Currency.BTC, Currency.EUR), BTC_CNH(Currency.BTC, Currency.CNH), BTC_GBP(
	    Currency.BTC, Currency.GBP), LTC_BTC(Currency.LTC, Currency.BTC), LTC_USD(
	    Currency.LTC, Currency.USD), LTC_RUR(Currency.LTC, Currency.RUR), LTC_EUR(
	    Currency.LTC, Currency.EUR), LTC_CNH(Currency.LTC, Currency.CNH), LTC_GBP(
	    Currency.LTC, Currency.GBP), NMC_BTC(Currency.NMC, Currency.BTC), NMC_USD(
	    Currency.NMC, Currency.USD), NVC_BTC(Currency.NVC, Currency.BTC), NVC_USD(
	    Currency.NVC, Currency.USD), USD_RUR(Currency.USD, Currency.RUR), EUR_USD(
	    Currency.EUR, Currency.USD), EUR_RUR(Currency.EUR, Currency.RUR), USD_CNH(
	    Currency.USD, Currency.CNH), GBP_USD(Currency.GBP, Currency.USD), TRC_BTC(
	    Currency.TRC, Currency.BTC), PPC_BTC(Currency.PPC, Currency.BTC), PPC_USD(
	    Currency.PPC, Currency.USD), FTC_BTC(Currency.FTC, Currency.BTC), XPM_BTC(
	    Currency.XPM, Currency.BTC);

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
