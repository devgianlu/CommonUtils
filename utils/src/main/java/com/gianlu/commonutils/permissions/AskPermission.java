package com.gianlu.commonutils.permissions;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.gianlu.commonutils.dialogs.DialogUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.concurrent.ThreadLocalRandom;

public final class AskPermission {
    private static final String PHANTOM_TAG = PhantomFragment.class.getName();

    public static void ask(@NonNull final FragmentActivity activity, @NonNull final String permission, @NonNull final Listener listener) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            listener.permissionGranted(permission);
            return;
        }

        if (ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED) {
            listener.permissionGranted(permission);
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(activity);
                listener.askRationale(builder);
                builder.setPositiveButton(android.R.string.ok, (dialog, which) -> request(activity, permission, listener));

                DialogUtils.showDialog(activity, builder);
            } else {
                request(activity, permission, listener);
            }
        }
    }

    private static final String TAG = AskPermission.class.getSimpleName();

    private static void request(@NonNull FragmentActivity activity, @NonNull String permission, @NonNull Listener listener) {
        int code = ThreadLocalRandom.current().nextInt(32);
        PhantomFragment fragment = PhantomFragment.get(listener, permission, code);

        try {
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .add(fragment, PHANTOM_TAG + code)
                    .commitNow();
        } catch (IllegalStateException ex) {
            Log.w(TAG, ex);
        }
    }

    public interface Listener {
        void permissionGranted(@NonNull String permission);

        void permissionDenied(@NonNull String permission);

        void askRationale(@NonNull AlertDialog.Builder builder);
    }

    public static class PhantomFragment extends Fragment {
        private String permission;
        private Listener listener;
        private int code;

        @NonNull
        public static PhantomFragment get(Listener listener, String permission, int code) {
            PhantomFragment fragment = new PhantomFragment();
            fragment.listener = listener;
            fragment.permission = permission;
            fragment.code = code;
            return fragment;
        }

        @Override
        public void onAttach(@NonNull Context context) {
            super.onAttach(context);

            requestPermissions(new String[]{permission}, code);
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            if (listener != null && code == requestCode) {
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED)
                        listener.permissionGranted(permissions[i]);
                    else
                        listener.permissionDenied(permissions[i]);
                }
            }

            try {
                if (getActivity() != null) {
                    FragmentManager manager = getActivity().getSupportFragmentManager();
                    manager.executePendingTransactions();
                    manager.beginTransaction()
                            .remove(this)
                            .commitAllowingStateLoss();
                }
            } catch (IllegalStateException ex) {
                Log.w(TAG, ex);
            }
        }
    }
}
