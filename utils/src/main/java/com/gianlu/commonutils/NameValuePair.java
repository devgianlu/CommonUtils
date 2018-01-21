package com.gianlu.commonutils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class NameValuePair {
    private final String key;
    private final String value;

    public NameValuePair(@NonNull String key, @Nullable String value) {
        this.key = key;
        this.value = value;
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
    public String toString() {
        return "(" + key + "=" + value + ")";
    }
}
