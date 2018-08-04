package com.gianlu.commonutils.Preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashSet;
import java.util.Set;

public final class Prefs {
    private static SharedPreferences prefs;

    public static JSONArray getJSONArray(Context context, PrefKey key, JSONArray fallback) throws JSONException {
        init(context);
        return new JSONArray(getBase64String(context, key, fallback.toString()));
    }

    public static JSONArray getJSONArray(SharedPreferences prefs, PrefKey key, JSONArray fallback) throws JSONException {
        init(prefs);
        return new JSONArray(getBase64String(prefs, key, fallback.toString()));
    }

    public static void putJSONArray(Context context, PrefKey key, JSONArray value) {
        init(context);
        putBase64String(context, key, value.toString());
    }

    public static void putJSONArray(SharedPreferences prefs, PrefKey key, JSONArray value) {
        init(prefs);
        putBase64String(prefs, key, value.toString());
    }

    public static String getBase64String(Context context, PrefKey key, String fallback) {
        init(context);
        return new String(Base64.decode(prefs.getString(key.getKey(), Base64.encodeToString(fallback.getBytes(), Base64.NO_WRAP)).getBytes(), Base64.NO_WRAP));
    }

    public static String getBase64String(SharedPreferences prefs, PrefKey key, String fallback) {
        init(prefs);
        return new String(Base64.decode(prefs.getString(key.getKey(), Base64.encodeToString(fallback.getBytes(), Base64.NO_WRAP)).getBytes(), Base64.NO_WRAP));
    }

    public static void putBase64String(Context context, PrefKey key, String value) {
        init(context);
        prefs.edit().putString(key.getKey(), Base64.encodeToString(value.getBytes(), Base64.NO_WRAP)).apply();
    }

    public static void putBase64String(SharedPreferences prefs, PrefKey key, String value) {
        init(prefs);
        prefs.edit().putString(key.getKey(), Base64.encodeToString(value.getBytes(), Base64.NO_WRAP)).apply();
    }

    public static boolean getBoolean(Context context, PrefKey key, boolean fallback) {
        init(context);
        return prefs.getBoolean(key.getKey(), fallback);
    }

    public static boolean getBoolean(SharedPreferences prefs, PrefKey key, boolean fallback) {
        init(prefs);
        return prefs.getBoolean(key.getKey(), fallback);
    }

    public static String getString(Context context, PrefKey key, String fallback) {
        init(context);
        return prefs.getString(key.getKey(), fallback);
    }

    public static String getString(SharedPreferences prefs, PrefKey key, String fallback) {
        init(prefs);
        return prefs.getString(key.getKey(), fallback);
    }

    public static void putString(Context context, PrefKey key, String value) {
        init(context);
        prefs.edit().putString(key.getKey(), value).apply();
    }

    public static void putString(SharedPreferences prefs, PrefKey key, String value) {
        init(prefs);
        prefs.edit().putString(key.getKey(), value).apply();
    }

    private static void init(Context context) {
        if (prefs != null) return;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    private static void init(SharedPreferences prefs) {
        if (Prefs.prefs != null) return;
        Prefs.prefs = prefs;
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

    public static void removeFromSet(Context context, PrefKey key, String value) {
        init(context);
        Set<String> set = new HashSet<>(getSet(context, key, new HashSet<String>()));
        set.remove(value);
        prefs.edit().putStringSet(key.getKey(), set).apply();
    }

    public static void addToSet(Context context, PrefKey key, String value) {
        init(context);
        Set<String> set = new HashSet<>(getSet(context, key, new HashSet<String>()));
        if (!set.contains(value)) set.add(value);
        prefs.edit().putStringSet(key.getKey(), set).apply();
    }

    public static void addToSet(SharedPreferences prefs, PrefKey key, String value) {
        init(prefs);
        Set<String> set = new HashSet<>(getSet(prefs, key, new HashSet<String>()));
        if (!set.contains(value)) set.add(value);
        prefs.edit().putStringSet(key.getKey(), set).apply();
    }

    public static Set<String> getSet(Context context, PrefKey key, Set<String> fallback) {
        init(context);
        return getSet(key, fallback);
    }

    public static boolean setContains(Context context, PrefKey key, String item) {
        init(context);
        Set<String> set = prefs.getStringSet(key.getKey(), null);
        return set != null && set.contains(item);
    }

    public static boolean setContains(SharedPreferences prefs, PrefKey key, String item) {
        init(prefs);
        Set<String> set = prefs.getStringSet(key.getKey(), null);
        return set != null && set.contains(item);
    }

    private static Set<String> getSet(PrefKey key, Set<String> fallback) {
        Set<String> set = prefs.getStringSet(key.getKey(), fallback);
        if (set == null) return null;
        return new HashSet<>(set);
    }

    public static Set<String> getSet(SharedPreferences prefs, PrefKey key, Set<String> fallback) {
        init(prefs);
        return getSet(key, fallback);
    }

    public static void putSet(Context context, PrefKey key, Set<String> set) {
        init(context);
        prefs.edit().putStringSet(key.getKey(), set).apply();
    }

    public static void putSet(SharedPreferences prefs, PrefKey key, Set<String> set) {
        init(prefs);
        prefs.edit().putStringSet(key.getKey(), set).apply();
    }

    public static void remove(SharedPreferences prefs, PrefKey key) {
        init(prefs);
        prefs.edit().remove(key.getKey()).apply();
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

    public static long getLong(SharedPreferences prefs, PrefKey key, long fallback) {
        init(prefs);
        return prefs.getLong(key.getKey(), fallback);
    }

    public static void putLong(Context context, PrefKey key, long value) {
        init(context);
        prefs.edit().putLong(key.getKey(), value).apply();
    }

    public static void putLong(SharedPreferences prefs, PrefKey key, long value) {
        init(prefs);
        prefs.edit().putLong(key.getKey(), value).apply();
    }

    public static boolean has(Context context, PrefKey key) {
        init(context);
        return prefs.contains(key.getKey());
    }

    public static boolean has(SharedPreferences prefs, PrefKey key) {
        init(prefs);
        return prefs.contains(key.getKey());
    }

    public static boolean isSetEmpty(Context context, PrefKey key) {
        Set<String> set = getSet(context, key, null);
        return set == null || set.isEmpty();
    }

    public enum Keys implements PrefKey {
        TRACKING_DISABLE("trackingDisable"),
        TUTORIAL_DISCOVERIES("tutorialDiscoveries"),
        NIGHT_MODE("nightModeEnabled"),
        ANALYTICS_USER_ID("analyticsUserId");

        private final String key;

        Keys(String key) {
            this.key = key;
        }

        @NonNull
        @Override
        public String getKey() {
            return key;
        }
    }

    public interface PrefKey {
        @NonNull
        String getKey();
    }
}
