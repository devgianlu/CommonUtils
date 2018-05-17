package com.gianlu.commonutils.Drawer;

import android.content.Context;
import android.support.annotation.NonNull;

public interface BaseDrawerProfile {
    @NonNull
    String getProfileName(Context context);

    @NonNull
    String getSecondaryText(Context context);

    @NonNull
    String getInitials(Context context);
}
