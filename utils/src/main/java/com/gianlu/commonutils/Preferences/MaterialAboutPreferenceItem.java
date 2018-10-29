package com.gianlu.commonutils.Preferences;

import com.danielstone.materialaboutlibrary.items.MaterialAboutActionItem;
import com.danielstone.materialaboutlibrary.items.MaterialAboutItemOnClickAction;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

public class MaterialAboutPreferenceItem extends MaterialAboutActionItem {
    Listener listener;

    public MaterialAboutPreferenceItem(@StringRes int textRes, @DrawableRes int iconRes, @NonNull final Class<? extends BasePreferenceFragment> clazz) {
        super(textRes, 0, iconRes);
        setOnClickAction(new MaterialAboutItemOnClickAction() {
            @Override
            public void onClick() {
                if (listener != null) listener.onPreferenceSelected(clazz);
            }
        });
    }

    public interface Listener {
        void onPreferenceSelected(@NonNull Class<? extends BasePreferenceFragment> clazz);
    }
}
