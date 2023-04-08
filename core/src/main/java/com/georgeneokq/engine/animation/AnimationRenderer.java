package com.georgeneokq.engine.animation;

public interface AnimationRenderer {
    void render(float delta);
    void start();
    void stop();
    boolean isRunning();
    float getTimeElapsed();
}
