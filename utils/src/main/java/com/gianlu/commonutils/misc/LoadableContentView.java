package com.gianlu.commonutils.misc;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.gianlu.commonutils.R;

public class LoadableContentView extends FrameLayout {
    private static final int DEFAULT_ANIMATION_DURATION = 500;
    private final ProgressBar loading;
    private final View scrim;
    private volatile boolean inflating;
    private AlertDialog attachedDialog;

    public LoadableContentView(@NonNull Context context) {
        this(context, null, 0);
    }

    public LoadableContentView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadableContentView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        inflating = true;
        View.inflate(context, R.layout.view_loadable_content, this);
        inflating = false;

        scrim = findViewById(R.id.loadableContent_scrim);
        loading = findViewById(R.id.loadableContent_loading);
        notLoading(false);
    }

    private static void animate(@NonNull View view, boolean disappear) {
        Animation animation;
        if (disappear) {
            view.setAlpha(1f);
            view.setVisibility(VISIBLE);
            animation = new AlphaAnimation(1f, 0f);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    view.setVisibility(GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
        } else {
            view.setAlpha(0f);
            view.setVisibility(VISIBLE);
            animation = new AlphaAnimation(0f, 1f);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    view.setAlpha(1f);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
        }

        animation.setDuration(DEFAULT_ANIMATION_DURATION);
        view.startAnimation(animation);
    }

    private static void toggleButtons(@NonNull AlertDialog dialog, boolean enabled) {
        if (!dialog.isShowing()) return;

        dialog.setCancelable(enabled);

        Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        if (button != null) button.setEnabled(enabled);

        button = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        if (button != null) button.setEnabled(enabled);

        button = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
        if (button != null) button.setEnabled(enabled);
    }

    public void setScrimColor(@ColorInt int color) {
        scrim.setBackgroundColor(color);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        ViewGroup.LayoutParams params = scrim.getLayoutParams();
        if (params != null) params.height = getHeight();
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (inflating) {
            super.addView(child, index, params);
        } else {
            child.setVisibility(VISIBLE);
            super.addView(child, 0, params);
        }
    }

    public void notLoading(boolean animate) {
        if (attachedDialog != null) toggleButtons(attachedDialog, true);

        if (animate) {
            animate(scrim, true);
            animate(loading, true);
        } else {
            scrim.setVisibility(GONE);
            loading.setVisibility(GONE);
        }
    }

    public void loading(boolean animate) {
        if (attachedDialog != null) toggleButtons(attachedDialog, false);

        if (animate) {
            animate(scrim, false);
            animate(loading, false);
        } else {
            scrim.setVisibility(VISIBLE);
            loading.setVisibility(VISIBLE);
        }
    }

    public void detachDialog() {
        attachedDialog = null;
    }

    public void attachDialog(@NonNull AlertDialog dialog) {
        attachedDialog = dialog;
    }
}
