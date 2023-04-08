package com.georgeneokq.game.mapstate;

import com.badlogic.gdx.utils.Array;
import com.georgeneokq.engine.actor.Actor;
import com.georgeneokq.game.actor.InteractiveEntry;
import com.georgeneokq.game.map.MapStateController;

public class PetalburgRoomController extends MapStateController {

    /*
     * 1: Undefeated
     * 2: Defeated
     */

    @Override
    public void updateByState(int state, Array<Actor> actors) {
        if(state == 2) {
            enableEntries(actors);
        }
    }

    private void enableEntries(Array<Actor> actors) {
        for(Actor actor : actors) {
            if(actor instanceof InteractiveEntry) {
                ((InteractiveEntry) actor).setEnabled(true);
            }
        }
    }
}
