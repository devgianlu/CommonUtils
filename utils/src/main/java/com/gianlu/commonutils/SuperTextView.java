package com.gianlu.commonutils;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.widget.AppCompatTextView;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

public class SuperTextView extends AppCompatTextView {
    private boolean isCompact;

    public SuperTextView(Context context, String fullText, String compatText, @ColorInt int textColor) {
        super(context);
        setCompactedText(fullText, compatText);
        setTextColor(textColor);
    }

    public SuperTextView(Context context, String fullText, String compatText) {
        super(context);
        setCompactedText(fullText, compatText);
    }

    public SuperTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SuperTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SuperTextView(Context context, @StringRes int str) {
        super(context);
        setHtml(str);
    }

    public SuperTextView(Context context, String str) {
        this(context, str, -1);
    }

    public SuperTextView(Context context, @StringRes int str, Object... args) {
        super(context, null);
        setHtml(str, args);
    }

    public SuperTextView(Context context, String str, @ColorInt int textColor) {
        super(context);
        setHtml(str);
        if (textColor != -1) setTextColor(textColor);
    }

    @NonNull
    public static String makeBold(String str) {
        return "<b>" + str + "</b>";
    }

    public void setHtml(String html) {
        setText(Html.fromHtml(html));
    }

    public void setHtml(@StringRes int html, Object... args) {
        setHtml(getContext().getString(html, args));
    }

    public void setCompactedText(final String fullText, final String compatText) {
        isCompact = true;
        setHtml(compatText);
        setEllipsize(TextUtils.TruncateAt.END);
        setMaxLines(1);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCompact) {
                    setHtml(fullText.replace(compatText, makeBold(compatText)));
                    setEllipsize(null);
                    setMaxLines(Integer.MAX_VALUE);
                } else {
                    setHtml(compatText);
                    setEllipsize(TextUtils.TruncateAt.END);
                    setMaxLines(1);
                }

                isCompact = !isCompact;
            }
        });
    }

    public void setTypeface(@NonNull String path) {
        setTypeface(FontsManager.get().get(getContext(), path));
    }
}
