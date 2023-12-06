package com.gianlu.commonutils.ui;

import android.os.Bundle;

import androidx.annotation.CallSuper;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.gianlu.commonutils.preferences.CommonPK;
import com.gianlu.commonutils.preferences.Prefs;

public abstract class NightlyActivity extends AppCompatActivity {

    @Override
    @CallSuper
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        applyNight();
        super.onCreate(savedInstanceState);
    }

    public final void applyNight() {
        getDelegate().setLocalNightMode(Prefs.getBoolean(CommonPK.NIGHT_MODE, false) ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_UNSPECIFIED);
    }
}
