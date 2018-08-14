package com.gianlu.commonutils.Analytics;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.gianlu.commonutils.CommonUtils;
import com.gianlu.commonutils.FossUtils;
import com.gianlu.commonutils.Logging;
import com.gianlu.commonutils.Preferences.Prefs;
import com.gianlu.commonutils.Preferences.PrefsStorageModule;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.yarolegovich.mp.io.MaterialPreferences;

import java.util.UUID;

import io.fabric.sdk.android.Fabric;

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
        if (tracker != null && event != null && !isDebug() && Prefs.getBoolean(Prefs.Keys.TRACKING_ENABLED, true))
            tracker.logEvent(event, bundle);
    }

    protected abstract boolean isDebug();

    @SuppressWarnings("deprecation")
    private void deprecatedBackwardCompatibility() {
        if (Prefs.has(Prefs.Keys.TRACKING_DISABLE)) {
            boolean old = Prefs.getBoolean(Prefs.Keys.TRACKING_DISABLE, false);
            Prefs.putBoolean(Prefs.Keys.TRACKING_ENABLED, !old);
            Prefs.putBoolean(Prefs.Keys.CRASH_REPORT_ENABLED, !old);
            Prefs.remove(Prefs.Keys.TRACKING_DISABLE);
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

        if (FossUtils.hasCrashlytics()) {
            Fabric.with(this, new Crashlytics.Builder()
                    .core(new CrashlyticsCore.Builder()
                            .disabled(isDebug() || !Prefs.getBoolean(Prefs.Keys.CRASH_REPORT_ENABLED, true))
                            .build())
                    .build());

            String uuid = Prefs.getString(Prefs.Keys.ANALYTICS_USER_ID, null);
            if (uuid == null) {
                uuid = UUID.randomUUID().toString();
                Prefs.putString(Prefs.Keys.ANALYTICS_USER_ID, uuid);
            }

            Crashlytics.setUserIdentifier(uuid);
        } else {
            Prefs.putBoolean(Prefs.Keys.CRASH_REPORT_ENABLED, false);
        }

        if (FossUtils.hasFirebaseAnalytics()) {
            tracker = FirebaseAnalytics.getInstance(this);
            tracker.setAnalyticsCollectionEnabled(!isDebug() && Prefs.getBoolean(Prefs.Keys.TRACKING_ENABLED, true));
        } else {
            Prefs.putBoolean(Prefs.Keys.TRACKING_ENABLED, false);
        }

        MaterialPreferences.instance().setStorageModule(new PrefsStorageModule.Factory());
    }
}
