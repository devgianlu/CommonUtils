package com.gianlu.commonutils.analytics;

import android.app.Application;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;

import com.gianlu.commonutils.CommonUtils;
import com.gianlu.commonutils.logging.Logging;
import com.gianlu.commonutils.preferences.CommonPK;
import com.gianlu.commonutils.preferences.Prefs;
import com.gianlu.commonutils.preferences.PrefsStorageModule;
import com.yarolegovich.mp.io.MaterialPreferences;

public abstract class BaseCommonApplication extends Application implements Thread.UncaughtExceptionHandler {

    @SuppressWarnings("deprecation")
    private void deprecatedBackwardCompatibility() {
        if (Prefs.has(CommonPK.TRACKING_DISABLE)) {
            boolean old = Prefs.getBoolean(CommonPK.TRACKING_DISABLE, false);
            Prefs.putBoolean(CommonPK.TRACKING_ENABLED, !old);
            Prefs.putBoolean(CommonPK.CRASH_REPORT_ENABLED, !old);
            Prefs.remove(CommonPK.TRACKING_DISABLE);
        }
    }

    /**
     * Never called in debug.
     */
    @Override
    public final void uncaughtException(@NonNull Thread thread, @NonNull Throwable throwable) {
        Logging.log(throwable);

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
        Logging.init(this);
        Logging.clearLogs(this);
        if (!isDebug()) Thread.setDefaultUncaughtExceptionHandler(this);

        deprecatedBackwardCompatibility();

        MaterialPreferences.instance().setStorageModule(new PrefsStorageModule.Factory());
    }
}
