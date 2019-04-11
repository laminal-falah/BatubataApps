package com.palcomtech.batubataapps.ui.bahanJadi;

import android.arch.lifecycle.ViewModel;

public class BahanJadiViewModel extends ViewModel {

    private FiltersBahanJadi mFiltersBahanJadi;

    public BahanJadiViewModel() {
        mFiltersBahanJadi = FiltersBahanJadi.getDefault();
    }

    public FiltersBahanJadi getmFiltersBahanJadi() {
        return mFiltersBahanJadi;
    }

    public void setmFiltersBahanJadi(FiltersBahanJadi mFiltersBahanJadi) {
        this.mFiltersBahanJadi = mFiltersBahanJadi;
    }
}
