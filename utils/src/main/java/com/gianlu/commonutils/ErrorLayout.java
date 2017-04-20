package com.gianlu.commonutils;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Keep;
import android.support.annotation.StringRes;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

@Keep
@SuppressWarnings({"unused", "WeakerAccess"})
public class ErrorLayout {
    public static void show(final ViewGroup parent, final String errorMessage) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            _show(parent, errorMessage);
        } else {
            new Handler(parent.getContext().getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    _show(parent, errorMessage);
                }
            });
        }
    }

    public static void hide(final ViewGroup parent) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            _hide(parent);
        } else {
            new Handler(parent.getContext().getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    _hide(parent);
                }
            });
        }
    }

    private static void _show(ViewGroup parent, String errorMessage) {
        LinearLayout container = (LinearLayout) parent.findViewById(R.id.errorLayout_container);
        TextView message = (TextView) container.findViewById(R.id.errorLayout_message);
        message.setText(errorMessage);
        container.setVisibility(View.VISIBLE);
    }

    public static void _hide(ViewGroup parent) {
        LinearLayout container = (LinearLayout) parent.findViewById(R.id.errorLayout_container);
        container.setVisibility(View.VISIBLE);
    }

    public static void show(ViewGroup parent, @StringRes int errorMessage) {
        show(parent, parent.getContext().getString(errorMessage));
    }
}
