package com.gianlu.commonutils;

import android.os.Bundle;

import com.gianlu.commonutils.Preferences.Prefs;

import androidx.annotation.CallSuper;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public abstract class NightlyActivity extends AppCompatActivity {

    @Override
    @CallSuper
    protected void onResume() {
        super.onResume();
        applyNight();
    }

    @Override
    @CallSuper
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        applyNight();
        super.onCreate(savedInstanceState);
    }

    public final void applyNight() {
        getDelegate().setLocalNightMode(Prefs.getBoolean(CommonPK.NIGHT_MODE, false) ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_AUTO);
    }
}
