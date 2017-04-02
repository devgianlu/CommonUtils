package com.gianlu.commonutils;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.ColorInt;
import android.support.annotation.Keep;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
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

    public static abstract class InfiniteAdapter<T extends ViewHolder, E> extends RecyclerView.Adapter<ViewHolder> {
        static final int ITEM_LOADING = 0;
        static final int ITEM_NORMAL = 1;
        static final int ITEM_SEPARATOR = 2;
        protected final LayoutInflater inflater;
        protected final Context context;
        protected final List<ItemEnclosure<E>> items;
        final int maxPages;
        private final int primary_shadow;
        int page = 1;
        long currDay = -1;
        private IFailedLoadingContent listener;

        public InfiniteAdapter(Context context, List<E> items, int maxPages, @ColorInt int primary_shadow) {
            this.inflater = LayoutInflater.from(context);
            this.context = context;
            this.primary_shadow = primary_shadow;
            this.items = new ArrayList<>();
            this.maxPages = maxPages;

            populate(items);
        }

        private void populate(List<E> elements) {
            for (E element : elements) {
                Date date = getDateFromItem(element);
                if (currDay != date.getTime() / 86400000) {
                    items.add(new ItemEnclosure<E>(null, date));
                    currDay = date.getTime() / 86400000;
                }

                items.add(new ItemEnclosure<>(element, date));
            }
        }

        protected abstract Date getDateFromItem(E item);

        private void attachListener(IFailedLoadingContent listener) {
            this.listener = listener;
        }

        @Override
        public int getItemViewType(int position) {
            if (items.get(position) == null) return ITEM_LOADING;
            else if (items.get(position).item == null) return ITEM_SEPARATOR;
            else return ITEM_NORMAL;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == ITEM_LOADING)
                return new LoadingViewHolder(inflater.inflate(R.layout.loading_item, parent, false));
            else if (viewType == ITEM_SEPARATOR)
                return new SeparatorViewHolder(inflater.inflate(R.layout.separator_item, parent, false));
            else
                return createViewHolder(parent);
        }

        @Override
        @SuppressWarnings("unchecked")
        public void onBindViewHolder(ViewHolder holder, int position) {
            if (items.get(position) != null) {
                if (items.get(position).item == null) {
                    SeparatorViewHolder separator = (SeparatorViewHolder) holder;
                    separator.line.setBackgroundColor(primary_shadow);
                    separator.date.setText(CommonUtils.getVerbalDateFormatter().format(items.get(position).date));
                } else {
                    userBindViewHolder((T) holder, position);
                }
            }
        }

        protected abstract void userBindViewHolder(T holder, int position);

        protected abstract ViewHolder createViewHolder(ViewGroup parent);

        private void loadMoreContent() {
            if (page > maxPages)
                return;

            if (items.get(items.size() - 1) != null) {
                items.add(null);
                notifyItemInserted(items.size() - 1);
            }

            page++;
            moreContent(page, new IContentProvider<E>() {
                @Override
                public void onMoreContent(final List<E> content) {
                    new Handler(context.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            if (items.get(items.size() - 1) == null) {
                                items.remove(null);
                                notifyItemRemoved(items.size() - 1);
                            }

                            int start = items.size();
                            populate(content);
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

        protected abstract void moreContent(int page, IContentProvider<E> provider);

        protected interface IContentProvider<E> {
            void onMoreContent(List<E> content);

            void onFailed(Exception ex);
        }

        public static class ItemEnclosure<E> {
            final Date date;
            final E item;

            public ItemEnclosure(@Nullable E item, Date date) {
                this.date = date;
                this.item = item;
            }

            public E getItem() {
                return item;
            }
        }

        private class LoadingViewHolder extends ViewHolder {
            LoadingViewHolder(View itemView) {
                super(itemView);
            }
        }

        private class SeparatorViewHolder extends ViewHolder {
            final TextView date;
            final View line;

            SeparatorViewHolder(View itemView) {
                super(itemView);

                line = itemView.findViewById(R.id.separatorItem_line);
                date = (TextView) itemView.findViewById(R.id.separatorItem_date);
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
