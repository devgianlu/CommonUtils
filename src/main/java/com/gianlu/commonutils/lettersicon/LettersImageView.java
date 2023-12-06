package com.gianlu.commonutils.lettersicon;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class LettersImageView extends View {
    private final DrawingHelper helper;
    private String letters;

    public LettersImageView(Context context) {
        this(context, null, 0);
    }

    public LettersImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LettersImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        helper = new DrawingHelper(context);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    public void setLetters(@Nullable String str) {
        if (str == null) {
            letters = null;
            setVisibility(GONE);
            invalidate();
            return;
        }

        if (str.length() <= 2) letters = str;
        else letters = str.substring(0, 2);

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (letters != null) helper.draw(letters, true, canvas);
    }
}
