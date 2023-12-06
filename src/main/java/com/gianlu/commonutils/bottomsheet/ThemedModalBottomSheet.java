package com.gianlu.commonutils.bottomsheet;

import android.content.Context;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;

import com.gianlu.commonutils.R;

public abstract class ThemedModalBottomSheet<Setup, Update> extends BaseModalBottomSheet<Setup, Update> {

    @Nullable
    @Override
    protected final LayoutInflater createLayoutInflater(@NonNull Context context, @NonNull Setup payload) {
        return LayoutInflater.from(new ContextThemeWrapper(context, getCustomTheme(payload)));
    }

    @StyleRes
    protected int getCustomTheme(@NonNull Setup payload) {
        return R.style.AppTheme;
    }
}
