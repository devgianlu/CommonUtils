package com.gianlu.commonutils.Billing;

import org.json.JSONException;
import org.json.JSONObject;

public class PurchasedProduct {
    public static final int PURCHASED = 0;
    public static final int CANCELED = 1;
    public final String orderId;
    public final String productId;
    public final int purchaseState;
    public final String developerPayload;

    public PurchasedProduct(JSONObject obj) throws JSONException {
        orderId = obj.getString("orderId");
        productId = obj.getString("productId");
        purchaseState = obj.getInt("purchaseState");
        developerPayload = obj.getString("developerPayload");
    }
}
