package com.gianlu.commonutils;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

public class MaximumHeightRecyclerView extends RecyclerView {
    private final int mMaxHeight;

    public MaximumHeightRecyclerView(Context context) {
        this(context, null, 0);
    }

    public MaximumHeightRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaximumHeightRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MaximumHeightRecyclerView, 0, 0);
        try {
            mMaxHeight = a.getDimensionPixelSize(R.styleable.MaximumHeightRecyclerView_maxHeight, 0);
        } finally {
            a.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);

        if (mMaxHeight > 0) {
            setMeasuredDimension(getMeasuredWidth(), MeasureSpec.makeMeasureSpec(
                    Math.min(mMaxHeight, MeasureSpec.getSize(heightSpec)),
                    MeasureSpec.EXACTLY));
        }
    }
}
