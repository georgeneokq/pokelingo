package com.georgeneokq.engine.manager;

import com.badlogic.gdx.Game;
import com.georgeneokq.game.Globals;
import com.georgeneokq.engine.screen.AbstractScreen;

import java.util.HashMap;

public class ScreensManager {

    private static class ScreenNotCachedException extends RuntimeException {
        public ScreenNotCachedException(String msg) {
            super(msg);
        }
    }

    private static ScreensManager screensManager;

    /* Cache original  */
    private HashMap<Class, AbstractScreen> classScreenMap = new HashMap<>();

    /* Keep track of current screen class */
    private AbstractScreen currentScreen = null;

    private Game game;

    private Globals globals;

    private ScreensManager() {
        globals = Globals.getInstance();
    }

    public static ScreensManager getInstance() {
        if(screensManager == null) {
            screensManager = new ScreensManager();
        }
        return screensManager;
    }

    public void initialize(Game game) {
        this.game = game;
    }

    public void notifyResolutionChanged() {
        for(AbstractScreen screen : classScreenMap.values()) {
            screen.onResolutionChanged(
                    globals.resolutionWidth, globals.resolutionHeight);
        }
    }

    /*
     * Cache screens in a HashMap.
     * The screens in the cache will never have its state altered.
     * This is done by returning a deep clone of the screen
     * in getScreen().
     * This method can also serves to invalidate the cache of screens
     * and replace them with a new instance.
     */
    public void cacheScreens(AbstractScreen ...screens) {
        // For every screen, check if there is a current cache entry for it.
        // If it does, dispose it before replacing
        for(AbstractScreen screen: screens) {
            Class<? extends AbstractScreen> screenClass = screen.getClass();
            AbstractScreen cachedScreen = classScreenMap.get(screenClass);
            if(cachedScreen != null)
                cachedScreen.dispose();
            classScreenMap.put(screen.getClass(), screen);
        }
    }

    /*
     * Returns a deep clone of the cached screen
     */
    public AbstractScreen getScreen(Class screenClass) {
        AbstractScreen cachedScreen = classScreenMap.get(screenClass);
        if(cachedScreen == null) {
            throw new ScreenNotCachedException(
                String.format("Screen for class %s is not registered using registerScreens()",
                        screenClass.getName())
            );
        }

        // Return a clone of the screen
        AbstractScreen clonedScreen = null;

        try {
            clonedScreen = cachedScreen.clone();
            clonedScreen.subscribeToEvents();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        return clonedScreen;
    }

    public void changeScreen(Class screenClass) {
        // Dispose the previous screen clone
        if(currentScreen != null) {
            currentScreen.unsubscribe();
            currentScreen.dispose();
        }

        currentScreen = getScreen(screenClass);
        currentScreen.initialize();

        game.setScreen(currentScreen);
    }

    public AbstractScreen getCurrentScreen() {
        return currentScreen;
    }

    public void setCurrentScreen(AbstractScreen screen) {
        this.currentScreen = screen;
    }

    public Game getGame() {
        return game;
    }
}
