package com.gianlu.commonutils;

import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;

import java.util.Arrays;

public final class MaterialColors {
    private static final int[] COLORS = new int[]{R.color.red, R.color.pink, R.color.purple, R.color.deepPurple, R.color.indigo, R.color.blue, R.color.lightBlue, R.color.cyan, R.color.teal, R.color.green, R.color.lightGreen, R.color.lime, R.color.yellow, R.color.amber, R.color.orange, R.color.deepOrange, R.color.brown};
    private final int[] colors;

    private MaterialColors(int[] colors) {
        this.colors = Arrays.copyOf(colors, colors.length);
    }

    @NonNull
    public static MaterialColors getInstance() {
        return new MaterialColors(COLORS);
    }

    @NonNull
    public static MaterialColors getShuffledInstance() {
        MaterialColors colors = getInstance();
        colors.shuffle();
        return colors;
    }

    @ColorRes
    public int getColor(int pos) {
        int i = pos;
        while (i >= colors.length)
            i = i - colors.length;

        return colors[i];
    }

    public void shuffle() {
        CommonUtils.shuffleArray(colors);
    }

    public int[] getColorsRes() {
        return colors;
    }
}
