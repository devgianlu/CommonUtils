package com.gianlu.commonutils.Preferences;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;

import com.gianlu.commonutils.R;

import java.io.Serializable;

public abstract class BaseThirdPartProjectsFragment extends AppCompatPreferenceFragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(R.string.third_part);
        setHasOptionsMenu(true);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setPositiveButton(android.R.string.ok, null);

        ThirdPartProject[] projects = getProjects();
        PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(getActivity());
        for (final ThirdPartProject project : projects) {
            Preference preference = new Preference(getActivity());
            preference.setTitle(project.title);
            preference.setSummary(project.license.msg);
            preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    showDialog(builder.setTitle(project.title).setMessage(project.message));
                    return true;
                }
            });

            screen.addPreference(preference);
        }

        for (final ThirdPartProject.License license : ThirdPartProject.License.values()) {
            if (license.isUsed(projects)) {
                Preference preference = new Preference(getActivity());
                preference.setTitle(license.title);
                preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(license.url)));
                        return true;
                    }
                });
                screen.addPreference(preference);
            }
        }

        setPreferenceScreen(screen);
    }

    @NonNull
    protected abstract ThirdPartProject[] getProjects();

    public static class ThirdPartProject implements Serializable {
        private final int title;
        private final int message;
        private final License license;

        public ThirdPartProject(@StringRes int title, @StringRes int message, License license) {
            this.title = title;
            this.message = message;
            this.license = license;
        }

        public enum License {
            APACHE(R.string.apacheLicenseName, R.string.licensedApache, "http://www.apache.org/licenses/LICENSE-2.0"),
            MIT(R.string.mitLicenseName, R.string.licensedMit, "https://opensource.org/licenses/MIT");

            private final int msg;
            private final String url;
            public int title;

            License(@StringRes int title, @StringRes int msg, String url) {
                this.title = title;
                this.msg = msg;
                this.url = url;
            }

            public boolean isUsed(ThirdPartProject[] projects) {
                for (ThirdPartProject project : projects)
                    if (project.license == this)
                        return true;

                return false;
            }
        }
    }
}
