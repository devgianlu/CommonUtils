package com.gianlu.commonutils.adapters;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collection;

public final class SelectiveDividerItemDecoration extends RecyclerView.ItemDecoration {
    private static final int[] ATTRS = new int[]{android.R.attr.listDivider};
    private final Collection<Integer> positions;
    private final Rect mBounds = new Rect();
    private final int mOrientation;
    private final Drawable mDivider;

    /**
     * Creates a divider {@link RecyclerView.ItemDecoration} that can be used with a
     * {@link LinearLayoutManager}.
     *
     * @param context     Current context, it will be used to access resources.
     * @param orientation Divider orientation. Should be {@link RecyclerView#HORIZONTAL} or {@link RecyclerView#VERTICAL}.
     */
    public SelectiveDividerItemDecoration(@NonNull Context context, @RecyclerView.Orientation int orientation, @NonNull Collection<Integer> positions) {
        this.positions = positions;
        this.mOrientation = orientation;

        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        try {
            this.mDivider = a.getDrawable(0);
        } finally {
            a.recycle();
        }
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        if (parent.getLayoutManager() == null || mDivider == null) return;
        if (mOrientation == RecyclerView.VERTICAL) drawVertical(c, parent);
        else drawHorizontal(c, parent);
    }

    public boolean isDecorated(int pos) {
        return positions.contains(pos);
    }

    private void drawVertical(Canvas canvas, RecyclerView parent) {
        canvas.save();

        int left;
        int right;
        if (parent.getClipToPadding()) {
            left = parent.getPaddingLeft();
            right = parent.getWidth() - parent.getPaddingRight();
            canvas.clipRect(left, parent.getPaddingTop(), right, parent.getHeight() - parent.getPaddingBottom());
        } else {
            left = 0;
            right = parent.getWidth();
        }

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            if (isDecorated(i)) {
                View child = parent.getChildAt(i);
                parent.getDecoratedBoundsWithMargins(child, mBounds);
                int bottom = mBounds.bottom + Math.round(child.getTranslationY());
                int top = bottom - mDivider.getIntrinsicHeight();
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(canvas);
            }
        }

        canvas.restore();
    }

    private void drawHorizontal(Canvas canvas, RecyclerView parent) {
        canvas.save();

        int top;
        int bottom;
        if (parent.getClipToPadding()) {
            top = parent.getPaddingTop();
            bottom = parent.getHeight() - parent.getPaddingBottom();
            canvas.clipRect(parent.getPaddingLeft(), top, parent.getWidth() - parent.getPaddingRight(), bottom);
        } else {
            top = 0;
            bottom = parent.getHeight();
        }

        RecyclerView.LayoutManager lm = parent.getLayoutManager();
        if (lm == null) return;

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            if (isDecorated(i)) {
                View child = parent.getChildAt(i);
                lm.getDecoratedBoundsWithMargins(child, mBounds);
                int right = mBounds.right + Math.round(child.getTranslationX());
                int left = right - mDivider.getIntrinsicWidth();
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(canvas);
            }
        }

        canvas.restore();
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        if (mDivider == null) {
            outRect.set(0, 0, 0, 0);
            return;
        }

        int pos = parent.indexOfChild(view);
        if (pos != -1 && isDecorated(pos)) {
            if (mOrientation == RecyclerView.VERTICAL)
                outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
            else outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
        }
    }
}
