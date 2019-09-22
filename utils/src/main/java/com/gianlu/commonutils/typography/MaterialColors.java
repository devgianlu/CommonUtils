package com.gianlu.commonutils.typography;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;

import com.gianlu.commonutils.CommonUtils;
import com.gianlu.commonutils.R;

import java.util.Arrays;

public final class MaterialColors {
    private static final int[] COLORS = new int[]{R.color.red, R.color.pink, R.color.purple, R.color.deepPurple, R.color.indigo, R.color.blue, R.color.lightBlue, R.color.cyan, R.color.teal, R.color.green, R.color.lightGreen, R.color.lime, R.color.yellow, R.color.amber, R.color.orange, R.color.deepOrange, R.color.brown};
    private final int[] colors;
    private int index = 0;

    private MaterialColors() {
        this.colors = Arrays.copyOf(COLORS, COLORS.length);
    }

    @NonNull
    public static MaterialColors getInstance() {
        return new MaterialColors();
    }

    @NonNull
    public static MaterialColors getShuffledInstance() {
        MaterialColors colors = getInstance();
        colors.shuffle();
        return colors;
    }

    @ColorRes
    public int getColor(int i) {
        return colors[i % colors.length];
    }

    @ColorRes
    public int next() {
        return getColor(index++);
    }

    public void shuffle() {
        if (index != 0) throw new IllegalStateException();
        CommonUtils.shuffleArray(colors);
    }

    public int[] getColorsRes() {
        return colors;
    }
}
