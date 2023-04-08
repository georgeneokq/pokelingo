package com.georgeneokq.engine.actor;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class CollidableActorAI extends CollidableActor {

    private Rectangle worldBounds;

    public CollidableActorAI(float width, float height, float x, float y,
                             float speedX, float speedY, Rectangle worldBounds) {
        super(width, height, x, y, speedX, speedY, false, null);
        this.worldBounds = worldBounds;
    }

    public CollidableActorAI(float width, float height, float x, float y,
                             float speedX, float speedY, Rectangle worldBounds,
                              OnCollisionListener onCollisionListener) {
        super(width, height, x, y, speedX, speedY, false, null, onCollisionListener);
        this.worldBounds = worldBounds;
    }

    public CollidableActorAI(float width, float height, float x, float y,
                             float speedX, float speedY, Rectangle worldBounds, Sprite sprite) {
        super(width, height, x, y, speedX, speedY, false, null, sprite);
        this.worldBounds = worldBounds;
    }

    public CollidableActorAI(float width, float height, float x, float y,
                             float speedX, float speedY, Rectangle worldBounds, Sprite sprite,
                             OnCollisionListener onCollisionListener) {
        super(width, height, x, y, speedX, speedY, false, null, sprite, onCollisionListener);
        this.worldBounds = worldBounds;
    }

    public CollidableActorAI(float width, float height, float x, float y,
                             float speedX, float speedY, Rectangle worldBounds,
                              TextureAtlas animationTextureAtlas) {
        super(width, height, x, y, speedX, speedY, false, null, animationTextureAtlas);
        this.worldBounds = worldBounds;
    }

    public CollidableActorAI(float width, float height, float x, float y,
                             float speedX, float speedY, Rectangle worldBounds,
                             TextureAtlas animationTextureAtlas,
                             OnCollisionListener onCollisionListener) {
        super(width, height, x, y, speedX, speedY, false, null, animationTextureAtlas, onCollisionListener);
        this.worldBounds = worldBounds;
    }

    private Random random = new Random();

    // Seconds
    private boolean autoWalking = true;
    private float timeSinceMovementStarted = 0;
    private float timeSinceMovementStopped = 0;
    private float minimumMovementTime = 1;
    private float minimumStopTime = 3;

    // Random action to be executed
    private Runnable currentMovementRunnable = null;
    private final Runnable[] movementRunnables = new Runnable[] {
            this::moveUp,
            this::moveRight,
            this::moveDown,
            this::moveLeft
    };

    // Cache the previous action performed to avoid repeating
    private int previousMovementRunnableIndex = -1;

    private boolean moving = false;

    // Override default behaviour
    @Override
    public void outOfWorldBounds() { }

    @Override
    public void act(float delta) {
        if(!autoWalking)
            return;

        // Move in a random direction for a minimum period of time,
        // stop for a minimum period of time
        if(moving) {
            timeSinceMovementStarted += delta;
        } else {
            timeSinceMovementStopped += delta;
        }

        boolean randomFactor = random.nextFloat() > 0.4;
        boolean shouldChangeMovement = (
                (timeSinceMovementStarted > minimumMovementTime ||
                        timeSinceMovementStopped > minimumStopTime) &&
                randomFactor
        );

        if(shouldChangeMovement) {
            if(moving) {
                stopMovement();
            } else {
                startMovement();
            }
        }

        // Perform movement
        if(currentMovementRunnable != null)
            currentMovementRunnable.run();

        // Force stop movement if out of world bounds
        if(currentMovementRunnable != null &&
                goingOutOfBounds(worldBounds.x, worldBounds.y, worldBounds.width, worldBounds.height)) {
            stopMovement();
            return;
        }

        super.act(delta);
    }

    private int getRandomMovementIndex() {
        if(movementRunnables.length == 1)
            return 0;

        int randomMovementIndex;
        do {
            randomMovementIndex = random.nextInt(movementRunnables.length);
        } while(randomMovementIndex == previousMovementRunnableIndex);
        return randomMovementIndex;
    }

    public void stopMovement() {
        currentMovementRunnable = null;
        moving = false;
        timeSinceMovementStopped = 0;
        timeSinceMovementStarted = 0;
        idle();
    }

    public void startMovement() {
        int movementIndex = getRandomMovementIndex();
        previousMovementRunnableIndex = movementIndex;
        currentMovementRunnable = movementRunnables[movementIndex];
        moving = true;
        timeSinceMovementStopped = 0;
        timeSinceMovementStarted = 0;
    }

    @Override
    public void onCollision(Collidable collidable) {
        // If onCollisionListener passed into parameter, it takes precedence over
        // the default behaviour
        if(onCollisionListener != null) {
            onCollisionListener.onCollision(this);
            return;
        }

        // Stop the movement of the AI upon colliding
        stopMovement();
    }

    public float getMinimumMovementTime() {
        return minimumMovementTime;
    }

    public void setMinimumMovementTime(float minimumMovementTime) {
        this.minimumMovementTime = minimumMovementTime;
    }

    public float getMinimumStopTime() {
        return minimumStopTime;
    }

    public void setMinimumStopTime(float minimumStopTime) {
        this.minimumStopTime = minimumStopTime;
    }

    public boolean isAutoWalking() {
        return autoWalking;
    }

    public void setAutoWalking(boolean autoWalking) {
        this.autoWalking = autoWalking;
    }
}
