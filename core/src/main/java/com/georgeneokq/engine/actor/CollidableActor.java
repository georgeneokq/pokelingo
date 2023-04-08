package com.georgeneokq.engine.actor;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;

public class CollidableActor extends Actor implements Collidable {

    public interface OnCollisionListener {
        void onCollision(CollidableActor actor);
    }

    protected OnCollisionListener onCollisionListener = null;

    public CollidableActor(Rectangle bounds) {
        this(bounds.width, bounds.height, bounds.x, bounds.y);
    }

    public CollidableActor(float width, float height, float x, float y) {
        super(width, height, x, y, 0, 0, false, null);
    }

    public CollidableActor(float width, float height, float x, float y,
                           float speedX, float speedY, boolean allowDiagonalMovement, Controls controls) {
        super(width, height, x, y, speedX, speedY, allowDiagonalMovement, controls);
    }

    public CollidableActor(float width, float height, float x, float y,
                           float speedX, float speedY, boolean allowDiagonalMovement, Controls controls,
                           OnCollisionListener onCollisionListener) {
        super(width, height, x, y, speedX, speedY, allowDiagonalMovement, controls);
        this.onCollisionListener = onCollisionListener;
    }

    public CollidableActor(float width, float height, float x, float y, float speedX, float speedY,
                 boolean allowDiagonalMovement, Controls controls, Sprite sprite) {
        super(width, height, x, y, speedX, speedY, allowDiagonalMovement, controls, sprite);
    }


    public CollidableActor(float width, float height, float x, float y, float speedX, float speedY,
                 boolean allowDiagonalMovement, Controls controls, Sprite sprite,
                 OnCollisionListener onCollisionListener) {
        super(width, height, x, y, speedX, speedY, allowDiagonalMovement, controls, sprite);
        this.onCollisionListener = onCollisionListener;
    }

    public CollidableActor(float width, float height, float x, float y,
                           float speedX, float speedY, boolean allowDiagonalMovement,
                           Controls controls, TextureAtlas animationTextureAtlas) {
        super(width, height, x, y, speedX, speedY, allowDiagonalMovement, controls, animationTextureAtlas);
    }

    public CollidableActor(float width, float height, float x, float y,
                           float speedX, float speedY, boolean allowDiagonalMovement,
                           Controls controls, TextureAtlas animationTextureAtlas,
                           OnCollisionListener onCollisionListener) {
        super(width, height, x, y, speedX, speedY, allowDiagonalMovement, controls, animationTextureAtlas);
        this.onCollisionListener = onCollisionListener;
    }

    @Override
    public boolean collidesWith(Collidable collidable) {
        Rectangle collidableForecastedBounds = this.getForecastedBounds();
        Rectangle otherCollidableBounds = collidable.getForecastedBounds();
        return collidableForecastedBounds.overlaps(otherCollidableBounds);
    }

    /*
     * For subclasses to decide behaviour.
     * We provide two ways to specify collision behaviour:
     * Through the constructor by passing in an optional OnCollisionListener,
     * or overriding the onCollision method directly
     */
    @Override
    public void onCollision(Collidable collidable) {
        if(onCollisionListener != null)
            onCollisionListener.onCollision(this);
    }

    public Rectangle getForecastedBounds() {
        return new Rectangle(x + dx, y + dy, width, height);
    }
}
