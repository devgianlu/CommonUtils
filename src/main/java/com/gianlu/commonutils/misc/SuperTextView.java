package com.gianlu.commonutils.misc;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;

import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.FontRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.gianlu.commonutils.CommonUtils;
import com.gianlu.commonutils.typography.FontsManager;
import com.google.android.material.textview.MaterialTextView;

public class SuperTextView extends MaterialTextView {
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
    public static String makeItalic(@NonNull String str) {
        return "<i>" + str + "</i>";
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

    public void setTypeface(@FontRes int res) {
        FontsManager.set(res, this);
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

        public Builder colorAttr(@AttrRes int color) {
            view.setTextColor(CommonUtils.resolveAttrAsColor(view.getContext(), color));
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

        public Builder typeface(@FontRes int res) {
            view.setTypeface(res);
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

        public Builder sizeSp(int sp) {
            view.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp);
            return this;
        }

        public Builder paddingDp(@Nullable Integer left, @Nullable Integer top, @Nullable Integer right, @Nullable Integer bottom) {
            CommonUtils.setPaddingDip(view, left, top, right, bottom);
            return this;
        }
    }
}