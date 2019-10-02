package com.gianlu.commonutils.misc;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.AppCompatTextView;

import com.gianlu.commonutils.CommonUtils;
import com.gianlu.commonutils.typography.FontsManager;

public class SuperTextView extends AppCompatTextView {
    private boolean isCompact;

    public SuperTextView(Context context) {
        this(context, null, 0);
    }

    public SuperTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SuperTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @NonNull
    public static String makeBold(@NonNull String str) {
        return "<b>" + str + "</b>";
    }

    public static SuperTextView text(@NonNull Context context, String text) {
        return new Builder(context).text(text).build();
    }

    public static SuperTextView text(@NonNull Context context, @StringRes int textRes) {
        return new Builder(context).text(textRes).build();
    }

    public static SuperTextView html(@NonNull Context context, String html) {
        return new Builder(context).html(html).build();
    }

    public static SuperTextView html(@NonNull Context context, @StringRes int textRes, Object... args) {
        return new Builder(context).html(textRes, args).build();
    }

    public static SuperTextView compactable(@NonNull Context context, String fullText, String compactText) {
        return new Builder(context).compactable(fullText, compactText).build();
    }

    @NonNull
    public static Builder builder(@NonNull Context context) {
        return new Builder(context);
    }

    public void setHtml(@Nullable String html) {
        if (html == null) setText(null);
        else setText(Html.fromHtml(html));
    }

    public void setHtml(@StringRes int textRes, Object... args) {
        setHtml(getContext().getString(textRes, args));
    }

    public void setCompactableText(@NonNull String fullText, @NonNull String compatText) {
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

    public static class Builder {
        private final SuperTextView view;

        private Builder(@NonNull Context context) {
            this.view = new SuperTextView(context);
        }

        @NonNull
        public SuperTextView build() {
            return view;
        }

        public Builder text(@StringRes int textRes) {
            view.setText(textRes);
            return this;
        }

        public Builder text(String text) {
            view.setText(text);
            return this;
        }

        public Builder color(@ColorInt int color) {
            view.setTextColor(color);
            return this;
        }

        public Builder colorRes(@ColorRes int colorRes) {
            CommonUtils.setTextColor(view, colorRes);
            return this;
        }

        public Builder compactable(@NonNull String fullText, @NonNull String compatText) {
            view.setCompactableText(fullText, compatText);
            return this;
        }

        public Builder typeface(@NonNull @FontsManager.Font String path) {
            view.setTypeface(path);
            return this;
        }

        public Builder html(@StringRes int textRes, Object... args) {
            view.setHtml(textRes, args);
            return this;
        }

        public Builder html(String text) {
            view.setHtml(text);
            return this;
        }
    }
}