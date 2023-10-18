package com.gianlu.commonutils.analytics;

import android.app.Application;
import android.util.Log;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
            UncaughtExceptionActivity.startActivity(this, getGithubProjectName(), throwable);
    }

    /**
     * Handle an uncaught exception in a non-debug environment.
     *
     * @param thread    The thread the exception happened on
     * @param throwable The exception
     * @return Whether the {@link UncaughtExceptionActivity} should be shown
     */
    protected boolean uncaughtNotDebug(Thread thread, Throwable throwable) {
        return true;
    }

    protected abstract boolean isDebug();

    @Nullable
    protected abstract String getGithubProjectName();

    @Override
    @CallSuper
    public void onCreate() {
        super.onCreate();
        CommonUtils.setDebug(isDebug());

        // Set custom uncaught exception handler
        if (!isDebug()) Thread.setDefaultUncaughtExceptionHandler(this);

        // Init prefs
        Prefs.init(this);
        MaterialPreferences.setStorageModule(new PrefsStorageModule.Factory());
    }
}
