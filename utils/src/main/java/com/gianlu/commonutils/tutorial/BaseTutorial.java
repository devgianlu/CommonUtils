package com.gianlu.commonutils.tutorial;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.Toolbar;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;

public abstract class BaseTutorial {
    public final TutorialManager.Discovery discovery;
    private TapTargetSequence sequence;
    private Context context;

    public BaseTutorial(@NonNull TutorialManager.Discovery discovery) {
        this.discovery = discovery;
    }

    public final void newSequence(@NonNull Activity activity) {
        sequence = new TapTargetSequence(activity);
        context = activity;
    }

    public final void show(@NonNull Listener listener) {
        if (sequence != null)
            sequence.continueOnCancel(true)
                    .listener(new ListenerWrapper(listener))
                    .start();
    }

    @NonNull
    public final TapTarget forToolbarMenuItem(@NonNull Toolbar toolbar, @IdRes int menuItemId, @StringRes int title, @StringRes int description) {
        return prepareAndAdd(TapTarget.forToolbarMenuItem(toolbar, menuItemId, context.getString(title), context.getString(description)));
    }

    @NonNull
    public final TapTarget forBounds(@NonNull Rect rect, @StringRes int title, @StringRes int description) {
        return prepareAndAdd(TapTarget.forBounds(rect, context.getString(title), context.getString(description)));
    }

    @NonNull
    public TapTarget forView(@NonNull View view, @StringRes int title, @StringRes int description) {
        return prepareAndAdd(TapTarget.forView(view, context.getString(title), context.getString(description)));
    }

    @NonNull
    private TapTarget prepareAndAdd(@NonNull TapTarget target) {
        sequence.target(target);

        if (context != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(new int[]{android.R.attr.textColorPrimary, android.R.attr.textColorSecondary, android.R.attr.colorBackground});
            try {
                target.titleTextColorInt(a.getColor(0, Color.WHITE));
                target.targetCircleColorInt(a.getColor(0, Color.WHITE));
                target.descriptionTextColorInt(a.getColor(1, Color.WHITE));
                target.outerCircleColorInt(a.getColor(2, Color.BLACK));
            } finally {
                a.recycle();
            }
        }

        return target;
    }

    public interface Listener {
        void onSequenceFinish(@NonNull BaseTutorial tutorial);

        void onSequenceStep(@NonNull BaseTutorial tutorial, @NonNull TapTarget lastTarget, boolean targetClicked);

        void onSequenceCanceled(@NonNull BaseTutorial tutorial, @NonNull TapTarget lastTarget);
    }

    private class ListenerWrapper implements TapTargetSequence.Listener {
        private final Listener listener;

        ListenerWrapper(@NonNull Listener listener) {
            this.listener = listener;
        }

        @Override
        public void onSequenceFinish() {
            listener.onSequenceFinish(BaseTutorial.this);
        }

        @Override
        public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {
            listener.onSequenceStep(BaseTutorial.this, lastTarget, targetClicked);
        }

        @Override
        public void onSequenceCanceled(TapTarget lastTarget) {
            listener.onSequenceCanceled(BaseTutorial.this, lastTarget);
        }
    }
}
