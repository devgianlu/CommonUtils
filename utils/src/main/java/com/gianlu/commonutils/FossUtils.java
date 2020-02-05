package com.gianlu.commonutils;

public final class FossUtils {

    private FossUtils() {
    }

    public static boolean hasGoogleBilling() {
        try {
            Class.forName("com.android.billingclient.api.BillingClient");
            return true;
        } catch (ClassNotFoundException ex) {
            return false;
        }
    }

    public static boolean hasCrashlytics() {
        try {
            Class.forName("com.crashlytics.android.Crashlytics");
            return true;
        } catch (ClassNotFoundException ex) {
            return false;
        }
    }

    public static boolean hasFirebaseAnalytics() {
        try {
            Class.forName("com.google.firebase.analytics.FirebaseAnalytics");
            return true;
        } catch (ClassNotFoundException ex) {
            return false;
        }
    }
}
