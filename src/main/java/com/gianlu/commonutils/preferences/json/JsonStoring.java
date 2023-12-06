package com.gianlu.commonutils.preferences.json;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.LruCache;

import com.gianlu.commonutils.preferences.Prefs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public abstract class JsonStoring {
    private static JsonStoring intoPrefsDefault;
    private final static LruCache<File, JsonStoring> intoFileCache = new LruCache<>(5);

    JsonStoring() {
    }

    @NonNull
    public static JsonStoring intoPrefs() {
        if (intoPrefsDefault == null) intoPrefsDefault = new PrefsJsonStoring();
        return intoPrefsDefault;
    }

    @NonNull
    public static JsonStoring intoFile(@NonNull File file) {
        JsonStoring storing;
        if ((storing = intoFileCache.get(file)) == null) {
            storing = new FileJsonStoring(file);
            intoFileCache.put(file, storing);
        }

        return storing;
    }

    public boolean isJsonArrayEmpty(@NonNull String key) throws JSONException {
        JSONArray array = getJsonArray(key);
        if (array == null) return true;
        else return array.length() == 0;
    }

    public final boolean isJsonArrayEmpty(@NonNull Prefs.Key key) throws JSONException {
        return isJsonArrayEmpty(key.key());
    }

    @Nullable
    public abstract JSONObject getJsonObject(@NonNull String key) throws JSONException;

    @Nullable
    public abstract JSONArray getJsonArray(@NonNull String key) throws JSONException;

    @Nullable
    public final JSONArray getJsonArray(@NonNull Prefs.Key key) throws JSONException {
        return getJsonArray(key.key());
    }

    @Nullable
    public final JSONObject getJsonObject(@NonNull Prefs.Key key) throws JSONException {
        return getJsonObject(key.key());
    }

    public abstract void putJsonArray(@NonNull String key, JSONArray array) throws JSONException;

    public abstract void putJsonObject(@NonNull String key, JSONObject obj) throws JSONException;

    public final void putJsonArray(@NonNull Prefs.Key key, JSONArray array) throws JSONException {
        putJsonArray(key.key(), array);
    }

    public final void putJsonObject(@NonNull Prefs.Key key, JSONObject obj) throws JSONException {
        putJsonObject(key.key(), obj);
    }
}
