package com.palcomtech.batubataapps.ui.bahanMentah;

import android.text.TextUtils;

import com.google.firebase.firestore.Query;
import com.palcomtech.batubataapps.model.BahanMentah;

public class FiltersBahanMentah {
    private String sortBy = null;
    private Query.Direction sortDirection = null;

    public FiltersBahanMentah() {
    }

    public static FiltersBahanMentah getDefault() {
        FiltersBahanMentah filtersBahanMentah = new FiltersBahanMentah();
        filtersBahanMentah.setSortBy(BahanMentah.TIMESTAMPS);
        filtersBahanMentah.setSortDirection(Query.Direction.DESCENDING);
        return filtersBahanMentah;
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
