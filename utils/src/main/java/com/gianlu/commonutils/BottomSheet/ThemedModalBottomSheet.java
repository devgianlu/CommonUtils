package com.gianlu.commonutils.BottomSheet;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;

public abstract class ThemedModalBottomSheet<Setup, Update> extends BaseModalBottomSheet<Setup, Update> {

    @Nullable
    @Override
    protected final LayoutInflater createLayoutInflater(@NonNull Context context, @NonNull Setup payload) {
        return LayoutInflater.from(new ContextThemeWrapper(context, getCustomTheme(payload)));
    }

    @StyleRes
    protected abstract int getCustomTheme(@NonNull Setup payload);
}
