package com.gianlu.commonutils.Preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.yarolegovich.mp.io.StorageModule;

import java.util.Set;

public class PrefsStorageModule implements StorageModule {
    private final SharedPreferences prefs;

    public PrefsStorageModule(SharedPreferences prefs) {
        this.prefs = prefs;
    }

    @Override
    public void saveBoolean(String key, boolean value) {
        Prefs.putBoolean(prefs, key, value);
    }

    @Override
    public void saveString(String key, String value) {
        Prefs.putString(prefs, key, value);
    }

    @Override
    public void saveInt(String key, int value) {
        Prefs.putInt(prefs, key, value);
    }

    @Override
    public void saveStringSet(String key, Set<String> value) {
        Prefs.putSet(prefs, key, value);
    }

    @Override
    public boolean getBoolean(String key, boolean defaultVal) {
        return Prefs.getBoolean(prefs, key, defaultVal);
    }

    @Override
    public String getString(String key, String defaultVal) {
        return Prefs.getString(prefs, key, defaultVal);
    }

    @Override
    public int getInt(String key, int defaultVal) {
        return Prefs.getInt(prefs, key, defaultVal);
    }

    @Override
    public Set<String> getStringSet(String key, Set<String> defaultVal) {
        return Prefs.getSet(prefs, key, defaultVal);
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
            return new PrefsStorageModule(PreferenceManager.getDefaultSharedPreferences(context));
        }
    }
}
