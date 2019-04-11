package com.palcomtech.batubataapps.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedManager {
    private static final String SP_APP = "APP";

    public static final String LANG = "in";

    private final SharedPreferences sp;
    private final SharedPreferences.Editor editor;

    public SharedManager(Context context) {
        this.sp = context.getSharedPreferences(SP_APP, Context.MODE_PRIVATE);
        editor = sp.edit();
        editor.apply();
    }

    public void saveSPString(String keySP, String value){
        editor.putString(keySP, value);
        editor.apply();
    }

    public String getLANG() {
        return sp.getString(LANG, "in");
    }
}
