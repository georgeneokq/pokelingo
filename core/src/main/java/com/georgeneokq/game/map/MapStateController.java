package com.georgeneokq.game.map;

import com.badlogic.gdx.utils.Array;
import com.georgeneokq.engine.actor.Actor;

public abstract class MapStateController {
    public abstract void updateByState(int state, Array<Actor> actors);
}
