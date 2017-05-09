package com.gianlu.commonutils.Drawer;

import android.support.annotation.DrawableRes;

@SuppressWarnings({"unused", "WeakerAccess"})
public class BaseDrawerItem {
    public final int id;
    public final int icon;
    public final String name;
    public int badgeNumber = -1;

    public BaseDrawerItem(int id, @DrawableRes int icon, String name) {
        this.id = id;
        this.icon = icon;
        this.name = name;
    }
}
