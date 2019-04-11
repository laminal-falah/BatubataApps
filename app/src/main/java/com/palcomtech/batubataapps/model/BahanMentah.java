package com.palcomtech.batubataapps.model;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

@IgnoreExtraProperties
public class BahanMentah {

    public static final String COLLECTION = "bahan_mentah";
    public static final String TIMESTAMPS = "timestamps";

    public static final String FIELD_BAHAN = "nama_bahan";
    public static final String FIELD_STOK = "stok";

    private String uid;
    private String nama_bahan;
    private double stok;
    private String satuan;
    private @ServerTimestamp Date timestamps;

    public BahanMentah() {
    }

    public BahanMentah(String uid, String nama_bahan, double stok, String satuan) {
        this.uid = uid;
        this.nama_bahan = nama_bahan;
        this.stok = stok;
        this.satuan = satuan;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNama_bahan() {
        return nama_bahan;
    }

    public void setNama_bahan(String nama_bahan) {
        this.nama_bahan = nama_bahan;
    }

    public double getStok() {
        return stok;
    }

    public void setStok(double stok) {
        this.stok = stok;
    }

    public String getSatuan() {
        return satuan;
    }

    public void setSatuan(String satuan) {
        this.satuan = satuan;
    }

    public Date getTimestamps() {
        return timestamps;
    }

    public void setTimestamps(Date timestamps) {
        this.timestamps = timestamps;
    }
}
