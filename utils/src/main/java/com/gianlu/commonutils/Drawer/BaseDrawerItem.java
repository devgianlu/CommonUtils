package com.gianlu.commonutils.Drawer;

import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;

public class BaseDrawerItem {
    public final int id;
    public final int icon;
    public final String name;
    public int badgeNumber = -1;
    public boolean active = false;

    public BaseDrawerItem(int id, @DrawableRes int icon, @NonNull String name) {
        if (id < 0) throw new IllegalArgumentException("Must be > 0!");

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
        return id;
    }
}
