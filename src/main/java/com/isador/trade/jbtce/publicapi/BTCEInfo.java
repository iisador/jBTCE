package com.isador.trade.jbtce.publicapi;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Created by isador
 * on 10.05.17
 */
public final class BTCEInfo {

    private final LocalDateTime serverTime;
    private final List<PairInfo> pairInfoList;

    BTCEInfo(LocalDateTime serverTime, List<PairInfo> pairInfoList) {
        this.serverTime = serverTime;
        this.pairInfoList = pairInfoList;
    }

    public LocalDateTime getServerTime() {
        return serverTime;
    }

    public List<PairInfo> getPairInfoList() {
        return Collections.unmodifiableList(pairInfoList);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BTCEInfo BTCEInfo = (BTCEInfo) o;
        return Objects.equals(serverTime, BTCEInfo.serverTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serverTime);
    }

    @Override
    public String toString() {
        return "BTCEInfo{" +
                "serverTime=" + serverTime +
                ", pairInfoList=" + pairInfoList +
                '}';
    }
}
