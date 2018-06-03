package com.gianlu.commonutils.Dialogs;

import android.app.Dialog;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.gianlu.commonutils.Toaster;

public abstract class ActivityWithDialog extends AppCompatActivity {
    private Dialog mDialog;

    public final void showDialog(@NonNull Dialog dialog) {
        mDialog = dialog;
        DialogUtils.showDialog(this, mDialog);
    }

    public final void showDialog(@NonNull AlertDialog.Builder dialog) {
        DialogUtils.showDialog(this, dialog, new DialogUtils.OnDialogCreatedListener() {
            @Override
            public void created(@NonNull Dialog dialog) {
                mDialog = dialog;
            }
        });
    }

    public final void showDialog(@NonNull DialogFragment dialog) {
        FragmentManager manager = getSupportFragmentManager();
        dialog.show(manager, null);
        mDialog = dialog.getDialog();
    }

    public final void showToast(@NonNull Toaster toaster) {
        toaster.show(this);
    }

    public final void dismissDialog() {
        if (mDialog != null) mDialog.dismiss();
        mDialog = null;
    }

    public final boolean hasVisibleDialog() {
        return mDialog != null && mDialog.isShowing();
    }

    @Override
    @CallSuper
    protected void onDestroy() {
        super.onDestroy();
        dismissDialog();
    }
}
