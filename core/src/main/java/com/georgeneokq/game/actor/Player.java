package com.georgeneokq.game.actor;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.georgeneokq.engine.actor.Actor;
import com.georgeneokq.engine.actor.Collidable;
import com.georgeneokq.engine.actor.Controls;
import com.georgeneokq.engine.actor.InteractableActor;
import com.georgeneokq.game.event.Events;
import com.georgeneokq.game.Globals;
import com.georgeneokq.engine.manager.EventManager;

public class Player extends InteractableActor implements EventManager.Subscriber {

    private Texture battleSprite;

    private Sound entrySound;

    private AssetManager assetManager;
    private EventManager eventManager;
    private String[] subscribedEvents = new String[] {
            Events.DIALOG_ENDED.name()
    };

    public Player(float width, float height, float x, float y, float speedX, float speedY,
                  boolean allowDiagonalMovement, Controls controls, TextureAtlas animationTextureAtlas,
                  Texture battleSprite) {
        super(width, height, x, y, speedX, speedY, allowDiagonalMovement, controls, animationTextureAtlas);

        this.battleSprite = battleSprite;
        assetManager = Globals.getInstance().getAssetManager();
        eventManager = EventManager.getInstance();
        eventManager.subscribe(this, subscribedEvents);

        entrySound = assetManager.get("audio/sound_effects/entry_exit.mp3");
    }

    @Override
    public void onCollision(Collidable collidable) {
        if(collidable instanceof Entry) {
            Entry entry = (Entry) collidable;

            // Only pass through the door if facing the correct direction
            if(entry.getDirection() == this.direction) {
                entrySound.play();
                entry.loadMap();
            }
            return;
        }
        dx = 0;
        dy = 0;
    }

    @Override
    public void interact(Actor actor) {
    }

    @Override
    public void eventReceived(String eventName, Object data) {
        if(eventName.equals(Events.DIALOG_ENDED.name())) {
            interacting = false;
        }
    }

    public int getMaxHP() {
        return 100;
    }

    public Texture getBattleSprite() {
        return battleSprite;
    }

    public void setBattleSprite(Texture battleSprite) {
        this.battleSprite = battleSprite;
    }
}
