package com.georgeneokq.game.actor;

import com.badlogic.gdx.math.Rectangle;
import com.georgeneokq.engine.actor.Direction;

public class SpawnPoint {
    private Rectangle bounds;
    private Direction direction;

    public SpawnPoint(Rectangle bounds, Direction direction) {
        this.bounds = bounds;
        this.direction = direction;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void setBounds(Rectangle bounds) {
        this.bounds = bounds;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }
}
