package com.gianlu.commonutils.Preferences;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.Preference;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.android.vending.billing.IInAppBillingService;
import com.gianlu.commonutils.Billing.Billing;
import com.gianlu.commonutils.Billing.Product;
import com.gianlu.commonutils.Billing.ProductAdapter;
import com.gianlu.commonutils.Billing.PurchasedProduct;
import com.gianlu.commonutils.CommonUtils;
import com.gianlu.commonutils.Dialogs.DialogUtils;
import com.gianlu.commonutils.Logging;
import com.gianlu.commonutils.R;
import com.gianlu.commonutils.Toaster;

import org.json.JSONException;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public abstract class BaseAboutFragment extends AppCompatPreferenceFragment {
    private int requestCode;
    private String devString;
    private IInAppBillingService billingService;
    private ServiceConnection serviceConnection;

    @Override
    public void onStart() {
        super.onStart();

        if (billingService == null) {
            serviceConnection = new ServiceConnection() {
                @Override
                public void onServiceDisconnected(ComponentName name) {
                    billingService = null;
                }

                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    billingService = IInAppBillingService.Stub.asInterface(service);
                    if (getCurrentDialog() != null && getCurrentDialog().isShowing()) donate();
                }
            };

            getActivity().bindService(new Intent("com.android.vending.billing.InAppBillingService.BIND")
                    .setPackage("com.android.vending"), serviceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    private void unbindBillingService() {
        Activity activity = getActivity();
        if (serviceConnection != null && activity != null) {
            try {
                activity.unbindService(serviceConnection);
            } catch (IllegalArgumentException ex) {
                Logging.log(ex);
            } finally {
                serviceConnection = null;
            }
        }
    }

    @Override
    public void onDestroy() {
        unbindBillingService();
        super.onDestroy();
    }

    @Override
    public void onStop() {
        unbindBillingService();
        super.onStop();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == this.requestCode) {
            if (data.getIntExtra("RESPONSE_CODE", Activity.RESULT_CANCELED) == Activity.RESULT_OK) {
                try {
                    PurchasedProduct purchasedProduct = new PurchasedProduct(data.getStringExtra("INAPP_PURCHASE_DATA"));
                    if (Objects.equals(purchasedProduct.developerPayload, devString)) {
                        if (purchasedProduct.purchaseState == PurchasedProduct.PURCHASED) {
                            Toaster.show(getActivity(), Toaster.Message.THANK_YOU, "Purchased " + purchasedProduct.productId + " with order ID " + purchasedProduct.orderId);
                        } else if (purchasedProduct.purchaseState == PurchasedProduct.CANCELED) {
                            Toaster.show(getActivity(), Toaster.Message.PURCHASING_CANCELED);
                        }
                    } else {
                        Toaster.show(getActivity(), Toaster.Message.FAILED_BUYING_ITEM, new Exception("Payloads mismatch!"));
                    }
                } catch (JSONException ex) {
                    Toaster.show(getActivity(), Toaster.Message.FAILED_BUYING_ITEM, ex);
                }
            } else {
                Toaster.show(getActivity(), Toaster.Message.PURCHASING_CANCELED);
            }
        }
    }

    @StringRes
    protected abstract int getAppNameRes();

    @NonNull
    protected abstract String getPackageName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.about_pref);
        getActivity().setTitle(R.string.about_app);
        setHasOptionsMenu(true);

        findPreference("donateGoogle").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                donate();
                return true;
            }
        });

        findPreference("donatePaypal").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://paypal.me/devgianlu")));
                return true;
            }
        });

        findPreference("email").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                CommonUtils.sendEmail(getActivity(), getString(getAppNameRes()), null);
                return true;
            }
        });

        findPreference("rate").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
                } catch (android.content.ActivityNotFoundException ex) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
                }
                return true;
            }
        });

        try {
            findPreference("app_version").setSummary(getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException ex) {
            findPreference("app_version").setSummary(R.string.unknown);
        }
    }

    private void donate() {
        showDialog(DialogUtils.progressDialog(getActivity(), R.string.connectingBillingService));
        if (billingService == null)
            return;

        Billing.requestProductsDetails(getActivity(), billingService, new Billing.IRequestProductDetails() {
            @Override
            public void onReceivedDetails(final Billing.IRequestProductDetails handler, final List<Product> products) {
                final Billing.IBuyProduct buyHandler = new Billing.IBuyProduct() {
                    @Override
                    public void onGotIntent(PendingIntent intent, String developerString) {
                        devString = developerString;
                        requestCode = new Random().nextInt();

                        try {
                            getActivity().startIntentSenderForResult(intent.getIntentSender(), requestCode, new Intent(), 0, 0, 0);
                        } catch (IntentSender.SendIntentException ex) {
                            Toaster.show(getActivity(), Toaster.Message.FAILED_CONNECTION_BILLING_SERVICE, ex);
                        }
                    }

                    @Override
                    public void onAPIException(int code) {
                        handler.onAPIException(code);
                    }

                    @Override
                    public void onUserCancelled() {
                        handler.onUserCancelled();
                    }

                    @Override
                    public void onFailed(Exception ex) {
                        Toaster.show(getActivity(), Toaster.Message.FAILED_CONNECTION_BILLING_SERVICE, ex);
                    }
                };
                dismissDialog();

                RecyclerView list = new RecyclerView(getActivity());
                list.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                list.setAdapter(new ProductAdapter(getActivity(), products, new ProductAdapter.IAdapter() {
                    @Override
                    public void onItemSelected(Product product) {
                        Billing.buyProduct(getActivity(), billingService, product, buyHandler);
                    }
                }));

                showDialog(new AlertDialog.Builder(getActivity())
                        .setTitle(getString(R.string.donate))
                        .setNegativeButton(android.R.string.cancel, null)
                        .setView(list));
            }

            @Override
            public void onAPIException(int code) {
                if (code == Billing.RESULT_BILLING_UNAVAILABLE)
                    Toaster.show(getActivity(), Toaster.Message.FAILED_CONNECTION_BILLING_SERVICE, "Code: " + code);
                else
                    Toaster.show(getActivity(), Toaster.Message.FAILED_BUYING_ITEM, "Code: " + code);
            }

            @Override
            public void onUserCancelled() {
                Toaster.show(getActivity(), Toaster.Message.BILLING_USER_CANCELLED);
            }

            @Override
            public void onFailed(Exception ex) {
                Toaster.show(getActivity(), Toaster.Message.FAILED_CONNECTION_BILLING_SERVICE, ex);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (billingService != null) {
            try {
                getActivity().unbindService(serviceConnection);
            } catch (IllegalArgumentException ex) {
                Logging.log(ex);
            }
        }
    }
}
