package com.gianlu.commonutils;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.Keep;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

@SuppressWarnings("unused,WeakerAccess")
@Keep
public class InfiniteRecyclerView extends RecyclerView {
    private IFailedLoadingContent listener;

    public InfiniteRecyclerView(Context context) {
        super(context);
        addOnScrollListener(new CustomScrollListener());
    }

    public InfiniteRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        addOnScrollListener(new CustomScrollListener());
    }

    public InfiniteRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        addOnScrollListener(new CustomScrollListener());
    }

    public void setFailedListener(IFailedLoadingContent listener) {
        this.listener = listener;

        InfiniteAdapter adapter = (InfiniteAdapter) getAdapter();
        if (getAdapter() != null)
            adapter.attachListener(listener);
    }

    public void setAdapter(InfiniteAdapter adapter) {
        super.setAdapter(adapter);

        if (listener != null)
            adapter.attachListener(listener);
    }

    public interface IFailedLoadingContent {
        void onFailedLoadingContent(Exception ex);
    }

    public static abstract class InfiniteAdapter<T extends ViewHolder, I> extends RecyclerView.Adapter<ViewHolder> {
        static final int ITEM_LOADING = 0;
        static final int ITEM_NORMAL = 1;
        protected final LayoutInflater inflater;
        protected final Context context;
        protected final List<I> items;
        final int maxPages;
        int page = 1;
        private IFailedLoadingContent listener;

        public InfiniteAdapter(Context context, List<I> items, int maxPages) {
            this.inflater = LayoutInflater.from(context);
            this.context = context;
            this.items = items;
            this.maxPages = maxPages;
        }

        private void attachListener(IFailedLoadingContent listener) {
            this.listener = listener;
        }

        @Override
        public int getItemViewType(int position) {
            if (items.get(position) == null) return ITEM_LOADING;
            else return ITEM_NORMAL;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == ITEM_LOADING)
                return new LoadingViewHolder(inflater.inflate(R.layout.loading_item, parent, false));
            else
                return createViewHolder(parent);
        }

        protected abstract void userBindViewHolder(T holder, int position);

        @Override
        @SuppressWarnings("unchecked")
        public void onBindViewHolder(ViewHolder holder, int position) {
            if (items.get(position) != null) {
                userBindViewHolder((T) holder, position);
            }
        }

        protected abstract ViewHolder createViewHolder(ViewGroup parent);

        private void loadMoreContent() {
            if (page > maxPages)
                return;

            if (items.get(items.size() - 1) != null) {
                items.add(null);
                notifyItemInserted(items.size() - 1);
            }

            page++;
            moreContent(page, new IContentProvider<I>() {
                @Override
                public void onMoreContent(final List<I> content) {
                    new Handler(context.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            if (items.get(items.size() - 1) == null) {
                                items.remove(null);
                                notifyItemRemoved(items.size() - 1);
                            }

                            int start = items.size();
                            items.addAll(content);
                            notifyItemRangeInserted(start, content.size());
                        }
                    });
                }

                @Override
                public void onFailed(Exception ex) {
                    if (listener != null)
                        listener.onFailedLoadingContent(ex);
                }
            });
        }

        protected abstract void moreContent(int page, IContentProvider<I> provider);

        protected interface IContentProvider<I> {
            void onMoreContent(List<I> content);

            void onFailed(Exception ex);
        }

        private class LoadingViewHolder extends ViewHolder {
            LoadingViewHolder(View itemView) {
                super(itemView);
            }
        }
    }

    private class CustomScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    if (!canScrollVertically(1)) ((InfiniteAdapter) getAdapter()).loadMoreContent();
                }
            });
        }
    }
}
