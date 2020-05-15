package com.gianlu.commonutils.adapters;


import android.util.Pair;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@UiThread
public abstract class OrderedRecyclerViewAdapter<VH extends RecyclerView.ViewHolder, E extends Filterable<F>, S, F> extends RecyclerView.Adapter<VH> {
    protected final SortingArrayList objs;
    protected final List<F> filters;
    protected final List<E> originalObjs;
    private final S defaultSorting;
    private String query;
    private RecyclerView list;

    public OrderedRecyclerViewAdapter(List<E> objs, S defaultSorting) {
        this.originalObjs = objs;
        this.objs = new SortingArrayList(objs);
        this.defaultSorting = defaultSorting;
        this.filters = new ArrayList<>();

        sort(defaultSorting);
        shouldUpdateItemCount(objs.size());
    }

    @Nullable
    protected final RecyclerView getList() {
        return list;
    }

    @Override
    @CallSuper
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        list = recyclerView;
    }

    @Override
    @CallSuper
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        list = recyclerView;
    }

    @SafeVarargs
    public final void setFilters(@NonNull F... filters) {
        setFilters(Arrays.asList(filters));
    }

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

    protected abstract boolean matchQuery(@NonNull E item, @Nullable String query);

    private void scrollToTop() {
        RecyclerView recyclerView = getList();
        if (recyclerView != null) recyclerView.scrollToPosition(0);
    }

    protected abstract void onSetupViewHolder(@NonNull VH holder, int position, @NonNull E payload);

    protected abstract void onUpdateViewHolder(@NonNull VH holder, int position, @NonNull E payload);

    protected void onUpdateViewHolder(@NonNull VH holder, int position, @NonNull Object payload) {
    }

    @Override
    @SuppressWarnings("unchecked")
    public final void onBindViewHolder(@NonNull VH holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            onSetupViewHolder(holder, position, objs.get(position));
        } else {
            for (Object payload : payloads) {
                if (payload instanceof WeakReference) {
                    WeakReference<E> castPayload = (WeakReference<E>) payload;
                    if (castPayload.get() != null)
                        onUpdateViewHolder(holder, position, castPayload.get());
                } else {
                    onUpdateViewHolder(holder, position, payload);
                }
            }
        }
    }

    public final void onBindViewHolder(@NonNull VH holder, int position) {
        // Never called
    }

    public final void itemChangedOrAdded(@NonNull E payload) {
        itemChangedOrAdded(payload, false);
    }

    private void itemChangedOrAdded(@NonNull E payload, boolean internal) {
        int posIntoOriginal = originalObjs.indexOf(payload);
        if (posIntoOriginal == -1) originalObjs.add(payload);
        else originalObjs.set(posIntoOriginal, payload);

        if (!filters.contains(payload.getFilterable()) && matchQuery(payload, query)) {
            Pair<Integer, Integer> res = objs.addAndSort(payload);
            if (res.first == -1)
                super.notifyItemInserted(res.second);
            else if (Objects.equals(res.first, res.second))
                super.notifyItemChanged(res.first, new WeakReference<>(payload));
            else
                super.notifyItemMoved(res.first, res.second);
        } else {
            int posIntoObjs = objs.indexOf(payload);
            if (posIntoObjs != -1) {
                objs.remove(posIntoObjs);
                super.notifyItemRemoved(posIntoObjs);
            }
        }

        if (!internal) shouldUpdateItemCount(objs.size());
    }

    public final void removeItem(E item) {
        removeItem(item, false);
    }

    private void removeItem(E item, boolean internal) {
        originalObjs.remove(item);

        int pos = objs.indexOf(item);
        if (pos != -1) {
            objs.remove(pos);
            super.notifyItemRemoved(pos);
            if (!internal) shouldUpdateItemCount(objs.size());
        }
    }

    public final void itemsAdded(List<E> items) {
        for (E item : items) itemChangedOrAdded(item, true);
        shouldUpdateItemCount(objs.size());
    }

    public final void itemsChanged(List<E> items) {
        for (E obj : new ArrayList<>(originalObjs))
            if (!items.contains(obj))
                removeItem(obj, true);

        itemsAdded(items);
    }

    public final void setFilters(@NonNull List<F> newFilters) {
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
