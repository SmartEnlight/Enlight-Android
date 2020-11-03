package com.david0926.enlight.util;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

public class TokenCache {

    private static SharedPreferences getSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void setToken(Context context, String token) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString("token_json", "Bearer " + token).apply();
    }

    public static String getToken(Context context) {
        return getSharedPreferences(context).getString("token_json", "");
    }
}
