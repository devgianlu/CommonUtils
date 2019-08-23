package com.gianlu.commonutils.BottomSheet;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.gianlu.commonutils.R;

public class ModalBottomSheetHeaderView extends FrameLayout {
    private final GradientDrawable bgDrawable;
    private boolean toolbarInflated = false;
    private TextView title;
    private ImageButton close;

    public ModalBottomSheetHeaderView(@NonNull Context context) {
        this(context, null, 0);
    }

    public ModalBottomSheetHeaderView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ModalBottomSheetHeaderView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        bgDrawable = (GradientDrawable) context.getDrawable(R.drawable.modal_bottom_sheet_header_background);
        if (bgDrawable == null) throw new RuntimeException();
        super.setBackground(bgDrawable);
    }

    @Override
    public void setBackgroundColor(@ColorInt int color) {
        bgDrawable.setColor(color);
    }

    public void setBackgroundColorRes(@ColorRes int color) {
        setBackgroundColor(ContextCompat.getColor(getContext(), color));
    }

    @Override
    public void setBackground(Drawable background) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setBackgroundResource(int resid) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setBackgroundTintList(@Nullable ColorStateList tint) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setBackgroundTintMode(@Nullable PorterDuff.Mode tintMode) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (toolbarInflated) throw new IllegalStateException();
        super.addView(child, index, params);
    }

    private void inflateToolbar() {
        if (toolbarInflated) return;

        inflate(getContext(), R.layout.sheet_toolbar_header, this);
        toolbarInflated = true;

        close = findViewById(R.id.sheetToolbar_close);
        title = findViewById(R.id.sheetToolbar_title);
    }

    public void setTitle(@NonNull String str) {
        inflateToolbar();
        title.setText(str);
    }

    public void showClose() {
        close.setVisibility(VISIBLE);
    }

    public void hideClose() {
        close.setVisibility(GONE);
    }
}
