package com.gianlu.commonutils.BottomSheet;

import android.content.Context;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.gianlu.commonutils.Dialogs.DialogUtils;
import com.gianlu.commonutils.Logging;
import com.gianlu.commonutils.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.FragmentActivity;

public abstract class BaseModalBottomSheet<Setup, Update> extends BottomSheetDialogFragment {
    private FloatingActionButton action;
    private FrameLayout header;
    private FrameLayout body;
    private ProgressBar loading;
    private Toolbar toolbar;
    private boolean onlyToolbar = false;
    private Setup payload;
    private int lastHeaderEndPadding = -1;

    @Nullable
    protected Setup getSetupPayload() {
        return payload;
    }

    /**
     * @return Whether the implementation provides a layout for the header
     */
    protected abstract boolean onCreateHeader(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent, @NonNull Setup payload);

    protected abstract void onCreateBody(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent, @NonNull Setup payload);

    protected abstract void onCustomizeToolbar(@NonNull Toolbar toolbar, @NonNull Setup payload);

    /**
     * @return Whether the implementation provides an action
     */
    protected abstract boolean onCustomizeAction(@NonNull FloatingActionButton action, @NonNull Setup payload);

    @Nullable
    protected LayoutInflater createLayoutInflater(@NonNull Context context, @NonNull Setup payload) {
        return null;
    }

    @NonNull
    protected BottomSheetCallback prepareCallback() {
        return new BottomSheetCallback();
    }

    @Override
    @CallSuper
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Window window = getDialog().getWindow();
        if (window != null) {
            View bottomSheet = window.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet == null)
                window.getDecorView().getViewTreeObserver().addOnGlobalLayoutListener(new AttachCallbackTreeObserver());
            else
                attachCustomCallback(bottomSheet);
        }
    }

    private void attachCustomCallback(@NonNull View bottomSheet) {
        BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setBottomSheetCallback(prepareCallback());

        attachedBehavior(bottomSheet.getContext(), behavior);
    }

    @CallSuper
    protected void attachedBehavior(@NonNull Context context, @NonNull BottomSheetBehavior behavior) {
        int screenHeight = context.getResources().getDisplayMetrics().heightPixels;
        int peekHeight = screenHeight * 9 / 16;
        behavior.setPeekHeight(peekHeight);
    }

    public final void update(@NonNull Update payload) {
        if (getDialog() != null && getDialog().isShowing() && DialogUtils.isContextValid(getContext()))
            onRequestedUpdate(payload);
    }

    protected void onRequestedUpdate(@NonNull Update payload) {
    }

    @Override
    public final View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (payload == null) {
            Logging.log(new NullPointerException("Payload is null!"));
            dismissAllowingStateLoss();
            return null;
        }

        LayoutInflater themedInflater = createLayoutInflater(requireContext(), payload);
        if (themedInflater != null) inflater = themedInflater;

        CoordinatorLayout layout = (CoordinatorLayout) inflater.inflate(R.layout.modal_bottom_sheet, container, false);

        toolbar = layout.findViewById(R.id.modalBottomSheet_toolbar);
        header = layout.findViewById(R.id.modalBottomSheet_header);
        body = layout.findViewById(R.id.modalBottomSheet_body);
        loading = layout.findViewById(R.id.modalBottomSheet_loading);
        action = layout.findViewById(R.id.modalBottomSheet_action);

        onCustomizeToolbar(toolbar, payload);
        onlyToolbar = !onCreateHeader(inflater, header, payload);
        onCreateBody(inflater, body, payload);

        invalidateAction();

        if (onlyToolbar) showToolbar();
        else showHeader();

        return layout;
    }

    protected final void invalidateAction() {
        if (onCustomizeAction(action, payload)) {
            action.show();
            lastHeaderEndPadding = header.getPaddingEnd();
            int end = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, getResources().getDisplayMetrics());
            header.setPaddingRelative(header.getPaddingStart(), header.getPaddingTop(), end, header.getPaddingBottom());
        } else {
            action.hide();
            if (lastHeaderEndPadding != -1)
                header.setPaddingRelative(header.getPaddingStart(), header.getPaddingTop(), lastHeaderEndPadding, header.getPaddingBottom());
        }
    }

    public final void show(@Nullable FragmentActivity activity, @NonNull Setup payload) {
        if (activity == null) return;

        this.payload = payload;
        DialogUtils.showDialog(activity, this);
    }

    private void displayClose() {
        toolbar.setNavigationIcon(R.drawable.baseline_clear_24);
        toolbar.setNavigationOnClickListener(v -> dismissAllowingStateLoss());
    }

    private void hideClose() {
        toolbar.setNavigationIcon(null);
        toolbar.setNavigationOnClickListener(null);
    }

    private void showToolbar() {
        toolbar.setVisibility(View.VISIBLE);
        header.setVisibility(View.GONE);
    }

    private void showHeader() {
        toolbar.setVisibility(View.GONE);
        header.setVisibility(View.VISIBLE);
    }

    public void isLoading(boolean set) {
        if (set) {
            loading.setVisibility(View.VISIBLE);
            body.setVisibility(View.GONE);
        } else {
            loading.setVisibility(View.GONE);
            body.setVisibility(View.VISIBLE);
        }
    }

    private boolean isFullscreen(@NonNull View bottomSheet) {
        int parentHeight = ((View) bottomSheet.getParent()).getHeight();
        int sheetHeight = bottomSheet.getHeight();
        return parentHeight == sheetHeight;
    }

    private class AttachCallbackTreeObserver implements ViewTreeObserver.OnGlobalLayoutListener {

        @Override
        public void onGlobalLayout() {
            Window window = getDialog().getWindow();
            if (window != null) {
                View bottomSheet = window.findViewById(com.google.android.material.R.id.design_bottom_sheet);
                if (bottomSheet != null) {
                    attachCustomCallback(bottomSheet);
                    window.getDecorView().getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        }
    }

    public class BottomSheetCallback extends BottomSheetBehavior.BottomSheetCallback {

        @Override
        @CallSuper
        public void onStateChanged(@NonNull View bottomSheet, @BottomSheetBehavior.State int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) dismissAllowingStateLoss();

            if (newState == BottomSheetBehavior.STATE_EXPANDED && isFullscreen(bottomSheet))
                displayClose();
            else
                hideClose();

            if (!onlyToolbar) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED && isFullscreen(bottomSheet))
                    showToolbar();
                else if (newState == BottomSheetBehavior.STATE_COLLAPSED)
                    showHeader();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    }
}
