package com.gianlu.commonutils.Preferences;

import android.app.Dialog;
import android.content.Intent;
import android.preference.PreferenceFragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;

import com.gianlu.commonutils.Dialogs.DialogUtils;

public abstract class AppCompatPreferenceFragment extends PreferenceFragment {
    private Dialog mDialog;

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

    @Nullable
    protected Dialog getCurrentDialog() {
        return mDialog;
    }

    public void showDialog(@NonNull Dialog dialog) {
        mDialog = dialog;
        DialogUtils.showDialogValid(getActivity(), mDialog);
    }

    public void showDialog(@NonNull AlertDialog.Builder builder) {
        DialogUtils.showDialogValid(getActivity(), builder, new DialogUtils.OnDialogCreatedListener() {
            @Override
            public void created(@NonNull Dialog dialog) {
                mDialog = dialog;
            }
        });
    }

    public void dismissDialog() {
        if (mDialog != null) mDialog.dismiss();
        mDialog = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dismissDialog();
    }
}
