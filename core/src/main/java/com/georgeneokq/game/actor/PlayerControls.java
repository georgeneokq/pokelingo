package com.georgeneokq.game.actor;

import com.georgeneokq.engine.actor.Controls;

public class PlayerControls extends Controls {

    private int interactionKey;

    public PlayerControls(int upKey, int downKey, int leftKey, int rightKey, int interactionKey) {
        super(upKey, downKey, leftKey, rightKey);
        this.interactionKey = interactionKey;
    }

    public int getInteractionKey() {
        return interactionKey;
    }

    public void setInteractionKey(int interactionKey) {
        this.interactionKey = interactionKey;
    }
}
