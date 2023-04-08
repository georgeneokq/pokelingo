package com.georgeneokq.engine.animation;

import com.badlogic.gdx.scenes.scene2d.Stage;

public class Transitions {
    private TransitionRenderer transitionRenderer;
    private Stage stage;

    public Transitions(Stage stage) {
        this.stage = stage;
    }

    public void startFlashTransition(int numFlashes, float flashInterval, float timeBeforeTransition, OnTransitionFinishListener onFinishListener) {
        stopCurrentAnimation();
        transitionRenderer = new FlashTransition(stage, numFlashes, flashInterval, timeBeforeTransition, onFinishListener);
        transitionRenderer.start();
    }

    public void startFadeOutTransition(float fadeTime, float timeBeforeTransition, OnTransitionFinishListener onFinishListener) {
        startFadeOutTransition(fadeTime, timeBeforeTransition, onFinishListener, true);
    }

    public void startFadeOutTransition(float fadeTime, float timeBeforeTransition,
                                       OnTransitionFinishListener onFinishListener, boolean removeOverlay) {
        stopCurrentAnimation();
        transitionRenderer = new FadeOutTransition(stage, fadeTime, timeBeforeTransition, onFinishListener, removeOverlay);
        transitionRenderer.start();
    }

    public void startFadeInTransition(float fadeTime, float timeBeforeTransition, OnTransitionFinishListener onFinishListener) {
        stopCurrentAnimation();
        transitionRenderer = new FadeInTransition(stage, fadeTime, timeBeforeTransition, onFinishListener);
        transitionRenderer.start();
    }

    public void render(float delta) {
        if(transitionRenderer != null && transitionRenderer.isRunning())
            transitionRenderer.render(delta);
    }

    public boolean isRunning() {
        if(transitionRenderer != null)
            return transitionRenderer.isRunning();
        return false;
    }

    public void stopAnimation() {
        stopCurrentAnimation();
    }

    public float getTimeElapsed() {
        if(transitionRenderer != null)
            return transitionRenderer.getTimeElapsed();
        return 0;
    }

    private void stopCurrentAnimation() {
        if(transitionRenderer != null && transitionRenderer.isRunning()) {
            transitionRenderer.stop();
        }
    }
}
