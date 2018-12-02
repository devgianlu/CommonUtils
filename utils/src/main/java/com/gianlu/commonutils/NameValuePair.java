package com.gianlu.commonutils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class NameValuePair {
    private final String key;
    private final String value;

    public NameValuePair(@NonNull String key, @Nullable String value) {
        this.key = key;
        this.value = value;
    }

    @NonNull
    public static JSONObject toJson(List<NameValuePair> list) throws JSONException {
        JSONObject obj = new JSONObject();
        for (NameValuePair entry : list) obj.put(entry.key, entry.value);
        return obj;
    }

    @Nullable
    public String value() {
        return value;
    }

    @NonNull
    public String key() {
        return key;
    }

    public String value(String fallback) {
        return value == null ? fallback : value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NameValuePair that = (NameValuePair) o;
        return Objects.equals(key, that.key) && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }

    @NonNull
    @Override
    public String toString() {
        return "(" + key + "=" + value + ")";
    }
}
