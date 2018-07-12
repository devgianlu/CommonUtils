package com.gianlu.commonutils;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;

import com.gianlu.commonutils.Preferences.Prefs;

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
        getDelegate().setLocalNightMode(Prefs.getBoolean(this, Prefs.Keys.NIGHT_MODE, false) ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_AUTO);
    }
}
