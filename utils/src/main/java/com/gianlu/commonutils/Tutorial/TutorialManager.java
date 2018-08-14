package com.gianlu.commonutils.Tutorial;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;

import com.getkeepsafe.taptargetview.TapTarget;
import com.gianlu.commonutils.Dialogs.DialogUtils;
import com.gianlu.commonutils.Preferences.Prefs;

import java.util.ArrayList;
import java.util.List;

public final class TutorialManager implements BaseTutorial.Listener {
    private final Listener listener;
    private final List<BaseTutorial> tutorials;
    private boolean isShowingTutorial = false;

    public TutorialManager(Listener listener, Discovery... discoveries) {
        this.listener = listener;

        if (discoveries.length == 0)
            throw new IllegalStateException("Then why are you initializing this...");

        try {
            tutorials = new ArrayList<>();
            for (Discovery discovery : discoveries)
                tutorials.add(discovery.tutorialClass().newInstance());
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new RuntimeException("Something is wrong with your tutorials!", ex);
        }
    }

    public static void restartTutorial() {
        Prefs.remove(Prefs.Keys.TUTORIAL_DISCOVERIES);
    }

    private boolean shouldShowFor(@NonNull BaseTutorial tutorial) {
        return !Prefs.setContains(Prefs.Keys.TUTORIAL_DISCOVERIES, tutorial.discovery.name());
    }

    private void setShown(@NonNull BaseTutorial tutorial) {
        Prefs.addToSet(Prefs.Keys.TUTORIAL_DISCOVERIES, tutorial.discovery.name());
    }

    @UiThread
    public void tryShowingTutorials(final Activity activity) {
        if (isShowingTutorial || !DialogUtils.isContextValid(activity)) return;

        for (BaseTutorial tutorial : tutorials) {
            if (shouldShowFor(tutorial) && listener.canShow(tutorial)) {
                show(activity, tutorial);
                return;
            }
        }
    }

    @UiThread
    private void show(@NonNull Activity activity, @NonNull final BaseTutorial tutorial) {
        tutorial.newSequence(activity);
        if (listener.buildSequence(tutorial)) {
            isShowingTutorial = true;
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tutorial.show(TutorialManager.this);
                }
            });
        }
    }

    @Override
    public void onSequenceFinish(@NonNull BaseTutorial tutorial) {
        isShowingTutorial = false;
        setShown(tutorial);
    }

    @Override
    public void onSequenceStep(@NonNull BaseTutorial tutorial, @NonNull TapTarget lastTarget, boolean targetClicked) {
    }

    @Override
    public void onSequenceCanceled(@NonNull BaseTutorial tutorial, @NonNull TapTarget lastTarget) {
    }

    public interface Discovery {
        @NonNull
        Class<? extends BaseTutorial> tutorialClass();

        @NonNull
        String name();
    }

    @UiThread
    public interface Listener {
        boolean canShow(@NonNull BaseTutorial tutorial);

        /**
         * @return Whether the action was successful
         */
        boolean buildSequence(@NonNull BaseTutorial tutorial);
    }
}
