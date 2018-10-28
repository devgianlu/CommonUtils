package com.gianlu.commonutils.Analytics;

import android.app.Application;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.crashlytics.android.core.CrashlyticsCore;
import com.gianlu.commonutils.CommonPK;
import com.gianlu.commonutils.CommonUtils;
import com.gianlu.commonutils.FossUtils;
import com.gianlu.commonutils.Logging;
import com.gianlu.commonutils.Preferences.Prefs;
import com.gianlu.commonutils.Preferences.PrefsStorageModule;
import com.yarolegovich.mp.io.MaterialPreferences;

import java.util.UUID;

import io.fabric.sdk.android.Fabric;

public abstract class AnalyticsApplication extends Application implements Thread.UncaughtExceptionHandler {

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

    @Override
    public final void uncaughtException(Thread thread, Throwable throwable) {
        Logging.log(throwable);

        if (!CommonUtils.isDebug()) {
            if (FossUtils.hasFabric())
                Crashlytics.logException(throwable);

            UncaughtExceptionActivity.startActivity(this, throwable);
        }
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
        } else {
            Prefs.putBoolean(CommonPK.CRASH_REPORT_ENABLED, false);
        }

        MaterialPreferences.instance().setStorageModule(new PrefsStorageModule.Factory());
    }
}
