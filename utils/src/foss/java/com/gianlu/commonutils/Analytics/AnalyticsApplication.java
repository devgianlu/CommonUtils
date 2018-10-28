package com.gianlu.commonutils.Analytics;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.gianlu.commonutils.CommonPK;
import com.gianlu.commonutils.Logging;
import com.gianlu.commonutils.Preferences.Prefs;

public abstract class AnalyticsApplication extends BaseCommonApplication {

    public static void sendAnalytics(String event, @Nullable Bundle bundle) {
        Logging.log("Dropping event " + event + " because of foss version.", true);
    }

    public static void sendAnalytics(@NonNull String event) {
        sendAnalytics(event, null);
    }

    @Override
    @CallSuper
    public void uncaughtException(Thread thread, Throwable throwable) {
    }

    @Override
    @CallSuper
    public void onCreate() {
        super.onCreate();

        Prefs.putBoolean(CommonPK.CRASH_REPORT_ENABLED, false);
        Prefs.putBoolean(CommonPK.TRACKING_ENABLED, false);
    }
}
