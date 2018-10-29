package com.gianlu.commonutils.Drawer;

import android.content.Context;

import androidx.annotation.NonNull;

public interface BaseDrawerProfile {
    @NonNull
    String getProfileName(@NonNull Context context);

    @NonNull
    String getSecondaryText(@NonNull Context context);

    @NonNull
    String getInitials(@NonNull Context context);
}
