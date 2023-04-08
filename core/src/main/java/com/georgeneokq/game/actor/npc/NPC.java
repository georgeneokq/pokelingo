package com.georgeneokq.game.actor.npc;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.georgeneokq.engine.actor.Actor;
import com.georgeneokq.engine.actor.InteractableActor;
import com.georgeneokq.game.event.Events;
import com.georgeneokq.game.Globals;
import com.georgeneokq.game.event.DialogEventData;
import com.georgeneokq.engine.manager.EventManager;

public class NPC extends InteractableActor implements EventManager.Subscriber {

    private EventManager eventManager;
    private Globals globals;
    private String name;

    public NPC(Rectangle bounds, String name, TextureAtlas animationTextureAtlas) {
        super(bounds.width, bounds.height, bounds.x, bounds.y, 1, 1,
                false, null, animationTextureAtlas);
        this.name = name;
        eventManager = EventManager.getInstance();
        globals = Globals.getInstance();
        eventManager.subscribe(this, new String[] {
                Events.DIALOG_ENDED.name()
        });
    }

    @Override
    public void interact(Actor actor) {
        if(isAnimationEnabled()) {
            super.interact(actor);
        }
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
