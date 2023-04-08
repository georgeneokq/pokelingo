package com.georgeneokq.engine.animation;

public interface TransitionRenderer {
    void render(float delta);
    void start();
    void stop();
    boolean isRunning();
    float getTimeElapsed();
}
