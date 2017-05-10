package com.isador.trade.jbtce.constants;

import com.google.gson.annotations.SerializedName;

import static com.isador.trade.jbtce.constants.Currency.*;

public enum Pair {
    @SerializedName("btc_usd")BTC_USD(BTC, USD),
    @SerializedName("btc_rur")BTC_RUR(BTC, RUR),
    @SerializedName("btc_eur")BTC_EUR(BTC, EUR),
    @SerializedName("ltc_btc")LTC_BTC(LTC, BTC),
    @SerializedName("ltc_usd")LTC_USD(LTC, USD),
    @SerializedName("ltc_rur")LTC_RUR(LTC, RUR),
    @SerializedName("ltc_eur")LTC_EUR(LTC, EUR),
    @SerializedName("nmc_btc")NMC_BTC(NMC, BTC),
    @SerializedName("nmc_usd")NMC_USD(NMC, USD),
    @SerializedName("nvc_btc")NVC_BTC(NVC, BTC),
    @SerializedName("nvc_usd")NVC_USD(NVC, USD),
    @SerializedName("usd_rur")USD_RUR(USD, RUR),
    @SerializedName("eur_usd")EUR_USD(EUR, USD),
    @SerializedName("eur_rur")EUR_RUR(EUR, RUR),
    @SerializedName("ppc_btc")PPC_BTC(PPC, BTC),
    @SerializedName("ppc_usd")PPC_USD(PPC, USD),
    @SerializedName("dsh_btc")DSH_BTC(DSH, BTC),
    @SerializedName("dsh_usd")DSH_USD(DSH, USD),
    @SerializedName("dsh_rur")DSH_RUR(DSH, RUR),
    @SerializedName("dsh_eur")DSH_EUR(DSH, EUR),
    @SerializedName("dsh_ltc")DSH_LTC(DSH, LTC),
    @SerializedName("dsh_eth")DSH_ETH(DSH, ETH),
    @SerializedName("eth_btc")ETH_BTC(ETH, BTC),
    @SerializedName("eth_usd")ETH_USD(ETH, USD),
    @SerializedName("eth_eur")ETH_EUR(ETH, EUR),
    @SerializedName("eth_ltc")ETH_LTC(ETH, LTC),
    @SerializedName("eth_rur")ETH_RUR(ETH, RUR);

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

    public String getName() {
        return toString().toLowerCase();
    }
}