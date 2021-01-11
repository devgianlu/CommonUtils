package com.gianlu.commonutils.misc;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;

import com.gianlu.commonutils.R;

@UiThread
public final class BreadcrumbsView extends LinearLayout implements View.OnClickListener {
    private final LayoutInflater inflater;
    private final int arrowRes;
    private Listener listener;
    private HorizontalScrollView parent;
    private final int mColor;

    public BreadcrumbsView(Context context) {
        this(context, null, 0);
    }

    public BreadcrumbsView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BreadcrumbsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.inflater = LayoutInflater.from(context);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.BreadcrumbsView, defStyleAttr, 0);

        try {
            mColor = a.getColor(a.getIndex(R.styleable.BreadcrumbsView_color), Color.BLACK);
            arrowRes = a.getResourceId(a.getIndex(R.styleable.BreadcrumbsView_arrowRes), 0);
            if (arrowRes == 0)
                throw new IllegalArgumentException("Must specify a resource for the arrow!");
        } finally {
            a.recycle();
        }

        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);

        int startAndEnd = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, context.getResources().getDisplayMetrics());
        setPaddingRelative(startAndEnd, 0, startAndEnd, 0);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (!(getParent() instanceof HorizontalScrollView))
            throw new IllegalStateException("Parent must be a HorizontalScrollView!");

        parent = (HorizontalScrollView) getParent();
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void clearListener() {
        this.listener = null;
    }

    @Override
    public void onClick(View v) {
        if (v instanceof TextView) {
            Item item = (Item) v.getTag();
            if (listener != null) listener.onSegmentSelected(item);
        }
    }

    private void addItemInternal(@NonNull Item item) {
        if (getChildCount() > 0) addView(createArrow());
        addView(createButton(item));
    }

    public void addItem(@NonNull Item item) {
        addItemInternal(item);
        scrollToEnd();
    }

    public void addItemNoScroll(@NonNull Item item) {
        addItemInternal(item);
    }

    public void addItems(@NonNull Item... items) {
        for (Item item : items) addItemInternal(item);
        scrollToEnd();
    }

    public void scrollToEnd() {
        post(() -> {
            if (parent != null) parent.fullScroll(View.FOCUS_RIGHT);
        });
    }

    @NonNull
    private ImageView createArrow() {
        ImageView arrow = (ImageView) inflater.inflate(R.layout.breadcrumbs_arrow, this, false);
        arrow.setImageResource(arrowRes);
        arrow.setImageTintList(ColorStateList.valueOf(mColor));
        return arrow;
    }

    @NonNull
    private TextView createButton(@NonNull Item item) {
        TextView button = (TextView) inflater.inflate(R.layout.breadcrumbs_button, this, false);
        button.setTextColor(mColor);
        button.setText(item.text);
        button.setTag(item);
        button.setOnClickListener(this);
        return button;
    }

    public void clear() {
        removeAllViews();
    }

    public void removeFrom(@NonNull Item item) {
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            if (view.getTag() == item)
                removeFrom(i);
        }
    }

    private void removeFrom(int pos) {
        if (getChildCount() == 0) return;

        for (int i = getChildCount() - 1; i >= (pos == 0 ? 0 : (pos - 1)); i--)
            removeViewAt(i);
    }

    /**
     * @return Whether the user can't navigate back
     */
    public boolean navigateBack() {
        removeFrom(getChildCount() - 1);
        if (getChildCount() == 0) {
            return true;
        } else {
            onClick(getChildAt(getChildCount() - 1));
            return false;
        }
    }

    public interface Listener {

        @UiThread
        void onSegmentSelected(@NonNull Item item);
    }

    public static class Item {
        public final String text;
        public final int type;
        public final Object data;

        public Item(@NonNull String text, int type, @Nullable Object data) {
            this.text = text;
            this.type = type;
            this.data = data;
        }

        public Item(@NonNull String text, int type) {
            this(text, type, null);
        }
    }
}
