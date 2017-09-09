package com.gianlu.commonutils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Base64;

import java.util.HashSet;
import java.util.Set;

public class Prefs {
    private static SharedPreferences prefs;

    public static String getBase64String(Context context, PrefKey key, String fallback) {
        init(context);
        return new String(Base64.decode(prefs.getString(key.getKey(), Base64.encodeToString(fallback.getBytes(), Base64.NO_WRAP)).getBytes(), Base64.NO_WRAP));
    }

    public static void putBase64String(Context context, PrefKey key, String value) {
        init(context);
        prefs.edit().putString(key.getKey(), Base64.encodeToString(value.getBytes(), Base64.NO_WRAP)).apply();
    }

    public static boolean getBoolean(Context context, PrefKey key, boolean fallback) {
        init(context);
        return prefs.getBoolean(key.getKey(), fallback);
    }

    public static String getString(Context context, PrefKey key, String fallback) {
        init(context);
        return prefs.getString(key.getKey(), fallback);
    }

    public static void putString(Context context, PrefKey key, String value) {
        init(context);
        prefs.edit().putString(key.getKey(), value).apply();
    }

    private static void init(Context context) {
        if (prefs != null) return;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static int getInt(Context context, PrefKey key, int fallback) {
        init(context);
        return prefs.getInt(key.getKey(), fallback);
    }

    public static int getFakeInt(Context context, PrefKey key, int fallback) {
        init(context);
        return Integer.parseInt(prefs.getString(key.getKey(), String.valueOf(fallback)));
    }

    public static long getFakeLong(Context context, PrefKey key, long fallback) {
        init(context);
        return Long.parseLong(prefs.getString(key.getKey(), String.valueOf(fallback)));
    }

    @SuppressWarnings("ConstantConditions")
    public static void removeFromSet(Context context, PrefKey key, String value) {
        init(context);
        Set<String> set = new HashSet<>(getSet(context, key, new HashSet<String>()));
        set.remove(value);
        prefs.edit().putStringSet(key.getKey(), set).apply();
    }

    @SuppressWarnings("ConstantConditions")
    public static void addToSet(Context context, PrefKey key, String value) {
        init(context);
        Set<String> set = new HashSet<>(getSet(context, key, new HashSet<String>()));
        if (!set.contains(value)) set.add(value);
        prefs.edit().putStringSet(key.getKey(), set).apply();
    }

    public static Set<String> getSet(Context context, PrefKey key, @Nullable Set<String> fallback) {
        init(context);
        Set<String> set = prefs.getStringSet(key.getKey(), fallback);
        if (set == null) return null;
        return new HashSet<>(set);
    }

    public static void putSet(Context context, PrefKey key, Set<String> set) {
        init(context);
        prefs.edit().putStringSet(key.getKey(), set).apply();
    }

    public static void remove(Context context, PrefKey key) {
        init(context);
        prefs.edit().remove(key.getKey()).apply();
    }

    public static void putBoolean(Context context, PrefKey key, boolean value) {
        init(context);
        prefs.edit().putBoolean(key.getKey(), value).apply();
    }

    public static void putInt(Context context, PrefKey key, int value) {
        init(context);
        prefs.edit().putInt(key.getKey(), value).apply();
    }

    public static long getLong(Context context, PrefKey key, long fallback) {
        init(context);
        return prefs.getLong(key.getKey(), fallback);
    }

    public static void putLong(Context context, PrefKey key, long value) {
        init(context);
        prefs.edit().putLong(key.getKey(), value).apply();
    }

    public enum Keys implements PrefKey {
        TRACKING_DISABLE("trackingDisable"),
        LAST_USED_PROFILE("lastUsedProfile");

        private final String key;

        Keys(String key) {
            this.key = key;
        }

        @Override
        public String getKey() {
            return key;
        }
    }

    public interface PrefKey {
        String getKey();
    }
}
