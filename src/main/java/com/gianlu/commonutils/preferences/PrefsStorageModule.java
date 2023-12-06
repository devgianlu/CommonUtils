package com.gianlu.commonutils.preferences;

import android.content.Context;

import androidx.annotation.NonNull;

import com.yarolegovich.mp.io.StorageModule;

import java.util.Set;

public class PrefsStorageModule implements StorageModule {

    @Override
    public void saveBoolean(@NonNull String key, boolean value) {
        Prefs.putBoolean(key, value);
    }

    @Override
    public void saveString(@NonNull String key, String value) {
        Prefs.putString(key, value);
    }

    @Override
    public void saveInt(@NonNull String key, int value) {
        Prefs.putInt(key, value);
    }

    @Override
    public void saveStringSet(@NonNull String key, Set<String> value) {
        Prefs.putSet(key, value);
    }

    @Override
    public boolean getBoolean(@NonNull String key, boolean defaultVal) {
        return Prefs.getBoolean(key, defaultVal);
    }

    @Override
    public String getString(@NonNull String key, String defaultVal) {
        return Prefs.getString(key, defaultVal);
    }

    @Override
    public int getInt(@NonNull String key, int defaultVal) {
        return Prefs.getInt(key, defaultVal);
    }

    @Override
    public Set<String> getStringSet(@NonNull String key, Set<String> defaultVal) {
        return Prefs.getSet(key, defaultVal);
    }

    public static class Factory implements StorageModule.Factory {

        @NonNull
        @Override
        public StorageModule create(@NonNull Context context) {
            return new PrefsStorageModule();
        }
    }
}
