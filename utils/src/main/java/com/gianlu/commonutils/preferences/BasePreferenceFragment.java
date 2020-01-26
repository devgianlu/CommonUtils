package com.gianlu.commonutils.preferences;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.gianlu.commonutils.dialogs.FragmentWithDialog;
import com.yarolegovich.mp.AbsMaterialCheckablePreference;
import com.yarolegovich.mp.AbsMaterialPreference;
import com.yarolegovich.mp.MaterialPreferenceCategory;
import com.yarolegovich.mp.MaterialPreferenceScreen;
import com.yarolegovich.mp.io.MaterialPreferences;
import com.yarolegovich.mp.io.StorageModule;

public abstract class BasePreferenceFragment extends FragmentWithDialog {
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

    protected final void addPreference(@NonNull AbsMaterialPreference preference) {
        preference.setStorageModule(storageModule);
        if (lastCategory == null) screen.addView(preference);
        else lastCategory.addView(preference);
    }

    protected final void removePreference(@NonNull final AbsMaterialPreference preference) {
        if (lastCategory == null) screen.removeView(preference);
        else lastCategory.removeView(preference);
    }

    protected final void clearPreferences() {
        screen.useLinearLayout();
    }

    protected final void rebuildPreferences() {
        clearPreferences();
        buildPreferences(screen.getContext());
    }

    protected final void addController(AbsMaterialCheckablePreference controller, boolean showWhenChecked, AbsMaterialPreference... dependent) {
        screen.setVisibilityController(controller, dependent, showWhenChecked);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        screen = new MaterialPreferenceScreen(requireContext());
        screen.useLinearLayout();
        storageModule = MaterialPreferences.getStorageModule(requireContext());
        buildPreferences(requireContext());
        return screen;
    }

    @StringRes
    public abstract int getTitleRes();
}
