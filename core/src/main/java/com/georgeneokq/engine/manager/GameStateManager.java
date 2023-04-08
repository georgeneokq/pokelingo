package com.georgeneokq.engine.manager;

import static com.georgeneokq.engine.manager.GameState.*;

import com.georgeneokq.game.event.Events;

public class GameStateManager implements EventManager.Subscriber {
    private GameState state;

    private static GameStateManager gameStateManager = null;

    private GameStateManager() {
        this.state = STOPPED;

        EventManager.getInstance().subscribe(this, new String[] {
                Events.PAUSED.name(),
                Events.RESUMED.name()
        });
    }

    public static GameStateManager getInstance() {
        if(gameStateManager == null)
            gameStateManager = new GameStateManager();
        return gameStateManager;
    }

    public void togglePlayPauseState() {
        if(state == PLAYING)
            state = PAUSED;
        else if(state == PAUSED)
            state = PLAYING;
    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
    }

    @Override
    public void eventReceived(String eventName, Object data) {
        togglePlayPauseState();
    }
}
