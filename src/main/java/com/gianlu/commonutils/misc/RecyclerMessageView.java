package com.gianlu.commonutils.misc;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.gianlu.commonutils.R;

public class RecyclerMessageView extends FrameLayout {
    private final ProgressBar loading;
    private final InfiniteRecyclerView list;
    private final MessageView message;
    private final SwipeRefreshLayout swipeRefresh;

    public RecyclerMessageView(@NonNull Context context) {
        this(context, null, 0);
    }

    public RecyclerMessageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecyclerMessageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.view_recycler_msg, this, true);

        loading = findViewById(R.id.recyclerViewLayout_loading);
        list = findViewById(R.id.recyclerViewLayout_list);
        swipeRefresh = findViewById(R.id.recyclerViewLayout_swipeRefresh);
        message = findViewById(R.id.recyclerViewLayout_message);

        disableSwipeRefresh();
    }

    public void enableSwipeRefresh(@NonNull SwipeRefreshLayout.OnRefreshListener listener, @ColorRes int... colors) {
        if (colors.length <= 0) throw new IllegalArgumentException("Provide at least one color!");

        swipeRefresh.setEnabled(true);
        swipeRefresh.setColorSchemeResources(colors);
        swipeRefresh.setOnRefreshListener(listener);
        list.setVisibility(VISIBLE);
    }

    public void disableSwipeRefresh() {
        swipeRefresh.setEnabled(false);
        list.setVisibility(GONE);
    }

    public void hideMessage() {
        message.hide();
    }

    public void showError(String text) {
        hideList();
        message.error(text);
    }

    public void showError(@StringRes int textRes, Object... args) {
        hideList();
        message.error(textRes, args);
    }

    public void showInfo(@StringRes int textRes, Object... args) {
        hideList();
        message.info(textRes, args);
    }

    public void showList() {
        list.setVisibility(VISIBLE);

        message.hide();
        stopLoading();
    }

    public void hideList() {
        list.setVisibility(GONE);
        stopLoading();
    }

    public boolean isLoading() {
        return loading.getVisibility() == VISIBLE;
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

    public void loadListData(@NonNull RecyclerView.Adapter adapter, boolean show) {
        list.setAdapter(adapter);
        if (show) showList();
    }

    public void loadListData(@NonNull RecyclerView.Adapter adapter) {
        loadListData(adapter, true);
    }

    public void setLayoutManager(@NonNull RecyclerView.LayoutManager layoutManager) {
        list.setLayoutManager(layoutManager);
    }

    public void linearLayoutManager(@RecyclerView.Orientation int orientation, boolean reversed) {
        list.setLayoutManager(new LinearLayoutManager(list.getContext(), orientation, reversed));
    }

    public void dividerDecoration(@RecyclerView.Orientation int orientation) {
        list.addItemDecoration(new DividerItemDecoration(list.getContext(), orientation));
    }

    public void addDecoration(@NonNull RecyclerView.ItemDecoration decor) {
        list.addItemDecoration(decor);
    }

    @NonNull
    public ProgressBar progressBar() {
        return loading;
    }

    @NonNull
    public InfiniteRecyclerView list() {
        return list;
    }

    @NonNull
    public SwipeRefreshLayout swipeRefreshLayout() {
        return swipeRefresh;
    }

    @NonNull
    public MessageView message() {
        return message;
    }
}
