package com.gianlu.commonutils.tutorial;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.Toolbar;

import me.toptas.fancyshowcase.FancyShowCaseQueue;
import me.toptas.fancyshowcase.FancyShowCaseView;
import me.toptas.fancyshowcase.FocusShape;

public abstract class BaseTutorial {
    public final TutorialManager.Discovery discovery;
    private FancyShowCaseQueue queue;
    private Activity activity;

    public BaseTutorial(@NonNull TutorialManager.Discovery discovery) {
        this.discovery = discovery;
    }

    public final void newSequence(@NonNull Activity activity) {
        this.activity = activity;
        this.queue = new FancyShowCaseQueue();
    }

    public final void show(@NonNull Listener listener) {
        if (queue == null) return;

        queue.setCompleteListener(() -> listener.onComplete(this));
        queue.show();
    }

    @NonNull
    public final FancyShowCaseView.Builder forToolbarMenuItem(@NonNull Toolbar toolbar, @IdRes int menuItemId, @StringRes int title) {
        return new FancyShowCaseView.Builder(activity)
                .title(activity.getString(title))
                .focusOn(toolbar.findViewById(menuItemId));
    }

    @NonNull
    public final FancyShowCaseView.Builder forBounds(@NonNull Rect rect, @StringRes int title) {
        return new FancyShowCaseView.Builder(activity)
                .focusRectAtPosition(rect.left, rect.top, rect.width(), rect.height())
                .title(activity.getString(title));
    }

    @NonNull
    public FancyShowCaseView.Builder forView(@NonNull View view, @StringRes int title) {
        return new FancyShowCaseView.Builder(activity)
                .focusOn(view)
                .focusShape(FocusShape.ROUNDED_RECTANGLE)
                .title(activity.getString(title));
    }

    public void add(@NonNull FancyShowCaseView.Builder builder) {
        queue.add(builder.build());
    }

    public interface Listener {
        void onComplete(@NonNull BaseTutorial tutorial);
    }
}
