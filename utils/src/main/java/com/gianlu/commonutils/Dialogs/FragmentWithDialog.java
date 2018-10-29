package com.gianlu.commonutils.Dialogs;

import android.app.Dialog;
import android.content.Context;

import com.gianlu.commonutils.Toaster;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

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
