package com.gianlu.commonutils.dialogs;

import android.app.Dialog;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.gianlu.commonutils.ui.NightlyActivity;
import com.gianlu.commonutils.ui.Toaster;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public abstract class ActivityWithDialog extends NightlyActivity implements DialogUtils.ShowStuffInterface {
    private final Map<Long, Dialog> mDialogs = new HashMap<>(5);

    @Override
    public final long showDialog(@NonNull Dialog dialog) {
        long handle = DialogUtils.showDialogValid(this, dialog);
        mDialogs.put(handle, dialog);
        return handle;
    }

    @Override
    public final long showDialog(@NonNull AlertDialog.Builder builder) {
        return DialogUtils.showDialogValid(this, builder, (dialog, handle) -> mDialogs.put(handle, dialog));
    }

    @Override
    public final long showDialog(@NonNull DialogFragment dialog) {
        return showDialog(dialog, null);
    }

    @Override
    public final long showDialog(@NonNull DialogFragment dialog, @Nullable String tag) {
        long handle = DialogUtils.showDialog(this, dialog, tag);
        mDialogs.put(handle, dialog.getDialog());
        return handle;
    }

    @Override
    public final void showToast(@NonNull Toaster toaster) {
        toaster.show(this);
    }

    @Override
    public final void dismissDialog(long handle) {
        Dialog dialog = mDialogs.remove(handle);
        if (dialog != null) dialog.dismiss();
    }

    @Override
    public final void dismissDialog() {
        dismissDialog(DialogUtils.lastDialogHandle());
    }

    @Override
    public final long showProgress(@StringRes int res) {
        return showDialog(DialogUtils.progressDialog(this, res));
    }

    public final boolean hasVisibleDialog() {
        Iterator<Dialog> values = mDialogs.values().iterator();
        while (values.hasNext()) {
            Dialog dialog = values.next();
            if (dialog == null || !dialog.isShowing())
                values.remove();
        }

        return !mDialogs.isEmpty();
    }

    @Override
    @CallSuper
    protected void onDestroy() {
        super.onDestroy();

        Iterator<Dialog> values = mDialogs.values().iterator();
        while (values.hasNext()) {
            Dialog dialog = values.next();
            if (dialog != null) dialog.dismiss();
            values.remove();
        }
    }
}
