package com.gianlu.commonutils.Preferences.Json;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

final class FileJsonStoring extends JsonStoring { // TODO
    private final File file;

    FileJsonStoring(@NonNull File file) {
        this.file = file;
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
