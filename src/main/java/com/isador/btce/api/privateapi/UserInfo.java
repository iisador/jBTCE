package com.isador.btce.api.privateapi;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;

public final class UserInfo {

    @SerializedName("server_time")
    private final LocalDateTime serverTime;

    private final Rights rights;
    private final Funds funds;
    private final int openOrdersCount;
    private final int transactionCount;

    UserInfo(Rights rights, Funds funds, int openOrdersCount, LocalDateTime serverTime, int transactionCount) {
        this.rights = rights;
        this.funds = funds;
        this.openOrdersCount = openOrdersCount;
        this.serverTime = serverTime;
        this.transactionCount = transactionCount;
    }

    public Funds getFunds() {
        return funds;
    }

    public int getOpenOrdersCount() {
        return openOrdersCount;
    }

    public Rights getRights() {
        return rights;
    }

    public LocalDateTime getServerTime() {
        return serverTime;
    }

    public int getTransactionCount() {
        return transactionCount;
    }

    @Override
    public String toString() {
        return "UserInfo [funds=" + funds + ", openOrdersCount="
                + openOrdersCount + ", serverTime=" + serverTime
                + ", transactionCount=" + transactionCount + ", rights="
                + rights + "]";
    }

    public static final class Rights {

        private final int info;
        private final int trade;
        private final int withdraw;

        Rights(int info, int trade, int withdraw) {
            this.info = info;
            this.trade = trade;
            this.withdraw = withdraw;
        }

        public int getInfo() {
            return info;
        }

        public int getTrade() {
            return trade;
        }

        public int getWithdraw() {
            return withdraw;
        }

        @Override
        public String toString() {
            return "Rights [info=" + info + ", trade=" + trade + ", withdraw="
                    + withdraw + "]";
        }
    }
}
