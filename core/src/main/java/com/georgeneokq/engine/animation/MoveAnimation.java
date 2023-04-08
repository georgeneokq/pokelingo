package com.georgeneokq.engine.animation;

import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Simple animation to move from one point to another
 */
public class MoveAnimation implements AnimationRenderer {

    private Actor actor;
    private float originalX;
    private float originalY;
    private float currentX;
    private float currentY;
    private float targetX;
    private float targetY;

    private float elapsedTime;
    private float totalAnimationTime;
    private float timeBeforeAnimation;
    private OnAnimationFinishListener animationFinishListener;
    private boolean animating = false;
    private boolean move;

    public MoveAnimation() {
    }

    public MoveAnimation(Actor actor, float targetX, float targetY, float animationTime,
                         float timeBeforeAnimation, OnAnimationFinishListener animationFinishListener) {
        this(actor, targetX, targetY, animationTime, timeBeforeAnimation, animationFinishListener, true);
    }

    /**
     *
     * @param actor Actor to animate
     * @param targetX Destination X-coordinate
     * @param targetY Destination Y-coordinate
     * @param animationTime Total animation time
     * @param timeBeforeAnimation Time before animation
     * @param animationFinishListener Callback to be called upon animation finish
     * @param move Specify whether to actually move the actor.
     *             Can be set to false to use this class as a utility to retrieve
     *             coordinates for an animation (e.g. when drawing using a batch)
     */
    public MoveAnimation(Actor actor, float targetX, float targetY, float animationTime,
                         float timeBeforeAnimation, OnAnimationFinishListener animationFinishListener,
                         boolean move) {
        this.actor = actor;
        this.targetX = targetX;
        this.targetY = targetY;
        this.totalAnimationTime = animationTime;
        this.timeBeforeAnimation = timeBeforeAnimation;
        this.animationFinishListener = animationFinishListener;
        this.move = move;
    }

    @Override
    public void render(float delta) {
        if (!animating) return;

        elapsedTime += delta;

        if(timeBeforeAnimation != 0) {
            if (elapsedTime < timeBeforeAnimation) {
                return;
            } else {
                timeBeforeAnimation = 0;
            }
        }

        if (elapsedTime >= totalAnimationTime) {
            if(move) {
                actor.setX(targetX);
                actor.setY(targetY);
            }
            stop();
            if(animationFinishListener != null) {
                animationFinishListener.onFinish(this);
            }
            return;
        }

        float ratio = elapsedTime / totalAnimationTime;
        float diffX = targetX - actor.getX();
        float diffY = targetY - actor.getY();

        currentX = originalX + ratio * diffX;
        currentY = originalY + ratio * diffY;

        if(move) {
            actor.setX(currentX);
            actor.setY(currentY);
        }
    }

    public float getCurrentX() {
        return currentX;
    }

    public float getCurrentY() {
        return currentY;
    }

    @Override
    public void start() {
        originalX = actor.getX();
        originalY = actor.getY();
        animating = true;
    }

    @Override
    public void stop() {
        animating = false;
    }

    @Override
    public boolean isRunning() {
        return animating;
    }

    @Override
    public float getTimeElapsed() {
        if(timeBeforeAnimation != 0) return 0;
        return elapsedTime;
    }
}
