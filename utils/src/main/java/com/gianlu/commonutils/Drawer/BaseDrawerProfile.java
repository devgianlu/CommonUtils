package com.gianlu.commonutils.Drawer;

import android.content.Context;

import androidx.annotation.NonNull;

public interface BaseDrawerProfile {
    @NonNull
    String getPrimaryText(@NonNull Context context);

    @NonNull
    String getSecondaryText(@NonNull Context context);
}
