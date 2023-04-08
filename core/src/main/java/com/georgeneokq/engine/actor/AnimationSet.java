package com.georgeneokq.engine.actor;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashMap;
import java.util.Map;

public class AnimationSet {

    private Map<Direction, Animation> walking;
    private Map<Direction, Animation> running;
    private Map<Direction, TextureRegion> standing;
    private Map<Direction, Animation> biking;

    public AnimationSet(Animation walkNorth,
                        Animation walkSouth,
                        Animation walkEast,
                        Animation walkWest,
                        TextureRegion standNorth,
                        TextureRegion standSouth,
                        TextureRegion standEast,
                        TextureRegion standWest) {
        walking = new HashMap<Direction, Animation>();
        walking.put(Direction.UP, walkNorth);
        walking.put(Direction.DOWN, walkSouth);
        walking.put(Direction.RIGHT, walkEast);
        walking.put(Direction.LEFT, walkWest);
        standing = new HashMap<Direction, TextureRegion>();
        standing.put(Direction.UP, standNorth);
        standing.put(Direction.DOWN, standSouth);
        standing.put(Direction.RIGHT, standEast);
        standing.put(Direction.LEFT, standWest);
    }

    public void addBiking(Animation bikeNorth, Animation bikeSouth, Animation bikeEast, Animation bikeWest) {
        biking = new HashMap<Direction, Animation>();
        biking.put(Direction.UP, bikeNorth);
        biking.put(Direction.DOWN, bikeSouth);
        biking.put(Direction.RIGHT, bikeEast);
        biking.put(Direction.LEFT, bikeWest);
    }

    public void addRunning(Animation runNorth, Animation runSouth, Animation runEast, Animation runWest) {
        running = new HashMap<Direction, Animation>();
        running.put(Direction.UP, runNorth);
        running.put(Direction.DOWN, runSouth);
        running.put(Direction.RIGHT, runEast);
        running.put(Direction.LEFT, runWest);
    }

    public Animation getBiking(Direction dir) {
        return biking.get(dir);
    }

    public Animation getRunning(Direction dir) {
        return running.get(dir);
    }

    public Animation getWalking(Direction dir) {
        return walking.get(dir);
    }

    public TextureRegion getStanding(Direction dir) {
        return standing.get(dir);
    }

}

