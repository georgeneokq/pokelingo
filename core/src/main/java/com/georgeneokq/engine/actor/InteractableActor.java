package com.georgeneokq.engine.actor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.georgeneokq.engine.manager.SettingsManager;
import com.georgeneokq.engine.settings.HotkeySetting;

import java.util.ArrayList;
import java.util.List;

public abstract class InteractableActor extends CollidableActor implements Interactable {

    protected boolean interacting = false;
    protected boolean interactionKeyPressed = false;

    public List<Actor> actorsInView = new ArrayList<>();

    public InteractableActor(Rectangle bounds) {
        super(bounds.width, bounds.height, bounds.x, bounds.y);
    }

    public InteractableActor(float width, float height, float x, float y) {
        super(width, height, x, y);
    }

    public InteractableActor(float width, float height, float x, float y, float speedX, float speedY, boolean allowDiagonalMovement, Controls controls) {
        super(width, height, x, y, speedX, speedY, allowDiagonalMovement, controls);
    }

    public InteractableActor(float width, float height, float x, float y, float speedX, float speedY, boolean allowDiagonalMovement, Controls controls, OnCollisionListener onCollisionListener) {
        super(width, height, x, y, speedX, speedY, allowDiagonalMovement, controls, onCollisionListener);
    }

    public InteractableActor(float width, float height, float x, float y, float speedX, float speedY, boolean allowDiagonalMovement, Controls controls, Sprite sprite) {
        super(width, height, x, y, speedX, speedY, allowDiagonalMovement, controls, sprite);
    }

    public InteractableActor(float width, float height, float x, float y, float speedX, float speedY, boolean allowDiagonalMovement, Controls controls, Sprite sprite, OnCollisionListener onCollisionListener) {
        super(width, height, x, y, speedX, speedY, allowDiagonalMovement, controls, sprite, onCollisionListener);
    }

    public InteractableActor(float width, float height, float x, float y, float speedX, float speedY, boolean allowDiagonalMovement, Controls controls, TextureAtlas animationTextureAtlas) {
        super(width, height, x, y, speedX, speedY, allowDiagonalMovement, controls, animationTextureAtlas);
    }

    public InteractableActor(float width, float height, float x, float y, float speedX, float speedY, boolean allowDiagonalMovement, Controls controls, TextureAtlas animationTextureAtlas, OnCollisionListener onCollisionListener) {
        super(width, height, x, y, speedX, speedY, allowDiagonalMovement, controls, animationTextureAtlas, onCollisionListener);
    }

    /*
     * Default field of view: 300 x 200 units.
     * Subclasses can override this behaviour
     */
    public Rectangle getFieldOfView() {
        float characterCenterX = x + width / 2f;
        float characterCenterY = y + height / 2f;
        float interactionFieldWidth = 300;
        float interactionFieldHeight = 200;
        float rectX = 0;
        float rectY = 0;
        float rectWidth = 0;
        float rectHeight = 0;

        // Decide rectangle dimensions based on the direction faced
        switch(direction) {
            case UP:
                rectX = characterCenterX - interactionFieldWidth / 2f;
                rectY = y + height;
                rectWidth = interactionFieldWidth;
                rectHeight = interactionFieldHeight;
                break;
            case LEFT:
                rectX = x - interactionFieldHeight;
                rectY = characterCenterY - interactionFieldWidth / 2f;
                rectWidth = interactionFieldHeight;
                rectHeight = interactionFieldWidth;
                break;
            case DOWN:
                rectX = characterCenterX - interactionFieldWidth / 2f;
                rectY = y - interactionFieldHeight;
                rectWidth = interactionFieldWidth;
                rectHeight = interactionFieldHeight;
                break;
            case RIGHT:
                rectX = x + width;
                rectY = characterCenterY - interactionFieldWidth / 2f;
                rectWidth = interactionFieldHeight;
                rectHeight = interactionFieldWidth;
                break;
            default:
                return new Rectangle();
        }

        return new Rectangle(rectX, rectY, rectWidth, rectHeight);
    }

    /*
     * Default field of interaction to be right in front of the player.
     * Takes into account the direction that the player is facing.
     * Subclasses can override this behaviour
     */
    @Override
    public Rectangle getFieldOfInteraction() {
        float characterCenterX = x + width / 2f;
        float characterCenterY = y + height / 2f;
        float interactionFieldWidth = width;
        float interactionFieldHeight;
        float rectX = 0;
        float rectY = 0;
        float rectWidth = 0;
        float rectHeight = 0;

        // Decide rectangle dimensions based on the direction faced
        switch(direction) {
            case UP:
                interactionFieldHeight = speedY;
                rectX = characterCenterX - interactionFieldWidth / 2f;
                rectY = y + height;
                rectWidth = interactionFieldWidth;
                rectHeight = interactionFieldHeight;
                break;
            case LEFT:
                interactionFieldHeight = speedX;
                rectX = x - interactionFieldHeight;
                rectY = characterCenterY - interactionFieldWidth / 2f;
                rectWidth = interactionFieldHeight;
                rectHeight = interactionFieldWidth;
                break;
            case DOWN:
                interactionFieldHeight = speedY;
                rectX = characterCenterX - interactionFieldWidth / 2f;
                rectY = y - interactionFieldHeight;
                rectWidth = interactionFieldWidth;
                rectHeight = interactionFieldHeight;
                break;
            case RIGHT:
                interactionFieldHeight = speedX;
                rectX = x + width;
                rectY = characterCenterY - interactionFieldWidth / 2f;
                rectWidth = interactionFieldHeight;
                rectHeight = interactionFieldWidth;
                break;
            default:
                return new Rectangle();
        }

        return new Rectangle(rectX, rectY, rectWidth, rectHeight);
    }

    @Override
    public void handleKeyPress() {
        super.handleKeyPress();
        SettingsManager settingsManager = SettingsManager.getInstance();
        int interactionKey =
                settingsManager.getSetting("player.controls.interaction_key", HotkeySetting.class).getValue();
        if(Gdx.input.isKeyJustPressed(interactionKey)) {
           interactionKeyPressed = true;
        }
    }

    @Override
    public void actorEnterView(Actor actor) {
        actorsInView.add(actor);
    }

    @Override
    public void actorInView(Actor actor) {}

    @Override
    public void actorLeaveView(Actor actor) {
        actorsInView.remove(actor);
    }

    @Override
    public void interact(Actor actor) {
        // Face the actor who triggered the interact
        direction = actor.getOppositeDirection();
    }

    public boolean isActorInView(Actor actor) {
        return actor.getBounds().overlaps(getFieldOfView());
    }

    public boolean isInteracting() {
        return interacting;
    }

    public void setInteracting() {
        this.interacting = interacting;
    }

    public boolean isInteractionKeyPressed() {
        return interactionKeyPressed;
    }

    public void setInteractionKeyPressed(boolean interactionKeyPressed) {
        this.interactionKeyPressed = interactionKeyPressed;
    }
}
