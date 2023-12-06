package com.gianlu.commonutils.tutorial;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;

import com.gianlu.commonutils.dialogs.DialogUtils;
import com.gianlu.commonutils.preferences.CommonPK;
import com.gianlu.commonutils.preferences.Prefs;

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
        Prefs.remove(CommonPK.TUTORIAL_DISCOVERIES);
    }

    private boolean shouldShowFor(@NonNull BaseTutorial tutorial) {
        return !Prefs.setContains(CommonPK.TUTORIAL_DISCOVERIES, tutorial.discovery.name());
    }

    private void setShown(@NonNull BaseTutorial tutorial) {
        Prefs.addToSet(CommonPK.TUTORIAL_DISCOVERIES, tutorial.discovery.name());
    }

    @UiThread
    public boolean tryShowingTutorials(final Activity activity) {
        if (isShowingTutorial || !DialogUtils.isContextValid(activity)) return false;

        for (BaseTutorial tutorial : tutorials) {
            if (shouldShowFor(tutorial) && listener.canShow(tutorial))
                return show(activity, tutorial);
        }

        return false;
    }

    @UiThread
    private boolean show(@NonNull Activity activity, @NonNull final BaseTutorial tutorial) {
        tutorial.newSequence(activity);
        if (listener.buildSequence(tutorial)) {
            isShowingTutorial = true;
            activity.runOnUiThread(() -> tutorial.show(TutorialManager.this));
            return true;
        }

        return false;
    }

    @Override
    public void onComplete(@NonNull BaseTutorial tutorial) {
        isShowingTutorial = false;
        setShown(tutorial);
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
