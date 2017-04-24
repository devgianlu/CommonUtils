package com.gianlu.commonutils.Drawer;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.Keep;
import android.support.annotation.StringRes;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gianlu.commonutils.LetterIconBig;
import com.gianlu.commonutils.R;

import java.util.ArrayList;
import java.util.List;

@Keep
@SuppressWarnings({"unused", "WeakerAccess"})
public class DrawerManager {
    private final Context context;
    private final ActionBarDrawerToggle drawerToggle;
    private final DrawerLayout drawerLayout;
    private final ISetup setup;
    private final List<BaseDrawerItem> menuItems;
    private final List<BaseDrawerProfile> profiles;
    private MenuItemsAdapter menuItemsAdapter;
    private IDrawerListener listener;
    private boolean isProfilesLockedUntilSelected;
    private ProfilesAdapter profilesAdapter;

    private DrawerManager(Initializer initializer) {
        this.context = initializer.drawerLayout.getContext();
        this.setup = initializer.setup;
        this.drawerLayout = initializer.drawerLayout;
        this.menuItems = initializer.menuItems;
        this.profiles = initializer.profiles;
        drawerToggle = new ActionBarDrawerToggle(initializer.activity, drawerLayout, initializer.toolbar, setup.getOpenDrawerDesc(), setup.getCloseDrawerDesc());
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerToggle.syncState();

        setupMenuItems();
        setupProfiles();
        setupProfilesFooter();
    }

    public static Initializer setup(Activity activity, final DrawerLayout drawerLayout, Toolbar toolbar, ISetup setup) {
        LinearLayout realLayout = (LinearLayout) drawerLayout.getChildAt(1);
        realLayout.setBackgroundResource(setup.getColorAccent());

        ImageView headerBackground = (ImageView) realLayout.findViewById(R.id.drawerHeader_background);
        headerBackground.setImageResource(setup.getHeaderBackground());

        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        activity.getWindow().setStatusBarColor(Color.TRANSPARENT);

        return new Initializer(activity, drawerLayout, toolbar, setup);
    }

