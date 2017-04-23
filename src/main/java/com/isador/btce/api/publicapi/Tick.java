package com.isador.btce.api.publicapi;

import com.google.gson.annotations.SerializedName;
import com.isador.btce.api.constants.Pair;

import java.time.LocalDateTime;

public final class Tick {

    private final double avg;
    private final double buy;
    private final double high;
    private final double last;
    private final double low;
    private final double sell;
    private final double vol;
    private final LocalDateTime updated;
    private final Pair pair;

    @SerializedName("vol_cur")
    private final double volCur;

    @SerializedName("server_time")
    private final LocalDateTime serverTime;

    Tick(double avg, double buy, double high, double last, double low, double sell, LocalDateTime serverTime, LocalDateTime updated, double vol, double volCur, Pair pair) {
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
        this.pair = pair;
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

    public Pair getPair() {
        return pair;
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
                ", pair=" + pair +
                ", volCur=" + volCur +
                ", serverTime=" + serverTime +
                '}';
    }
}
