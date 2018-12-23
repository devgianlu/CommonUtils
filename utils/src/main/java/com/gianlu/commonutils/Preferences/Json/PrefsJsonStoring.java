package com.gianlu.commonutils.Preferences.Json;

import org.json.JSONArray;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

final class PrefsJsonStoring extends JsonStoring { // TODO

    PrefsJsonStoring() {
    }

    @Nullable
    @Override
    public JSONObject getJsonObject(@NonNull String key) {
        return null;
    }

    @Nullable
    @Override
    public JSONArray getJsonArray(@NonNull String key) {
        return null;
    }
}
