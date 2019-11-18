package com.gianlu.commonutils.drawer;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.gianlu.commonutils.R;

public final class StatusBarPadding extends View {
    private final int mHeight;

    public StatusBarPadding(Context context) {
        this(context, null, 0);
    }

    public StatusBarPadding(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StatusBarPadding(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.StatusBarPadding, 0, 0);
        try {
            int color = a.getColor(R.styleable.StatusBarPadding_colorRes, ContextCompat.getColor(context, R.color.colorPrimaryVariant));
            setBackgroundColor(color);
        } finally {
            a.recycle();
        }

        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0)
            mHeight = getResources().getDimensionPixelSize(resourceId);
        else
            mHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(mHeight, MeasureSpec.EXACTLY));
    }
}
