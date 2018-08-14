package com.gianlu.commonutils.Preferences;

import android.content.Context;
import android.os.Bundle;

import com.yarolegovich.mp.io.StorageModule;

import java.util.Set;

public class PrefsStorageModule implements StorageModule {

    @Override
    public void saveBoolean(String key, boolean value) {
        Prefs.putBoolean(key, value);
    }

    @Override
    public void saveString(String key, String value) {
        Prefs.putString(key, value);
    }

    @Override
    public void saveInt(String key, int value) {
        Prefs.putInt(key, value);
    }

    @Override
    public void saveStringSet(String key, Set<String> value) {
        Prefs.putSet(key, value);
    }

    @Override
    public boolean getBoolean(String key, boolean defaultVal) {
        return Prefs.getBoolean(key, defaultVal);
    }

    @Override
    public String getString(String key, String defaultVal) {
        return Prefs.getString(key, defaultVal);
    }

    @Override
    public int getInt(String key, int defaultVal) {
        return Prefs.getInt(key, defaultVal);
    }

    @Override
    public Set<String> getStringSet(String key, Set<String> defaultVal) {
        return Prefs.getSet(key, defaultVal);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
    }

    @Override
    public void onRestoreInstanceState(Bundle savedState) {
    }

    public static class Factory implements StorageModule.Factory {

        @Override
        public StorageModule create(Context context) {
            return new PrefsStorageModule();
        }
    }
}
