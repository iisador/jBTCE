package com.isador.trade.jbtce.publicapi;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;
import java.util.Objects;

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

    Tick(double avg, double buy, double high, double last, double low, double sell, LocalDateTime serverTime, LocalDateTime updated, double vol, double volCur) {
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

    public double getAvg() {
        return avg;
    }

    public double getBuy() {
        return buy;
    }

    public double getHigh() {
        return high;
    }

    public double getLast() {
        return last;
    }

    public double getLow() {
        return low;
    }

    public double getSell() {
        return sell;
    }

    public LocalDateTime getServerTime() {
        return serverTime;
    }

    public LocalDateTime getUpdated() {
        return updated;
    }

    public double getVol() {
        return vol;
    }

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
