package com.gianlu.commonutils.Drawer;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

public class Initializer<P extends BaseDrawerProfile> {
    final Activity activity;
    final DrawerLayout drawerLayout;
    final Toolbar toolbar;
    final DrawerManager.ISetup<P> setup;
    final List<BaseDrawerItem> menuItems = new ArrayList<>();
    final List<P> profiles = new ArrayList<>();
    DrawerManager.ILogout logoutHandler;
    P singleProfile;

    public Initializer(Activity activity, DrawerLayout drawerLayout, Toolbar toolbar, DrawerManager.ISetup<P> setup) {
        this.activity = activity;
        this.drawerLayout = drawerLayout;
        this.toolbar = toolbar;
        this.setup = setup;
    }

    public Initializer<P> hasSingleProfile(P profile, @Nullable DrawerManager.ILogout handler) {
        this.singleProfile = profile;
        this.logoutHandler = handler;
        return this;
    }

    public Initializer<P> addMenuItemSeparator() {
        menuItems.add(null);
        return this;
    }

    public Initializer<P> addMenuItem(BaseDrawerItem item) {
        menuItems.add(item);
        return this;
    }

    public Initializer<P> addProfileItem(P profile) {
        logoutHandler = null;
        singleProfile = null;
        profiles.add(profile);
        return this;
    }

    public Initializer<P> addProfiles(List<P> profiles) {
        for (P profile : profiles) addProfileItem(profile);
        return this;
    }
}
