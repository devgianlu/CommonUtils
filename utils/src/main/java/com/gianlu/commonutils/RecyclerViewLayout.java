package com.gianlu.commonutils;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

public class RecyclerViewLayout extends FrameLayout {
    private final ProgressBar loading;
    private final RecyclerView list;
    private final MessageView message;
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
        message = findViewById(R.id.recyclerViewLayout_message);

        disableSwipeRefresh();
    }

    public RecyclerViewLayout(@NonNull LayoutInflater inflater) {
        this(inflater.getContext(), null, 0);
    }

    public void enableSwipeRefresh(SwipeRefreshLayout.OnRefreshListener listener, @ColorRes int... colors) {
        if (colors.length <= 0) throw new IllegalArgumentException("Provide at least one color!");

        swipeRefreshEnabled = true;
        swipeRefresh.setEnabled(true);
        swipeRefresh.setColorSchemeResources(colors);
        swipeRefresh.setOnRefreshListener(listener);
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

        message.hide();
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

    public void showError(@NonNull String text) {
        hideList();
        message.setError(text);
    }

    public void showError(@StringRes int textRes, Object... args) {
        hideList();
        message.setError(textRes, args);
    }

    public void showInfo(@StringRes int textRes, Object... args) {
        hideList();
        message.setInfo(textRes, args);
    }

    public void loadListData(RecyclerView.Adapter adapter, boolean show) {
        list.setAdapter(adapter);
        if (show) showList();
    }

    public void loadListData(RecyclerView.Adapter adapter) {
        loadListData(adapter, true);
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

    public void hideMessage() {
        message.hide();
    }
}
