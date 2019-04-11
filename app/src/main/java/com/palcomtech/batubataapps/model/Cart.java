package com.palcomtech.batubataapps.model;

import com.google.firebase.firestore.IgnoreExtraProperties;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@IgnoreExtraProperties
public class Cart {

    public static final String COLLECTION = "cart";

    public static final String FIELD_ID = "idBahan";
    public static final String FIELD_NAMA = "nmBahan";
    public static final String FIELD_JMLH = "jlBahan";
    public static final String FIELD_HARGA = "hrgBahan";
    public static final String FIELD_SUBTOTAL = "subTotal";

    private String idBahan;
    private String nmBahan;
    private double jlBahan;
    private double hrgBahan;
    private double subTotal;

    public Cart() {
    }

    public Cart(String idBahan, String nmBahan, double jlBahan, double hrgBahan, double subTotal) {
        this.idBahan = idBahan;
        this.nmBahan = nmBahan;
        this.jlBahan = jlBahan;
        this.hrgBahan = hrgBahan;
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

    public double getJlBahan() {
        return jlBahan;
    }

    public void setJlBahan(double jlBahan) {
        this.jlBahan = jlBahan;
    }

    public double getHrgBahan() {
        return hrgBahan;
    }

    public void setHrgBahan(double hrgBahan) {
        this.hrgBahan = hrgBahan;
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
        return "Cart{" +
                "idBahan='" + idBahan + '\'' +
                ", nmBahan='" + nmBahan + '\'' +
                ", jlBahan=" + jlBahan +
                ", hrgBahan=" + hrgBahan +
                ", subTotal=" + subTotal +
                '}';
    }
}
