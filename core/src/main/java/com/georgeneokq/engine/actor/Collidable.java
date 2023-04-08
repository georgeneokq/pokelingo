package com.georgeneokq.engine.actor;


import com.badlogic.gdx.math.Rectangle;

public interface Collidable {
    boolean collidesWith(Collidable collided);
    void onCollision(Collidable collided);
    Rectangle getBounds();
    Rectangle getForecastedBounds();
}
