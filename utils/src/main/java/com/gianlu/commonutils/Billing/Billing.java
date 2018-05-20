package com.gianlu.commonutils.Billing;

import android.app.PendingIntent;
import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;

import com.android.vending.billing.IInAppBillingService;
import com.gianlu.commonutils.CommonUtils;
import com.gianlu.commonutils.Logging;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Billing {
    public static final int RESULT_BILLING_UNAVAILABLE = 3;
    private static final ArrayList<String> donationProducts = new ArrayList<>(Arrays.asList(
            "donation.lemonade",
            "donation.coffee",
            "donation.hamburger",
            "donation.pizza",
            "donation.sushi",
            "donation.champagne"
    ));
    private static final int RESULT_OK = 0;
    private static final int RESULT_USER_CANCELED = 1;

    public static void requestProductsDetails(final Context context, final IInAppBillingService service, @NonNull final OnRequestProductDetails handler) {
        if (service == null) {
            handler.onFailed(new NullPointerException("IInAppBillingService is null"));
            return;
        }

        final Bundle bundle = new Bundle();
        bundle.putStringArrayList("ITEM_ID_LIST", Billing.donationProducts);

        new Thread(new Runnable() {
            @Override
            public void run() {
                Bundle response;
                try {
                    response = service.getSkuDetails(3, context.getApplicationContext().getPackageName(), "inapp", bundle);
                } catch (RemoteException ex) {
                    handler.onFailed(ex);
                    return;
                }

                int respCode = response.getInt("RESPONSE_CODE");
                if (respCode == RESULT_OK) {
                    List<String> detailsList = response.getStringArrayList("DETAILS_LIST");

                    if (detailsList == null) {
                        handler.onFailed(new NullPointerException("Response bundle is null"));
                        return;
                    }

                    List<Product> products = new ArrayList<>();
                    for (String productDetails : detailsList) {
                        try {
                            products.add(new Product(new JSONObject(productDetails)));
                        } catch (JSONException ex) {
                            Logging.log(ex);
                        }
                    }

                    handler.onReceivedDetails(handler, products);
                } else if (respCode == RESULT_USER_CANCELED) {
                    handler.onUserCancelled();
                } else {
                    handler.onAPIException(respCode);
                }
            }
        }).start();
    }

    public static void buyProduct(final Context context, final IInAppBillingService service, @NonNull final Product product, @NonNull final OnBuyProduct handler) {
        if (service == null) {
            handler.onFailed(new NullPointerException("IInAppBillingService is null"));
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                String developerString = CommonUtils.randomString(30, new SecureRandom());

                Bundle response;
                try {
                    response = service.getBuyIntent(3, context.getApplicationContext().getPackageName(), product.productId, product.type, developerString);
                } catch (RemoteException ex) {
                    handler.onFailed(ex);
                    return;
                }

                if (response == null) {
                    handler.onFailed(new NullPointerException("Response bundle is null"));
                    return;
                }

                int respCode = response.getInt("RESPONSE_CODE");
                if (respCode == RESULT_OK) {
                    PendingIntent intent = response.getParcelable("BUY_INTENT");
                    if (intent != null) handler.onGotIntent(intent, developerString);
                } else if (respCode == RESULT_USER_CANCELED) {
                    handler.onUserCancelled();
                } else {
                    handler.onAPIException(respCode);
                }
            }
        }).start();
    }

    public interface OnRequestProductDetails {
        void onReceivedDetails(@NonNull OnRequestProductDetails handler, List<Product> products);

        void onAPIException(int code);

        void onUserCancelled();

        void onFailed(@NonNull Exception ex);
    }

    public interface OnBuyProduct {
        void onGotIntent(@NonNull PendingIntent intent, @NonNull String developerString);

        void onAPIException(int code);

        void onUserCancelled();

        void onFailed(@NonNull Exception ex);
    }
}
