package com.georgeneokq.engine.actor;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class NonCollidableActorAI extends NonCollidableActor {

    private Rectangle worldBounds;

    public NonCollidableActorAI(float width, float height, float x, float y,
                             float speedX, float speedY, Rectangle worldBounds) {
        super(width, height, x, y, speedX, speedY, false, null);
        this.worldBounds = worldBounds;
    }

    public NonCollidableActorAI(float width, float height, float x, float y,
                             float speedX, float speedY, Rectangle worldBounds, Sprite sprite) {
        super(width, height, x, y, speedX, speedY, false, null, sprite);
        this.worldBounds = worldBounds;
    }

    public NonCollidableActorAI(float width, float height, float x, float y,
                             float speedX, float speedY, Rectangle worldBounds,
                             TextureAtlas animationTextureAtlas) {
        super(width, height, x, y, speedX, speedY, false, null, animationTextureAtlas);
        this.worldBounds = worldBounds;
    }

    private Random random = new Random();

    // Seconds
    private float timeSinceMovementChanged = 0;
    private float minimumMovementTime = 1;

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
        // Move in a random direction for a minimum period of time or
        // until it is about to collide into something
        timeSinceMovementChanged += delta;

        boolean shouldChangeMovement = (
                timeSinceMovementChanged > minimumMovementTime &&
                        random.nextFloat() > 0.4
        );

        if(shouldChangeMovement) {
            if(moving) {
                stopMovement();
            } else {
                moving = true;
                int movementIndex = getRandomMovementIndex();
                previousMovementRunnableIndex = movementIndex;
                currentMovementRunnable = movementRunnables[movementIndex];
            }
            timeSinceMovementChanged = 0;
        }

        // Perform movement
        if(currentMovementRunnable != null)
            currentMovementRunnable.run();

        // Force stop movement if out of world bounds
        if(goingOutOfBounds(worldBounds.x, worldBounds.y, worldBounds.width, worldBounds.height)) {
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
        timeSinceMovementChanged = 0;
        idle();
    }
}
