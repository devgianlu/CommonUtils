package com.gianlu.commonutils.misc;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.AppCompatTextView;

import com.gianlu.commonutils.typography.FontsManager;

public class SuperTextView extends AppCompatTextView {
    private boolean isCompact;

    public SuperTextView(@NonNull Context context, @NonNull String fullText, @NonNull String compatText, @ColorInt int textColor) {
        super(context);
        setCompactedText(fullText, compatText);
        setTextColor(textColor);
    }

    public SuperTextView(@NonNull Context context, @NonNull String fullText, @NonNull String compatText) {
        super(context);
        setCompactedText(fullText, compatText);
    }

    public SuperTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SuperTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SuperTextView(@NonNull Context context, @StringRes int str) {
        super(context);
        setHtml(str);
    }

    public SuperTextView(@NonNull Context context, @NonNull String str) {
        this(context, str, -1);
    }

    public SuperTextView(@NonNull Context context, @StringRes int str, Object... args) {
        super(context, null);
        setHtml(str, args);
    }

    public SuperTextView(@NonNull Context context, @StringRes int str, @ColorInt int color) {
        super(context, null);
        setHtml(str);
        setTextColor(color);
    }

    public SuperTextView(@NonNull Context context, @NonNull String str, @ColorInt int textColor) {
        super(context);
        setHtml(str);
        if (textColor != 0) setTextColor(textColor);
    }

    @NonNull
    public static String makeBold(@NonNull String str) {
        return "<b>" + str + "</b>";
    }

    public void setHtml(@Nullable String html) {
        if (html == null) setText(null);
        else setText(Html.fromHtml(html));
    }

    public void setHtml(@StringRes int html, Object... args) {
        setHtml(getContext().getString(html, args));
    }

    public void setCompactedText(@NonNull String fullText, @NonNull String compatText) {
        isCompact = true;
        setHtml(compatText);
        setEllipsize(TextUtils.TruncateAt.END);
        setMaxLines(1);
        setOnClickListener(v -> {
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
        });
    }

    public void setTypeface(@NonNull @FontsManager.Font String path) {
        FontsManager.set(path, this);
    }
}
