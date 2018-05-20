package com.gianlu.commonutils.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;


public class DialogUtils {
    private static final Handler handler = new Handler(Looper.getMainLooper());

    public static void showDialog(@Nullable Activity activity, @NonNull AlertDialog.Builder builder) {
        if (activity == null) return;
        activityWithDialog(activity).showDialog(builder);
    }

    public static void showDialog(@Nullable Activity activity, @NonNull Dialog dialog) {
        if (activity == null) return;
        activityWithDialog(activity).showDialog(dialog);
    }

    public static void showDialog(@Nullable FragmentActivity activity, @NonNull DialogFragment dialog) {
        if (activity == null) return;

        FragmentManager manager = activity.getSupportFragmentManager();
        dialog.show(manager, null);
    }

    @NonNull
    private static ActivityWithDialog activityWithDialog(@NonNull Activity activity) {
        if (activity instanceof ActivityWithDialog)
            return (ActivityWithDialog) activity;
        else
            throw new IllegalArgumentException("Activity is not a subclass of ActivityWithDialog: " + activity);
    }

    public static void dismissDialog(@Nullable Activity activity) {
        if (activity == null) return;
        activityWithDialog(activity).dismissDialog();
    }

    public static void showDialogInternal(final Context context, final Dialog dialog) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (context instanceof Activity && ((Activity) context).isFinishing()) return;
                dialog.show();
            }
        });
    }

    public static void showDialogInternal(final Context context, final AlertDialog.Builder builder, final IDialog listener) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (context instanceof Activity && ((Activity) context).isFinishing()) return;
                Dialog dialog = builder.create();
                if (listener != null) listener.created(dialog);
                dialog.show();
            }
        });
    }

    @NonNull
    private static ProgressDialog progressDialog(@NonNull Context context, @NonNull String message) {
        ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage(message);
        pd.setIndeterminate(true);
        pd.setCancelable(false);
        return pd;
    }

    @NonNull
    public static ProgressDialog progressDialog(@NonNull Context context, @StringRes int message) {
        return progressDialog(context, context.getString(message));
    }

    public interface IDialog {
        void created(Dialog dialog);
    }
}
