package com.gianlu.commonutils;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.util.TypedValue;
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
        if (parent == null) return;
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
        LinearLayout container = parent.findViewById(R.id.messageLayout_container);
        TextView message = container.findViewById(R.id.messageLayout_message);
        message.setText(errorMessage);
        ImageView icon = container.findViewById(R.id.messageLayout_icon);
        icon.setImageResource(iconRes);
        container.setVisibility(View.VISIBLE);
    }

    private static void _hide(ViewGroup parent) {
        LinearLayout container = parent.findViewById(R.id.messageLayout_container);
        container.setVisibility(View.GONE);
    }

    private static void _setPaddingTop(ViewGroup parent, int padding) {
        int paddingPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, padding, parent.getResources().getDisplayMetrics());
        parent.findViewById(R.id.messageLayout_container).setPadding(0, paddingPx, 0, 0);
    }

    public static void show(ViewGroup parent, @StringRes int message, @DrawableRes int icon) {
        if (parent == null) return;
        show(parent, parent.getContext().getString(message), icon);
    }

    public static void setPaddingTop(final ViewGroup parent, final int padding) {
        if (parent == null) return;
        if (Looper.myLooper() == Looper.getMainLooper()) {
            _setPaddingTop(parent, padding);
        } else {
            new Handler(parent.getContext().getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    _setPaddingTop(parent, padding);
                }
            });
        }
    }
}
