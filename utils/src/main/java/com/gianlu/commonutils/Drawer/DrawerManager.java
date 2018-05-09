package com.gianlu.commonutils.Drawer;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
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

import com.gianlu.commonutils.CommonUtils;
import com.gianlu.commonutils.LettersIcons.LettersImageView;
import com.gianlu.commonutils.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DrawerManager<P extends BaseDrawerProfile> {
    private final Context context;
    private final ActionBarDrawerToggle drawerToggle;
    private final DrawerLayout drawerLayout;
    private final Config<P> config;
    private final int colorAccent;
    private final int colorPrimaryShadow;
    private MenuItemsAdapter menuItemsAdapter;
    private IDrawerListener<P> listener;
    private boolean isProfilesLockedUntilSelected;
    private ProfilesAdapter<P> profilesAdapter;

    private DrawerManager(Config<P> config, Activity activity, DrawerLayout drawerLayout, Toolbar toolbar) {
        this.config = config;
        this.context = drawerLayout.getContext();
        this.drawerLayout = drawerLayout;
        drawerToggle = new ActionBarDrawerToggle(activity, this.drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer);
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerToggle.syncState();

        colorAccent = ContextCompat.getColor(context, config.colorAccentRes);
        colorPrimaryShadow = CommonUtils.manipulateAlpha(ContextCompat.getColor(context, config.colorPrimaryRes), 0.54f);

        LinearLayout realLayout = (LinearLayout) this.drawerLayout.getChildAt(1);
        realLayout.setBackgroundColor(colorAccent);

        ImageView headerBackground = realLayout.findViewById(R.id.drawerHeader_background);
        headerBackground.setImageResource(config.headerDrawable);

        setupMenuItems();
        if (config.singleProfile != null) {
            setupSingleProfile(config.logoutHandler);
            setCurrentProfile(config.singleProfile);
        } else {
            setupProfiles();
            setupProfilesFooter();
        }
    }

    public void simulateMenuItemClick(int id) {
        BaseDrawerItem which = findMenuItem(id);
        if (which != null && listener != null)
            listener.onMenuItemSelected(which);
    }

    @Nullable
    private BaseDrawerItem findMenuItem(int id) {
        for (BaseDrawerItem item : config.menuItems)
            if (item.id == id)
                return item;

        return null;
    }

    public void setupSingleProfile(final ILogout logoutHandler) {
        final ImageView logout = drawerLayout.findViewById(R.id.drawerHeader_action);
        logout.setImageResource(R.drawable.ic_exit_to_app_white_48dp);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (logoutHandler != null) logoutHandler.logout();
            }
        });
    }

    private void setupProfilesFooter() {
        LinearLayout profilesFooter = drawerLayout.findViewById(R.id.drawer_profilesFooter);
        LayoutInflater inflater = LayoutInflater.from(context);

        profilesFooter.addView(MenuItemsAdapter.SeparatorViewHolder.getSeparator(context, colorPrimaryShadow));

        MenuItemsAdapter.ViewHolder addProfile = new MenuItemsAdapter.ViewHolder(inflater, profilesFooter);
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

        MenuItemsAdapter.ViewHolder editProfile = new MenuItemsAdapter.ViewHolder(inflater, profilesFooter);
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

    public DrawerManager<P> refreshProfiles(List<P> newProfiles) {
        config.profiles.clear();
        config.profiles.addAll(newProfiles);

        setupProfiles();
        profilesAdapter.startProfilesTest();
        return this;
    }

    private void setupProfiles() {
        RecyclerView profilesList = drawerLayout.findViewById(R.id.drawer_profilesList);
        profilesList.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        profilesAdapter = config.adapterProvider.provide(context, config.profiles, listener);
        profilesList.setAdapter(profilesAdapter);

        final ImageView dropdownToggle = drawerLayout.findViewById(R.id.drawerHeader_action);
        dropdownToggle.setImageResource(R.drawable.ic_arrow_drop_down_white_48dp);
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
                                    if (profilesAdapter != null)
                                        profilesAdapter.startProfilesTest();
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
        RecyclerView menuList = drawerLayout.findViewById(R.id.drawer_menuList);
        menuList.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        menuItemsAdapter = new MenuItemsAdapter(context, config.menuItems, R.drawable.drawer_badge, colorPrimaryShadow, new MenuItemsAdapter.IAdapter() {
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
            drawerLayout.findViewById(R.id.drawerHeader_action).setEnabled(true);

            isProfilesLockedUntilSelected = false;
        }
    }

    public void setDrawerListener(final IDrawerListener<P> listener) {
        this.listener = listener;

        if (menuItemsAdapter != null)
            menuItemsAdapter.setDrawerListener(new MenuItemsAdapter.IAdapter() {
                @Override
                public void onMenuItemSelected(BaseDrawerItem which) {
                    if (listener != null) setDrawerState(false, listener.onMenuItemSelected(which));
                }
            });
        if (profilesAdapter != null)
            profilesAdapter.setDrawerListener(new ProfilesAdapter.IAdapter<P>() {
                @Override
                public void onProfileSelected(P profile) {
                    if (listener != null) listener.onProfileSelected(profile);
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
        return !config.profiles.isEmpty();
    }

    private void setProfilesDrawerOpen() {
        if (drawerLayout.findViewById(R.id.drawer_profileContainer).getVisibility() == View.INVISIBLE) {
            drawerLayout.findViewById(R.id.drawerHeader_action).callOnClick();
        }
    }

    public void openProfiles(boolean lockUntilSelected) {
        setDrawerState(true, true);
        setProfilesDrawerOpen();

        isProfilesLockedUntilSelected = lockUntilSelected;
        if (lockUntilSelected) {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
            drawerLayout.findViewById(R.id.drawerHeader_action).setEnabled(false);
        }
    }

    public DrawerManager<P> setCurrentProfile(@NonNull P profile) {
        TextView profileName = drawerLayout.findViewById(R.id.drawerHeader_profileName);
        profileName.setText(profile.getProfileName(context));
        TextView secondaryText = drawerLayout.findViewById(R.id.drawerHeader_profileSecondaryText);
        secondaryText.setText(profile.getSecondaryText(context));
        LettersImageView currAccount = drawerLayout.findViewById(R.id.drawerHeader_currentAccount);
        currAccount.setLetters(profile.getInitials(context));
        return this;
    }

    public boolean isOpen() {
        return drawerLayout.isDrawerOpen(GravityCompat.START);
    }

    public interface IDrawerListener<P extends BaseDrawerProfile> {
        boolean onMenuItemSelected(BaseDrawerItem which);

        void onProfileSelected(P profile);

        void addProfile();

        void editProfile(List<P> items);
    }
    public interface ILogout {
        void logout();
    }

    public static class Config<P extends BaseDrawerProfile> {
        private final List<BaseDrawerItem> menuItems = new ArrayList<>();
        private final List<P> profiles = new ArrayList<>();
        private final int headerDrawable;
        private DrawerManager.ILogout logoutHandler = null;
        private P singleProfile = null;
        private final int colorAccentRes;
        private final int colorPrimaryRes;
        private AdapterProvider<P> adapterProvider;

        public Config(@DrawableRes int headerDrawable) {
            this.colorAccentRes = R.color.colorAccent;
            this.colorPrimaryRes = R.color.colorPrimary;
            this.headerDrawable = headerDrawable;
        }

        public Config<P> singleProfile(P profile, @Nullable DrawerManager.ILogout handler) {
            this.singleProfile = profile;
            this.logoutHandler = handler;
            this.profiles.clear();
            return this;
        }

        public Config<P> addMenuItemSeparator() {
            menuItems.add(null);
            return this;
        }

        public Config<P> addMenuItem(BaseDrawerItem item) {
            menuItems.add(item);
            return this;
        }

        public Config<P> addProfiles(Collection<P> profiles, @NonNull AdapterProvider<P> adapterProvider) {
            this.adapterProvider = adapterProvider;
            this.profiles.addAll(profiles);
            this.logoutHandler = null;
            this.singleProfile = null;
            return this;
        }

        public DrawerManager<P> build(Activity activity, DrawerLayout drawerLayout, Toolbar toolbar) {
            return new DrawerManager<>(this, activity, drawerLayout, toolbar);
        }

        public interface AdapterProvider<P extends BaseDrawerProfile> {
            ProfilesAdapter<P> provide(@NonNull Context context, @NonNull List<P> profiles, IDrawerListener<P> listener);
        }
    }
}
