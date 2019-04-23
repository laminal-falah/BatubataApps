package com.palcomtech.batubataapps.model;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

@IgnoreExtraProperties
public class BahanJadi {

    public static final String COLLECTION = "bahan_jadi";
    public static final String TIMESTAMPS = "timestamps";

    public static final String FIELD_FID = "uidMentah";
    public static final String FIELD_BAHAN = "nama_bahan";
    public static final String FIELD_STOCK = "stok";
    public static final String FIELD_PRICE = "harga";

    private String uid;
    private String uidMentah;
    private String nama_bahan;
    private double stok;
    private double harga;
    private @ServerTimestamp Date timestamps;

    public BahanJadi() {
    }

    public BahanJadi(String uid, String uidMentah, String nama_bahan, double stok, double harga) {
        this.uid = uid;
        this.uidMentah = uidMentah;
        this.nama_bahan = nama_bahan;
        this.stok = stok;
        this.harga = harga;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUidMentah() {
        return uidMentah;
    }

    public void setUidMentah(String uidMentah) {
        this.uidMentah = uidMentah;
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

    public double getHarga() {
        return harga;
    }

    public void setHarga(double harga) {
        this.harga = harga;
    }

    public Date getTimestamps() {
        return timestamps;
    }

    public void setTimestamps(Date timestamps) {
        this.timestamps = timestamps;
    }

    @Override
    public String toString() {
        return "BahanJadi{" +
                "uid='" + uid + '\'' +
                ", uidMentah='" + uidMentah + '\'' +
                ", nama_bahan='" + nama_bahan + '\'' +
                ", stok=" + stok +
                ", harga=" + harga +
                ", timestamps=" + timestamps +
                '}';
    }
}
