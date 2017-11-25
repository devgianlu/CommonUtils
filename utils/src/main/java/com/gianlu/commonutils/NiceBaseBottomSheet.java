package com.gianlu.commonutils;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

// TODO: Must have maximum height
public abstract class NiceBaseBottomSheet extends BottomSheetBehavior.BottomSheetCallback {
    private final int headerRes;
    private final int contentRes;
    private final boolean forceLayoutInflating;
    private final Context context;
    private final BottomSheetBehavior<View> behavior;
    private final FrameLayout content;
    private final FrameLayout header;
    private final View mask;
    private FloatingActionButton action;

    public NiceBaseBottomSheet(ViewGroup parent, @LayoutRes int headerRes, @LayoutRes int contentRes, boolean forceLayoutInflating) {
        this.headerRes = headerRes;
        this.contentRes = contentRes;
        this.forceLayoutInflating = forceLayoutInflating;

        ViewGroup sheet = parent.findViewById(R.id.niceBottomSheet_sheet);
        context = sheet.getContext();
        behavior = BottomSheetBehavior.from((View) sheet);
        behavior.setBottomSheetCallback(this);
        behavior.setPeekHeight(0);
        behavior.setHideable(true);

        mask = parent.findViewById(R.id.niceBottomSheet_mask);
        mask.setVisibility(View.VISIBLE);
        mask.setBackgroundColor(Color.BLACK);
        mask.setClickable(false);
        mask.setAlpha(0);

        content = sheet.findViewById(R.id.niceBottomSheet_content);
        header = sheet.findViewById(R.id.niceBottomSheet_header);

        action = parent.findViewById(R.id.niceBottomSheet_action);
        if (action != null) action.setCompatElevation(sheet.getElevation() + 1);

        bindViewsInternal();
    }

    private void showMask() {
        mask.animate().cancel();
        mask.setAlpha(0);
        mask.setClickable(true);
        mask.animate().alpha(.2f).setDuration(200).start();
    }

    private void hideMask() {
        mask.animate().cancel();
        mask.animate().alpha(0).setDuration(200).withEndAction(new Runnable() {
            @Override
            public void run() {
                mask.setOnClickListener(null);
                mask.setClickable(false);
            }
        }).start();
    }

    @NonNull
    protected final Context getContext() {
        return context;
    }

    private void bindViewsInternal() {
        if (forceLayoutInflating || header.getChildCount() == 0 || content.getChildCount() == 0) {
            LayoutInflater inflater = LayoutInflater.from(context);
            header.removeAllViews();
            inflater.inflate(headerRes, header, true);
            content.removeAllViews();
            inflater.inflate(contentRes, content, true);
        }
    }

    public final void collapse() {
        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        hideMask();
    }

    public final void expand(Object... payloads) {
        bindViewsInternal();
        if (action != null && !onPrepareAction(action, payloads)) action = null;
        onCreateHeaderView(header, payloads);
        onCreateContentView(content, payloads);

        if (action != null) action.setVisibility(View.VISIBLE);
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        showMask();
    }

    protected boolean onPrepareAction(@NonNull FloatingActionButton fab, Object... payloads) {
        return false;
    }

    protected abstract void onCreateHeaderView(@NonNull ViewGroup parent, Object... payloads);

    protected abstract void onCreateContentView(@NonNull ViewGroup parent, Object... payloads);

    protected void onUpdateViews(Object... payloads) {
    }

    protected void cleanUp() {
    }

    @Override
    public final void onStateChanged(@NonNull View bottomSheet, int newState) {
        if (newState == BottomSheetBehavior.STATE_EXPANDED) {
            mask.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    collapse();
                }
            });
        } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
            if (action != null) action.setVisibility(View.GONE);
            behavior.setPeekHeight(0);
            hideMask();
            cleanUp();
        }
    }

    @Override
    public final void onSlide(@NonNull View bottomSheet, float slideOffset) {
        if (action != null)
            action.animate().scaleX(slideOffset).scaleY(slideOffset).setDuration(0).start();
    }

    public final boolean isExpanded() {
        return behavior.getState() == BottomSheetBehavior.STATE_EXPANDED;
    }

    public final void update(Object... payloads) {
        if (!isExpanded()) return;
        onUpdateViews(payloads);
    }
}
