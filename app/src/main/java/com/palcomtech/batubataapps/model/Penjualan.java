package com.palcomtech.batubataapps.model;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

@IgnoreExtraProperties
public class Penjualan {

    public static final String COLLECTION = "penjualan";
    public static final String TIMESTAMPS = "timestamps";

    public static final String FIELD_NAME = "namePembeli";
    public static final String FIELD_TOTAL = "totalPembelian";
    public static final String FIELD_TGL = "tglPembelian";

    private String namePembeli;
    private double totalPembelian;
    private String tglPembelian;
    private @ServerTimestamp Date timestamps;

    public Penjualan() {
    }

    public Penjualan(String namePembeli, double totalPembelian, String tglPembelian) {
        this.namePembeli = namePembeli;
        this.totalPembelian = totalPembelian;
        this.tglPembelian = tglPembelian;
    }

    public String getNamePembeli() {
        return namePembeli;
    }

    public void setNamePembeli(String namePembeli) {
        this.namePembeli = namePembeli;
    }

    public double getTotalPembelian() {
        return totalPembelian;
    }

    public void setTotalPembelian(double totalPembelian) {
        this.totalPembelian = totalPembelian;
    }

    public String getTglPembelian() {
        return tglPembelian;
    }

    public void setTglPembelian(String tglPembelian) {
        this.tglPembelian = tglPembelian;
    }

    public Date getTimestamps() {
        return timestamps;
    }

    public void setTimestamps(Date timestamps) {
        this.timestamps = timestamps;
    }
}
