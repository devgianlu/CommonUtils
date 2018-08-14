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

@SuppressWarnings("unused")
public final class Prefs {
    private static SharedPreferences prefs;

    // init

    public static void init(Context context) {
        if (prefs != null) return;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void init(SharedPreferences prefs) {
        if (Prefs.prefs != null) return;
        Prefs.prefs = prefs;
    }

    // putBoolean

    public static void putBoolean(PrefKey key, boolean value) {
        putBoolean(key.getKey(), value);
    }

    public static void putBoolean(String key, boolean value) {
        prefs.edit().putBoolean(key, value).apply();
    }

    // putString

    public static void putString(PrefKey key, String value) {
        putString(key.getKey(), value);
    }

    public static void putString(String key, String value) {
        prefs.edit().putString(key, value).apply();
    }

    // putInt

    public static void putInt(String key, int value) {
        prefs.edit().putInt(key, value).apply();
    }

    public static void putInt(PrefKey key, int value) {
        putInt(key.getKey(), value);
    }

    // putSet

    public static void putSet(Context context, PrefKey key, Set<String> set) {
        putSet(key.getKey(), set);
    }

    public static void putSet(String key, Set<String> value) {
        prefs.edit().putStringSet(key, value).apply();
    }

    // getBoolean

    public static boolean getBoolean(String key, boolean fallback) {
        return prefs.getBoolean(key, fallback);
    }

    public static boolean getBoolean(PrefKey key, boolean fallback) {
        return getBoolean(key.getKey(), fallback);
    }

    // getString

    public static String getString(String key, String fallback) {
        return prefs.getString(key, fallback);
    }

    public static String getString(PrefKey key, String fallback) {
        return getString(key.getKey(), fallback);
    }

    // getInt

    public static int getInt(String key, int fallback) {
        try {
            return prefs.getInt(key, fallback);
        } catch (ClassCastException ex) {
            try {
                return Integer.parseInt(prefs.getString(key, Integer.toString(fallback)));
            } catch (NumberFormatException exx) {
                return fallback;
            }
        }
    }

    public static int getInt(PrefKey key, int fallback) {
        return getInt(key.getKey(), fallback);
    }

    // getSet

    public static Set<String> getSet(String key, Set<String> fallback) {
        Set<String> set = prefs.getStringSet(key, fallback);
        if (set == null) return null;
        return new HashSet<>(set);
    }

    public static Set<String> getSet(PrefKey key, Set<String> fallback) {
        return getSet(key.getKey(), fallback);
    }

    // getJSONArray

    public static JSONArray getJSONArray(String key, JSONArray fallback) throws JSONException {
        return new JSONArray(getBase64String(key, fallback.toString()));
    }

    public static JSONArray getJSONArray(PrefKey key, JSONArray fallback) throws JSONException {
        return getJSONArray(key.getKey(), fallback);
    }

    // putJSONArray

    public static void putJSONArray(PrefKey key, JSONArray value) {
        putJSONArray(key.getKey(), value);
    }

    public static void putJSONArray(String key, JSONArray value) {
        putBase64String(key, value.toString());
    }

    // getBase64String

    public static String getBase64String(PrefKey key, String fallback) {
        return getBase64String(key.getKey(), fallback);
    }

    public static String getBase64String(String key, String fallback) {
        return new String(Base64.decode(prefs.getString(key, Base64.encodeToString(fallback.getBytes(), Base64.NO_WRAP)).getBytes(), Base64.NO_WRAP));
    }

    // putBase64String

    public static void putBase64String(PrefKey key, String value) {
        putBase64String(key.getKey(), value);
    }

    public static void putBase64String(String key, String value) {
        prefs.edit().putString(key, Base64.encodeToString(value.getBytes(), Base64.NO_WRAP)).apply();
    }

    // set operations

    public static boolean isSetEmpty(PrefKey key) {
        return isSetEmpty(key.getKey());
    }

    public static boolean isSetEmpty(String key) {
        Set<String> set = getSet(key, null);
        return set == null || set.isEmpty();
    }

    public static void removeFromSet(String key, String value) {
        Set<String> set = getSet(key, new HashSet<String>());
        set.remove(value);
        prefs.edit().putStringSet(key, set).apply();
    }

    public static void removeFromSet(PrefKey key, String value) {
        removeFromSet(key.getKey(), value);
    }

    public static void addToSet(String key, String value) {
        Set<String> set = getSet(key, new HashSet<String>());
        if (!set.contains(value)) set.add(value);
        prefs.edit().putStringSet(key, set).apply();
    }

    public static void addToSet(PrefKey key, String value) {
        addToSet(key.getKey(), value);
    }

    public static boolean setContains(String key, String value) {
        Set<String> set = prefs.getStringSet(key, null);
        return set != null && set.contains(value);
    }

    public static boolean setContains(PrefKey key, String value) {
        return setContains(key.getKey(), value);
    }

    // remove

    public static void remove(String key) {
        prefs.edit().remove(key).apply();
    }

    public static void remove(PrefKey key) {
        remove(key.getKey());
    }

    // getLong

    public static long getLong(String key, long fallback) {
        return prefs.getLong(key, fallback);
    }

    public static long getLong(PrefKey key, long fallback) {
        return getLong(key.getKey(), fallback);
    }

    // putLong

    public static void putLong(String key, long value) {
        prefs.edit().putLong(key, value).apply();
    }

    public static void putLong(PrefKey key, long value) {
        putLong(key.getKey(), value);
    }

    // has

    public static boolean has(String key) {
        return prefs.contains(key);
    }

    public static boolean has(PrefKey key) {
        return has(key.getKey());
    }

    public enum Keys implements PrefKey {
        @Deprecated
        TRACKING_DISABLE("trackingDisable"),
        TUTORIAL_DISCOVERIES("tutorialDiscoveries"),
        NIGHT_MODE("nightModeEnabled"),
        ANALYTICS_USER_ID("analyticsUserId"),
        TRACKING_ENABLED("trackingEnabled"),
        CRASH_REPORT_ENABLED("crashReportEnabled");

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
