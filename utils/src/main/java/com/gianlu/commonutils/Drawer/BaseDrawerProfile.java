package com.gianlu.commonutils.Drawer;

import android.content.Context;

public interface BaseDrawerProfile {
    String getProfileName(Context context);

    String getSecondaryText(Context context);

    String getInitials(Context context);
}
