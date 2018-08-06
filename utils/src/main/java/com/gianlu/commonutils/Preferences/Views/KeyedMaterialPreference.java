package com.gianlu.commonutils.Preferences.Views;

import android.support.annotation.NonNull;

import com.yarolegovich.mp.io.StorageModule;

/**
 * Used to have only one {@link com.gianlu.commonutils.Preferences.BasePreferenceFragment#addPreference(KeyedMaterialPreference)}
 */
public interface KeyedMaterialPreference {
    void setStorageModule(@NonNull StorageModule storageModule);
}
