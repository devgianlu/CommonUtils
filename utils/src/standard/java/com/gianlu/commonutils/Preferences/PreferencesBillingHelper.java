package com.gianlu.commonutils.Preferences;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
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
import com.gianlu.commonutils.Dialogs.DialogUtils;
import com.gianlu.commonutils.Logging;
import com.gianlu.commonutils.R;
import com.gianlu.commonutils.Toaster;

import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PreferencesBillingHelper {
    private final Object billingReady = new Object();
    private final Listener listener;
    private final List<String> products;
    private BillingClient billingClient;

    PreferencesBillingHelper(@NonNull Listener listener, String... products) {
        this.listener = listener;
        this.products = Arrays.asList(products);
    }

    public void onStart(Activity activity) {
        billingClient = BillingClient.newBuilder(activity).setListener(new InternalListener()).build();
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
                    listener.showToast(Toaster.build().message(R.string.failedBillingConnection));
                }
            }
        });
    }

    private void buyProduct(Activity activity, @NonNull SkuDetails product) {
        BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                .setSku(product.getSku()).setType(BillingClient.SkuType.INAPP)
                .build();

        int responseCode = billingClient.launchBillingFlow(activity, flowParams);
        if (responseCode != BillingClient.BillingResponse.OK) handleBillingErrors(responseCode);
    }

    private void showDonateDialog(final Activity activity, List<SkuDetails> products) {
        RecyclerView list = new RecyclerView(activity);
        list.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.VERTICAL, false));
        list.setAdapter(new SkuAdapter(activity, products, new SkuAdapter.Listener() {
            @Override
            public void onItemSelected(@NonNull SkuDetails product) {
                buyProduct(activity, product);
                listener.dismissDialog();
            }
        }));

        listener.showDialog(new AlertDialog.Builder(activity)
                .setTitle(activity.getString(R.string.donate))
                .setNegativeButton(android.R.string.cancel, null)
                .setView(list));
    }

    public void donate(final Activity activity, boolean wasWaiting) {
        if (!wasWaiting)
            listener.showDialog(DialogUtils.progressDialog(activity, R.string.connectingBillingService));

        if (billingClient != null && billingClient.isReady()) {
            SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
            params.setSkusList(products).setType(BillingClient.SkuType.INAPP);
            billingClient.querySkuDetailsAsync(params.build(), new SkuDetailsResponseListener() {
                @Override
                public void onSkuDetailsResponse(@BillingClient.BillingResponse int responseCode, List<SkuDetails> products) {
                    listener.dismissDialog();

                    if (responseCode == BillingClient.BillingResponse.OK)
                        showDonateDialog(activity, products);
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
                            donate(activity, true);
                        } catch (InterruptedException ex) {
                            Logging.log(ex);
                        }
                    }
                }
            }.start();
        }
    }

    private void handleBillingErrors(@BillingClient.BillingResponse int code) {
        switch (code) {
            case BillingClient.BillingResponse.BILLING_UNAVAILABLE:
            case BillingClient.BillingResponse.SERVICE_UNAVAILABLE:
            case BillingClient.BillingResponse.SERVICE_DISCONNECTED:
                listener.showToast(Toaster.build().message(R.string.failedBillingConnection));
                break;
            case BillingClient.BillingResponse.USER_CANCELED:
                listener.showToast(Toaster.build().message(R.string.userCancelled));
                break;
            case BillingClient.BillingResponse.DEVELOPER_ERROR:
            case BillingClient.BillingResponse.ITEM_UNAVAILABLE:
            case BillingClient.BillingResponse.FEATURE_NOT_SUPPORTED:
            case BillingClient.BillingResponse.ITEM_ALREADY_OWNED:
            case BillingClient.BillingResponse.ITEM_NOT_OWNED:
            case BillingClient.BillingResponse.ERROR:
                listener.showToast(Toaster.build().message(R.string.failedBuying));
                break;
            case BillingClient.BillingResponse.OK:
                break;
        }
    }

    public interface Listener {
        void showToast(@NonNull Toaster toaster);

        void showDialog(@NonNull AlertDialog.Builder builder);

        void showDialog(@NonNull Dialog dialog);

        void dismissDialog();
    }

    public static class SkuAdapter extends RecyclerView.Adapter<SkuAdapter.ViewHolder> {
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

    private class InternalListener implements PurchasesUpdatedListener {

        @Override
        public void onPurchasesUpdated(@BillingClient.BillingResponse int responseCode, List<Purchase> purchases) {
            if (responseCode == BillingClient.BillingResponse.OK)
                listener.showToast(Toaster.build().message(R.string.thankYou).extra(purchases.toString()));
            else
                handleBillingErrors(responseCode);
        }
    }
}
