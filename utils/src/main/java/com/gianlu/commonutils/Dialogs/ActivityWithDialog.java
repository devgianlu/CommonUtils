package com.gianlu.commonutils.Dialogs;

import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

public abstract class ActivityWithDialog extends AppCompatActivity {
    private Dialog mDialog;

    public void showDialog(@NonNull Dialog dialog) {
        mDialog = dialog;
        DialogUtils.showDialogInternal(this, mDialog);
    }

    public void showDialog(@NonNull AlertDialog.Builder dialog) {
        DialogUtils.showDialogInternal(this, dialog, new DialogUtils.IDialog() {
            @Override
            public void created(Dialog dialog) {
                mDialog = dialog;
            }
        });
    }

    public void showDialog(@NonNull DialogFragment dialog) {
        FragmentManager manager = getSupportFragmentManager();
        dialog.show(manager, null);
    }

    public void dismissDialog() {
        if (mDialog != null) mDialog.dismiss();
        mDialog = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissDialog();
    }
}
