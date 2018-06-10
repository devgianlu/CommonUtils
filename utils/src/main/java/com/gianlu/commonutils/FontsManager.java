package com.gianlu.commonutils;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.util.LruCache;

public class FontsManager {
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
    public static FontsManager get() {
        if (instance == null) instance = new FontsManager();
        return instance;
    }

    @NonNull
    public Typeface get(@NonNull Context context, @NonNull String path) {
        Typeface typeface = cache.get(path);
        if (typeface == null) {
            typeface = Typeface.createFromAsset(context.getAssets(), path);
            cache.put(path, typeface);
        }

        return typeface;
    }
}
