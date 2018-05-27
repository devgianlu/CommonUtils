package com.gianlu.commonutils.BottomSheet;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.gianlu.commonutils.Logging;
import com.gianlu.commonutils.R;

public abstract class BaseModalBottomSheet extends BottomSheetDialogFragment {
    private BottomSheetBehavior behavior;
    private FrameLayout header;
    private FrameLayout body;
    private ProgressBar loading;
    private Toolbar toolbar;
    private FloatingActionButton action;
    private boolean onlyToolbar = false;

    /**
     * @return Whether the implementation provides a layout for the header
     */
    protected abstract boolean onCreateHeader(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent, @NonNull Bundle args) throws MissingArgumentException;

    protected abstract void onCreateBody(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent, @NonNull Bundle args) throws MissingArgumentException;

    protected abstract void onCustomizeToolbar(@NonNull Toolbar toolbar, @NonNull Bundle args) throws MissingArgumentException;

    /**
     * @return Whether the implementation provides an action
     */
    protected abstract boolean onCustomizeAction(@NonNull FloatingActionButton action, @NonNull Bundle args) throws MissingArgumentException;

    @NonNull
    protected BottomSheetCallback prepareCallback() {
        return new BottomSheetCallback();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Window window = getDialog().getWindow();
        if (window != null) {
            behavior = BottomSheetBehavior.from(window.findViewById(android.support.design.R.id.design_bottom_sheet));
            behavior.setBottomSheetCallback(prepareCallback());
        }
    }

    @Override
    public final View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        CoordinatorLayout layout = (CoordinatorLayout) inflater.inflate(R.layout.modal_bottom_sheet, container, false);

        Bundle args = getArguments();
        if (args == null) args = new Bundle();

        toolbar = layout.findViewById(R.id.modalBottomSheet_toolbar);
        header = layout.findViewById(R.id.modalBottomSheet_header);
        body = layout.findViewById(R.id.modalBottomSheet_body);
        loading = layout.findViewById(R.id.modalBottomSheet_loading);
        action = layout.findViewById(R.id.modalBottomSheet_action);

        try {
            onCustomizeToolbar(toolbar, args);
            onlyToolbar = !onCreateHeader(inflater, header, args);
            onCreateBody(inflater, body, args);

            if (onCustomizeAction(action, args)) action.setVisibility(View.VISIBLE);
            else action.setVisibility(View.GONE);
        } catch (MissingArgumentException ex) {
            Logging.log(ex);
            dismiss();
            return null;
        }

        if (onlyToolbar) showToolbar();
        else showHeader();

        return layout;
    }

    private void displayClose() {
        toolbar.setNavigationIcon(R.drawable.ic_clear_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
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

    public static class MissingArgumentException extends Exception {
    }

    public class BottomSheetCallback extends BottomSheetBehavior.BottomSheetCallback {

        @Override
        @CallSuper
        public void onStateChanged(@NonNull View bottomSheet, @BottomSheetBehavior.State int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) dismiss();

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
