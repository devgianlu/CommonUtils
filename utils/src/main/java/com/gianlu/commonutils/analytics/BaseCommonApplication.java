package com.gianlu.commonutils.analytics;

import android.app.Application;
import android.util.Log;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;

import com.gianlu.commonutils.CommonUtils;
import com.gianlu.commonutils.preferences.Prefs;
import com.gianlu.commonutils.preferences.PrefsStorageModule;
import com.yarolegovich.mp.io.MaterialPreferences;

public abstract class BaseCommonApplication extends Application implements Thread.UncaughtExceptionHandler {
    private static final String TAG = BaseCommonApplication.class.getSimpleName();

    /**
     * Never called in debug.
     */
    @Override
    public final void uncaughtException(@NonNull Thread thread, @NonNull Throwable throwable) {
        Log.wtf(TAG, throwable);

        if (uncaughtNotDebug(thread, throwable))
            UncaughtExceptionActivity.startActivity(this, throwable);
    }

    protected boolean uncaughtNotDebug(Thread thread, Throwable throwable) {
        return true;
    }

    protected abstract boolean isDebug();

    @Override
    @CallSuper
    public void onCreate() {
        super.onCreate();

        Prefs.init(this);

        CommonUtils.setDebug(isDebug());
        if (!isDebug()) Thread.setDefaultUncaughtExceptionHandler(this);

        MaterialPreferences.setStorageModule(new PrefsStorageModule.Factory());
    }
}
