package com.georgeneokq.engine.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.georgeneokq.engine.actor.Actor;
import com.georgeneokq.engine.actor.CollidableActor;
import com.georgeneokq.engine.actor.Interactable;
import com.georgeneokq.engine.actor.InteractableActor;
import com.georgeneokq.engine.actor.NonCollidableActor;
import com.georgeneokq.game.event.Events;
import com.georgeneokq.engine.manager.EventManager;
import com.georgeneokq.engine.manager.GameState;
import com.georgeneokq.engine.manager.GameStateManager;
import com.georgeneokq.engine.manager.SettingsManager;
import com.georgeneokq.engine.settings.HotkeySetting;

/*
 * Provides custom methods to manage actor behaviour according to game state
 * and provides an interface to handle collisions.
 * Also in charge of emitting PAUSED and RESUMED events
 */
public class ExtendedStage extends Stage {
    private Array<CollidableActor> collidableActors = new Array<>();
    private Array<NonCollidableActor> nonCollidableActors = new Array<>();
    private Array<InteractableActor> interactableActors = new Array<>();
    private SettingsManager settingsManager;
    private GameStateManager gameStateManager;
    private EventManager eventManager;

    private int pauseGameHotkey;

    private boolean inputsEnabled = true;

    public ExtendedStage(Viewport viewport) {
        super(viewport);
        settingsManager = SettingsManager.getInstance();
        gameStateManager = GameStateManager.getInstance();
        eventManager = EventManager.getInstance();

        pauseGameHotkey = settingsManager.getSetting("general.pause_key", HotkeySetting.class).getValue();
    }

    public void addActors(Actor ...actors) {
        for(Actor actor : actors) {
            this.addActor(actor);
            if(actor instanceof NonCollidableActor) {
                nonCollidableActors.add((NonCollidableActor) actor);
            } else if(actor instanceof Interactable) {
                interactableActors.add((InteractableActor) actor);
            } else if(actor instanceof CollidableActor) {
                collidableActors.add((CollidableActor) actor);
            }
        }
    }

    // Get all Actor objects, regardless of collidable or non-collidable
    public Array<Actor> getAllActors() {
        Array<Actor> actors = new Array<>();
        actors.addAll(collidableActors);
        actors.addAll(nonCollidableActors);
        actors.addAll(interactableActors);
        return actors;
    }

    private void reportCollisions() {
        Array<CollidableActor> allCollidableActors = new Array<>();
        allCollidableActors.addAll(collidableActors);
        allCollidableActors.addAll(interactableActors);
        for (int i = 0; i < allCollidableActors.size; i++) {
            CollidableActor collidableActor = allCollidableActors.get(i);
            for (CollidableActor otherCollidableActor : allCollidableActors) {
                if (otherCollidableActor == collidableActor)
                    continue;

                // Check collision. Currently assumes that every actor's hit box is a rectangle
                boolean colliding = collidableActor.collidesWith(otherCollidableActor);
                if (colliding)
                    collidableActor.onCollision(otherCollidableActor);
            }
        }
    }

    /*
     * Checks if any actors are going out of bounds
     */
    private void reportOutOfBounds() {
        for (Actor actor : getAllActors()) {
            if (actor.goingOutOfBounds(0, 0, getViewport().getWorldWidth(),
                    getViewport().getWorldHeight()))
                actor.outOfWorldBounds();
        }
    }

    /*
     * Checks and reports if an actor has another actor in its field of view
     */
    public void reportFieldOfView() {
        Array<Actor> actors = getAllActors();

        for (int i = 0; i < actors.size; i++) {
            Actor actor = actors.get(i);

            if(!(actor instanceof Interactable))
                continue;

            InteractableActor interactableActor = (InteractableActor) actor;

            for (Actor otherActor : actors) {
                if (actor == otherActor)
                    continue;

                // Was already in vision
                if(interactableActor.isActorInView(otherActor) &&
                    interactableActor.actorsInView.contains(otherActor)) {

                    interactableActor.actorInView(otherActor);

                } else if(interactableActor.isActorInView(otherActor) &&
                        !interactableActor.actorsInView.contains(otherActor)) {

                    interactableActor.actorEnterView(otherActor);

                } else if(!interactableActor.isActorInView(otherActor) &&
                        interactableActor.actorsInView.contains(otherActor)) {

                    interactableActor.actorLeaveView(otherActor);

                }
            }
        }
    }

    public void updateAndDraw(float delta) {
        if(!inputsEnabled) {
            act(delta);
            draw();
            return;
        }

        handleKeyPress();

        if (gameStateManager.getState() == GameState.PAUSED) {
            draw();
            return;
        }

        /*
         * Handle key press behavior
         */
        for(Actor actor : getAllActors()) {
            actor.handleKeyPress();

            if(actor instanceof InteractableActor) {
                InteractableActor interactableActor = (InteractableActor) actor;
                if(interactableActor.isInteractionKeyPressed()) {
                    // Find the first other interactable actor in the current
                    // interactable actor's field of interaction.
                    // If found, call its interact() method with the other interactable
                    // as a parameter for both sides
                    for(InteractableActor otherInteractableActor : interactableActors) {
                        if(interactableActor.equals(otherInteractableActor))
                            continue;

                        if(interactableActor.getFieldOfInteraction()
                                .overlaps(otherInteractableActor.getBounds()) &&
                            !interactableActor.isInteracting() &&
                            !otherInteractableActor.isInteracting()) {
                            interactableActor.interact(otherInteractableActor);
                            otherInteractableActor.interact(interactableActor);
                        }
                    }
                    interactableActor.setInteractionKeyPressed(false);
                }
            }
        }

        // Handle collisions next. Collisions may overwrite the speed of an actor
        reportCollisions();

        // Handle actors going out of bounds
        reportOutOfBounds();

        // Handle field of view
        reportFieldOfView();

        // Draw updated actors
        act(delta);
        draw();
    }

    // Emit events
    private void handleKeyPress() {
        if(Gdx.input.isKeyJustPressed(pauseGameHotkey)) {
            GameState gameState = gameStateManager.getState();

            if(gameState == GameState.PLAYING)
                eventManager.emit(Events.PAUSED.name());
            else if(gameState == GameState.PAUSED)
                eventManager.emit(Events.RESUMED.name());
        }
    }

    public void pauseActors() {
        // Call onPause on all actors
        for (Actor actor : getAllActors()) {
            actor.onPause();
        }
    }

    public void resumeActors() {
        // Call onResume on all actors
        for (Actor actor : getAllActors()) {
            actor.onResume();
        }
    }

    public boolean isInputsEnabled() {
        return inputsEnabled;
    }

    public void setInputsEnabled(boolean inputsEnabled) {
        this.inputsEnabled = inputsEnabled;
    }
}
