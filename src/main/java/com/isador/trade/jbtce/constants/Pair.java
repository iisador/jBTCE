package com.isador.trade.jbtce.constants;

import static com.isador.trade.jbtce.constants.Currency.*;

public enum Pair {
    BTC_USD(BTC, USD),
    BTC_RUR(BTC, RUR),
    BTC_EUR(BTC, EUR),
    LTC_BTC(LTC, BTC),
    LTC_USD(LTC, USD),
    LTC_RUR(LTC, RUR),
    LTC_EUR(LTC, EUR),
    NMC_BTC(NMC, BTC),
    NMC_USD(NMC, USD),
    NVC_BTC(NVC, BTC),
    NVC_USD(NVC, USD),
    USD_RUR(USD, RUR),
    EUR_USD(EUR, USD),
    EUR_RUR(EUR, RUR),
    PPC_BTC(PPC, BTC),
    PPC_USD(PPC, USD),
    DSH_BTC(DSH, BTC),
    DSH_USD(DSH, USD),
    DSH_RUR(DSH, RUR),
    DSH_EUR(DSH, EUR),
    DSH_LTC(DSH, LTC),
    DSH_ETH(DSH, ETH),
    ETH_BTC(ETH, BTC),
    ETH_USD(ETH, USD),
    ETH_EUR(ETH, EUR),
    ETH_LTC(ETH, LTC),
    ETH_RUR(ETH, RUR);

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
