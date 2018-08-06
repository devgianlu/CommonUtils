package com.gianlu.commonutils.Preferences;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gianlu.commonutils.Preferences.Views.KeyedMaterialPreference;
import com.yarolegovich.mp.MaterialPreferenceCategory;
import com.yarolegovich.mp.MaterialPreferenceScreen;
import com.yarolegovich.mp.io.MaterialPreferences;
import com.yarolegovich.mp.io.StorageModule;

public abstract class BasePreferenceFragment extends Fragment {
    private MaterialPreferenceScreen screen;
    private MaterialPreferenceCategory lastCategory;
    private StorageModule storageModule;

    protected abstract void buildPreferences(@NonNull Context context);

    protected final void addCategory(@StringRes int title) {
        MaterialPreferenceCategory category = new MaterialPreferenceCategory(requireContext());
        category.setTitle(getString(title));
        screen.addView(category);
        lastCategory = category;
    }

    protected final void addPreference(@NonNull KeyedMaterialPreference preference) {
        preference.setStorageModule(storageModule);
        if (lastCategory == null) screen.addView((View) preference);
        else lastCategory.addView((View) preference);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        screen = new MaterialPreferenceScreen(requireContext());
        storageModule = MaterialPreferences.getStorageModule(requireContext());
        buildPreferences(requireContext());
        return screen;
    }

    @StringRes
    public abstract int getTitleRes();
}
