package com.gianlu.commonutils.ui;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.gianlu.commonutils.CommonUtils;
import com.gianlu.commonutils.dialogs.DialogUtils;

public final class Toaster {
    private final static Handler handler = new Handler(Looper.getMainLooper());
    private static final String TAG = Toaster.class.getSimpleName();
    private final Context context;
    private String msg;
    private int msgRes;
    private Object[] args;
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

    public void show() {
        if (context == null) throw new IllegalStateException("Missing context instance!");
        show(context);
    }

    public void show(@NonNull Context context) {
        if (shown) {
            if (CommonUtils.isDebug()) System.out.println("Skipping toast, already shown!");
            return;
        }

        if (!DialogUtils.isContextValid(context)) {
            if (CommonUtils.isDebug())
                System.out.println("Skipping toast, context is invalid: " + context);
            return;
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
        if (msg.length() > 48) duration = Toast.LENGTH_LONG;
        else duration = Toast.LENGTH_SHORT;

        Runnable action = () -> {
            if (DialogUtils.isContextValid(context))
                Toast.makeText(context, msg, duration).show();
        };

        if (Looper.myLooper() == Looper.getMainLooper()) action.run();
        else handler.post(action);

        Log.v(TAG, buildLogMessage(context));
        shown = true;
    }

    @NonNull
    private String buildLogMessage(@NonNull Context ctx) {
        return "Toaster{context=" + ctx + ", msg='" + msg + "', extra=" + extra + '}';
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if (!shown && CommonUtils.isDebug()) System.err.println("Leaked " + this);
    }
}
