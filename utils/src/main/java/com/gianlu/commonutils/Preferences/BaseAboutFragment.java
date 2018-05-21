package com.gianlu.commonutils.Preferences;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.gianlu.commonutils.CommonUtils;
import com.gianlu.commonutils.Dialogs.DialogUtils;
import com.gianlu.commonutils.Logging;
import com.gianlu.commonutils.R;
import com.gianlu.commonutils.Toaster;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class BaseAboutFragment extends AppCompatPreferenceFragment implements PurchasesUpdatedListener {
    private static final List<String> BILLING_PRODUCTS = new ArrayList<>(Arrays.asList(
            "donation.lemonade",
            "donation.coffee",
            "donation.hamburger",
            "donation.pizza",
            "donation.sushi",
            "donation.champagne"
    ));
    private final Object billingReady = new Object();
    private BillingClient billingClient;

    private void handleBillingErrors(@BillingClient.BillingResponse int code) {
        switch (code) {
            case BillingClient.BillingResponse.BILLING_UNAVAILABLE:
            case BillingClient.BillingResponse.SERVICE_UNAVAILABLE:
            case BillingClient.BillingResponse.SERVICE_DISCONNECTED:
                Toaster.show(getActivity(), Toaster.Message.FAILED_CONNECTION_BILLING_SERVICE);
                break;
            case BillingClient.BillingResponse.USER_CANCELED:
                Toaster.show(getActivity(), Toaster.Message.BILLING_USER_CANCELLED);
                break;
            case BillingClient.BillingResponse.DEVELOPER_ERROR:
            case BillingClient.BillingResponse.ITEM_UNAVAILABLE:
            case BillingClient.BillingResponse.FEATURE_NOT_SUPPORTED:
            case BillingClient.BillingResponse.ITEM_ALREADY_OWNED:
            case BillingClient.BillingResponse.ITEM_NOT_OWNED:
            case BillingClient.BillingResponse.ERROR:
                Toaster.show(getActivity(), Toaster.Message.FAILED_BUYING_ITEM);
                break;
            case BillingClient.BillingResponse.OK:
                break;
        }
    }

    @Override
    @CallSuper
    public void onStart() {
        super.onStart();

        billingClient = BillingClient.newBuilder(getActivity()).setListener(this).build();
        billingClient.startConnection(new BillingClientStateListener() {
            private boolean retried = false;

            @Override
            public void onBillingSetupFinished(@BillingClient.BillingResponse int code) {
                if (code == BillingClient.BillingResponse.OK) {
                    synchronized (billingReady) {
                        billingReady.notifyAll();
                    }
                } else {
                    handleBillingErrors(code);
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                if (!retried) {
                    retried = true;
                    billingClient.startConnection(this);
                } else {
                    Toaster.show(getActivity(), Toaster.Message.FAILED_CONNECTION_BILLING_SERVICE);
                }
            }
        });
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
                donate(false);
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

        final Uri openSourceUrl = getOpenSourceUrl();
        if (openSourceUrl != null) {
            Preference pref = new Preference(getActivity());
            pref.setTitle(R.string.openSource);
            pref.setSummary(R.string.openSource_desc);
            pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    startActivity(new Intent(Intent.ACTION_VIEW, openSourceUrl));
                    return true;
                }
            });

            getPreferenceScreen().addPreference(pref);
        }
    }

    @Nullable
    protected abstract Uri getOpenSourceUrl();

    private void buyProduct(@NonNull SkuDetails product) {
        BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                .setSku(product.getSku()).setType(BillingClient.SkuType.INAPP)
                .build();

        int responseCode = billingClient.launchBillingFlow(getActivity(), flowParams);
        if (responseCode != BillingClient.BillingResponse.OK) handleBillingErrors(responseCode);
    }

    private void showDonateDialog(List<SkuDetails> products) {
        RecyclerView list = new RecyclerView(getActivity());
        list.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        list.setAdapter(new SkuAdapter(getActivity(), products, new SkuAdapter.Listener() {
            @Override
            public void onItemSelected(@NonNull SkuDetails product) {
                buyProduct(product);
                dismissDialog();
            }
        }));

        showDialog(new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.donate))
                .setNegativeButton(android.R.string.cancel, null)
                .setView(list));
    }

    private void donate(boolean wasWaiting) {
        if (!wasWaiting)
            showDialog(DialogUtils.progressDialog(getActivity(), R.string.connectingBillingService));

        if (billingClient != null && billingClient.isReady()) {
            SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
            params.setSkusList(BILLING_PRODUCTS).setType(BillingClient.SkuType.INAPP);
            billingClient.querySkuDetailsAsync(params.build(), new SkuDetailsResponseListener() {
                @Override
                public void onSkuDetailsResponse(@BillingClient.BillingResponse int responseCode, List<SkuDetails> products) {
                    dismissDialog();

                    if (responseCode == BillingClient.BillingResponse.OK)
                        showDonateDialog(products);
                    else
                        handleBillingErrors(responseCode);
                }
            });
        } else {
            new Thread() {
                @Override
                public void run() {
                    synchronized (billingReady) {
                        try {
                            billingReady.wait();
                            donate(true);
                        } catch (InterruptedException ex) {
                            Logging.log(ex);
                        }
                    }
                }
            }.start();
        }
    }

    @Override
    public void onPurchasesUpdated(@BillingClient.BillingResponse int responseCode, List<Purchase> purchases) {
        if (responseCode == BillingClient.BillingResponse.OK)
            Toaster.show(getActivity(), Toaster.Message.THANK_YOU, purchases.toString());
        else
            handleBillingErrors(responseCode);
    }

    static class SkuAdapter extends RecyclerView.Adapter<SkuAdapter.ViewHolder> {
        private final List<SkuDetails> products;
        private final Listener listener;
        private final LayoutInflater inflater;

        SkuAdapter(Context context, List<SkuDetails> products, Listener listener) {
            this.products = products;
            this.listener = listener;
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        @NonNull
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(parent);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            final SkuDetails item = products.get(position);

            switch (item.getSku()) {
                case "donation.lemonade":
                    holder.icon.setImageResource(R.drawable.ic_juice_64dp);
                    break;
                case "donation.coffee":
                    holder.icon.setImageResource(R.drawable.ic_coffee_64dp);
                    break;
                case "donation.hamburger":
                    holder.icon.setImageResource(R.drawable.ic_hamburger_64dp);
                    break;
                case "donation.pizza":
                    holder.icon.setImageResource(R.drawable.ic_pizza_64dp);
                    break;
                case "donation.sushi":
                    holder.icon.setImageResource(R.drawable.ic_sushi_64dp);
                    break;
                case "donation.champagne":
                    holder.icon.setImageResource(R.drawable.ic_champagne_64dp);
                    break;
            }

            holder.title.setText(item.getTitle());
            holder.description.setText(item.getDescription());
            holder.buy.setText(item.getPrice());
            holder.buy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) listener.onItemSelected(item);
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) listener.onItemSelected(item);
                }
            });
        }

        @Override
        public int getItemCount() {
            return products.size();
        }

        public interface Listener {
            void onItemSelected(@NonNull SkuDetails product);
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final ImageView icon;
            final TextView title;
            final TextView description;
            final Button buy;

            ViewHolder(ViewGroup parent) {
                super(inflater.inflate(R.layout.product_item, parent, false));

                icon = itemView.findViewById(R.id.productItem_icon);
                title = itemView.findViewById(R.id.productItem_title);
                description = itemView.findViewById(R.id.productItem_description);
                buy = itemView.findViewById(R.id.productItem_buy);
                buy.setFocusable(false);
            }
        }
    }
}
