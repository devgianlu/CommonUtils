package com.gianlu.commonutils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import com.gianlu.commonutils.Dialogs.DialogUtils;

import java.util.concurrent.ThreadLocalRandom;

public class AskPermission {
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
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                listener.askRationale(builder);
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request(activity, permission, listener);
                    }
                });

                DialogUtils.showDialog(activity, builder);
            } else {
                request(activity, permission, listener);
            }
        }
    }

    private static void request(@NonNull FragmentActivity activity, @NonNull String permission, @NonNull Listener listener) {
        int code = ThreadLocalRandom.current().nextInt(32);
        PhantomFragment fragment = PhantomFragment.get(listener, permission, code);

        activity.getSupportFragmentManager()
                .beginTransaction()
                .add(fragment, PHANTOM_TAG + code)
                .commitNow();
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
        public void onAttach(Context context) {
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

            if (getActivity() != null) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .remove(this)
                        .commitNowAllowingStateLoss();
            }
        }
    }
}
