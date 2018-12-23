package com.gianlu.commonutils.Preferences.Json;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.LruCache;

public abstract class JsonStoring {
    private static JsonStoring intoPrefsDefault;
    private static LruCache<File, JsonStoring> intoFileCache = new LruCache<>(5);

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

    @Nullable
    public abstract JSONObject getJsonObject(@NonNull String key);

    @Nullable
    public abstract JSONArray getJsonArray(@NonNull String key);
}
