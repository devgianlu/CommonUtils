package com.gianlu.commonutils.Preferences.Views;

import android.content.Context;
import android.support.annotation.NonNull;

import com.gianlu.commonutils.Preferences.Prefs;
import com.yarolegovich.mp.MaterialSeekBarPreference;

public class KeyedMaterialSeekBarPreference extends MaterialSeekBarPreference implements KeyedMaterialPreference {
    public KeyedMaterialSeekBarPreference(Context context, @NonNull Prefs.PrefKey key, int defaultValue) {
        super(context, null);

        this.key = key.getKey();
        this.defaultValue = Integer.toString(defaultValue);

        // FIXME: Min/max
    }
}
