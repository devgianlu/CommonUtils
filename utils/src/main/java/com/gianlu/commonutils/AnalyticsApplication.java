package com.gianlu.commonutils;

import android.app.Application;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.annotation.XmlRes;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.io.PrintWriter;
import java.io.StringWriter;

public abstract class AnalyticsApplication extends Application implements Thread.UncaughtExceptionHandler {
    private Tracker tracker;

    public static void sendAnalytics(Context context, HitBuilders.HitBuilder<?> builder) {
        AnalyticsApplication app = get(context);
        if (app != null) app.sendAnalytics(builder);
    }

    private static String getAppName(Context context) {
        int id = context.getResources().getIdentifier("app_name", "string", context.getPackageName());
        if (id == 0) return context.getString(com.gianlu.commonutils.R.string.unknown);
        else return context.getString(id);
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
        if (CommonUtils.isDebug()) {
            throwable.printStackTrace();
        } else {
            StringWriter writer = new StringWriter();
            throwable.printStackTrace(new PrintWriter(writer));

            sendAnalytics(this, new HitBuilders.ExceptionBuilder()
                    .setDescription(writer.toString())
                    .setFatal(true));

            UncaughtExceptionActivity.startActivity(this, getAppName(this), throwable);
        }
    }

    public final void sendAnalytics(HitBuilders.HitBuilder<?> builder) {
        if (tracker != null && builder != null) tracker.send(builder.build());
    }

    protected abstract boolean isDebug();

    @XmlRes
    protected abstract int getTrackerConfiguration();

    @Override
    public void onCreate() {
        super.onCreate();

        CommonUtils.setDebug(isDebug());
        Logging.init(this);
        Thread.setDefaultUncaughtExceptionHandler(this);

        if (!isDebug()) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(getApplicationContext());
            analytics.enableAutoActivityReports(this);
            tracker = analytics.newTracker(getTrackerConfiguration());
            tracker.enableAdvertisingIdCollection(true);
            tracker.enableExceptionReporting(true);
        }
    }
}
