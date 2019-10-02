package com.gianlu.commonutils.drawer;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gianlu.commonutils.CommonUtils;
import com.gianlu.commonutils.R;
import com.gianlu.commonutils.adapters.SelectiveDividerItemDecoration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class DrawerManager<P extends BaseDrawerProfile, E extends Enum> implements MenuItemsAdapter.Listener<E> {
    private final Context context;
    private final ActionBarDrawerToggle mDrawerToggle;
    private final DrawerLayout mDrawerLayout;
    private final Config<P, E> config;
    private final RecyclerView mMenuItemsList;
    private final RecyclerView mProfilesList;
    private final ImageButton mAction;
    private final RecyclerView mProfilesMenuItemsList;
    private final LinearLayout mProfilesContainer;
    private MenuItemsAdapter<E> mMenuItemsAdapter;
    private ProfilesAdapter<P, ?> profilesAdapter;
    private SelectiveDividerItemDecoration mMenuItemsDecoration;

    private DrawerManager(@NonNull Config<P, E> config, @NonNull Activity activity, @NonNull DrawerLayout drawerLayout, @NonNull Toolbar toolbar) {
        this.config = config;
        this.context = activity;

        mDrawerLayout = drawerLayout;
        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                mMenuItemsList.requestLayout();
                mProfilesList.requestLayout();
                mProfilesMenuItemsList.requestLayout();
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
        mMenuItemsList.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        mMenuItemsList.setHasFixedSize(true);

        mProfilesList = drawerLayout.findViewById(R.id.drawer_profilesList);
        mProfilesList.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        mProfilesList.setHasFixedSize(true);

        mProfilesMenuItemsList = drawerLayout.findViewById(R.id.drawer_profilesMenuItems);
        mProfilesMenuItemsList.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        mProfilesMenuItemsList.setHasFixedSize(true);

        mAction = drawerLayout.findViewById(R.id.drawerHeader_action);

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
        mAction.setOnClickListener(v -> {
            if (config.actionListener != null) config.actionListener.drawerAction();
        });
    }

    @Override
    public void onMenuItemSelected(@NonNull BaseDrawerItem<E> which) {
        if (config.menuListener != null)
            setDrawerState(false, config.menuListener.onDrawerMenuItemSelected(which));
    }

    private void setupProfilesMenuItems() {
        mProfilesMenuItemsList.setAdapter(new MenuItemsAdapter<>(context, config.profilesMenuItems, this));
    }

    private void setupMenuItems() {
        mMenuItemsAdapter = new MenuItemsAdapter<>(context, config.menuItems, this);
        mMenuItemsList.setAdapter(mMenuItemsAdapter);
        mMenuItemsList.removeItemDecoration(mMenuItemsDecoration);
        mMenuItemsDecoration = new SelectiveDividerItemDecoration(context, RecyclerView.VERTICAL, config.menuSeparators);
        mMenuItemsList.addItemDecoration(mMenuItemsDecoration);
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
        mAction.setOnClickListener(view -> toggleProfileList());
    }

    public void updateBadge(@NonNull E which, int badgeNumber) {
        if (mMenuItemsAdapter != null) mMenuItemsAdapter.updateBadge(which, badgeNumber);
    }

    public void setActiveItem(@NonNull E which) {
        if (mMenuItemsAdapter != null) mMenuItemsAdapter.setActiveItem(which);
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
        profileName.setText(profile.getPrimaryText(context));
        TextView secondaryText = mDrawerLayout.findViewById(R.id.drawerHeader_profileSecondaryText);
        secondaryText.setText(profile.getSecondaryText(context));
    }

    public boolean isOpen() {
        return mDrawerLayout.isDrawerOpen(GravityCompat.START);
    }

    public interface MenuDrawerListener<E extends Enum> {
        boolean onDrawerMenuItemSelected(@NonNull BaseDrawerItem<E> item);
    }

    public interface ProfilesDrawerListener<P extends BaseDrawerProfile> {
        void onDrawerProfileSelected(@NonNull P profile);

        boolean onDrawerProfileLongClick(@NonNull P profile);
    }

    public interface OnAction {
        void drawerAction();
    }

    public static class Config<P extends BaseDrawerProfile, E extends Enum> {
        private final List<BaseDrawerItem<E>> menuItems = new ArrayList<>();
        private final List<BaseDrawerItem<E>> profilesMenuItems = new ArrayList<>();
        private final List<Integer> menuSeparators = new ArrayList<>();
        private final List<P> profiles = new ArrayList<>();
        private final MenuDrawerListener<E> menuListener;
        private OnAction actionListener = null;
        private P singleProfile = null;
        private ProfilesDrawerListener<P> profilesListener;
        private AdapterProvider<P> adapterProvider;

        public Config(@NonNull MenuDrawerListener<E> menuListener) {
            this.menuListener = menuListener;
        }

        public Config<P, E> singleProfile(@NonNull P profile, @Nullable OnAction actionListener) {
            this.singleProfile = profile;
            this.actionListener = actionListener;
            this.profiles.clear();
            return this;
        }

        public Config<P, E> addMenuItemSeparator() {
            menuSeparators.add(menuItems.size() - 1);
            return this;
        }

        public Config<P, E> addMenuItem(@NonNull BaseDrawerItem<E> item) {
            menuItems.add(item);
            return this;
        }

        public Config<P, E> addProfilesMenuItem(@NonNull BaseDrawerItem<E> item) {
            profilesMenuItems.add(item);
            return this;
        }

        public Config<P, E> addProfiles(@NonNull Collection<P> profiles, @NonNull ProfilesDrawerListener<P> profilesListener, @NonNull AdapterProvider<P> adapterProvider) {
            this.profilesListener = profilesListener;
            this.adapterProvider = adapterProvider;
            this.profiles.addAll(profiles);
            this.actionListener = null;
            this.singleProfile = null;
            return this;
        }

        @NonNull
        public DrawerManager<P, E> build(@NonNull Activity activity, @NonNull DrawerLayout drawerLayout, @NonNull Toolbar toolbar) {
            return new DrawerManager<>(this, activity, drawerLayout, toolbar);
        }

        public interface AdapterProvider<P extends BaseDrawerProfile> {
            @NonNull
            ProfilesAdapter<P, ?> provide(@NonNull Context context, @NonNull List<P> profiles, @NonNull ProfilesDrawerListener<P> listener);
        }
    }
}
