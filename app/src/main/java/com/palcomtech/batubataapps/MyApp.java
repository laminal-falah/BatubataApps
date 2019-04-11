package com.palcomtech.batubataapps;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.google.firebase.FirebaseApp;
import com.palcomtech.batubataapps.service.ReceiverNetworkService;
import com.palcomtech.batubataapps.utils.LocaleUtils;
import com.palcomtech.batubataapps.utils.SharedManager;


public class MyApp extends Application implements Application.ActivityLifecycleCallbacks {

    public static Activity appActivity;

    private SharedManager mSharedManager;

    @Override
    public void onCreate() {
        super.onCreate();
        connection();
        registerActivityLifecycleCallbacks(this);
        FirebaseApp.initializeApp(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        mSharedManager = new SharedManager(base);
        super.attachBaseContext(LocaleUtils.setLocale(base,mSharedManager.getLANG()));
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        appActivity = activity;
        Intent i = new Intent(this, ReceiverNetworkService.class);
        sendBroadcast(i);
    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        //getApplicationContext().unregisterReceiver(new ReceiverNetworkService());
    }

    private void connection() {
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        getApplicationContext().registerReceiver(new ReceiverNetworkService(), intentFilter);
    }
}
