package com.georgeneokq.engine.animation;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.georgeneokq.engine.factory.DrawableFactory;

public class FadeOutTransition implements TransitionRenderer {

    private boolean running;
    private float timeElapsed = 0;
    private float totalTransitionTime;
    private float timeBeforeTransition;
    private Stage stage;

    private Table overlay;
    private Drawable background;
    private OnTransitionFinishListener onFinishListener;
    private boolean removeOverlayAfterFinish;

    public FadeOutTransition(Stage stage, float fadeTime, float timeBeforeTransition,
                             OnTransitionFinishListener onFinishListener) {
        this(stage, fadeTime, timeBeforeTransition, onFinishListener, true);
    }

    public FadeOutTransition(Stage stage, float fadeTime, float timeBeforeTransition,
                             OnTransitionFinishListener onFinishListener, boolean removeOverlayAfterFinish) {
        this.stage = stage;
        this.timeBeforeTransition = timeBeforeTransition;
        this.onFinishListener = onFinishListener;
        this.overlay = new Table();
        this.overlay.setFillParent(true);
        totalTransitionTime = fadeTime;
        this.removeOverlayAfterFinish = removeOverlayAfterFinish;
    }

    /**
     * Get delta by time elapsed
     */
    private float getAlpha() {
        return timeElapsed / totalTransitionTime;
    }

    @Override
    public void render(float delta) {
        if(!running) return;

        timeElapsed += delta;

        if(timeElapsed < timeBeforeTransition)
            return;

        background = DrawableFactory.fromColor(new Color(0, 0, 0, getAlpha()));
        overlay.setBackground(background);

        if(timeElapsed > totalTransitionTime) {
            stop();
        }
    }

    @Override
    public void start() {
        // Attach an overlay to the provided stage
        background = DrawableFactory.fromColor(new Color(0, 0, 0, 0));
        overlay.setBackground(background);
        stage.addActor(overlay);
        running = true;
    }

    @Override
    public void stop() {
        if(removeOverlayAfterFinish)
            removeOverlay();

        timeElapsed = 0;
        totalTransitionTime = 0;
        running = false;

        if(onFinishListener != null)
            onFinishListener.onFinish(this);
    }

    /**
     * Removes the overlay used for the animation
     */
    public void removeOverlay() {
        if(overlay != null)
            overlay.remove();
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    public float getTimeElapsed() {
        return timeElapsed;
    }
}
