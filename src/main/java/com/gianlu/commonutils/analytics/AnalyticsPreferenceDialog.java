package com.gianlu.commonutils.analytics;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.gianlu.commonutils.R;
import com.gianlu.commonutils.preferences.CommonPK;
import com.gianlu.commonutils.preferences.Prefs;
import com.google.android.material.switchmaterial.SwitchMaterial;

public final class AnalyticsPreferenceDialog extends DialogFragment {
    public static final String TAG = AnalyticsPreferenceDialog.class.getName();

    @NonNull
    public static AnalyticsPreferenceDialog get() {
        return new AnalyticsPreferenceDialog();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle(R.string.prefs_usageStatistics);
        return dialog;
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.dialog_analytics_preference, container, false);

        SwitchMaterial tracking = layout.findViewById(R.id.analyticsPrefsDialog_tracking);
        tracking.setChecked(Prefs.getBoolean(CommonPK.TRACKING_ENABLED, true));
        tracking.setOnCheckedChangeListener((buttonView, isChecked) -> Prefs.putBoolean(CommonPK.TRACKING_ENABLED, isChecked));

        SwitchMaterial crashReport = layout.findViewById(R.id.analyticsPrefsDialog_crashReport);
        crashReport.setChecked(Prefs.getBoolean(CommonPK.CRASH_REPORT_ENABLED, true));
        crashReport.setOnCheckedChangeListener((buttonView, isChecked) -> Prefs.putBoolean(CommonPK.CRASH_REPORT_ENABLED, isChecked));

        Button ok = layout.findViewById(R.id.analyticsPrefsDialog_ok);
        ok.setOnClickListener(v -> dismissAllowingStateLoss());

        return layout;
    }
}
