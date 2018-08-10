package com.gianlu.commonutils.Analytics;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.crashlytics.android.Crashlytics;
import com.gianlu.commonutils.CommonUtils;
import com.gianlu.commonutils.FossUtils;
import com.gianlu.commonutils.Logging;
import com.gianlu.commonutils.Preferences.Prefs;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.UUID;

public abstract class AnalyticsApplication extends Application implements Thread.UncaughtExceptionHandler {
    private FirebaseAnalytics tracker;

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
            if (FossUtils.hasCrashlytics())
                Crashlytics.logException(throwable);

            if (uncaughtNotDebug(thread, throwable))
                UncaughtExceptionActivity.startActivity(this, throwable);
        }
    }

    protected boolean uncaughtNotDebug(Thread thread, Throwable throwable) {
        return true;
    }

    public final void sendAnalytics(String event, @Nullable Bundle bundle) {
        if (tracker != null && event != null && !isDebug() && !Prefs.getBoolean(this, Prefs.Keys.TRACKING_DISABLE, false))
            tracker.logEvent(event, bundle);
    }

    protected abstract boolean isDebug();

    @Override
    @CallSuper
    public void onCreate() {
        super.onCreate();

        CommonUtils.setDebug(isDebug());
        Logging.init(this);
        Logging.clearLogs(this);
        Thread.setDefaultUncaughtExceptionHandler(this);

        if (FossUtils.hasCrashlytics()) {
            String uuid = Prefs.getString(this, Prefs.Keys.ANALYTICS_USER_ID, null);
            if (uuid == null) {
                uuid = UUID.randomUUID().toString();
                Prefs.putString(this, Prefs.Keys.ANALYTICS_USER_ID, uuid);
            }

            Crashlytics.setUserIdentifier(uuid);
        }

        if (FossUtils.hasFirebaseAnalytics()) {
            tracker = FirebaseAnalytics.getInstance(this);
            tracker.setAnalyticsCollectionEnabled(!isDebug() && !Prefs.getBoolean(this, Prefs.Keys.TRACKING_DISABLE, false));
        } else {
            Prefs.putBoolean(this, Prefs.Keys.TRACKING_DISABLE, true);
        }
    }
}
