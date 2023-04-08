package com.georgeneokq.engine.actor;

import com.badlogic.gdx.math.Rectangle;

public interface Interactable {
    Rectangle getFieldOfView();

    Rectangle getFieldOfInteraction();

    // When an actor first enters field of view
    void actorEnterView(Actor actor);

    // When an actor leaves field of view
    void actorLeaveView(Actor actor);

    // Whenever an actor is in the field of view
    void actorInView(Actor actor);

    // Invoke interaction
    void interact(Actor actor);
}
