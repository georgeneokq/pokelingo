package com.georgeneokq.engine.animation;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.georgeneokq.engine.factory.DrawableFactory;

public class FadeInTransition implements TransitionRenderer {

    private boolean running;
    private float timeElapsed = 0;
    private float timeBeforeTransition;
    private float totalTransitionTime;
    private Stage stage;

    private Table overlay;
    private Drawable background;
    private OnTransitionFinishListener onFinishListener;

    public FadeInTransition(Stage stage, float fadeTime, float timeBeforeTransition,
                            OnTransitionFinishListener onFinishListener) {
        this.stage = stage;
        this.timeBeforeTransition = timeBeforeTransition;
        this.onFinishListener = onFinishListener;
        this.overlay = new Table();
        this.overlay.setFillParent(true);
        totalTransitionTime = fadeTime;
    }

    /**
     * Get delta by time elapsed
     */
    private float getAlpha() {
        return 1 - (timeElapsed / totalTransitionTime);
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
        background = DrawableFactory.fromColor(new Color(0, 0, 0, 1));
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
