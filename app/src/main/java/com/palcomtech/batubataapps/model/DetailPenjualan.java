package com.palcomtech.batubataapps.model;

import com.google.firebase.firestore.IgnoreExtraProperties;

import javax.annotation.Nullable;

@IgnoreExtraProperties
public class DetailPenjualan {

    public static final String COLLECTION = "detail_penjualan";

    public static final String FIELD_ID = "idBahan";
    public static final String FIELD_NM = "nmBahan";
    public static final String FIELD_HRG = "hrgBahan";
    public static final String FIELD_JMLH = "jlBahan";
    public static final String FIELD_SUB = "subTotal";

    private String idBahan;
    private String nmBahan;
    private double hrgBahan;
    private double jlBahan;
    private double subTotal;

    public DetailPenjualan() {
    }

    public DetailPenjualan(String idBahan, String nmBahan, double hrgBahan, double jlBahan, double subTotal) {
        this.idBahan = idBahan;
        this.nmBahan = nmBahan;
        this.hrgBahan = hrgBahan;
        this.jlBahan = jlBahan;
        this.subTotal = subTotal;
    }

    public String getIdBahan() {
        return idBahan;
    }

    public void setIdBahan(String idBahan) {
        this.idBahan = idBahan;
    }

    public String getNmBahan() {
        return nmBahan;
    }

    public void setNmBahan(String nmBahan) {
        this.nmBahan = nmBahan;
    }

    public double getHrgBahan() {
        return hrgBahan;
    }

    public void setHrgBahan(double hrgBahan) {
        this.hrgBahan = hrgBahan;
    }

    public double getJlBahan() {
        return jlBahan;
    }

    public void setJlBahan(double jlBahan) {
        this.jlBahan = jlBahan;
    }

    public double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(double subTotal) {
        this.subTotal = subTotal;
    }

    @Nullable
    @Override
    public String toString() {
        return "DetailPenjualan{" +
                "idBahan='" + idBahan + '\'' +
                ", nmBahan='" + nmBahan + '\'' +
                ", hrgBahan=" + hrgBahan +
                ", jlBahan=" + jlBahan +
                ", subTotal=" + subTotal +
                '}';
    }
}
