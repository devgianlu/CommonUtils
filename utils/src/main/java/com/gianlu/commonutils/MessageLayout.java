package com.gianlu.commonutils;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

@SuppressWarnings({"unused", "WeakerAccess"})
public class MessageLayout {
    public static void show(final ViewGroup parent, final String message, @DrawableRes final int icon) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            _show(parent, message, icon);
        } else {
            new Handler(parent.getContext().getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    _show(parent, message, icon);
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

    private static void _show(ViewGroup parent, String errorMessage, @DrawableRes int iconRes) {
        LinearLayout container = (LinearLayout) parent.findViewById(R.id.messageLayout_container);
        TextView message = (TextView) container.findViewById(R.id.messageLayout_message);
        message.setText(errorMessage);
        ImageView icon = (ImageView) container.findViewById(R.id.messageLayout_icon);
        icon.setImageResource(iconRes);
        container.setVisibility(View.VISIBLE);
    }

    public static void _hide(ViewGroup parent) {
        LinearLayout container = (LinearLayout) parent.findViewById(R.id.messageLayout_container);
        container.setVisibility(View.VISIBLE);
    }

    public static void show(ViewGroup parent, @StringRes int message, @DrawableRes int icon) {
        show(parent, parent.getContext().getString(message), icon);
    }
}
