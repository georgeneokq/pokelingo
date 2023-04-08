package com.georgeneokq.game.actor;

import com.badlogic.gdx.math.Rectangle;
import com.georgeneokq.engine.actor.Actor;
import com.georgeneokq.engine.actor.InteractableActor;
import com.georgeneokq.game.Globals;
import com.georgeneokq.game.event.Events;
import com.georgeneokq.game.event.DialogEventData;
import com.georgeneokq.engine.manager.EventManager;

public class InteractableObject extends InteractableActor implements EventManager.Subscriber {

    private EventManager eventManager;
    private Globals globals;
    private String name;

    public InteractableObject(Rectangle bounds, String name) {
        super(bounds);
        this.name = name;
        eventManager = EventManager.getInstance();
        globals = Globals.getInstance();
        eventManager.subscribe(this, new String[] {
               Events.DIALOG_ENDED.name()
        });
    }

    @Override
    public void interact(Actor actor) {
        interacting = true;
        String stringName = String.format("interactable_%s", name);
        DialogEventData data = new DialogEventData(globals.getDialog(stringName), this);
        eventManager.emit(Events.DIALOG_STARTED.name(), data);
    }

    @Override
    public void eventReceived(String eventName, Object data) {
        if(eventName.equals(Events.DIALOG_ENDED.name())) {
            interacting = false;
        }
    }
}
