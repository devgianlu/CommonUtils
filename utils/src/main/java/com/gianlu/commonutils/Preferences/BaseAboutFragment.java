package com.gianlu.commonutils.Preferences;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import com.gianlu.commonutils.CommonUtils;
import com.gianlu.commonutils.FossUtils;
import com.gianlu.commonutils.R;

public abstract class BaseAboutFragment extends AppCompatPreferenceFragment implements PreferencesBillingHelper.Listener {
    private PreferencesBillingHelper billingHelper;

    @Override
    @CallSuper
    public void onStart() {
        super.onStart();

        if (billingHelper == null && FossUtils.hasGoogleBilling()) {
            billingHelper = new PreferencesBillingHelper(this, "donation.lemonade",
                    "donation.coffee",
                    "donation.hamburger",
                    "donation.pizza",
                    "donation.sushi",
                    "donation.champagne");
            billingHelper.onStart(getActivity());
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

        Preference trackingDisable = findPreference(Prefs.Keys.TRACKING_DISABLE.getKey());
        Preference donateGoogle = findPreference("donateGoogle");
        Preference rateGoogle = findPreference("rate");
        if (FossUtils.hasGoogleBilling()) {
            donateGoogle.setEnabled(true);
            donateGoogle.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    billingHelper.donate(getActivity(), false);
                    return true;
                }
            });

            rateGoogle.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
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

            trackingDisable.setEnabled(true);
        } else {
            donateGoogle.setEnabled(false);
            rateGoogle.setEnabled(false);
            trackingDisable.setEnabled(false);
        }

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
}
