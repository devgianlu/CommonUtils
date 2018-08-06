package com.gianlu.commonutils.Preferences.Views;

import android.content.Context;
import android.support.annotation.NonNull;

import com.gianlu.commonutils.Preferences.Prefs;
import com.yarolegovich.mp.MaterialCheckboxPreference;

public class KeyedMaterialCheckboxPreference extends MaterialCheckboxPreference implements KeyedMaterialPreference {
    public KeyedMaterialCheckboxPreference(Context context, @NonNull Prefs.PrefKey key, boolean defaultValue) {
        super(context, null);

        this.key = key.getKey();
        this.defaultValue = Boolean.toString(defaultValue);
    }
}
