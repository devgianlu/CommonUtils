package com.gianlu.commonutils.lifecycle;

import android.app.Activity;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class LifecycleAwareHandler {
    private final Handler handler;

    public LifecycleAwareHandler(@NonNull Handler handler) {
        this.handler = handler;
    }

    public static boolean canPost(@Nullable Object ctx) {
        if (ctx instanceof Activity) {
            return !((Activity) ctx).isDestroyed() && !((Activity) ctx).isFinishing();
        } else if (ctx instanceof Fragment) {
            return ((Fragment) ctx).isAdded() && canPost(((Fragment) ctx).getActivity());
        } else {
            return true;
        }
    }

    public void post(Object ctx, Runnable r) {
        if (canPost(ctx)) handler.post(r);
    }
}
