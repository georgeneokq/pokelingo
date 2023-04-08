package com.georgeneokq.engine.animation;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.georgeneokq.engine.factory.DrawableFactory;

public class FlashTransition implements TransitionRenderer {

    private int numFlashes;
    private float flashInterval;
    private boolean running;
    private float timeBeforeTransition;
    private float timeElapsed = 0;
    private float totalTransitionTime;
    private boolean peaked = false;
    private Stage stage;

    private Table overlay;
    private Drawable background;
    private OnTransitionFinishListener onFinishListener;

    public FlashTransition(Stage stage, int numFlashes, float flashInterval, float timeBeforeTransition,
                           OnTransitionFinishListener onFinishListener) {
        this.stage = stage;
        this.numFlashes = numFlashes;
        this.flashInterval = flashInterval;
        this.timeBeforeTransition = timeBeforeTransition;
        this.onFinishListener = onFinishListener;
        this.overlay = new Table();
        this.overlay.setFillParent(true);
        totalTransitionTime = numFlashes * flashInterval;
    }

    /**
     * Get delta by time elapsed
     */
    private float getAlpha() {
        float currentFlashElapsedTime = timeElapsed % flashInterval;
        float halfFlashTime = flashInterval / 2;
        float alpha;

        // If current flash elapsed time is less than half of the total flash time,
        // alpha should increase towards 1. If not, decrease towards 0
        if(currentFlashElapsedTime <= halfFlashTime) {
            alpha = currentFlashElapsedTime / halfFlashTime;
            peaked = true;
        }
        else if(peaked) {
            alpha = 1;
            peaked = false;
        }
        else {
            try {
                // Might try to execute 1 divided by 0
                alpha = 1 / (currentFlashElapsedTime / halfFlashTime);
            } catch(ArithmeticException exception) {
                alpha = 0;
            }
        }

        return alpha;
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
        if(overlay != null)
            overlay.remove();

        timeElapsed = 0;
        totalTransitionTime = 0;
        running = false;
        if(onFinishListener != null)
            onFinishListener.onFinish(this);
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    public float getTimeElapsed() {
        return timeElapsed;
    }
}
