package com.gianlu.commonutils.bottomsheet;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.gianlu.commonutils.R;
import com.gianlu.commonutils.dialogs.DialogUtils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public abstract class BaseModalBottomSheet<Setup, Update> extends BottomSheetDialogFragment {
    private FloatingActionButton action;
    private ModalBottomSheetHeaderView header;
    private FrameLayout body;
    private ProgressBar loading;
    private boolean hasNoScroll = false;
    private boolean hasBody = true;
    private Setup payload;
    private int lastHeaderEndPadding = -1;
    private CoordinatorLayout layout;
    private BottomSheetBehavior<?> behavior;
    private View bottomSheet;

    @NonNull
    public Setup getSetupPayload() {
        return payload;
    }

    protected abstract void onCreateHeader(@NonNull LayoutInflater inflater, @NonNull ModalBottomSheetHeaderView header, @NonNull Setup payload);

    protected abstract void onCreateBody(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent, @NonNull Setup payload);

    protected boolean onCreateNoScrollBody(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent, @NonNull Setup payload) {
        return false;
    }

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

        Window window;
        if (getDialog() != null && (window = getDialog().getWindow()) != null) {
            bottomSheet = window.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            window.getDecorView().getViewTreeObserver().addOnGlobalLayoutListener(new AttachCallbackTreeObserver());
        }
    }

    private void attachCustomCallback(@NonNull View bottomSheet) {
        bottomSheet.setBackgroundColor(Color.TRANSPARENT);

        behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.addBottomSheetCallback(prepareCallback());
        attachedBehavior(bottomSheet.getContext(), behavior);
    }

    private int getPeekHeight() {
        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        return screenHeight * 9 / 16;
    }

    @CallSuper
    protected void attachedBehavior(@NonNull Context context, @NonNull BottomSheetBehavior<?> behavior) {
        behavior.setPeekHeight(getPeekHeight());
        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        prepareCollapsed();
    }

    public final void postUpdate(@NonNull Update payload) {
        if (getActivity() != null) getActivity().runOnUiThread(() -> update(payload));
    }

    @UiThread
    public final void update(@NonNull Update payload) {
        if (getDialog() != null && getDialog().isShowing() && DialogUtils.isContextValid(getContext()))
            onReceivedUpdate(payload);
    }

    @UiThread
    protected void onReceivedUpdate(@NonNull Update payload) {
    }

    protected void onExpandedStateChanged(@NonNull ModalBottomSheetHeaderView header, boolean expanded) {
    }

    private void heightChanged() {
        if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED && isFullscreen()) {
            header.showClose();
            header.squared();
            restoreNotCollapsed();
            onExpandedStateChanged(header, true);
        } else {
            header.hideClose();
            header.rounded();
            onExpandedStateChanged(header, false);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.TransparentBottomSheetTheme);
    }

    protected void setHasBody(boolean hasBody) {
        this.hasBody = hasBody;
    }

    protected void setDraggable(boolean draggable) {
        if (behavior != null) behavior.setDraggable(draggable);
    }

    @Override
    public final View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (payload == null) {
            dismissAllowingStateLoss();
            return null;
        }

        LayoutInflater themedInflater = createLayoutInflater(requireContext(), payload);
        if (themedInflater != null) inflater = themedInflater;

        layout = (CoordinatorLayout) inflater.inflate(R.layout.modal_bottom_sheet, container, false);
        layout.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            if ((oldTop != top || oldBottom != bottom) && behavior != null) heightChanged();
        });

        header = layout.findViewById(R.id.modalBottomSheet_header);
        body = layout.findViewById(R.id.modalBottomSheet_body);
        loading = layout.findViewById(R.id.modalBottomSheet_loading);
        action = layout.findViewById(R.id.modalBottomSheet_action);

        onCreateHeader(inflater, header, payload);
        onCreateBody(inflater, body, payload);

        NestedScrollView scrollable = layout.findViewById(R.id.modalBottomSheet_scrollable);
        scrollable.setVisibility(hasBody ? View.VISIBLE : View.GONE);

        FrameLayout bodyNoScroll = layout.findViewById(R.id.modalBottomSheet_bodyNoScroll);
        hasNoScroll = onCreateNoScrollBody(inflater, bodyNoScroll, payload);
        bodyNoScroll.setVisibility(hasNoScroll ? View.VISIBLE : View.GONE);

        if (!hasBody && !hasNoScroll)
            throw new IllegalStateException();

        invalidateAction();
        header.setCloseOnClickListener(v -> dismiss());

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
        DialogUtils.showDialog(activity, this, null);
    }

    public final void show(@Nullable Fragment fragment, @NonNull Setup payload) {
        if (fragment == null) return;

        this.payload = payload;
        DialogUtils.showDialog(fragment, this, null);
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

    private boolean isFullscreen() {
        if (bottomSheet == null) return false;

        int parentHeight = ((View) bottomSheet.getParent()).getHeight();
        int sheetHeight = bottomSheet.getHeight();
        return parentHeight == sheetHeight;
    }

    private void prepareCollapsed() {
        if (hasNoScroll) {
            int peek = getPeekHeight();
            if (layout.getHeight() > peek) layout.getLayoutParams().height = getPeekHeight();
            else return;
        } else {
            layout.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
        }

        layout.requestLayout();
    }

    private void restoreNotCollapsed() {
        layout.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
        layout.requestLayout();
    }

    private class AttachCallbackTreeObserver implements ViewTreeObserver.OnGlobalLayoutListener {

        @Override
        public void onGlobalLayout() {
            Window window;
            if (getDialog() != null && (window = getDialog().getWindow()) != null) {
                bottomSheet = window.findViewById(com.google.android.material.R.id.design_bottom_sheet);
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
            BaseModalBottomSheet.this.bottomSheet = bottomSheet;

            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismissAllowingStateLoss();
                return;
            }

            heightChanged();

            if (newState == BottomSheetBehavior.STATE_COLLAPSED)
                prepareCollapsed();

            if (newState == BottomSheetBehavior.STATE_DRAGGING && hasNoScroll)
                restoreNotCollapsed();
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    }
}
