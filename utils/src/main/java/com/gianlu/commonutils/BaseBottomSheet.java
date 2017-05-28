package com.gianlu.commonutils;

import android.content.Context;
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
    private final BottomSheetBehavior behavior;
    protected E current;

    protected BaseBottomSheet(View parent, @LayoutRes int layoutRes) {
        View sheet = parent.findViewById(R.id.bottomSheet_container);
        context = sheet.getContext();
        behavior = BottomSheetBehavior.from(sheet);
        behavior.setBottomSheetCallback(this);
        behavior.setPeekHeight(0);
        behavior.setHideable(true);
        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        mask = parent.findViewById(R.id.bottomSheet_mask);
        title = (TextView) sheet.findViewById(R.id.bottomSheet_title);
        content = (FrameLayout) sheet.findViewById(R.id.bottomSheet_content);
        LayoutInflater.from(context).inflate(layoutRes, content, true);

        ImageButton close = (ImageButton) sheet.findViewById(R.id.bottomSheet_close);
        close.setBackgroundResource(getRippleDark());
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        mask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collapse();
            }
        });

        mainHandler = new Handler(context.getMainLooper());
    }

    protected abstract int getRippleDark();

    @Override
    public void onStateChanged(@NonNull View bottomSheet, int newState) {
        if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
            behavior.setPeekHeight(0);
            mask.setVisibility(View.GONE);
        } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
            mask.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onSlide(@NonNull View bottomSheet, float slideOffset) {
    }

    public boolean shouldUpdate() {
        return behavior.getState() == BottomSheetBehavior.STATE_EXPANDED;
    }

    public void expand(E item) {
        current = item;
        setupViewInternal(item);
        updateViewInternal(item);
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
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
        updateView(item);
    }

    public void collapse() {
        current = null;
        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }
}
