package com.palcomtech.batubataapps.model;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

@IgnoreExtraProperties
public class LogBahanMentah {

    public static final String COLLECTION = "log_bahan_mentah";
    public static final String TIMESTAMPS = "timestamps";

    private String rawId;
    private double quantity;
    private @ServerTimestamp Date timestamps;

    public LogBahanMentah() {
    }

    public LogBahanMentah(double quantity) {
        this.quantity = quantity;
    }

    public LogBahanMentah(String rawId, double quantity) {
        this.rawId = rawId;
        this.quantity = quantity;
    }

    public String getRawId() {
        return rawId;
    }

    public void setRawId(String rawId) {
        this.rawId = rawId;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public Date getTimestamps() {
        return timestamps;
    }

    public void setTimestamps(Date timestamps) {
        this.timestamps = timestamps;
    }
}
