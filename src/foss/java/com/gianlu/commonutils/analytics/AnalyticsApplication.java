package com.gianlu.commonutils.analytics;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.gianlu.commonutils.preferences.CommonPK;
import com.gianlu.commonutils.preferences.Prefs;

import java.util.Locale;

public abstract class AnalyticsApplication extends BaseCommonApplication {
    private static final String TAG = AnalyticsApplication.class.getSimpleName();

    public static void sendAnalytics(String event, @Nullable Bundle bundle) {
        Log.d(TAG, String.format("(event: %s, bundle: %s)", event, bundle == null ? null : bundle.toString()));
    }

    public static void sendAnalytics(@NonNull String event) {
        sendAnalytics(event, null);
    }

    public static void setUserProperty(@NonNull String key, boolean value) {
        setUserProperty(key, String.valueOf(value));
    }

    public static void setUserProperty(@NonNull String key, @Nullable String value) {
        Log.d(TAG, String.format("User property (key: %s, value: %s)", key, value));
    }

    public static void setCrashlyticsString(@NonNull String key, @NonNull String val) {
        Log.d(TAG, String.format("(key: %s, value: %s)", key, val));
    }

    public static void setCrashlyticsInt(@NonNull String key, int val) {
        Log.d(TAG, String.format(Locale.getDefault(), "(key: %s, value: %d)", key, val));
    }

    public static void setCrashlyticsBool(@NonNull String key, boolean val) {
        Log.d(TAG, String.format("(key: %s, value: %b)", key, val));
    }

    public static void setCrashlyticsLong(@NonNull String key, long val) {
        Log.d(TAG, String.format(Locale.getDefault(), "(key: %s, value: %d)", key, val));
    }

    public static void crashlyticsLog(@NonNull String msg) {
    }

    @Override
    @CallSuper
    public void onCreate() {
        super.onCreate();

        Prefs.putBoolean(CommonPK.CRASH_REPORT_ENABLED, false);
        Prefs.putBoolean(CommonPK.TRACKING_ENABLED, false);
    }
}
