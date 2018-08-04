package com.gianlu.commonutils.Drawer;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gianlu.commonutils.CommonUtils;
import com.gianlu.commonutils.LettersIcons.LettersImageView;
import com.gianlu.commonutils.R;
import com.gianlu.commonutils.SelectiveDividerItemDecoration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class DrawerManager<P extends BaseDrawerProfile> implements MenuItemsAdapter.Listener {
    private final Context context;
    private final ActionBarDrawerToggle mDrawerToggle;
    private final DrawerLayout mDrawerLayout;
    private final Config<P> config;
    private final RecyclerView mMenuItemsList;
    private final RecyclerView mProfilesList;
    private final ImageButton mAction;
    private final RecyclerView mProfilesMenuItemsList;
    private final LinearLayout mProfilesContainer;
    private MenuItemsAdapter menuItemsAdapter;
    private ProfilesAdapter<P, ?> profilesAdapter;
    private SelectiveDividerItemDecoration menuItemsDecoration;

    private DrawerManager(@NonNull Config<P> config, @NonNull Activity activity, @NonNull DrawerLayout drawerLayout, @NonNull Toolbar toolbar) {
        this.config = config;
        this.context = drawerLayout.getContext();

        mDrawerLayout = drawerLayout;
        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                mDrawerToggle.syncState();
            }
        });

        mProfilesContainer = drawerLayout.findViewById(R.id.drawer_profilesContainer);

        mDrawerToggle = new ActionBarDrawerToggle(activity, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer);
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerToggle.syncState();

        mMenuItemsList = drawerLayout.findViewById(R.id.drawer_menuItemsList);
        mMenuItemsList.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        mMenuItemsList.setHasFixedSize(true);

        mProfilesList = drawerLayout.findViewById(R.id.drawer_profilesList);
        mProfilesList.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        mProfilesList.setHasFixedSize(true);

        mProfilesMenuItemsList = drawerLayout.findViewById(R.id.drawer_profilesMenuItems);
        mProfilesMenuItemsList.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        mProfilesMenuItemsList.setHasFixedSize(true);

        mAction = drawerLayout.findViewById(R.id.drawerHeader_action);

        ImageView headerBackground = drawerLayout.findViewById(R.id.drawerHeader_background);
        headerBackground.setImageResource(config.headerDrawable);

        setupMenuItems();
        if (config.singleProfile != null) {
            setupSingleProfile();
            setCurrentProfile(config.singleProfile);
        } else {
            setupProfiles();
            setupProfilesMenuItems();
        }
    }

    private void setupSingleProfile() {
        mAction.setImageResource(R.drawable.outline_exit_to_app_24);
        mAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (config.actionListener != null) config.actionListener.drawerAction();
            }
        });
    }

    @Override
    public void onMenuItemSelected(@NonNull BaseDrawerItem which) {
        if (config.menuListener != null)
            setDrawerState(false, config.menuListener.onDrawerMenuItemSelected(which));
    }

    private void setupProfilesMenuItems() {
        mProfilesMenuItemsList.setAdapter(new MenuItemsAdapter(context, config.profilesMenuItems, this));
    }

    private void setupMenuItems() {
        menuItemsAdapter = new MenuItemsAdapter(context, config.menuItems, this);
        mMenuItemsList.setAdapter(menuItemsAdapter);
        mMenuItemsList.removeItemDecoration(menuItemsDecoration);
        menuItemsDecoration = new SelectiveDividerItemDecoration(context, SelectiveDividerItemDecoration.VERTICAL, config.menuSeparators);
        mMenuItemsList.addItemDecoration(menuItemsDecoration);
    }

    public void refreshProfiles(List<P> newProfiles) {
        config.profiles.clear();
        config.profiles.addAll(newProfiles);

        setupProfiles();
        profilesAdapter.startProfilesTest();
    }

    private void openProfilesList() {
        CommonUtils.animateCollapsingArrowBellows(mAction, false);

        mProfilesContainer.setAlpha(0);
        mProfilesContainer.setVisibility(View.VISIBLE);
        mProfilesContainer.animate()
                .alpha(1)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        mProfilesContainer.setAlpha(1);
                        if (profilesAdapter != null) profilesAdapter.startProfilesTest();
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

        mMenuItemsList.animate()
                .alpha(0)
                .setDuration(200)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        mMenuItemsList.setVisibility(View.GONE);
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

    private void closeProfilesList() {
        CommonUtils.animateCollapsingArrowBellows(mAction, true);

        mMenuItemsList.setAlpha(0);
        mMenuItemsList.setVisibility(View.VISIBLE);
        mMenuItemsList.animate()
                .alpha(1)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        mMenuItemsList.setAlpha(1);
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

        mProfilesContainer.animate()
                .alpha(0)
                .setDuration(200)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        mProfilesContainer.setVisibility(View.GONE);
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

    private void toggleProfileList() {
        if (mProfilesContainer.getVisibility() == View.GONE) openProfilesList();
        else closeProfilesList();
    }

    private void setupProfiles() {
        profilesAdapter = config.adapterProvider.provide(context, config.profiles, config.profilesListener);
        mProfilesList.setAdapter(profilesAdapter);

        mAction.setImageResource(R.drawable.baseline_arrow_drop_down_24);
        mAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleProfileList();
            }
        });
    }

    public void updateBadge(int which, int badgeNumber) {
        if (menuItemsAdapter != null) menuItemsAdapter.updateBadge(which, badgeNumber);
    }

    public void setActiveItem(int which) {
        if (menuItemsAdapter != null) menuItemsAdapter.setActiveItem(which);
    }

    public void setDrawerState(boolean open, boolean animate) {
        if (open) mDrawerLayout.openDrawer(GravityCompat.START, animate);
        else mDrawerLayout.closeDrawer(GravityCompat.START, animate);
    }

    public void syncTogglerState() {
        mDrawerToggle.syncState();
    }

    public void onTogglerConfigurationChanged(@NonNull Configuration conf) {
        mDrawerToggle.onConfigurationChanged(conf);
    }

    public boolean hasProfiles() {
        return !config.profiles.isEmpty();
    }

    public void setCurrentProfile(@NonNull P profile) {
        TextView profileName = mDrawerLayout.findViewById(R.id.drawerHeader_profileName);
        profileName.setText(profile.getProfileName(context));
        TextView secondaryText = mDrawerLayout.findViewById(R.id.drawerHeader_profileSecondaryText);
        secondaryText.setText(profile.getSecondaryText(context));
        LettersImageView currAccount = mDrawerLayout.findViewById(R.id.drawerHeader_currentAccount);
        currAccount.setLetters(profile.getInitials(context));
    }

    public boolean isOpen() {
        return mDrawerLayout.isDrawerOpen(GravityCompat.START);
    }

    public void reloadProfiles() {
        setupProfiles();
    }

    public interface MenuDrawerListener {
        boolean onDrawerMenuItemSelected(@NonNull BaseDrawerItem item);
    }

    public interface ProfilesDrawerListener<P extends BaseDrawerProfile> {
        void onDrawerProfileSelected(@NonNull P profile);

        boolean onDrawerProfileLongClick(@NonNull P profile);
    }

    public interface OnAction {
        void drawerAction();
    }

    public static class Config<P extends BaseDrawerProfile> {
        private final List<BaseDrawerItem> menuItems = new ArrayList<>();
        private final List<BaseDrawerItem> profilesMenuItems = new ArrayList<>();
        private final List<Integer> menuSeparators = new ArrayList<>();
        private final List<P> profiles = new ArrayList<>();
        private final MenuDrawerListener menuListener;
        private final int headerDrawable;
        private OnAction actionListener = null;
        private P singleProfile = null;
        private ProfilesDrawerListener<P> profilesListener;
        private AdapterProvider<P> adapterProvider;

        public Config(@NonNull MenuDrawerListener menuListener, @DrawableRes int headerDrawable) {
            this.menuListener = menuListener;
            this.headerDrawable = headerDrawable;
        }

        public Config<P> singleProfile(@NonNull P profile, @Nullable OnAction actionListener) {
            this.singleProfile = profile;
            this.actionListener = actionListener;
            this.profiles.clear();
            return this;
        }

        public Config<P> addMenuItemSeparator() {
            menuSeparators.add(menuItems.size() - 1);
            return this;
        }

        public Config<P> addMenuItem(@NonNull BaseDrawerItem item) {
            menuItems.add(item);
            return this;
        }

        public Config<P> addProfilesMenuItem(@NonNull BaseDrawerItem item) {
            profilesMenuItems.add(item);
            return this;
        }

        public Config<P> addProfiles(@NonNull Collection<P> profiles, @NonNull ProfilesDrawerListener<P> profilesListener, @NonNull AdapterProvider<P> adapterProvider) {
            this.profilesListener = profilesListener;
            this.adapterProvider = adapterProvider;
            this.profiles.addAll(profiles);
            this.actionListener = null;
            this.singleProfile = null;
            return this;
        }

        @NonNull
        public DrawerManager<P> build(@NonNull Activity activity, @NonNull DrawerLayout drawerLayout, @NonNull Toolbar toolbar) {
            return new DrawerManager<>(this, activity, drawerLayout, toolbar);
        }

        public interface AdapterProvider<P extends BaseDrawerProfile> {
            @NonNull
            ProfilesAdapter<P, ?> provide(@NonNull Context context, @NonNull List<P> profiles, @NonNull ProfilesDrawerListener<P> listener);
        }
    }
}
