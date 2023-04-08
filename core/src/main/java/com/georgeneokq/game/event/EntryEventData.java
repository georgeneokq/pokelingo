package com.georgeneokq.game.event;

import com.georgeneokq.game.actor.SpawnPoint;

public class EntryEventData {
    private String mapName;
    private SpawnPoint spawnPoint;

    public EntryEventData(String mapName, SpawnPoint spawnPoint) {
        this.mapName = mapName;
        this.spawnPoint = spawnPoint;
    }

    public String getMapName() {
        return mapName;
    }

    public SpawnPoint getSpawnPoint() {
        return spawnPoint;
    }
}
