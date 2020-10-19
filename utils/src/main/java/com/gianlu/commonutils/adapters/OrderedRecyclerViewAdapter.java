package com.gianlu.commonutils.adapters;


import android.util.Pair;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.recyclerview.widget.RecyclerView;

import com.gianlu.commonutils.CommonUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@UiThread
public abstract class OrderedRecyclerViewAdapter<VH extends RecyclerView.ViewHolder, E extends Filterable<F>, S, F> extends RecyclerView.Adapter<VH> {
    protected final SortingArrayList objs;
    protected final Set<F> filters;
    protected final List<E> originalObjs;
    private final S defaultSorting;
    private String query;
    private RecyclerView list;

    public OrderedRecyclerViewAdapter(List<E> objs, S defaultSorting) {
        this.originalObjs = objs;
        this.objs = new SortingArrayList(objs);
        this.defaultSorting = defaultSorting;
        this.filters = new HashSet<>();

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

    private void processQueryAndFilters() {
        objs.clear();

        for (E obj : originalObjs) {
            F[] itemFilters = obj.getMatchingFilters();
            if ((itemFilters == null || !CommonUtils.containsAny(filters, itemFilters)) && (query == null || matchQuery(obj, query)))
                objs.add(obj);
        }

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

    @NonNull
    private List<E> findFilterAll(Filter<E> filter) {
        List<E> list = new ArrayList<>(originalObjs.size());
        for (int i = 0; i < originalObjs.size(); i++) {
            E item = originalObjs.get(i);
            if (filter.accept(item)) list.add(item);
        }

        return list;
    }

    @Nullable
    private E findFilter(Filter<E> filter) {
        for (int i = 0; i < originalObjs.size(); i++) {
            E item = originalObjs.get(i);
            if (filter.accept(item))
                return item;
        }

        return null;
    }

    //region Add/change
    public final void itemChangedOrAdded(@NonNull E payload) {
        itemChangedOrAdded(payload, false);
    }

    public final void itemChanged(@NonNull Filter<E> filter) {
        E item = findFilter(filter);
        if (item != null) itemChangedOrAdded(item);
    }

    private void itemChangedOrAdded(@NonNull E payload, boolean internal) {
        int posIntoOriginal = originalObjs.indexOf(payload);
        if (posIntoOriginal == -1) originalObjs.add(payload);
        else originalObjs.set(posIntoOriginal, payload);

        F[] itemFilters = payload.getMatchingFilters();
        if ((itemFilters == null || !CommonUtils.containsAny(filters, itemFilters)) && matchQuery(payload, query)) {
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

    public final void itemsAdded(@NonNull List<E> items) {
        for (E item : items) itemChangedOrAdded(item, true);
        shouldUpdateItemCount(objs.size());
    }

    public final void itemsChanged(@NonNull List<E> items) {
        for (E obj : new ArrayList<>(originalObjs))
            if (!items.contains(obj))
                removeItem(obj, true);

        itemsAdded(items);
    }
    //endregion

    //region Remove
    public final void removeItems(@NonNull Filter<E> filter) {
        List<E> items = findFilterAll(filter);
        if (!items.isEmpty()) {
            for (E item : items) removeItem(item, true);
            shouldUpdateItemCount(objs.size());
        }
    }

    public final void removeItem(@NonNull E item) {
        removeItem(item, false);
    }

    public final void removeItem(@NonNull Filter<E> filter) {
        E item = findFilter(filter);
        if (item != null) removeItem(item);
    }

    private void removeItem(@NonNull E item, boolean internal) {
        originalObjs.remove(item);

        int pos = objs.indexOf(item);
        if (pos != -1) {
            objs.remove(pos);
            super.notifyItemRemoved(pos);
            if (!internal) shouldUpdateItemCount(objs.size());
        }
    }
    //endregion

    //region Filtering
    @SafeVarargs
    public final void setFilters(@NonNull F... filters) {
        setFilters(Arrays.asList(filters));
    }

    public final void addFilter(@NonNull F newFilter) {
        if (filters.add(newFilter)) processQueryAndFilters();
    }

    public final void removeFilter(@NonNull F newFilter) {
        if (filters.remove(newFilter)) processQueryAndFilters();
    }

    public final void setFilters(@NonNull List<F> newFilters) {
        filters.clear();
        if (filters.addAll(newFilters))
            processQueryAndFilters();
    }

    public final void filterWithQuery(String query) {
        this.query = query;
        processQueryAndFilters();
    }
    //endregion

    @Override
    public final int getItemCount() {
        return objs.size();
    }

    protected abstract void shouldUpdateItemCount(int count);

    @NonNull
    public abstract Comparator<E> getComparatorFor(@NonNull S sorting);

    public final void sort(S sorting) {
        objs.sort(sorting);
        super.notifyDataSetChanged();
        scrollToTop();
    }

    public interface Filter<E> {
        boolean accept(@NonNull E elm);
    }

    public final class SortingArrayList extends BaseSortingArrayList<E, S> {

        SortingArrayList(List<E> objs) {
            super(objs, defaultSorting);
        }

        @NonNull
        @Override
        public Comparator<E> getComparator(@NonNull S sorting) {
            return getComparatorFor(sorting);
        }
    }
}
