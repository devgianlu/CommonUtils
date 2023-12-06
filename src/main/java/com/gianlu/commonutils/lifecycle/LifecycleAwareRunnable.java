package com.gianlu.commonutils.lifecycle;

import androidx.annotation.NonNull;

public abstract class LifecycleAwareRunnable implements Runnable {
    private final LifecycleAwareHandler handler;
    private final Object ctx;

    public LifecycleAwareRunnable(@NonNull LifecycleAwareHandler handler, @NonNull Object ctx) {
        this.handler = handler;
        this.ctx = ctx;
    }

    protected void post(@NonNull Runnable r) {
        handler.post(ctx, r);
    }
}
