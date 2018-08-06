package com.gianlu.commonutils.Preferences;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.danielstone.materialaboutlibrary.MaterialAboutFragment;
import com.danielstone.materialaboutlibrary.items.MaterialAboutActionItem;
import com.danielstone.materialaboutlibrary.items.MaterialAboutItemOnClickAction;
import com.danielstone.materialaboutlibrary.items.MaterialAboutTitleItem;
import com.danielstone.materialaboutlibrary.model.MaterialAboutCard;
import com.danielstone.materialaboutlibrary.model.MaterialAboutList;
import com.gianlu.commonutils.Dialogs.ActivityWithDialog;
import com.gianlu.commonutils.FossUtils;
import com.gianlu.commonutils.LogsActivity;
import com.gianlu.commonutils.R;

import java.util.List;

public abstract class BasePreferenceActivity extends ActivityWithDialog implements MaterialAboutPreferenceItem.Listener, PreferencesBillingHelper.Listener {
    private PreferencesBillingHelper billingHelper;

    @Override
    protected final void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_preference);

        ActionBar bar = getSupportActionBar();
        if (bar != null) bar.setDisplayHomeAsUpEnabled(true);

        showMainFragment();
    }

    private void showMainFragment() {
        setTitle(R.string.preferences);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.basePreference, MainFragment.get(), MainFragment.class.getName())
                .commit();
    }

    @Override
    @CallSuper
    protected void onStart() {
        super.onStart();

        if (billingHelper == null && FossUtils.hasGoogleBilling()) {
            billingHelper = new PreferencesBillingHelper(this, "donation.lemonade",
                    "donation.coffee",
                    "donation.hamburger",
                    "donation.pizza",
                    "donation.sushi",
                    "donation.champagne");
            billingHelper.onStart(this);
        }
    }

    @CallSuper
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public final void onBackPressed() {
        Fragment main = getSupportFragmentManager().findFragmentByTag(MainFragment.class.getName());
        if (main == null) showMainFragment();
        else super.onBackPressed();
    }

    @Override
    public final void onPreferenceSelected(@NonNull Class<? extends BasePreferenceFragment> clazz) {
        try {
            BasePreferenceFragment fragment = clazz.newInstance();
            String tag = fragment.getClass().getName();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.basePreference, fragment, tag)
                    .commit();

            setTitle(fragment.getTitleRes());
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void donate() {
        if (billingHelper != null) billingHelper.donate(this, false);
    }

    @NonNull
    protected abstract List<MaterialAboutPreferenceItem> getPreferencesItems();

    public static class MainFragment extends MaterialAboutFragment {
        private BasePreferenceActivity parent;
        private MaterialAboutPreferenceItem.Listener listener;

        @NonNull
        public static MainFragment get() {
            return new MainFragment();
        }

        @Override
        protected int getTheme() {
            return R.style.MaterialAbout_Default;
        }

        @Override
        public void onAttach(Context context) {
            super.onAttach(context);

            parent = (BasePreferenceActivity) context;
            listener = (MaterialAboutPreferenceItem.Listener) context;
        }

        @Override
        protected MaterialAboutList getMaterialAboutList(final Context context) {
            MaterialAboutCard developer = new MaterialAboutCard.Builder()
                    .title(R.string.about_app)
                    .addItem(new MaterialAboutVersionItem(context))
                    .addItem(new MaterialAboutTitleItem(R.string.developer, R.string.email, R.drawable.outline_info_24))
                    .build();

            MaterialAboutCard.Builder preferencesBuilder = null;
            List<MaterialAboutPreferenceItem> preferencesItems = parent.getPreferencesItems();
            if (!preferencesItems.isEmpty()) {
                preferencesBuilder = new MaterialAboutCard.Builder()
                        .title(R.string.preferences);

                for (MaterialAboutPreferenceItem item : preferencesItems) {
                    preferencesBuilder.addItem(item);
                    item.listener = listener;
                }
            }

            MaterialAboutCard logs = new MaterialAboutCard.Builder()
                    .title(R.string.logs)
                    .addItem(new MaterialAboutActionItem.Builder()
                            .icon(R.drawable.baseline_announcement_24)
                            .text(R.string.logs)
                            .setOnClickAction(new MaterialAboutItemOnClickAction() {
                                @Override
                                public void onClick() {
                                    startActivity(new Intent(context, LogsActivity.class));
                                }
                            }).build())
                    .addItem(new MaterialAboutActionItem.Builder()
                            .icon(R.drawable.baseline_delete_24)
                            .text(R.string.deleteAllLogs)
                            .setOnClickAction(new MaterialAboutItemOnClickAction() {
                                @Override
                                public void onClick() {
                                    // TODO
                                }
                            }).build())
                    .build();

            // TODO: Donate
            // TODO: Third-part projects

            MaterialAboutList.Builder listBuilder = new MaterialAboutList.Builder();
            listBuilder.addCard(developer);
            if (preferencesBuilder != null) listBuilder.addCard(preferencesBuilder.build());
            listBuilder.addCard(logs);
            return listBuilder.build();
        }
    }
}
