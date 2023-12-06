package com.gianlu.commonutils.preferences;

public abstract class CommonPK {
    public static final Prefs.KeyWithDefault<Boolean> NIGHT_MODE = new Prefs.KeyWithDefault<>("nightModeEnabled", false);
    public static final Prefs.KeyWithDefault<Boolean> TRACKING_ENABLED = new Prefs.KeyWithDefault<>("trackingEnabled", true);
    public static final Prefs.KeyWithDefault<Boolean> CRASH_REPORT_ENABLED = new Prefs.KeyWithDefault<>("crashReportEnabled", true);
    public static final Prefs.Key TUTORIAL_DISCOVERIES = new Prefs.Key("tutorialDiscoveries");
    public static final Prefs.Key ANALYTICS_USER_ID = new Prefs.Key("analyticsUserId");
}
