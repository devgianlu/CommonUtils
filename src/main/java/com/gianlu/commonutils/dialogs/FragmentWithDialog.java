package com.gianlu.commonutils.dialogs;

import android.app.Dialog;
import android.content.Context;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.gianlu.commonutils.ui.Toaster;

public abstract class FragmentWithDialog extends Fragment implements DialogUtils.ShowStuffInterface {
    private ActivityWithDialog activity;

    @Override
    @CallSuper
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof ActivityWithDialog)
            activity = (ActivityWithDialog) context;
        else
            throw new IllegalStateException("Parent activity isn't instance of ActivityWithDialog!");
    }

    @Override
    public void onDetach() {
        super.onDetach();

        activity = null;
    }

    @Override
    public final void dismissDialog(long handle) {
        if (activity != null) activity.dismissDialog(handle);
    }

    @Override
    public final void dismissDialog() {
        if (activity != null) activity.dismissDialog();
    }

    public final boolean hasVisibleDialog() {
        return activity != null && activity.hasVisibleDialog();
    }

    @Override
    public final long showDialog(@NonNull Dialog dialog) {
        return activity == null ? -1 : activity.showDialog(dialog);
    }

    @Override
    public final long showDialog(@NonNull AlertDialog.Builder dialog) {
        return activity == null ? -1 : activity.showDialog(dialog);
    }

    @Override
    public final void showToast(@NonNull Toaster toaster) {
        if (getContext() == null) return;
        toaster.show(getContext());
    }

    @Override
    public final long showDialog(@NonNull DialogFragment dialog) {
        return activity == null ? -1 : activity.showDialog(dialog);
    }

    @Override
    public final long showDialog(@NonNull DialogFragment fragment, @Nullable String tag) {
        return activity == null ? -1 : activity.showDialog(fragment, tag);
    }

    @Override
    public final long showProgress(@StringRes int res) {
        return activity == null ? -1 : activity.showProgress(res);
    }

    public final void onBackPressed() {
        if (getActivity() != null) getActivity().onBackPressed();
    }
}
