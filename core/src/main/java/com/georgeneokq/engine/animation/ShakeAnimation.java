package com.georgeneokq.engine.animation;

import com.badlogic.gdx.scenes.scene2d.Actor;

public class ShakeAnimation implements AnimationRenderer {

    private Actor actor;
    private float elapsedTime;
    private float timeBeforeAnimation;
    private float totalAnimationTime;
    private float movementInterval = 0.1f;
    private float intervalTimer = 0;
    private float movePixels;
    private float originalX;
    private float originalY;
    private boolean animating = false;

    // -1 left, 1 right
    private float previousMovement = -1;

    public ShakeAnimation(Actor actor, float animationTime, float timeBeforeAnimation) {
        this(actor, animationTime, timeBeforeAnimation, 10);
    }

    public ShakeAnimation(Actor actor, float animationTime, float timeBeforeAnimation, float movePixels) {
        this.actor = actor;
        this.totalAnimationTime = animationTime;
        this.timeBeforeAnimation = timeBeforeAnimation;
        this.movePixels = movePixels;
    }

    @Override
    public void render(float delta) {
        if(!animating) return;

        elapsedTime += delta;

        if(elapsedTime < timeBeforeAnimation) {
            return;
        }

        if(elapsedTime > totalAnimationTime) {
            stop();
            return;
        }

        intervalTimer += delta;

        if(intervalTimer >= movementInterval) {
            intervalTimer = 0;
            if(actor.getX() > originalX) {
                actor.setX(originalX);
                previousMovement = 1;
            } else if(actor.getX() < originalX) {
                actor.setX(originalX);
                previousMovement = -1;
            } else {
                // Move based on previous movement
                if(previousMovement == -1) {
                    actor.setX(originalX + movePixels);
                } else {
                    actor.setX(originalX - movePixels);
                }
            }
        }
    }

    @Override
    public void start() {
        stop();
        originalX = actor.getX();
        originalY = actor.getY();
        elapsedTime = 0;
        intervalTimer = 0;
        animating = true;
    }

    @Override
    public void stop() {
        if(!animating) return;
        actor.setX(originalX);
        actor.setY(originalY);
        animating = false;
    }

    @Override
    public boolean isRunning() {
        return animating;
    }

    @Override
    public float getTimeElapsed() {
        return elapsedTime;
    }
}
