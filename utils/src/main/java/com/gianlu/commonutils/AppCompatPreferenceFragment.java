package com.gianlu.commonutils;

import android.content.Intent;
import android.preference.PreferenceFragment;
import android.view.MenuItem;

@SuppressWarnings("unused")
public abstract class AppCompatPreferenceFragment extends PreferenceFragment {

    protected abstract Class getParent();

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (AppCompatPreferenceActivity.isXLargeTablet(getActivity()))
                getActivity().onBackPressed();
            else
                startActivity(new Intent(getActivity(), getParent()));

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
