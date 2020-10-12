package com.gianlu.commonutils.adapters;

import androidx.annotation.Nullable;

public interface Filterable<F> {
    @Nullable
    F[] getMatchingFilters();
}
