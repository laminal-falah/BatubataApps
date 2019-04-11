package com.palcomtech.batubataapps.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.palcomtech.batubataapps.MyApp;
import com.palcomtech.batubataapps.R;
import com.palcomtech.batubataapps.utils.NetworkUtils;
import com.palcomtech.batubataapps.utils.SnackbarUtils;

public class ReceiverNetworkService extends BroadcastReceiver {

    private boolean isOffline = false;
    private final SnackbarUtils snackbarUtils = new SnackbarUtils(MyApp.appActivity);
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            NetworkUtils.getConnectivityStatus(context);
            if (!NetworkUtils.isNetworkConnected(context)) {
                snackbarUtils.snackBarInfinite(MyApp.appActivity.getResources().getString(R.string.msg_no_internet));
                isOffline = true;
            } else {
                if (isOffline) {
                    snackbarUtils.snackBarLong(MyApp.appActivity.getResources().getString(R.string.msg_back_online));
                    isOffline = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
