package com.isador.trade.jbtce.publicapi;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Server info holder
 *
 * @author isador
 * @since 2.0.1
 */
public final class BTCEInfo {

    private final LocalDateTime serverTime;
    private final List<PairInfo> pairInfoList;

    public BTCEInfo(LocalDateTime serverTime, List<PairInfo> pairInfoList) {
        this.serverTime = serverTime;
        this.pairInfoList = pairInfoList;
    }

    /**
     * @return Server time
     */
    public LocalDateTime getServerTime() {
        return serverTime;
    }

    /**
     * @return pairs info list
     */
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