    private void setupProfilesFooter() {
        LinearLayout profilesFooter = (LinearLayout) drawerLayout.findViewById(R.id.drawer_profilesFooter);
        LayoutInflater inflater = LayoutInflater.from(context);

        profilesFooter.addView(MenuItemsAdapter.SeparatorViewHolder.getSeparator(context, setup.getColorPrimaryShadow()));

        MenuItemsAdapter.ViewHolder addProfile = new MenuItemsAdapter.ViewHolder(inflater, profilesFooter, setup.getRippleDark());
        addProfile.name.setText(context.getString(R.string.addProfile));
        addProfile.icon.setImageResource(R.drawable.ic_add_black_48dp);
        addProfile.badgeContainer.setVisibility(View.GONE);
        addProfile.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) listener.addProfile();
            }
        });
        profilesFooter.addView(addProfile.itemView);

        MenuItemsAdapter.ViewHolder editProfile = new MenuItemsAdapter.ViewHolder(inflater, profilesFooter, setup.getRippleDark());
        editProfile.name.setText(context.getString(R.string.editProfile));
        editProfile.icon.setImageResource(R.drawable.ic_mode_edit_black_48dp);
        editProfile.badgeContainer.setVisibility(View.GONE);
        editProfile.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) listener.editProfile(profilesAdapter.getItems());
            }
        });
        profilesFooter.addView(editProfile.itemView);
    }

    public DrawerManager refreshProfiles(List<? extends BaseDrawerProfile> newProfiles) {
        profiles.clear();
        profiles.addAll(newProfiles);

        setupProfiles();
        return this;
    }

    private void setupProfiles() {
        RecyclerView profilesList = (RecyclerView) drawerLayout.findViewById(R.id.drawer_profilesList);
        profilesList.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        profilesAdapter = setup.getProfilesAdapter(context, profiles, setup.getRippleDark(), listener);
        profilesList.setAdapter(profilesAdapter);

        final ImageView dropdownToggle = (ImageView) drawerLayout.findViewById(R.id.drawerHeader_dropdown);
        dropdownToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View profileContainer = drawerLayout.findViewById(R.id.drawer_profileContainer);
                final View menuContainer = drawerLayout.findViewById(R.id.drawer_menuList);

                if (profileContainer.getVisibility() == View.INVISIBLE) {
                    dropdownToggle.animate()
                            .rotation(180)
                            .setDuration(200)
                            .start();
                    profileContainer.setVisibility(View.VISIBLE);
                    profileContainer.setAlpha(0);
                    profileContainer.animate()
                            .alpha(1)
                            .setListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animator) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animator) {
                                    profileContainer.setAlpha(1);
                                    profilesAdapter.startProfilesTest(null);
                                }

                                @Override
                                public void onAnimationCancel(Animator animator) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animator) {

                                }
                            })
                            .setDuration(200)
                            .start();

                    menuContainer.animate()
                            .alpha(0)
                            .setDuration(200)
                            .setListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animator) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animator) {
                                    menuContainer.setVisibility(View.INVISIBLE);
                                }

                                @Override
                                public void onAnimationCancel(Animator animator) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animator) {

                                }
                            })
                            .start();
                } else {
                    dropdownToggle.animate()
                            .rotation(0)
                            .setDuration(200)
                            .start();

                    menuContainer.setVisibility(View.VISIBLE);
                    menuContainer.setAlpha(0);
                    menuContainer.animate()
                            .alpha(1)
                            .setListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animator) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animator) {
                                    menuContainer.setAlpha(1);
                                }

                                @Override
                                public void onAnimationCancel(Animator animator) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animator) {

                                }
                            })
                            .setDuration(200)
                            .start();

                    profileContainer.animate()
                            .alpha(0)
                            .setDuration(200)
                            .setListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animator) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animator) {
                                    profileContainer.setVisibility(View.INVISIBLE);
                                }

                                @Override
                                public void onAnimationCancel(Animator animator) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animator) {

                                }
                            })
                            .start();
                }
            }
        });
    }

    private void setupMenuItems() {
        RecyclerView menuList = (RecyclerView) drawerLayout.findViewById(R.id.drawer_menuList);
        menuList.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        menuItemsAdapter = new MenuItemsAdapter(context, menuItems, setup.getRippleDark(), setup.getDrawerBadge(), setup.getColorPrimaryShadow(), new MenuItemsAdapter.IAdapter() {
            @Override
            public void onMenuItemSelected(BaseDrawerItem which) {
                if (listener != null) setDrawerState(false, listener.onMenuItemSelected(which));
            }
        });
        menuList.setAdapter(menuItemsAdapter);
    }

    public void performUnlock() {
        if (isProfilesLockedUntilSelected) {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            drawerLayout.findViewById(R.id.drawerHeader_dropdown).setEnabled(true);

            isProfilesLockedUntilSelected = false;
        }
    }

    public void setDrawerListener(final IDrawerListener listener) {
        this.listener = listener;

        if (menuItemsAdapter != null)
            menuItemsAdapter.setDrawerListener(new MenuItemsAdapter.IAdapter() {
                @Override
                public void onMenuItemSelected(BaseDrawerItem which) {
                    if (listener != null) setDrawerState(false, listener.onMenuItemSelected(which));
                }
            });
        if (profilesAdapter != null)
            profilesAdapter.setDrawerListener(new ProfilesAdapter.IAdapter() {
                @Override
                public void onProfileSelected(BaseDrawerProfile profile) {
                    if (listener != null) listener.onProfileSelected(profile, false);
                    performUnlock();
                }
            });
    }

    public void updateBadge(int which, int badgeNumber) {
        if (menuItemsAdapter != null) menuItemsAdapter.updateBadge(which, badgeNumber);
    }

    public void setDrawerState(boolean open, boolean animate) {
        if (open) drawerLayout.openDrawer(GravityCompat.START, animate);
        else drawerLayout.closeDrawer(GravityCompat.START, animate);
    }

    public void syncTogglerState() {
        drawerToggle.syncState();
    }

    public void onTogglerConfigurationChanged(Configuration conf) {
        drawerToggle.onConfigurationChanged(conf);
    }

    public boolean hasProfiles() {
        return !profiles.isEmpty();
    }

    private void setProfilesDrawerOpen() {
        if (drawerLayout.findViewById(R.id.drawer_profileContainer).getVisibility() == View.INVISIBLE) {
            drawerLayout.findViewById(R.id.drawerHeader_dropdown).callOnClick();
        }
    }

    public void openProfiles(boolean lockUntilSelected) {
        setDrawerState(true, true);
        setProfilesDrawerOpen();

        isProfilesLockedUntilSelected = lockUntilSelected;
        if (lockUntilSelected) {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
            drawerLayout.findViewById(R.id.drawerHeader_dropdown).setEnabled(false);
        }
    }

    public void setCurrentProfile(BaseDrawerProfile profile) {
        LetterIconBig currAccount = (LetterIconBig) drawerLayout.findViewById(R.id.drawerHeader_currentAccount);
        currAccount.setColorScheme(setup.getColorAccent(), setup.getColorPrimary(), setup.getColorPrimaryShadow());

        TextView profileName = (TextView) drawerLayout.findViewById(R.id.drawerHeader_profileName);
        TextView profileAddr = (TextView) drawerLayout.findViewById(R.id.drawerHeader_profileAddr);

        profileName.setText(profile.getName());
        profileAddr.setText(profile.getAddress());
        currAccount.setInfo(profile.getName(), profile.getAddress(), profile.getPort());
    }

    public interface IDrawerListener {
        boolean onMenuItemSelected(BaseDrawerItem which);

        void onProfileSelected(BaseDrawerProfile profile, boolean fromRecent);

        void addProfile();

        void editProfile(List<BaseDrawerProfile> items);
    }

    public interface ISetup {
        @ColorRes
        int getColorAccent();

        @DrawableRes
        int getHeaderBackground();

        @StringRes
        int getOpenDrawerDesc();

        @StringRes
        int getCloseDrawerDesc();

        @DrawableRes
        int getRippleDark();

        @DrawableRes
        int getDrawerBadge();

        @ColorRes
        int getColorPrimaryShadow();

        @ColorRes
        int getColorPrimary();

        ProfilesAdapter getProfilesAdapter(Context context, List<BaseDrawerProfile> profiles, @DrawableRes int ripple_dark, DrawerManager.IDrawerListener listener);
    }

    public static class Initializer {
        private final Activity activity;
        private final DrawerLayout drawerLayout;
        private final Toolbar toolbar;
        private final ISetup setup;
        private final List<BaseDrawerItem> menuItems = new ArrayList<>();
        private final List<BaseDrawerProfile> profiles = new ArrayList<>();

        private Initializer(Activity activity, DrawerLayout drawerLayout, Toolbar toolbar, ISetup setup) {
            this.activity = activity;
            this.drawerLayout = drawerLayout;
            this.toolbar = toolbar;
            this.setup = setup;
        }

        public Initializer addMenuItemSeparator() {
            menuItems.add(null);
            return this;
        }

        public Initializer addMenuItem(BaseDrawerItem item) {
            menuItems.add(item);
            return this;
        }

        public Initializer addProfileItem(BaseDrawerProfile profile) {
            profiles.add(profile);
            return this;
        }

        public DrawerManager build() {
            return new DrawerManager(this);
        }

        public Initializer addProfiles(List<? extends BaseDrawerProfile> profiles) {
            for (BaseDrawerProfile profile : profiles)
                addProfileItem(profile);

            return this;
        }
    }
}
