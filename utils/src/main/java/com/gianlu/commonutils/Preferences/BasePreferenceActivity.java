package com.gianlu.commonutils.Preferences;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.danielstone.materialaboutlibrary.MaterialAboutFragment;
import com.danielstone.materialaboutlibrary.items.MaterialAboutActionItem;
import com.danielstone.materialaboutlibrary.items.MaterialAboutItem;
import com.danielstone.materialaboutlibrary.items.MaterialAboutItemOnClickAction;
import com.danielstone.materialaboutlibrary.items.MaterialAboutTitleItem;
import com.danielstone.materialaboutlibrary.model.MaterialAboutCard;
import com.danielstone.materialaboutlibrary.model.MaterialAboutList;
import com.gianlu.commonutils.Analytics.AnalyticsPreferenceDialog;
import com.gianlu.commonutils.CommonUtils;
import com.gianlu.commonutils.Dialogs.ActivityWithDialog;
import com.gianlu.commonutils.Dialogs.DialogUtils;
import com.gianlu.commonutils.FossUtils;
import com.gianlu.commonutils.Logging;
import com.gianlu.commonutils.LogsActivity;
import com.gianlu.commonutils.R;
import com.gianlu.commonutils.Toaster;
import com.gianlu.commonutils.Tutorial.TutorialManager;
import com.mikepenz.aboutlibraries.LibsBuilder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public abstract class BasePreferenceActivity extends ActivityWithDialog implements MaterialAboutPreferenceItem.Listener, PreferencesBillingHelper.Listener {
    private PreferencesBillingHelper billingHelper;

    private static void openLink(Context context, String uri) {
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uri)));
    }

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

    @DrawableRes
    protected abstract int getAppIconRes();

    protected abstract boolean hasTutorial();

    @NonNull
    protected List<MaterialAboutItem> customizeTutorialCard() {
        return new ArrayList<>();
    }

    @Nullable
    protected abstract String getOpenSourceUrl();

    protected abstract boolean disablePayPalOnGooglePlay();

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
            MaterialAboutCard.Builder developerBuilder = new MaterialAboutCard.Builder()
                    .title(R.string.about_app)
                    .addItem(new MaterialAboutTitleItem(R.string.app_name, 0, parent.getAppIconRes())
                            .setDesc(context.getString(R.string.devgianluCopyright, Calendar.getInstance().get(Calendar.YEAR))))
                    .addItem(new MaterialAboutVersionItem(context))
                    .addItem(new MaterialAboutActionItem(R.string.developer, R.string.devgianlu, R.drawable.baseline_person_24, new MaterialAboutItemOnClickAction() {
                        @Override
                        public void onClick() {
                            openLink(context, "https://gianlu.xyz");
                        }
                    }))
                    .addItem(new MaterialAboutActionItem(R.string.emailMe, R.string.devgianluEmail, R.drawable.baseline_mail_24, new MaterialAboutItemOnClickAction() {
                        @Override
                        public void onClick() {
                            CommonUtils.sendEmail(context, null);
                        }
                    }))
                    .addItem(new MaterialAboutActionItem(R.string.third_part, 0, R.drawable.baseline_extension_24, new MaterialAboutItemOnClickAction() {
                        @Override
                        public void onClick() {
                            LibsBuilder libsBuilder = new LibsBuilder()
                                    .withVersionShown(true)
                                    .withActivityTitle(context.getString(R.string.third_part));

                            List<String> toExclude = new ArrayList<>();

                            if (!FossUtils.hasCrashlytics())
                                toExclude.add("Crashlytics");

                            if (!FossUtils.hasFirebaseAnalytics() && !FossUtils.hasGoogleBilling())
                                toExclude.add("GooglePlayServices");

                            libsBuilder
                                    .withExcludedLibraries(toExclude.toArray(new String[0]))
                                    .start(context);
                        }
                    }));

            final String openSourceUrl = parent.getOpenSourceUrl();
            if (openSourceUrl != null) {
                developerBuilder.addItem(new MaterialAboutActionItem(R.string.openSource, R.string.openSource_desc, 0, new MaterialAboutItemOnClickAction() {
                    @Override
                    public void onClick() {
                        openLink(context, openSourceUrl);
                    }
                }));
            }

            if (FossUtils.hasCrashlytics() || FossUtils.hasFirebaseAnalytics()) {
                developerBuilder.addItem(new MaterialAboutActionItem(R.string.usageStatistics, R.string.usageStatisticsSummary, R.drawable.baseline_track_changes_24, new MaterialAboutItemOnClickAction() {
                    @Override
                    public void onClick() {
                        AnalyticsPreferenceDialog.get()
                                .show(parent.getSupportFragmentManager(), AnalyticsPreferenceDialog.TAG);
                    }
                }));
            }

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
                                    Logging.deleteAllLogs(context);
                                    DialogUtils.showToast(getActivity(), Toaster.build().message(R.string.logDeleted));
                                }
                            }).build())
                    .build();

            MaterialAboutCard.Builder donateBuilder = new MaterialAboutCard.Builder()
                    .title(R.string.rateDonate);
            if (FossUtils.hasGoogleBilling()) {
                donateBuilder.addItem(new MaterialAboutActionItem(R.string.rateApp, R.string.leaveReview, R.drawable.baseline_rate_review_24, new MaterialAboutItemOnClickAction() {
                    @Override
                    public void onClick() {
                        try {
                            openLink(context, "market://details?id=" + context.getPackageName());
                        } catch (android.content.ActivityNotFoundException ex) {
                            openLink(context, "https://play.google.com/store/apps/details?id=" + context.getPackageName());
                        }
                    }
                })).addItem(new MaterialAboutActionItem(R.string.donateGoogle, R.string.donateGoogleSummary, R.drawable.baseline_attach_money_24, new MaterialAboutItemOnClickAction() {
                    @Override
                    public void onClick() {
                        if (parent != null) parent.donate();
                    }
                }));
            }

            if (!FossUtils.hasGoogleBilling() || !parent.disablePayPalOnGooglePlay()) {
                donateBuilder.addItem(new MaterialAboutActionItem(R.string.donatePaypal, R.string.donatePaypalSummary, R.drawable.baseline_money_24, new MaterialAboutItemOnClickAction() {
                    @Override
                    public void onClick() {
                        openLink(context, "https://paypal.me/devgianlu");
                    }
                }));
            }

            MaterialAboutCard.Builder tutorialBuilder = null;
            if (parent.hasTutorial()) {
                tutorialBuilder = new MaterialAboutCard.Builder()
                        .title(R.string.tutorial);

                tutorialBuilder.addItem(new MaterialAboutActionItem(R.string.restartTutorial, 0, R.drawable.baseline_settings_backup_restore_24, new MaterialAboutItemOnClickAction() {
                    @Override
                    public void onClick() {
                        TutorialManager.restartTutorial();
                        parent.onBackPressed();
                    }
                }));

                List<MaterialAboutItem> items = parent.customizeTutorialCard();
                for (MaterialAboutItem item : items)
                    tutorialBuilder.addItem(item);
            }

            MaterialAboutList.Builder listBuilder = new MaterialAboutList.Builder();
            listBuilder.addCard(developerBuilder.build());
            if (preferencesBuilder != null) listBuilder.addCard(preferencesBuilder.build());
            listBuilder.addCard(donateBuilder.build());
            if (tutorialBuilder != null) listBuilder.addCard(tutorialBuilder.build());
            listBuilder.addCard(logs);
            return listBuilder.build();
        }
    }
}
