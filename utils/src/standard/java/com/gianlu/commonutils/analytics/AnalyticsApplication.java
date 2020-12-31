package com.gianlu.commonutils.analytics;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.gianlu.commonutils.CommonUtils;
import com.gianlu.commonutils.FossUtils;
import com.gianlu.commonutils.preferences.CommonPK;
import com.gianlu.commonutils.preferences.Prefs;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.util.UUID;

public abstract class AnalyticsApplication extends BaseCommonApplication {
    private static final String TAG = AnalyticsApplication.class.getSimpleName();
    private static FirebaseAnalytics ANALYTICS;
    private static boolean CRASHLYTICS_ENABLED = false;

    public static void sendAnalytics(@NonNull String eventName) {
        sendAnalytics(eventName, null);
    }

    public static void sendAnalytics(@NonNull String eventName, @Nullable Bundle bundle) {
        if (ANALYTICS != null) ANALYTICS.logEvent(eventName, bundle);
    }

    public static void setUserProperty(@NonNull String key, boolean value) {
        setUserProperty(key, String.valueOf(value));
    }

    public static void setUserProperty(@NonNull String key, @Nullable String value) {
        if (key.isEmpty() || key.length() > 24 || (value != null && value.length() > 36)) {
            Log.w(TAG, "Cannot set user property " + key + ": " + value);
            return;
        }

        if (ANALYTICS != null) ANALYTICS.setUserProperty(key, value);
    }

    public static void setCrashlyticsString(@NonNull String key, @NonNull String val) {
        if (CRASHLYTICS_ENABLED) FirebaseCrashlytics.getInstance().setCustomKey(key, val);
    }

    public static void setCrashlyticsInt(@NonNull String key, int val) {
        if (CRASHLYTICS_ENABLED) FirebaseCrashlytics.getInstance().setCustomKey(key, val);
    }

    public static void setCrashlyticsLong(@NonNull String key, long val) {
        if (CRASHLYTICS_ENABLED) FirebaseCrashlytics.getInstance().setCustomKey(key, val);
    }

    public static void setCrashlyticsBool(@NonNull String key, boolean val) {
        if (CRASHLYTICS_ENABLED) FirebaseCrashlytics.getInstance().setCustomKey(key, val);
    }

    public static void crashlyticsLog(@NonNull String msg) {
        if (CRASHLYTICS_ENABLED) FirebaseCrashlytics.getInstance().log(msg);
    }

    @Override
    protected boolean uncaughtNotDebug(Thread thread, Throwable throwable) {
        if (CRASHLYTICS_ENABLED) FirebaseCrashlytics.getInstance().recordException(throwable);
        return true;
    }

    @Override
    @CallSuper
    public void onCreate() {
        super.onCreate();

        String uuid = Prefs.getString(CommonPK.ANALYTICS_USER_ID, null);
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
            Prefs.putString(CommonPK.ANALYTICS_USER_ID, uuid);
        }

        if (FossUtils.hasFirebaseCrashlytics()) {
            if (Prefs.getBoolean(CommonPK.CRASH_REPORT_ENABLED) && !CommonUtils.isDebug()) {
                FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
                FirebaseCrashlytics.getInstance().setUserId(uuid);
                CRASHLYTICS_ENABLED = true;
            } else {
                FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(false);
                CRASHLYTICS_ENABLED = false;
            }
        } else {
            Prefs.putBoolean(CommonPK.CRASH_REPORT_ENABLED, false);
        }

        if (FossUtils.hasFirebaseAnalytics()) {
            ANALYTICS = FirebaseAnalytics.getInstance(this);
            if (Prefs.getBoolean(CommonPK.TRACKING_ENABLED) && !CommonUtils.isDebug()) {
                ANALYTICS.setUserId(uuid);
                ANALYTICS.setAnalyticsCollectionEnabled(true);
            } else {
                ANALYTICS.setAnalyticsCollectionEnabled(false);
                ANALYTICS = null;
            }
        } else {
            Prefs.putBoolean(CommonPK.TRACKING_ENABLED, false);
        }
    }
}
