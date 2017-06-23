package com.isador.trade.jbtce.privateapi;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;

/**
 * User info holder
 *
 * @author isador
 * @since 2.0.1
 */
public final class UserInfo {

    @SerializedName("server_time")
    private final LocalDateTime serverTime;

    private final Rights rights;
    private final Funds funds;
    private final int openOrdersCount;
    private final int transactionCount;

    public UserInfo(Rights rights, Funds funds, int openOrdersCount, LocalDateTime serverTime, int transactionCount) {
        this.rights = rights;
        this.funds = funds;
        this.openOrdersCount = openOrdersCount;
        this.serverTime = serverTime;
        this.transactionCount = transactionCount;
    }

    /**
     * @return Your account balance available for trading. Doesn’t include funds on your open orders
     */
    public Funds getFunds() {
        return funds;
    }

    /**
     * @return The number of your open orders
     */
    public int getOpenOrdersCount() {
        return openOrdersCount;
    }

    /**
     * @return The privileges of the current API key. At this time the privilege to withdraw is not used anywhere
     */
    public Rights getRights() {
        return rights;
    }

    /**
     * @return Server time
     */
    public LocalDateTime getServerTime() {
        return serverTime;
    }

    /**
     * @return Deprecated, is equal to 0
     */
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

    /**
     * User rights holder
     */
    public static final class Rights {

        private final int info;
        private final int trade;
        private final int withdraw;

        public Rights(int info, int trade, int withdraw) {
            this.info = info;
            this.trade = trade;
            this.withdraw = withdraw;
        }

        /**
         * @return info methods allowed
         */
        public int getInfo() {
            return info;
        }

        /**
         * @return trade allowed
         */
        public int getTrade() {
            return trade;
        }

        /**
         * At this time the privilege to withdraw is not used anywhere
         *
         * @return withdraw allowed
         */
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
