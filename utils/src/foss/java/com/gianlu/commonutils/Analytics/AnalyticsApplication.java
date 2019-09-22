package com.gianlu.commonutils.analytics;

import android.os.Bundle;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.gianlu.commonutils.CommonPK;
import com.gianlu.commonutils.Logging;
import com.gianlu.commonutils.preferences.Prefs;

public abstract class AnalyticsApplication extends BaseCommonApplication {

    public static void sendAnalytics(String event, @Nullable Bundle bundle) {
        Logging.log(String.format("(event: %s, bundle: %s)", event, bundle == null ? null : bundle.toString()), false);
    }

    public static void sendAnalytics(@NonNull String event) {
        sendAnalytics(event, null);
    }

    public static void setCrashlyticsString(@NonNull String key, @NonNull String val) {
        Logging.log(String.format("(key: %s, value: %s)", key, val), false);
    }

    public static void setCrashlyticsInt(@NonNull String key, int val) {
        Logging.log(String.format("(key: %s, value: %d)", key, val), false);
    }

    public static void setCrashlyticsBool(@NonNull String key, boolean val) {
        Logging.log(String.format("(key: %s, value: %b)", key, val), false);
    }

    public static void setCrashlyticsLong(@NonNull String key, long val) {
        Logging.log(String.format("(key: %s, value: %d)", key, val), false);
    }

    public static void crashlyticsLog(@NonNull String msg) {
        // Do nothing, this is called only from Logging#log
    }

    @Override
    @CallSuper
    public void onCreate() {
        super.onCreate();

        Prefs.putBoolean(CommonPK.CRASH_REPORT_ENABLED, false);
        Prefs.putBoolean(CommonPK.TRACKING_ENABLED, false);
    }
}
