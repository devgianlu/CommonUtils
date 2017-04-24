package com.gianlu.commonutils.Drawer;

import android.support.annotation.Keep;

@Keep
@SuppressWarnings({"unused", "WeakerAccess"})
public interface BaseDrawerProfile {
    String getGlobalName();

    String getName();

    String getAddress();

    int getPort();

    String toString();
}
