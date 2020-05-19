package com.gianlu.commonutils.misc;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.annotation.UiThread;

import com.gianlu.commonutils.R;

@UiThread
public class MessageView extends LinearLayout {
    private final SuperTextView text;
    private final ImageView icon;
    private final int errorRes;
    private final int infoRes;

    public MessageView(Context context) {
        this(context, null, 0);
    }

    public MessageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MessageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.view_message, this, true);
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER_HORIZONTAL);

        icon = findViewById(R.id.messageView_icon);
        text = findViewById(R.id.messageView_text);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MessageView, defStyleAttr, 0);
        try {
            errorRes = a.getResourceId(R.styleable.MessageView_errorRes, R.drawable.baseline_error_outline_24);
            infoRes = a.getResourceId(R.styleable.MessageView_infoRes, R.drawable.outline_info_24);
        } finally {
            a.recycle();
        }
    }

    public void set(@DrawableRes int iconRes, @StringRes int textRes, Object... args) {
        this.icon.setImageResource(iconRes);
        this.text.setHtml(textRes, args);
        this.setVisibility(VISIBLE);
    }

    public void set(@DrawableRes int iconRes, @NonNull String text) {
        this.icon.setImageResource(iconRes);
        this.text.setHtml(text);
        this.setVisibility(VISIBLE);
    }

    public void hide() {
        this.setVisibility(GONE);
    }

    public void info(@StringRes int textRes, Object... args) {
        set(infoRes, textRes, args);
    }

    public void info(@NonNull String text) {
        set(infoRes, text);
    }

    public void error(@StringRes int textRes, Object... args) {
        set(errorRes, textRes, args);
    }

    public void error(@NonNull String text) {
        set(errorRes, text);
    }

    @NonNull
    public SuperTextView text() {
        return text;
    }

    @NonNull
    public ImageView image() {
        return icon;
    }
}
