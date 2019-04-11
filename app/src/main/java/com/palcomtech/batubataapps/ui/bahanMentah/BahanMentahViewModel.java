package com.palcomtech.batubataapps.ui.bahanMentah;

import android.arch.lifecycle.ViewModel;

public class BahanMentahViewModel extends ViewModel {

    private FiltersBahanMentah mFiltersBahanMentah;

    public BahanMentahViewModel() {
        mFiltersBahanMentah = FiltersBahanMentah.getDefault();
    }

    public FiltersBahanMentah getmFiltersBahanMentah() {
        return mFiltersBahanMentah;
    }

    public void setmFiltersBahanMentah(FiltersBahanMentah mFiltersBahanMentah) {
        this.mFiltersBahanMentah = mFiltersBahanMentah;
    }
}
