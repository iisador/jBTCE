package com.isador.trade.jbtce.publicapi;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Tick holder
 *
 * @author isador
 * @since 2.0.1
 */
public final class Tick {

    @SerializedName("vol_cur")
    private final double volCur;

    @SerializedName("server_time")
    private final LocalDateTime serverTime;

    private final double avg;
    private final double buy;
    private final double high;
    private final double last;
    private final double low;
    private final double sell;
    private final double vol;
    private final LocalDateTime updated;

    public Tick(double avg, double buy, double high, double last, double low, double sell, LocalDateTime serverTime, LocalDateTime updated, double vol, double volCur) {
        this.avg = avg;
        this.buy = buy;
        this.high = high;
        this.last = last;
        this.low = low;
        this.sell = sell;
        this.serverTime = serverTime;
        this.updated = updated;
        this.vol = vol;
        this.volCur = volCur;
    }

    /**
     * @return average price
     */
    public double getAvg() {
        return avg;
    }

    /**
     * @return buy price
     */
    public double getBuy() {
        return buy;
    }

    /**
     * @return maximum price
     */
    public double getHigh() {
        return high;
    }

    /**
     * @return the price of the last trade
     */
    public double getLast() {
        return last;
    }

    /**
     * @return minimum price
     */
    public double getLow() {
        return low;
    }

    /**
     * @return sell price
     */
    public double getSell() {
        return sell;
    }

    /**
     * Available only with {@code PublicApiV2}
     *
     * @return server time
     */
    public LocalDateTime getServerTime() {
        return serverTime;
    }

    /**
     * @return last update of cache
     */
    public LocalDateTime getUpdated() {
        return updated;
    }

    /**
     * @return trade volume
     */
    public double getVol() {
        return vol;
    }

    /**
     * @return trade volume in currency
     */
    public double getVolCur() {
        return volCur;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Tick tick = (Tick) o;
        return Objects.equals(updated, tick.updated);
    }

    @Override
    public int hashCode() {
        return Objects.hash(updated);
    }

    @Override
    public String toString() {
        return "Tick{" +
                "avg=" + avg +
                ", buy=" + buy +
                ", high=" + high +
                ", last=" + last +
                ", low=" + low +
                ", sell=" + sell +
                ", vol=" + vol +
                ", updated=" + updated +
                ", volCur=" + volCur +
                ", serverTime=" + serverTime +
                '}';
    }
}
