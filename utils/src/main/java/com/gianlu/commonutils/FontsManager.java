package com.gianlu.commonutils;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.LruCache;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.StringDef;

public final class FontsManager {
    public final static String ROBOTO_MEDIUM = "fonts/Roboto-Medium.ttf";
    public final static String ROBOTO_THIN = "fonts/Roboto-Thin.ttf";
    public final static String ROBOTO_LIGHT = "fonts/Roboto-Light.ttf";
    public static final String ROBOTO_BLACK = "fonts/Roboto-Black.ttf";
    public static final String ROBOTO_REGULAR = "fonts/Roboto-Regular.ttf";
    public static final String ROBOTO_BOLD = "fonts/Roboto-Bold.ttf";
    private static FontsManager instance;
    private final LruCache<String, Typeface> cache;

    private FontsManager() {
        cache = new LruCache<>(10);
    }

    @NonNull
    public static Typeface get(@NonNull Context context, @NonNull @Font String path) {
        if (instance == null) instance = new FontsManager();

        Typeface typeface = instance.cache.get(path);
        if (typeface == null) {
            typeface = Typeface.createFromAsset(context.getAssets(), path);
            instance.cache.put(path, typeface);
        }

        return typeface;
    }

    public static void set(@NonNull @Font String path, @NonNull TextView... views) {
        for (TextView view : views) view.setTypeface(get(view.getContext(), path));
    }

    public static void set(@NonNull Context context, @NonNull Paint paint, @NonNull @Font String path) {
        paint.setTypeface(get(context, path));
    }

    @StringDef({ROBOTO_BLACK, ROBOTO_BOLD, ROBOTO_LIGHT,
            ROBOTO_MEDIUM, ROBOTO_REGULAR, ROBOTO_THIN})
    public @interface Font {
    }
}
