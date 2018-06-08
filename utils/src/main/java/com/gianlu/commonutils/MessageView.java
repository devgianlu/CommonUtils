package com.gianlu.commonutils;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.UiThread;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;

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

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, new int[]{R.styleable.MessageView_errorRes, R.styleable.MessageView_infoRes}, defStyleAttr, 0);
        try {
            errorRes = a.getResourceId(0, R.drawable.ic_error_outline_black_48dp);
            infoRes = a.getResourceId(1, R.drawable.ic_info_outline_black_48dp);
        } finally {
            a.recycle();
        }
    }

    private void set(@DrawableRes int iconRes, @StringRes int textRes, Object... args) {
        this.icon.setImageResource(iconRes);
        this.text.setHtml(textRes, args);
        this.setVisibility(VISIBLE);
    }

    private void set(@DrawableRes int iconRes, String text) {
        this.icon.setImageResource(iconRes);
        this.text.setHtml(text);
        this.setVisibility(VISIBLE);
    }

    public void hide() {
        this.setVisibility(GONE);
    }

    public void setInfo(@StringRes int textRes, Object... args) {
        set(infoRes, textRes, args);
    }

    public void setError(@StringRes int textRes, Object... args) {
        set(errorRes, textRes, args);
    }
}
