package com.gianlu.commonutils.permissions;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.gianlu.commonutils.dialogs.DialogUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;


public final class AskPermission {
    public static void ask(@NonNull final FragmentActivity activity, @NonNull final String permission, @NonNull final Listener listener) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            listener.permissionGranted(permission);
            return;
        }

        if (ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED) {
            listener.permissionGranted(permission);
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(activity);
            listener.askRationale(builder);
            builder.setPositiveButton(android.R.string.ok, (dialog, which) -> request(activity, permission, listener));

            DialogUtils.showDialog(activity, builder);
        } else {
            request(activity, permission, listener);
        }
    }

    private static final String TAG = AskPermission.class.getSimpleName();

    private static void request(@NonNull FragmentActivity activity, @NonNull String permission, @NonNull Listener listener) {
        PhantomFragment fragment = PhantomFragment.get(listener, permission);

        try {
            activity.getSupportFragmentManager().beginTransaction().add(fragment, null).commitNow();
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

        @NonNull
        public static PhantomFragment get(Listener listener, String permission) {
            PhantomFragment fragment = new PhantomFragment();
            fragment.listener = listener;
            fragment.permission = permission;
            return fragment;
        }

        @Override
        public void onAttach(@NonNull Context context) {
            super.onAttach(context);

            ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (listener != null) {
                    if (isGranted) {
                        listener.permissionGranted(permission);
                    } else {
                        listener.permissionDenied(permission);
                    }
                }

                try {
                    if (getActivity() != null) {
                        FragmentManager manager = getActivity().getSupportFragmentManager();
                        manager.executePendingTransactions();
                        manager.beginTransaction().remove(this).commitAllowingStateLoss();
                    }
                } catch (IllegalStateException ex) {
                    Log.w(TAG, ex);
                }
            });

            requestPermissionLauncher.launch(permission);
        }
    }
}
