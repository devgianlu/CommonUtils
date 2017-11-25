package com.gianlu.commonutils;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

@SuppressWarnings("unused")
public class RecyclerViewLayout extends FrameLayout {
    private final ProgressBar loading;
    private final RecyclerView list;
    private final SwipeRefreshLayout swipeRefresh;
    private boolean swipeRefreshEnabled = true;

    public RecyclerViewLayout(@NonNull Context context) {
        this(LayoutInflater.from(context), null, 0);
    }

    public RecyclerViewLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(LayoutInflater.from(context), attrs, 0);
    }

    public RecyclerViewLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(LayoutInflater.from(context), attrs, defStyleAttr);
    }

    public RecyclerViewLayout(@NonNull LayoutInflater inflater, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(inflater.getContext(), attrs, defStyleAttr);

        inflater.inflate(R.layout.recycler_view_layout, this, true);

        loading = findViewById(R.id.recyclerViewLayout_loading);
        list = findViewById(R.id.recyclerViewLayout_list);
        swipeRefresh = findViewById(R.id.recyclerViewLayout_swipeRefresh);
    }

    public RecyclerViewLayout(@NonNull LayoutInflater inflater) {
        this(inflater.getContext(), null, 0);
    }

    public void enableSwipeRefresh(@ColorRes int... colors) {
        swipeRefreshEnabled = true;
        swipeRefresh.setEnabled(true);
        swipeRefresh.setColorSchemeResources(colors);
        list.setVisibility(VISIBLE);
    }

    public void disableSwipeRefresh() {
        swipeRefreshEnabled = false;
        swipeRefresh.setVisibility(VISIBLE);
        swipeRefresh.setEnabled(false);
        list.setVisibility(GONE);
    }

    public void hideList() {
        if (swipeRefreshEnabled) {
            swipeRefresh.setVisibility(GONE);
        } else {
            list.setVisibility(GONE);
        }

        stopLoading();
    }

    public boolean isLoading() {
        return loading.getVisibility() == VISIBLE;
    }

    public void showList() {
        if (swipeRefreshEnabled) {
            swipeRefresh.setVisibility(VISIBLE);
        } else {
            list.setVisibility(VISIBLE);
        }

        MessageLayout.hide(this);
        stopLoading();
    }

    public void startLoading() {
        hideList();
        hideMessage();
        loading.setVisibility(VISIBLE);
    }

    public void stopLoading() {
        swipeRefresh.setRefreshing(false);
        loading.setVisibility(GONE);
    }

    public void showMessage(@StringRes int message, boolean error, Object... formatArgs) {
        showMessage(getContext().getString(message, formatArgs), error);
    }

    public void showMessage(@StringRes int message, @DrawableRes int icon) {
        hideList();
        MessageLayout.show(this, message, icon);
    }

    public void showMessage(@NonNull String message, boolean error) {
        hideList();
        MessageLayout.show(this, message, error ? R.drawable.ic_error_outline_black_48dp : R.drawable.ic_info_outline_black_48dp);
    }

    public void loadListData(RecyclerView.Adapter adapter) {
        list.setAdapter(adapter);
        showList();
    }

    public void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        list.setLayoutManager(layoutManager);
    }

    public ProgressBar getProgressBar() {
        return loading;
    }

    public RecyclerView getList() {
        return list;
    }

    public SwipeRefreshLayout getSwipeRefreshLayout() {
        return swipeRefresh;
    }

    public void setRefreshListener(SwipeRefreshLayout.OnRefreshListener listener) {
        swipeRefresh.setOnRefreshListener(listener);
    }

    public void hideMessage() {
        MessageLayout.hide(this);
    }
}
