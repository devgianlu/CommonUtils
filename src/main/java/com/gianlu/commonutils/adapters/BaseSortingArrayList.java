package com.gianlu.commonutils.adapters;

import android.util.Pair;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public abstract class BaseSortingArrayList<E, S> extends ArrayList<E> {
    private S currentSort;

    public BaseSortingArrayList(List<E> objs, S defaultSorting) {
        super(objs);
        this.currentSort = defaultSorting;
    }

    public void resort() {
        Collections.sort(this, getComparator(currentSort));
    }

    @NonNull
    public abstract Comparator<E> getComparator(S sorting);

    public void sort(S sorting) {
        currentSort = sorting;
        Collections.sort(this, getComparator(sorting));
    }

    public Pair<Integer, Integer> addAndSort(E element) {
        int from = indexOf(element);
        if (from == -1) add(element);
        else set(from, element);
        sort(currentSort);
        int to = indexOf(element);
        return new Pair<>(from, to);
    }
}
