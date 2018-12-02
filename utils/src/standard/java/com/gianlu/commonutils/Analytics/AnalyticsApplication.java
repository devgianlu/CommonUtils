package com.gianlu.commonutils.Analytics;

import android.os.Bundle;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.crashlytics.android.core.CrashlyticsCore;
import com.gianlu.commonutils.CommonPK;
import com.gianlu.commonutils.FossUtils;
import com.gianlu.commonutils.Preferences.Prefs;

import java.util.UUID;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.fabric.sdk.android.Fabric;

public abstract class AnalyticsApplication extends BaseCommonApplication {

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
        Crashlytics.setString(key, val);
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
        } else {
            Prefs.putBoolean(CommonPK.CRASH_REPORT_ENABLED, false);
        }
    }
}
