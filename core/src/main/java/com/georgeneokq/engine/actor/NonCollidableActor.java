package com.georgeneokq.engine.actor;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

/*
 * Not a single bit different from its parent class, simply serves as a "marker class"
 * to mark the actor as a non-collidable.
 */
public class NonCollidableActor extends Actor {

    public NonCollidableActor(float width, float height, float x, float y) {
        super(width, height, x, y, 0, 0, false, null);
    }

    public NonCollidableActor(float width, float height, float x, float y, float speedX, float speedY, boolean allowDiagonalMovement, Controls controls) {
        super(width, height, x, y, speedX, speedY, allowDiagonalMovement, controls);
    }

    public NonCollidableActor(float width, float height, float x, float y, float speedX, float speedY,
                           boolean allowDiagonalMovement, Controls controls, Sprite sprite) {
        super(width, height, x, y, speedX, speedY, allowDiagonalMovement, controls, sprite);
    }

    public NonCollidableActor(float width, float height, float x, float y, float speedX, float speedY, boolean allowDiagonalMovement, Controls controls, TextureAtlas animationTextureAtlas) {
        super(width, height, x, y, speedX, speedY, allowDiagonalMovement, controls, animationTextureAtlas);
    }
}
