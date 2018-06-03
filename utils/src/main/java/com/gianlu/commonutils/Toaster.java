package com.gianlu.commonutils;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.widget.Toast;

public class Toaster {
    private final static Handler handler = new Handler(Looper.getMainLooper());
    private final Context context;
    private String msg;
    private int msgRes;
    private Object[] args;
    private Throwable ex;
    private boolean error;
    private Object extra;
    private boolean shown = false;

    private Toaster(@Nullable Context context) {
        this.context = context;
    }

    @NonNull
    public static Toaster with(@NonNull Context context) {
        return new Toaster(context);
    }

    @NonNull
    public static Toaster build() {
        return new Toaster(null);
    }

    public Toaster message(@StringRes int str, Object... args) {
        if (context != null) {
            this.msg = context.getString(str, args);
            this.msgRes = 0;
            this.args = null;
        } else {
            this.msgRes = str;
            this.args = args;
            this.msg = null;
        }

        return this;
    }

    public Toaster message(String str) {
        this.msg = str;
        this.msgRes = 0;
        this.args = null;
        return this;
    }

    public Toaster extra(Object extra) {
        this.extra = extra;
        return this;
    }

    public Toaster ex(Throwable ex) {
        this.ex = ex;
        this.error = true;
        return this;
    }

    public Toaster error(boolean error) {
        this.error = error;
        return this;
    }

    public void show() {
        if (context == null) throw new IllegalStateException("Missing context instance!");
        show(context);
    }

    public void show(@NonNull final Context context) {
        if (shown) {
            if (CommonUtils.isDebug()) System.out.println("Skipping toast, already shown!");
            return;
        }

        if (context instanceof Activity) {
            if (((Activity) context).isFinishing() || ((Activity) context).isDestroyed()) {
                if (CommonUtils.isDebug())
                    System.out.println("Skipping toast, activity is destroying: " + context);
                return;
            }
        }

        if (msg == null) {
            if (msgRes != 0) {
                msg = context.getString(msgRes, args);
                msgRes = 0;
                args = null;
            } else {
                throw new IllegalArgumentException("Missing toast message!");
            }
        }

        final int duration;
        if (error || msg.length() > 48) duration = Toast.LENGTH_LONG;
        else duration = Toast.LENGTH_SHORT;

        Runnable action = new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, msg, duration).show();
            }
        };

        if (Looper.myLooper() == Looper.getMainLooper()) action.run();
        else handler.post(action);

        Logging.log(msg + (extra != null ? (" Extra: " + extra) : ""), ex != null);
        if (ex != null) Logging.log(ex);

        shown = true;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if (!shown && CommonUtils.isDebug()) System.err.println("Leaked " + this);
    }
}
