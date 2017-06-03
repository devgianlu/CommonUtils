package com.gianlu.commonutils.Sorting;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class OrderedRecyclerViewAdapter<VH extends RecyclerView.ViewHolder, E extends Filterable<F>, S, F> extends RecyclerView.Adapter<VH> {
    protected final SortingArrayList objs;
    private final S defaultSorting;
    private final List<E> originalObjs;
    private final List<F> filters;

    public OrderedRecyclerViewAdapter(List<E> objs, S defaultSorting) {
        this.originalObjs = objs;
        this.objs = new SortingArrayList(objs);
        this.defaultSorting = defaultSorting;
        this.filters = new ArrayList<>();

        sort(defaultSorting);
        shouldUpdateItemCount(objs.size());
    }

    @Nullable
    protected abstract RecyclerView getRecyclerView();

    private void processFilters() {
        objs.clear();

        for (E obj : originalObjs)
            if (!filters.contains(obj.getFilterable()))
                objs.add(obj);

        shouldUpdateItemCount(objs.size());
        super.notifyDataSetChanged();
        scrollToTop();
    }

    private void scrollToTop() {
        RecyclerView recyclerView = getRecyclerView();
        if (recyclerView != null) recyclerView.scrollToPosition(0);
    }

    private boolean notifyItemChangedOriginal(E payload) {
        int pos = originalObjs.indexOf(payload);
        if (pos == -1) {
            originalObjs.add(payload);
            processFilters();
            return true;
        } else {
            originalObjs.set(pos, payload);
            return false;
        }
    }

    public final void notifyItemChanged(E payload) {
        if (!notifyItemChangedOriginal(payload) && !filters.contains(payload.getFilterable())) {
            Pair<Integer, Integer> res = objs.addAndSort(payload);
            if (res.first == -1) super.notifyItemInserted(res.second);
            else if (Objects.equals(res.first, res.second))
                super.notifyItemChanged(res.first, payload);
            else super.notifyItemMoved(res.first, res.second);
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
        processFilters();
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
