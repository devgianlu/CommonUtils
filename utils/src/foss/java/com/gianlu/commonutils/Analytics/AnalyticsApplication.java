package com.gianlu.commonutils.Analytics;

import android.os.Bundle;

import com.gianlu.commonutils.CommonPK;
import com.gianlu.commonutils.Logging;
import com.gianlu.commonutils.Preferences.Prefs;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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

    @Override
    @CallSuper
    public void onCreate() {
        super.onCreate();

        Prefs.putBoolean(CommonPK.CRASH_REPORT_ENABLED, false);
        Prefs.putBoolean(CommonPK.TRACKING_ENABLED, false);
    }
}
