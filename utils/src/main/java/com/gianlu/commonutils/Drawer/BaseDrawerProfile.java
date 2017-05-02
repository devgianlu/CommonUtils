package com.gianlu.commonutils.Drawer;

import android.support.annotation.Keep;

@Keep
@SuppressWarnings({"unused", "WeakerAccess"})
public interface BaseDrawerProfile {
    String getProfileName();

    String getSecondaryText();

    String toString();

    String getInitials();
}
