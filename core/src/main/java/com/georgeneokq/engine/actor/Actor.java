package com.georgeneokq.engine.actor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Actor extends com.badlogic.gdx.scenes.scene2d.Actor
        implements Cloneable {

    protected float width;
    protected float height;
    protected float x;
    protected float y;
    protected float dx = 0;
    protected float dy = 0;
    protected float speedX;
    protected float speedY;
    protected boolean allowDiagonalMovement;
    private Controls controls;

    // For default pause behaviour
    private float originalDx;
    private float originalDy;

    // Non-animated sprite optionally passed in through constructor
    private Sprite sprite;

    // Only if animationTextureAtlas was passed into constructor,
    // these variables for animation will be used
    private boolean animationEnabled = false;
    private TextureAtlas animationTextureAtlas;
    private AnimationSet animations;
    private boolean isWalking = false;
    private float walkTimer;
    protected Direction direction = Direction.DOWN;
    private List<Integer> pressedDirectionKeys = new ArrayList<>();
    private Map<Integer, Runnable> movementRunnables = new HashMap<>();

    public Actor(float width, float height, float x, float y, float speedX, float speedY,
                 boolean allowDiagonalMovement, Controls controls) {
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
        this.speedX = speedX;
        this.speedY = speedY;
        this.allowDiagonalMovement = allowDiagonalMovement;
        this.controls = controls;

        // Map control keys to their movement methods
        if(controls != null) {
            movementRunnables.put(controls.getUpKey(), this::moveUp);
            movementRunnables.put(controls.getLeftKey(), this::moveLeft);
            movementRunnables.put(controls.getDownKey(), this::moveDown);
            movementRunnables.put(controls.getRightKey(), this::moveRight);
        }
    }

    public Actor(float width, float height, float x, float y, float speedX, float speedY,
                 boolean allowDiagonalMovement, Controls controls,
                  Sprite sprite) {
        this(width, height, x, y, speedX, speedY, allowDiagonalMovement, controls);
        this.sprite = sprite;
    }

    public Actor(float width, float height, float x, float y, float speedX, float speedY,
                 boolean allowDiagonalMovement, Controls controls,
                  TextureAtlas animationTextureAtlas) {
        this(width, height, x, y, speedX, speedY, allowDiagonalMovement, controls);

        /* Animation-related */
        if(animationTextureAtlas == null)
            return;
        this.animationTextureAtlas = animationTextureAtlas;
        animations = new AnimationSet(
                new Animation(0.3f/2f, animationTextureAtlas.findRegions("move_north"), Animation.PlayMode.LOOP_PINGPONG),
                new Animation(0.3f/2f, animationTextureAtlas.findRegions("move_south"), Animation.PlayMode.LOOP_PINGPONG),
                new Animation(0.3f/2f, animationTextureAtlas.findRegions("move_east"), Animation.PlayMode.LOOP_PINGPONG),
                new Animation(0.3f/2f, animationTextureAtlas.findRegions("move_west"), Animation.PlayMode.LOOP_PINGPONG),
                animationTextureAtlas.findRegion("idle_north"),
                animationTextureAtlas.findRegion("idle_south"),
                animationTextureAtlas.findRegion("idle_east"),
                animationTextureAtlas.findRegion("idle_west")
        );

        // Facing down (front) by default
        this.direction = Direction.DOWN;
        this.animationEnabled = true;
    }

    /* Define default game pause/resume behaviour for actors */
    public void onPause() {
        // save the current velocity then set to 0
        originalDx = dx;
        originalDy = dy;
        dx = 0;
        dy = 0;
    }

    public void onResume() {
        // Restore the velocity before the pause
        dx = originalDx;
        dy = originalDy;
        originalDx = 0;
        originalDy = 0;
    }

    /* Animation related  */
    public TextureRegion getAnimationSprite() {
        if(!animationEnabled)
            return null;
        if (isWalking)
            return (TextureRegion) animations.getWalking(direction).getKeyFrame(walkTimer);
        return animations.getStanding(direction);
    }

    public AnimationSet getAnimations() {
        return animations;
    }

    public void setAnimations(AnimationSet animations) {
        this.animations = animations;
    }

    public float getWalkTimer() {
        return walkTimer;
    }

    public void setWalkTimer(float walkTimer) {
        this.walkTimer = walkTimer;
    }

    // If animation is enabled, handle drawing.
    // If not, the method must be overridden by child classes to draw
    @Override
    public void draw(Batch batch, float parentAlpha) {
        if(animationEnabled)
            batch.draw(this.getAnimationSprite(), x, y, width, height);
        else if(sprite != null)
            batch.draw(sprite, x, y, width, height);
    }

    public void animatedMoveUp() {
        if(!animationEnabled)
            return;

        isWalking = true;
        this.direction = Direction.UP;
        dy = speedY;
    }

    public void animatedMoveDown() {
        if(!animationEnabled)
            return;

        isWalking = true;
        this.direction = Direction.DOWN;
        dy = -speedY;
    }

    public void animatedMoveLeft() {
        if(!animationEnabled)
            return;

        isWalking = true;
        this.direction = Direction.LEFT;
        dx = -speedX;
    }

    public void animatedMoveRight() {
        if(!animationEnabled)
            return;

        isWalking = true;
        this.direction = Direction.RIGHT;
        dx = speedX;
    }

    public void idle() {
        // Add Standing status
        isWalking = false;
        dx = 0;
        dy = 0;
    }

    // If animation is enabled, the "animatedMove~" methods will take care of movement,
    // so do not force subclasses to implement these methods using abstract keyword.
    public void moveUp() {
        if(animationEnabled)
            animatedMoveUp();
        else
            dy = speedY;
    }

    public void moveDown() {
        if(animationEnabled)
            animatedMoveDown();
        else
            dy = -speedY;
    }

    public void moveLeft() {
        if(animationEnabled)
            animatedMoveLeft();
        else
            dx = -speedX;
    }

    public void moveRight() {
        if(animationEnabled)
            animatedMoveRight();
        else
            dx = speedX;
    }

    // Update state based on keys pressed
    public void handleKeyPress() {
        if(controls == null)
            return;

        for(Integer key : movementRunnables.keySet()) {
            if(Gdx.input.isKeyJustPressed(key)) {
                // If key was just pressed, append to the array
                pressedDirectionKeys.add(key);
            } else if(!Gdx.input.isKeyPressed(key)) {
                // If key isn't pressed and is the array, remove it
                pressedDirectionKeys.remove(key);
            }
        }

        dx = 0;
        dy = 0;

        if(pressedDirectionKeys.isEmpty()) {
            idle();
        } else {
            // Start processing the last element in the list (latest key)
            for(int i = pressedDirectionKeys.size() - 1; i >= 0; i--) {
                int key = pressedDirectionKeys.get(i);
                movementRunnables.get(key).run();

                if(!allowDiagonalMovement)
                    break;
            }
        }
    }

    // No controls of this entity being pressed
    public final boolean isIdle() {
        if(controls == null)
            return true;
        return (!Gdx.input.isKeyPressed(controls.getUpKey()) &&
                !Gdx.input.isKeyPressed(controls.getLeftKey()) &&
                !Gdx.input.isKeyPressed(controls.getDownKey()) &&
                !Gdx.input.isKeyPressed(controls.getRightKey()));
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    /*
     * Check whether actor is outside of the given boundary
     */
    public boolean goingOutOfBounds(float boundaryX, float boundaryY,
                                    float boundaryWidth, float boundaryHeight) {
        return (x + dx + width >= boundaryX + boundaryWidth ||
                x + dx < boundaryX ||
                y + dy + height >= boundaryY + boundaryHeight ||
                y + dy < boundaryY);
    }

    /*
     * Called when actor is moving out of bounds
     */
    public void outOfWorldBounds() {
        dx = 0;
        dy = 0;
    }

    // Called by Stage to update the actor's position
    @Override
    public void act(float delta) {
        if(animationEnabled)
            this.walkTimer += delta;
        x += dx;
        y += dy;
    }

    @Override
    public Actor clone() throws CloneNotSupportedException {
        return (Actor) super.clone();
    }

    /* Utility functions for subclasses to use */
    public void changeDxTowardsZero(float rateOfChange) {
        if(dx > 0) {
            float forecastedDx = dx - rateOfChange * Gdx.graphics.getDeltaTime();
            if(forecastedDx < 0)
                dx = 0;
            else
                dx = forecastedDx;
        }

        if(dx < 0) {
            float forecastedDx = dx + rateOfChange * Gdx.graphics.getDeltaTime();
            if(forecastedDx > 0)
                dx = 0;
            else
                dx = forecastedDx;
        }
    }

    public void changeDyTowardsZero(float rateOfChange) {
        if(dy > 0) {
            float forecastedDy = dy - rateOfChange * Gdx.graphics.getDeltaTime();
            if(forecastedDy < 0)
                dy = 0;
            else
                dy = forecastedDy;
        }
        if(dy < 0) {
            float forecastedDy = dy + rateOfChange * Gdx.graphics.getDeltaTime();
            if(forecastedDy > 0)
                dy = 0;
            else
                dy = forecastedDy;
        }
    }

    public Direction getDirection() {
        return direction;
    }

    public Direction getOppositeDirection() {
        switch(direction) {
            case UP:
                return Direction.DOWN;
            case LEFT:
                return Direction.RIGHT;
            case DOWN:
                return Direction.UP;
            case RIGHT:
                return Direction.LEFT;
        }
        return null;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getDx() {
        return dx;
    }

    public void setDx(float dx) {
        this.dx = dx;
    }

    public float getDy() {
        return dy;
    }

    public void setDy(float dy) {
        this.dy = dy;
    }

    public float getSpeedX() {
        return speedX;
    }

    public void setSpeedX(float speedX) {
        this.speedX = speedX;
    }

    public float getSpeedY() {
        return speedY;
    }

    public void setSpeedY(float speedX) {
        this.speedY = speedY;
    }

    public Controls getControls() {
        return controls;
    }

    public void setControls(Controls controls) {
        this.controls = controls;
    }

    public boolean isAllowDiagonalMovement() {
        return allowDiagonalMovement;
    }

    public void setAllowDiagonalMovement(boolean allowDiagonalMovement) {
        this.allowDiagonalMovement = allowDiagonalMovement;
    }

    public boolean isAnimationEnabled() {
        return animationEnabled;
    }
}
