package com.gianlu.commonutils.typography;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.LruCache;
import android.widget.TextView;

import androidx.annotation.FontRes;
import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

public final class FontsManager {
    private static FontsManager instance;
    private final LruCache<Integer, Typeface> cache;

    private FontsManager() {
        cache = new LruCache<>(10);
    }

    @NonNull
    public static Typeface get(@NonNull Context context, @FontRes int res) {
        if (instance == null) instance = new FontsManager();

        Typeface typeface = instance.cache.get(res);
        if (typeface == null) {
            typeface = ResourcesCompat.getFont(context, res);
            if (typeface == null) throw new IllegalArgumentException("Font doesn't exist!");
            instance.cache.put(res, typeface);
        }

        return typeface;
    }

    public static void set(@FontRes int res, @NonNull TextView... views) {
        for (TextView view : views) view.setTypeface(get(view.getContext(), res));
    }

    public static void set(@NonNull Context context, @NonNull Paint paint, @FontRes int res) {
        paint.setTypeface(get(context, res));
    }
}
