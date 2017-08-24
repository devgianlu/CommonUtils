package com.gianlu.commonutils;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class BaseBottomSheet<E> extends BottomSheetBehavior.BottomSheetCallback {
    protected final TextView title;
    protected final Context context;
    protected final Handler mainHandler;
    protected final FrameLayout content;
    private final View mask;
    private final int layoutRes;
    private final BottomSheetBehavior behavior;
    private final boolean forceLayoutInflating;
    protected E current;

    public BaseBottomSheet(View parent, @LayoutRes int layoutRes, boolean forceLayoutInflating) {
        this.layoutRes = layoutRes;
        this.forceLayoutInflating = forceLayoutInflating;
        View sheet = parent.findViewById(R.id.bottomSheet_container);
        context = sheet.getContext();
        behavior = BottomSheetBehavior.from(sheet);
        behavior.setBottomSheetCallback(this);
        behavior.setPeekHeight(0);
        behavior.setHideable(true);
        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        mask = parent.findViewById(R.id.bottomSheet_mask);
        mask.setBackgroundColor(Color.BLACK);
        mask.setAlpha(.2f);

        title = sheet.findViewById(R.id.bottomSheet_title);
        content = sheet.findViewById(R.id.bottomSheet_content);
        LayoutInflater.from(context).inflate(layoutRes, content, true);

        ImageButton close = sheet.findViewById(R.id.bottomSheet_close);
        close.setBackground(CommonUtils.resolveAttrAsDrawable(context, R.attr.selectableItemBackgroundBorderless));
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collapse();
            }
        });

        mainHandler = new Handler(context.getMainLooper());

        bindViews();
    }

    @Override
    public void onStateChanged(@NonNull View bottomSheet, int newState) {
        if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
            behavior.setPeekHeight(0);
            mask.setVisibility(View.GONE);
        } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
            mask.setAlpha(.2f);
        }
    }

    @Override
    public void onSlide(@NonNull View bottomSheet, float slideOffset) {
    }

    public boolean shouldUpdate() {
        return behavior.getState() == BottomSheetBehavior.STATE_EXPANDED;
    }

    public abstract void bindViews();

    public void expand(E item) {
        if (forceLayoutInflating) {
            content.removeAllViews();
            LayoutInflater.from(context).inflate(layoutRes, content, true);
            bindViews();
        }

        current = item;
        setupViewInternal(item);
        updateViewInternal(item);
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        mask.setAlpha(0);
        mask.setVisibility(View.VISIBLE);
        mask.animate().alpha(.2f).setDuration(500).start();
        mask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collapse();
            }
        });
    }

    private void setupViewInternal(E item) {
        if (item == null) return;
        setupView(item);
    }

    protected abstract void setupView(@NonNull E item);

    protected abstract void updateView(@NonNull E item);

    private void updateViewInternal(E item) {
        if (item == null) return;
        current = item;
        updateView(item);
    }

    public void update(E item) {
        if (item == null) return;
        updateViewInternal(item);
    }

    public void collapse() {
        current = null;
        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        mask.animate().alpha(0).setDuration(500).start();
        mask.setClickable(false);
    }
}
