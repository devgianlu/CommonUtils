package com.gianlu.commonutils.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;

import com.gianlu.commonutils.Toaster;

public abstract class FragmentWithDialog extends Fragment {

    @Override
    @CallSuper
    public void onAttach(Context context) {
        super.onAttach(context);

        if (!(context instanceof ActivityWithDialog))
            throw new IllegalStateException("Parent activity isn't instance of ActivityWithDialog!");
    }

    public final void dismissDialog() {
        DialogUtils.dismissDialog(getActivity());
    }

    public final boolean hasVisibleDialog() {
        return DialogUtils.hasVisibleDialog(getActivity());
    }

    public final void showDialog(@NonNull Dialog dialog) {
        DialogUtils.showDialog(getActivity(), dialog);
    }

    public final void showDialog(@NonNull AlertDialog.Builder dialog) {
        DialogUtils.showDialog(getActivity(), dialog);
    }

    public final void showDialog(@NonNull DialogFragment dialog) {
        DialogUtils.showDialog(getActivity(), dialog);
    }

    public final void showToast(@NonNull Toaster toaster) {
        if (getContext() == null) return;
        toaster.show(getContext());
    }

    public final void onBackPressed() {
        if (getActivity() != null) getActivity().onBackPressed();
    }
}
