package com.gianlu.commonutils.preferences;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClient.BillingResponseCode;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.PendingPurchasesParams;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.gianlu.commonutils.R;
import com.gianlu.commonutils.dialogs.DialogUtils;
import com.gianlu.commonutils.ui.Toaster;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

public class PreferencesBillingHelper {
    private static final String TAG = PreferencesBillingHelper.class.getSimpleName();
    private final Object billingReady = new Object();
    private final DialogUtils.ShowStuffInterface listener;
    private final List<QueryProductDetailsParams.Product> products;
    private BillingClient billingClient;
    private boolean destroyed = false;

    public PreferencesBillingHelper(@NonNull DialogUtils.ShowStuffInterface listener, String... products) {
        this.listener = listener;
        this.products = new ArrayList<>();
        for (String product : products) {
            this.products.add(QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(product)
                    .setProductType(BillingClient.ProductType.INAPP)
                    .build());
        }
    }

    public void onStart(@NonNull Activity activity) {
        if (destroyed) throw new IllegalStateException();

        billingClient = BillingClient.newBuilder(activity)
                .enablePendingPurchases(PendingPurchasesParams.newBuilder()
                        .enableOneTimeProducts()
                        .enablePrepaidPlans()
                        .build())
                .setListener(new InternalListener()).build();
        billingClient.startConnection(new BillingClientStateListener() {
            private boolean retried = false;

            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingResponseCode.OK) {
                    synchronized (billingReady) {
                        billingReady.notifyAll();
                    }
                } else {
                    if (!destroyed) handleBillingErrors(billingResult.getResponseCode());
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                if (!retried) {
                    retried = true;
                    billingClient.startConnection(this);
                } else {
                    if (!destroyed)
                        listener.showToast(Toaster.build().message(R.string.failedBillingConnection));
                }
            }
        });
    }

    public void onDestroy() {
        destroyed = true;
        if (billingClient != null) billingClient.endConnection();

        synchronized (billingReady) {
            billingReady.notifyAll();
        }
    }

    private void buyProduct(@NonNull Activity activity, @NonNull ProductDetails product) {
        BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(List.of(BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(product)
                        .build()))
                .build();

        BillingResult result = billingClient.launchBillingFlow(activity, flowParams);
        if (result.getResponseCode() != BillingResponseCode.OK)
            handleBillingErrors(result.getResponseCode());
    }

    private void showDonateDialog(@NonNull Activity activity, List<ProductDetails> products) {
        RecyclerView list = new RecyclerView(activity);
        list.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.VERTICAL, false));
        list.setAdapter(new ProductAdapter(activity, products, product -> {
            buyProduct(activity, product);
            listener.dismissDialog();
        }));

        listener.showDialog(new MaterialAlertDialogBuilder(activity)
                .setTitle(activity.getString(R.string.donate))
                .setNegativeButton(android.R.string.cancel, null)
                .setView(list));
    }

    public void donate(@NonNull Activity activity, boolean wasWaiting) {
        if (!wasWaiting)
            listener.showDialog(DialogUtils.progressDialog(activity, R.string.connectingBillingService));

        if (billingClient != null && billingClient.isReady()) {
            billingClient.queryProductDetailsAsync(QueryProductDetailsParams.newBuilder().setProductList(products).build(), (billingResult, productDetailsList) -> {
                listener.dismissDialog();

                if (billingResult.getResponseCode() == BillingResponseCode.OK)
                    showDonateDialog(activity, productDetailsList.getProductDetailsList());
                else handleBillingErrors(billingResult.getResponseCode());
            });
        } else {
            new Thread() {
                @Override
                public void run() {
                    synchronized (billingReady) {
                        try {
                            billingReady.wait();
                            if (destroyed) {
                                listener.dismissDialog();
                                return;
                            }

                            donate(activity, true);
                        } catch (InterruptedException ex) {
                            Log.w(TAG, ex);
                        }
                    }
                }
            }.start();
        }
    }

    private void handleBillingErrors(@BillingResponseCode int code) {
        switch (code) {
            case BillingResponseCode.BILLING_UNAVAILABLE:
            case BillingResponseCode.SERVICE_UNAVAILABLE:
            case BillingResponseCode.SERVICE_DISCONNECTED:
                listener.showToast(Toaster.build().message(R.string.failedBillingConnection));
                break;
            case BillingResponseCode.USER_CANCELED:
                listener.showToast(Toaster.build().message(R.string.userCancelled));
                break;
            case BillingResponseCode.DEVELOPER_ERROR:
            case BillingResponseCode.ITEM_UNAVAILABLE:
            case BillingResponseCode.FEATURE_NOT_SUPPORTED:
            case BillingResponseCode.ITEM_ALREADY_OWNED:
            case BillingResponseCode.ITEM_NOT_OWNED:
            case BillingResponseCode.ERROR:
                listener.showToast(Toaster.build().message(R.string.failedBuying));
                break;
            case BillingResponseCode.OK:
                break;
        }
    }

    public static class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
        private final List<ProductDetails> products;
        private final Listener listener;
        private final LayoutInflater inflater;

        ProductAdapter(Context context, List<ProductDetails> products, Listener listener) {
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
            final ProductDetails item = products.get(position);

            switch (item.getProductId()) {
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

            final String price;
            if (item.getOneTimePurchaseOfferDetails() != null) {
                price = item.getOneTimePurchaseOfferDetails().getFormattedPrice();
            } else {
                price = "???";
            }

            holder.title.setText(item.getTitle());
            holder.description.setText(item.getDescription());
            holder.buy.setText(price);
            holder.buy.setOnClickListener(view -> {
                if (listener != null) listener.onItemSelected(item);
            });

            holder.itemView.setOnClickListener(v -> {
                if (listener != null) listener.onItemSelected(item);
            });
        }

        @Override
        public int getItemCount() {
            return products.size();
        }

        public interface Listener {
            void onItemSelected(@NonNull ProductDetails product);
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final ImageView icon;
            final TextView title;
            final TextView description;
            final Button buy;

            ViewHolder(ViewGroup parent) {
                super(inflater.inflate(R.layout.item_product, parent, false));

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
        public void onPurchasesUpdated(BillingResult br, @Nullable List<Purchase> purchases) {
            if (br.getResponseCode() == BillingResponseCode.OK) {
                listener.showToast(Toaster.build().message(R.string.thankYou).extra(purchases == null ? null : purchases.toString()));
                if (purchases == null || purchases.isEmpty()) return;

                for (Purchase p : purchases) {
                    if (p.isAcknowledged()) continue;

                    AcknowledgePurchaseParams params = AcknowledgePurchaseParams.newBuilder()
                            .setPurchaseToken(p.getPurchaseToken())
                            .build();
                    billingClient.acknowledgePurchase(params, br1 -> {
                        if (br1.getResponseCode() != BillingResponseCode.OK)
                            handleBillingErrors(br1.getResponseCode());
                    });
                }
            } else {
                handleBillingErrors(br.getResponseCode());
            }
        }
    }
}
