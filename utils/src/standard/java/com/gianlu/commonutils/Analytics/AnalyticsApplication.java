package com.gianlu.commonutils.Analytics;

import android.os.Bundle;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.crashlytics.android.core.CrashlyticsCore;
import com.gianlu.commonutils.CommonPK;
import com.gianlu.commonutils.FossUtils;
import com.gianlu.commonutils.Preferences.Prefs;

import java.util.UUID;

import io.fabric.sdk.android.Fabric;

public abstract class AnalyticsApplication extends BaseCommonApplication {
    private static volatile boolean CRASHLYTICS_READY = false;

    public static void sendAnalytics(@NonNull String eventName) {
        sendAnalytics(eventName, null);
    }

    public static void sendAnalytics(@NonNull String eventName, @Nullable Bundle bundle) {
        CustomEvent event = new CustomEvent(eventName);
        if (bundle != null) {
            for (String key : bundle.keySet()) {
                Object val = bundle.get(key);
                if (val instanceof String)
                    event.putCustomAttribute(key, (String) val);
                else if (val instanceof Number)
                    event.putCustomAttribute(key, (Number) val);
            }
        }

        Answers.getInstance().logCustom(event);
    }

    public static void setCrashlyticsString(@NonNull String key, @NonNull String val) {
        if (CRASHLYTICS_READY) Crashlytics.setString(key, val);
    }

    public static void setCrashlyticsInt(@NonNull String key, int val) {
        if (CRASHLYTICS_READY) Crashlytics.setInt(key, val);
    }

    public static void setCrashlyticsLong(@NonNull String key, long val) {
        if (CRASHLYTICS_READY) Crashlytics.setLong(key, val);
    }

    public static void setCrashlyticsBool(@NonNull String key, boolean val) {
        if (CRASHLYTICS_READY) Crashlytics.setBool(key, val);
    }

    public static void crashlyticsLog(@NonNull String msg) {
        if (CRASHLYTICS_READY) Crashlytics.log(msg);
    }

    @Override
    @CallSuper
    public void onCreate() {
        super.onCreate();

        if (FossUtils.hasFabric()) {
            Fabric.with(new Fabric.Builder(this)
                    .kits(new Crashlytics.Builder()
                            .answers(new Answers())
                            .core(new CrashlyticsCore.Builder()
                                    .disabled(isDebug() || !Prefs.getBoolean(CommonPK.CRASH_REPORT_ENABLED, true))
                                    .build())
                            .build())
                    .build());

            String uuid = Prefs.getString(CommonPK.ANALYTICS_USER_ID, null);
            if (uuid == null) {
                uuid = UUID.randomUUID().toString();
                Prefs.putString(CommonPK.ANALYTICS_USER_ID, uuid);
            }

            Crashlytics.setUserIdentifier(uuid);
            CRASHLYTICS_READY = true;
        } else {
            Prefs.putBoolean(CommonPK.CRASH_REPORT_ENABLED, false);
            CRASHLYTICS_READY = false;
        }
    }
}
