package com.gianlu.commonutils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

public class LetterIconBig extends View {
    private final Rect lettersBounds = new Rect();
    private final Rect textBounds = new Rect();
    private Paint shapePaint;
    private Paint letterPaint;
    private String letters;

    public LetterIconBig(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (isInEditMode())
            letters = "AA";
    }

    public void setColorScheme(@ColorRes int colorAccent, @ColorRes int colorPrimary_shadow) {
        letterPaint = new Paint();
        letterPaint.setColor(ContextCompat.getColor(getContext(), colorAccent));
        letterPaint.setAntiAlias(true);
        letterPaint.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Light.ttf"));
        letterPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 26, getResources().getDisplayMetrics()));

        shapePaint = new Paint();
        shapePaint.setAntiAlias(true);
        shapePaint.setColor(ContextCompat.getColor(getContext(), R.color.white));
        shapePaint.setShadowLayer(4, 0, 4, ContextCompat.getColor(getContext(), colorPrimary_shadow));
        setLayerType(LAYER_TYPE_SOFTWARE, shapePaint);
    }

    public void setInitials(String initials) {
        letters = initials;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (shapePaint == null || letterPaint == null) return;

        int viewWidthHalf = this.getMeasuredWidth() / 2;
        int viewHeightHalf = this.getMeasuredHeight() / 2;

        int radius;
        if (viewWidthHalf > viewHeightHalf)
            radius = viewHeightHalf - 4;
        else
            radius = viewWidthHalf - 4;

        canvas.drawCircle(viewWidthHalf, viewHeightHalf, radius, shapePaint);

        letterPaint.getTextBounds(letters, 0, letters.length(), lettersBounds);
        canvas.drawText(letters, viewWidthHalf - lettersBounds.exactCenterX(), viewHeightHalf - lettersBounds.exactCenterY() - textBounds.height() - 2, letterPaint);
    }
}
