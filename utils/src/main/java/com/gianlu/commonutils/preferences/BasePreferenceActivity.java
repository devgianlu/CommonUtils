package com.gianlu.commonutils.preferences;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.CallSuper;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;

import com.danielstone.materialaboutlibrary.MaterialAboutFragment;
import com.danielstone.materialaboutlibrary.items.MaterialAboutActionItem;
import com.danielstone.materialaboutlibrary.items.MaterialAboutItem;
import com.danielstone.materialaboutlibrary.items.MaterialAboutTitleItem;
import com.danielstone.materialaboutlibrary.model.MaterialAboutCard;
import com.danielstone.materialaboutlibrary.model.MaterialAboutList;
import com.gianlu.commonutils.FossUtils;
import com.gianlu.commonutils.R;
import com.gianlu.commonutils.analytics.AnalyticsPreferenceDialog;
import com.gianlu.commonutils.dialogs.ActivityWithDialog;
import com.gianlu.commonutils.dialogs.DialogUtils;
import com.gianlu.commonutils.logs.LogsHelper;
import com.gianlu.commonutils.tutorial.TutorialManager;
import com.gianlu.commonutils.ui.Toaster;
import com.yarolegovich.mp.MaterialStandardPreference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public abstract class BasePreferenceActivity extends ActivityWithDialog implements MaterialAboutPreferenceItem.Listener, PreferencesBillingHelper.Listener {
    private PreferencesBillingHelper billingHelper;

    private static void openLink(@NonNull Context context, @NonNull String uri) {
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uri)));
        } catch (ActivityNotFoundException ex) {
            Toaster.with(context).message(R.string.missingWebBrowser).show();
        }
    }

    @Override
    @CallSuper
    protected void onCreate(@Nullable Bundle savedInstanceState) {
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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
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
        if (isFinishing() || isDestroyed()) return;

        try {
            BasePreferenceFragment fragment = clazz.newInstance();
            String tag = fragment.getClass().getName();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.basePreference, fragment, tag)
                    .commitAllowingStateLoss();

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

    protected abstract boolean disableOtherDonationsOnGooglePlay();

    public static class TranslatorsFragment extends BasePreferenceFragment {

        @Override
        protected void buildPreferences(@NonNull Context context) {
            for (Translators.Item item : Translators.load(context)) {
                MaterialStandardPreference pref = new MaterialStandardPreference(context);
                pref.setTitle(item.name);
                pref.setSummary(item.languages);
                pref.setOnClickListener(v -> openLink(context, item.link));
                addPreference(pref);
            }
        }

        @Override
        public int getTitleRes() {
            return R.string.translators;
        }
    }

    public static class MainFragment extends MaterialAboutFragment {
        private BasePreferenceActivity parent;
        private MaterialAboutPreferenceItem.Listener listener;

        @NonNull
        public static MainFragment get() {
            return new MainFragment();
        }

        @Override
        public void onAttach(@NonNull Context context) {
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
                    .addItem(new MaterialAboutActionItem(R.string.prefs_developer, R.string.devgianlu, R.drawable.baseline_person_24, () -> openLink(context, "https://gianlu.xyz")))
                    .addItem(new MaterialAboutActionItem(R.string.emailMe, R.string.devgianluEmail, R.drawable.baseline_mail_24, () -> LogsHelper.sendEmail(context, null)));

            final String openSourceUrl = parent.getOpenSourceUrl();
            if (openSourceUrl != null) {
                developerBuilder.addItem(new MaterialAboutActionItem(R.string.openSource, R.string.openSource_desc, R.drawable.baseline_bug_report_24,
                        () -> openLink(context, openSourceUrl)));
            }

            if (FossUtils.hasFirebaseAnalytics() || FossUtils.hasFirebaseAnalytics()) {
                developerBuilder.addItem(new MaterialAboutActionItem(R.string.prefs_usageStatistics, R.string.prefs_usageStatisticsSummary, R.drawable.baseline_track_changes_24, () ->
                        AnalyticsPreferenceDialog.get().show(parent.getSupportFragmentManager(), AnalyticsPreferenceDialog.TAG)));
            }

            if (!Translators.load(context).isEmpty()) {
                developerBuilder.addItem(new MaterialAboutActionItem(R.string.translators, 0, R.drawable.baseline_translate_24,
                        () -> parent.onPreferenceSelected(TranslatorsFragment.class)));
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
                            .icon(R.drawable.baseline_mail_24)
                            .text(R.string.send_email)
                            .setOnClickAction(() -> LogsHelper.sendEmail(context, null))
                            .build())
                    .addItem(new MaterialAboutActionItem.Builder()
                            .icon(R.drawable.baseline_share_24)
                            .text(R.string.exportLogFiles)
                            .setOnClickAction(() -> {
                                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                                if (!LogsHelper.exportLogFiles(context, shareIntent)) {
                                    DialogUtils.showToast(getActivity(), Toaster.build().message(R.string.noLogs));
                                    return;
                                }

                                shareIntent.setType("text/plain");
                                startActivity(Intent.createChooser(shareIntent, getString(R.string.exportLogFiles)));
                            })
                            .build())
                    .build();

            MaterialAboutCard.Builder donateBuilder = new MaterialAboutCard.Builder()
                    .title(R.string.rateDonate);
            if (FossUtils.hasGoogleBilling()) {
                donateBuilder.addItem(new MaterialAboutActionItem(R.string.rateApp, R.string.leaveReview, R.drawable.baseline_rate_review_24, () -> {
                    try {
                        openLink(context, "market://details?id=" + context.getPackageName());
                    } catch (ActivityNotFoundException ex) {
                        openLink(context, "https://play.google.com/store/apps/details?id=" + context.getPackageName());
                    }
                })).addItem(new MaterialAboutActionItem(R.string.donateGoogle, R.string.donateGoogleSummary, R.drawable.baseline_attach_money_24, () -> {
                    if (parent != null) parent.donate();
                }));
            }

            if (!FossUtils.hasGoogleBilling() || !parent.disableOtherDonationsOnGooglePlay()) {
                donateBuilder.addItem(new MaterialAboutActionItem(R.string.donate, R.string.donate_summary, R.drawable.baseline_money_24, () -> openLink(context, "https://gianlu.xyz/donate/")));
            }

            MaterialAboutCard.Builder tutorialBuilder = null;
            if (parent.hasTutorial()) {
                tutorialBuilder = new MaterialAboutCard.Builder()
                        .title(R.string.prefs_tutorial);

                tutorialBuilder.addItem(new MaterialAboutActionItem(R.string.prefs_restartTutorial, 0, R.drawable.baseline_settings_backup_restore_24, () -> {
                    TutorialManager.restartTutorial();
                    parent.onBackPressed();
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
