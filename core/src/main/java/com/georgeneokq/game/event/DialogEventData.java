package com.georgeneokq.game.event;

import com.georgeneokq.engine.actor.Actor;
import com.georgeneokq.game.dialog.Dialog;

public class DialogEventData {
    private Dialog dialog;
    private Actor actor;
    private boolean startBattle;

    public DialogEventData(Dialog dialog, Actor actor) {
        this(dialog, actor, false);
    }

    public DialogEventData(Dialog dialog, Actor actor, boolean startBattle) {
        this.dialog = dialog;
        this.actor = actor;
        this.startBattle = startBattle;
    }

    public Dialog getDialog() {
        return dialog;
    }

    public Actor getActor() {
        return actor;
    }

    public boolean willStartBattle() {
        return startBattle;
    }
}
