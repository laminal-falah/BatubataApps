package com.palcomtech.batubataapps.ui.bahanJadi;

import android.text.TextUtils;

import com.google.firebase.firestore.Query;
import com.palcomtech.batubataapps.model.BahanJadi;

public class FiltersBahanJadi {
    private String sortBy = null;
    private Query.Direction sortDirection = null;

    public FiltersBahanJadi() {
    }

    public static FiltersBahanJadi getDefault() {
        FiltersBahanJadi filtersBahanJadi = new FiltersBahanJadi();
        filtersBahanJadi.setSortBy(BahanJadi.TIMESTAMPS);
        filtersBahanJadi.setSortDirection(Query.Direction.DESCENDING);
        return filtersBahanJadi;
    }

    public boolean hasSortBy() {
        return !(TextUtils.isEmpty(sortBy));
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public Query.Direction getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(Query.Direction sortDirection) {
        this.sortDirection = sortDirection;
    }
}
