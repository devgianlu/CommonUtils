package com.gianlu.commonutils.Analytics;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.gianlu.commonutils.CommonPK;
import com.gianlu.commonutils.CommonUtils;
import com.gianlu.commonutils.Logging;
import com.gianlu.commonutils.Preferences.Prefs;
import com.gianlu.commonutils.Preferences.PrefsStorageModule;
import com.yarolegovich.mp.io.MaterialPreferences;

public abstract class AnalyticsApplication extends Application implements Thread.UncaughtExceptionHandler {

    public static void sendAnalytics(Context context, @NonNull String event, @Nullable Bundle bundle) {
        AnalyticsApplication app = get(context);
        if (app != null) app.sendAnalytics(event, bundle);
    }

    public static void sendAnalytics(Context context, @NonNull String event) {
        sendAnalytics(context, event, null);
    }

    @Nullable
    public static AnalyticsApplication get(Context context) {
        if (context == null) return null;
        Context app = context.getApplicationContext();
        if (app instanceof AnalyticsApplication) return (AnalyticsApplication) app;
        else return null;
    }

    @Override
    public final void uncaughtException(Thread thread, Throwable throwable) {
        Logging.log(throwable);

        if (!CommonUtils.isDebug()) {
            if (uncaughtNotDebug(thread, throwable))
                UncaughtExceptionActivity.startActivity(this, throwable);
        }
    }

    protected boolean uncaughtNotDebug(Thread thread, Throwable throwable) {
        return true;
    }

    public final void sendAnalytics(String event, @Nullable Bundle bundle) {
        Logging.log("Dropping event " + event + " because of foss version.", true);
    }

    protected abstract boolean isDebug();

    @SuppressWarnings("deprecation")
    private void deprecatedBackwardCompatibility() {
        if (Prefs.has(CommonPK.TRACKING_DISABLE)) {
            boolean old = Prefs.getBoolean(CommonPK.TRACKING_DISABLE, false);
            Prefs.putBoolean(CommonPK.TRACKING_ENABLED, !old);
            Prefs.putBoolean(CommonPK.CRASH_REPORT_ENABLED, !old);
            Prefs.remove(CommonPK.TRACKING_DISABLE);
        }
    }

    @Override
    @CallSuper
    public void onCreate() {
        super.onCreate();

        Prefs.init(this);

        CommonUtils.setDebug(isDebug());
        Logging.init(this);
        Logging.clearLogs(this);
        Thread.setDefaultUncaughtExceptionHandler(this);

        deprecatedBackwardCompatibility();

        Prefs.putBoolean(CommonPK.CRASH_REPORT_ENABLED, false);

        Prefs.putBoolean(CommonPK.TRACKING_ENABLED, false);

        MaterialPreferences.instance().setStorageModule(new PrefsStorageModule.Factory());
    }
}
