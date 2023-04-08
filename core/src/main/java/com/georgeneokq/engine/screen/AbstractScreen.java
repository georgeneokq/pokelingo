package com.georgeneokq.engine.screen;

import com.badlogic.gdx.Screen;
import com.georgeneokq.game.Globals;
import com.georgeneokq.engine.manager.EventManager;

public abstract class AbstractScreen implements Screen, ResizableScreen, Cloneable, EventManager.Subscriber {

    public void subscribeToEvents() {
        EventManager.getInstance().subscribe(this, getSubscribedEvents());
    }

    // Called when changing screens.
    // A screen should override this method if it contains child components that are subscribers,
    // and unsubscribe them manually
    public void unsubscribe() {
        EventManager.getInstance().unsubscribe(this);
    }

    // To be overridden by subclass to return a list of event names to subscribe to
    public String[] getSubscribedEvents() { return new String[] {}; }

    // Convenience method to get string
    protected String getString(String stringName) {
        return Globals.getInstance().getString(stringName);
    }

    protected String getBattleMusicPath() {
        return Globals.getInstance().getBattleMusicPath();
    }

    protected int scaleFontSizeByResolution(int fontSize) {
        Globals globals = Globals.getInstance();
        float multiplier = 1;
        int resolutionWidth = globals.resolutionWidth;
        if(resolutionWidth == 1280) {
            multiplier = 0.8f;
        } else if(resolutionWidth == 1024) {
            multiplier = 0.6f;
        }
        return (int) (fontSize * multiplier);
    }

    @Override
    public void eventReceived(String eventName, Object data) { }

    @Override
    public void onResolutionChanged(int width, int height) {
        resize(width, height);
    }

    @Override
    public abstract AbstractScreen clone() throws CloneNotSupportedException;

    /**
     * To be called only when a clone is created by ScreenManager
     */
    public abstract void initialize();

    @Override
    public void show() { }

    @Override
    public void render(float delta) { }

    @Override
    public void pause() { }

    @Override
    public void resize(int width, int height) { }

    @Override
    public void resume() { }

    @Override
    public void hide() { }

    @Override
    public void dispose() { }
}
