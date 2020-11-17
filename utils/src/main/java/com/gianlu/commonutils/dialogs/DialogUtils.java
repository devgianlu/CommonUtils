package com.gianlu.commonutils.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.gianlu.commonutils.ui.Toaster;

import java.util.concurrent.atomic.AtomicLong;


public final class DialogUtils {
    private static final Handler handler = new Handler(Looper.getMainLooper());
    private static final String TAG = DialogUtils.class.getSimpleName();
    private static final AtomicLong handleCounter = new AtomicLong(0);

    private DialogUtils() {
    }

    public static long showDialog(@Nullable Activity activity, @NonNull AlertDialog.Builder builder) {
        if (activity == null) return -1;
        return activityWithDialog(activity).showDialog(builder);
    }

    public static long showDialog(@Nullable Activity activity, @NonNull Dialog dialog) {
        if (activity == null) return -1;
        return activityWithDialog(activity).showDialog(dialog);
    }

    public static long showDialog(@Nullable FragmentActivity activity, @NonNull DialogFragment dialog, @Nullable String tag) {
        if (activity == null) return -1;

        FragmentManager manager = activity.getSupportFragmentManager();
        try {
            dialog.show(manager, tag);
            return handleCounter.getAndIncrement();
        } catch (IllegalStateException ex) {
            Log.e(TAG, "Failed showing dialog.", ex); // We can't do nothing
            return -1;
        }
    }

    public static long showDialog(@Nullable Fragment fragment, @NonNull DialogFragment dialog, @Nullable String tag) {
        if (fragment == null) return -1;

        FragmentManager manager = fragment.getChildFragmentManager();
        try {
            dialog.show(manager, tag);
            return handleCounter.getAndIncrement();
        } catch (IllegalStateException ex) {
            Log.e(TAG, "Failed showing dialog.", ex); // We can't do nothing
            return -1;
        }
    }

    @NonNull
    private static ActivityWithDialog activityWithDialog(@NonNull Activity activity) {
        if (activity instanceof ActivityWithDialog)
            return (ActivityWithDialog) activity;
        else
            throw new IllegalArgumentException("Activity is not a subclass of ActivityWithDialog: " + activity);
    }

    public static void dismissDialog(@Nullable Activity activity, long handle) {
        if (activity == null) return;
        activityWithDialog(activity).dismissDialog(handle);
    }

    public static void dismissDialog(@Nullable Activity activity) {
        dismissDialog(activity, DialogUtils.lastDialogHandle());
    }

    public static boolean isContextValid(Context context) {
        return context instanceof Activity && !((Activity) context).isFinishing() && !((Activity) context).isDestroyed();
    }

    public static long showDialogValid(@NonNull Context context, @NonNull Dialog dialog) {
        handler.post(() -> {
            if (!isContextValid(context)) return;
            dialog.show();
        });

        return handleCounter.getAndIncrement();
    }

    public static long showDialogValid(@NonNull Context context, @NonNull AlertDialog.Builder builder, OnDialogCreatedListener listener) {
        long handle = handleCounter.getAndIncrement();

        handler.post(() -> {
            if (!isContextValid(context)) return;
            Dialog dialog = builder.create();
            if (listener != null) listener.created(dialog, handle);
            dialog.show();
        });

        return handle;
    }

    public static void showToast(Context context, @NonNull Toaster toaster) {
        if (context == null) return;
        toaster.show(context);
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

    public static boolean hasVisibleDialog(@Nullable Activity activity) {
        return activity != null && activityWithDialog(activity).hasVisibleDialog();
    }

    public static long lastDialogHandle() {
        return handleCounter.get() - 1;
    }

    public interface OnDialogCreatedListener {
        @UiThread
        void created(@NonNull Dialog dialog, long handle);
    }

    public interface ShowStuffInterface {
        long showDialog(@NonNull Dialog dialog);

        long showDialog(@NonNull AlertDialog.Builder dialog);

        long showDialog(@NonNull DialogFragment dialog);

        long showDialog(@NonNull DialogFragment dialog, @Nullable String tag);

        void showToast(@NonNull Toaster toaster);

        long showProgress(@StringRes int res);

        void dismissDialog(long handle);

        void dismissDialog();
    }
}
