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

    public static boolean hasFabric() {
        try {
            Class.forName("io.fabric.sdk.android.Fabric");
            return true;
        } catch (ClassNotFoundException ex) {
            return false;
        }
    }
}
