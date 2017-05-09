package com.gianlu.commonutils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

@SuppressWarnings({"unused", "WeakerAccess"})
public class Prefs {
    private static SharedPreferences prefs;

    public static boolean getBoolean(Context context, Keys key, boolean fallback) {
        init(context);
        return prefs.getBoolean(key.key, fallback);
    }

    public static String getString(Context context, Keys key, @Nullable String fallback) {
        init(context);
        return prefs.getString(key.key, fallback);
    }

    public static void editString(Context context, Keys key, String value) {
        init(context);
        prefs.edit().putString(key.key, value).apply();
    }

    private static void init(Context context) {
        if (prefs != null) return;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public enum Keys {
        LAST_USED_PROFILE("lastUsedProfile");
        public final String key;

        Keys(String key) {
            this.key = key;
        }
    }
}
