package com.gianlu.commonutils.Adapters;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class OrderedRecyclerViewAdapter<VH extends RecyclerView.ViewHolder, E extends Filterable<F>, S, F> extends RecyclerView.Adapter<VH> {
    protected final SortingArrayList objs;
    protected final List<F> filters;
    private final S defaultSorting;
    private final List<E> originalObjs;
    private String query;

    public OrderedRecyclerViewAdapter(List<E> objs, S defaultSorting) {
        this.originalObjs = objs;
        this.objs = new SortingArrayList(objs);
        this.defaultSorting = defaultSorting;
        this.filters = new ArrayList<>();

        sort(defaultSorting);
        shouldUpdateItemCount(objs.size());
    }

    @SafeVarargs
    public final void setFilters(F... filters) {
        setFilters(Arrays.asList(filters));
    }

    @Nullable
    protected abstract RecyclerView getRecyclerView();


    private void processQueryAndFilters() {
        objs.clear();

        for (E obj : originalObjs)
            if (!filters.contains(obj.getFilterable()) && (query == null || matchQuery(obj, query)))
                objs.add(obj);

        objs.resort();

        shouldUpdateItemCount(objs.size());
        super.notifyDataSetChanged();
        scrollToTop();
    }

    protected abstract boolean matchQuery(E item, @Nullable String query);

    private void scrollToTop() {
        RecyclerView recyclerView = getRecyclerView();
        if (recyclerView != null) recyclerView.scrollToPosition(0);
    }

    @Override
    @SuppressWarnings("unchecked")
    public final void onBindViewHolder(VH holder, int position, List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            Object payload = payloads.get(0);
            if (payload instanceof WeakReference) {
                WeakReference<E> castPayload = (WeakReference<E>) payload;
                if (castPayload.get() != null)
                    onBindViewHolder(holder, position, castPayload.get());
            } else {
                onBindViewHolder(holder, position, payload);
            }
        }
    }

    protected void onBindViewHolder(VH holder, int position, Object payload) {
    }

    protected abstract void onBindViewHolder(VH holder, int position, @NonNull E payload);

    public final void notifyItemChanged(E payload) {
        int pos = originalObjs.indexOf(payload);
        if (pos == -1) originalObjs.add(payload);
        else originalObjs.set(pos, payload);

        if (!filters.contains(payload.getFilterable()) && matchQuery(payload, query)) {
            Pair<Integer, Integer> res = objs.addAndSort(payload);
            if (res.first == -1)
                super.notifyItemInserted(res.second);
            else if (Objects.equals(res.first, res.second))
                super.notifyItemChanged(res.first, new WeakReference<>(payload));
            else
                super.notifyItemMoved(res.first, res.second);
        }
    }

    private void notifyItemRemoved(E item) {
        originalObjs.remove(item);

        int pos = objs.indexOf(item);
        if (pos != -1) {
            objs.remove(pos);
            super.notifyItemRemoved(pos);
        }
    }

    public final void notifyItemsChanged(List<E> items) {
        for (E obj : new ArrayList<>(objs))
            if (!items.contains(obj))
                notifyItemRemoved(obj);

        for (E item : items) notifyItemChanged(item);

        shouldUpdateItemCount(objs.size());
    }

    public final void setFilters(List<F> newFilters) {
        filters.clear();
        filters.addAll(newFilters);
        processQueryAndFilters();
    }

    public final void filterWithQuery(String query) {
        this.query = query;
        processQueryAndFilters();
    }

    @Override
    public final int getItemCount() {
        return objs.size();
    }

    protected abstract void shouldUpdateItemCount(int count);

    @NonNull
    public abstract Comparator<E> getComparatorFor(S sorting);

    public final void sort(S sorting) {
        objs.sort(sorting);
        super.notifyDataSetChanged();
        scrollToTop();
    }

    public final class SortingArrayList extends BaseSortingArrayList<E, S> {

        SortingArrayList(List<E> objs) {
            super(objs, defaultSorting);
        }

        @NonNull
        @Override
        public Comparator<E> getComparator(S sorting) {
            return getComparatorFor(sorting);
        }
    }
}
