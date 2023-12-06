package com.gianlu.commonutils.drawer;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

public class BaseDrawerItem<E extends Enum> {
    public final E id;
    public final int icon;
    public final String name;
    int badgeNumber = -1;
    boolean active = false;

    public BaseDrawerItem(@NonNull E id, @DrawableRes int icon, @NonNull String name) {
        this.id = id;
        this.icon = icon;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseDrawerItem that = (BaseDrawerItem) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id.ordinal();
    }
}
