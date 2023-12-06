package com.gianlu.commonutils.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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

    public static void putBoolean(Key key, boolean value) {
        putBoolean(key.key(), value);
    }

    public static void putBoolean(String key, boolean value) {
        prefs.edit().putBoolean(key, value).apply();
    }

    // putString

    public static void putString(Key key, String value) {
        putString(key.key(), value);
    }

    public static void putString(String key, String value) {
        prefs.edit().putString(key, value).apply();
    }

    // putInt

    public static void putInt(String key, int value) {
        prefs.edit().putInt(key, value).apply();
    }

    public static void putInt(Key key, int value) {
        putInt(key.key(), value);
    }

    // putSet

    public static void putSet(Key key, Set<String> set) {
        putSet(key.key(), set);
    }

    public static void putSet(String key, Set<String> value) {
        prefs.edit().putStringSet(key, value).apply();
    }

    // getBoolean

    public static boolean getBoolean(String key, boolean fallback) {
        return prefs.getBoolean(key, fallback);
    }

    public static boolean getBoolean(Key key, boolean fallback) {
        return getBoolean(key.key(), fallback);
    }

    public static boolean getBoolean(KeyWithDefault<Boolean> key) {
        return getBoolean(key.key(), key.fallback());
    }

    // getString

    public static String getString(String key, String fallback) {
        try {
            return prefs.getString(key, fallback);
        } catch (ClassCastException ex) {
            return String.valueOf(prefs.getInt(key, Integer.parseInt(fallback)));
        }
    }

    public static String getString(Key key, String fallback) {
        return getString(key.key(), fallback);
    }

    public static String getString(KeyWithDefault<String> key) {
        return getString(key.key(), key.fallback());
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

    public static int getInt(Key key, int fallback) {
        return getInt(key.key(), fallback);
    }

    public static int getInt(KeyWithDefault<Integer> key) {
        return getInt(key.key(), key.fallback());
    }

    // getSet

    public static Set<String> getSet(String key, Set<String> fallback) {
        Set<String> set = prefs.getStringSet(key, fallback);
        if (set == null) return null;
        return new HashSet<>(set);
    }

    public static Set<String> getSet(Key key, Set<String> fallback) {
        return getSet(key.key(), fallback);
    }

    public static Set<String> getSet(KeyWithDefault<Set<String>> key) {
        return getSet(key.key(), key.fallback());
    }

    // set operations

    public static boolean isSetEmpty(Key key) {
        return isSetEmpty(key.key());
    }

    public static boolean isSetEmpty(String key) {
        Set<String> set = getSet(key, null);
        return set == null || set.isEmpty();
    }

    public static void removeFromSet(String key, String value) {
        Set<String> set = getSet(key, new HashSet<>());
        set.remove(value);
        prefs.edit().putStringSet(key, set).apply();
    }

    public static void removeFromSet(Key key, String value) {
        removeFromSet(key.key(), value);
    }

    public static void addToSet(String key, String value) {
        Set<String> set = getSet(key, new HashSet<>());
        set.add(value);
        prefs.edit().putStringSet(key, set).apply();
    }

    public static void addToSet(Key key, String value) {
        addToSet(key.key(), value);
    }

    public static boolean setContains(String key, String value) {
        Set<String> set = prefs.getStringSet(key, null);
        return set != null && set.contains(value);
    }

    public static boolean setContains(Key key, String value) {
        return setContains(key.key(), value);
    }

    // remove

    public static void remove(String key) {
        prefs.edit().remove(key).apply();
    }

    public static void remove(Key key) {
        remove(key.key());
    }

    // getLong

    public static long getLong(String key, long fallback) {
        return prefs.getLong(key, fallback);
    }

    public static long getLong(Key key, long fallback) {
        return getLong(key.key(), fallback);
    }

    public static long getLong(KeyWithDefault<Long> key) {
        return getLong(key.key(), key.fallback());
    }

    // putLong

    public static void putLong(String key, long value) {
        prefs.edit().putLong(key, value).apply();
    }

    public static void putLong(Key key, long value) {
        putLong(key.key(), value);
    }

    // has

    public static boolean has(String key) {
        return prefs.contains(key);
    }

    public static boolean has(Key key) {
        return has(key.key());
    }

    public interface DefaultValueProvider<D> {

        @Nullable
        D getDefault();
    }

    public static class Key {
        private final String key;

        public Key(@NonNull String key) {
            this.key = key;
        }

        @NonNull
        public String key() {
            return key;
        }
    }

    public static class KeyWithDefault<D> extends Key {
        private final D fallback;
        private final DefaultValueProvider<D> fallbackProvider;
        private final boolean hasFallback;

        public KeyWithDefault(@NonNull String key, D fallback) {
            super(key);

            this.hasFallback = true;
            this.fallback = fallback;
            this.fallbackProvider = null;
        }

        public KeyWithDefault(@NonNull String key, @NonNull DefaultValueProvider<D> fallback) {
            super(key);

            this.hasFallback = false;
            this.fallback = null;
            this.fallbackProvider = fallback;
        }

        public D fallback() {
            if (hasFallback) return fallback;
            else if (fallbackProvider != null) return fallbackProvider.getDefault();
            else throw new IllegalArgumentException("What?!");
        }
    }
}
