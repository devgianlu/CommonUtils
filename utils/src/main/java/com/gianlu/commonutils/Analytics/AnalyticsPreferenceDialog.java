package com.gianlu.commonutils.Analytics;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.gianlu.commonutils.CommonPK;
import com.gianlu.commonutils.Preferences.Prefs;
import com.gianlu.commonutils.R;

public class AnalyticsPreferenceDialog extends DialogFragment {
    public static final String TAG = AnalyticsPreferenceDialog.class.getName();

    @NonNull
    public static AnalyticsPreferenceDialog get() {
        return new AnalyticsPreferenceDialog();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle(R.string.usageStatistics);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int styleId = getResources().getIdentifier("DialogFix", "style", requireContext().getPackageName());
        if (styleId != 0) setStyle(STYLE_NORMAL, styleId);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.dialog_analytics_preference, container, false);

        CheckBox tracking = layout.findViewById(R.id.analyticsPrefsDialog_tracking);
        tracking.setChecked(Prefs.getBoolean(CommonPK.TRACKING_ENABLED, true));
        tracking.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Prefs.putBoolean(CommonPK.TRACKING_ENABLED, isChecked);
            }
        });

        CheckBox crashReport = layout.findViewById(R.id.analyticsPrefsDialog_crashReport);
        crashReport.setChecked(Prefs.getBoolean(CommonPK.CRASH_REPORT_ENABLED, true));
        crashReport.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Prefs.putBoolean(CommonPK.CRASH_REPORT_ENABLED, isChecked);
            }
        });

        Button ok = layout.findViewById(R.id.analyticsPrefsDialog_ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissAllowingStateLoss();
            }
        });

        return layout;
    }
}
