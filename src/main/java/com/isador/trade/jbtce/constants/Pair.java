package com.isador.trade.jbtce.constants;

import com.google.gson.annotations.SerializedName;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.isador.trade.jbtce.constants.Currency.*;

/**
 * Available pairs. Pair is a combination of two currencies.
 *
 * @author isador
 * @since 2.0.1
 */
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

//    /**
//     * Construct pair from two currencies
//     *
//     * @param prim primary currency
//     * @param sec  secondary currency
//     * @return Pair object regarding provided currencies
//     * @throws IllegalArgumentException if no such pair
//     */
//    public static Pair from(Currency prim, Currency sec) {
//        return valueOf(String.format("%s_%s", prim.name(), sec.name()));
//    }

    /**
     * Prepare pairs for PublicV3 API url.
     * Ex. toUrlString(BTC_USD, EUR_USD, ETH_USD) will return 'btc_usd-eur_usd-eth_usd'
     *
     * @param pairs pair construct from
     * @return empty string if {@code pairs == null} or pairs is empty
     */
    public static String toUrlString(Pair[] pairs) {
        if (pairs == null || pairs.length == 0) {
            return "";
        }

        return Stream.of(pairs)
                .map(Pair::getName)
                .collect(Collectors.joining("-"));
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
