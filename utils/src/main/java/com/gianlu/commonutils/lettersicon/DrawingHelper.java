package com.gianlu.commonutils.lettersicon;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.TypedValue;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.gianlu.commonutils.CommonUtils;
import com.gianlu.commonutils.R;
import com.gianlu.commonutils.typography.FontsManager;

public final class DrawingHelper {
    private static final float REDUCE_FACTOR_NO_CIRCLE = 0.8f; // Reduce by 20%
    private static final float REDUCE_FACTOR_CIRCLE = 0.6f; // Reduce by 40%
    private static final int SHADOW_RADIUS = 4;
    private final Paint lettersPaintWithCircle;
    private final Paint lettersPaintNoCircle;
    private final Rect lettersBounds = new Rect();
    private final Paint shapePaint;

    public DrawingHelper(@NonNull Context context) {
        lettersPaintWithCircle = new Paint();
        lettersPaintWithCircle.setAntiAlias(true);
        FontsManager.set(context, lettersPaintWithCircle, R.font.roboto_light);
        lettersPaintWithCircle.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 20, context.getResources().getDisplayMetrics()));

        lettersPaintNoCircle = new Paint(lettersPaintWithCircle);
        lettersPaintNoCircle.setColor(ContextCompat.getColor(context, R.color.colorPrimary));
        lettersPaintWithCircle.setColor(ContextCompat.getColor(context, R.color.white));

        shapePaint = new Paint();
        shapePaint.setAntiAlias(true);
        shapePaint.setColor(ContextCompat.getColor(context, R.color.colorPrimary));
        shapePaint.setShadowLayer(SHADOW_RADIUS, 0, 4, CommonUtils.manipulateAlpha(ContextCompat.getColor(context, R.color.colorPrimary), 0.8f));
    }

    private static float hypotenuse(int x, int y) {
        return (float) Math.sqrt(x * x + y * y);
    }

    public void draw(@NonNull String letters, boolean circle, Canvas canvas) {
        int cx = canvas.getWidth() / 2;
        int cy = canvas.getHeight() / 2;
        int r = Math.min(cx, cy);

        float reduce;
        Paint lettersPaint;
        if (circle) {
            reduce = REDUCE_FACTOR_CIRCLE;
            r = r - SHADOW_RADIUS;
            canvas.drawCircle(cx, cy, r, shapePaint);
            lettersPaint = lettersPaintWithCircle;
        } else {
            reduce = REDUCE_FACTOR_NO_CIRCLE;
            lettersPaint = lettersPaintNoCircle;
        }

        lettersPaint.getTextBounds(letters, 0, letters.length(), lettersBounds);
        float factor = hypotenuse(lettersBounds.width() / 2, lettersBounds.height() / 2) / lettersPaint.getTextSize();
        lettersPaint.setTextSize(r / factor * reduce);
        lettersPaint.getTextBounds(letters, 0, letters.length(), lettersBounds);

        canvas.drawText(letters, cx - lettersBounds.exactCenterX(), cy - lettersBounds.exactCenterY(), lettersPaint);
    }
}
