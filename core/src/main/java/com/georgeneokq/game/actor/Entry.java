package com.georgeneokq.game.actor;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.georgeneokq.engine.actor.CollidableActor;
import com.georgeneokq.engine.actor.Direction;
import com.georgeneokq.game.event.EntryEventData;
import com.georgeneokq.game.Globals;
import com.georgeneokq.game.event.Events;
import com.georgeneokq.engine.manager.EventManager;
import com.georgeneokq.game.map.MapController;

public class Entry extends CollidableActor {

    private MapController mapController;
    private AssetManager assetManager;
    private EventManager eventManager;

    private String mapName;
    private String spawnPointName;

    public Entry(Rectangle bounds, Direction direction, String mapName, String spawnPointName, MapController mapController) {
        super(bounds.width, bounds.height, bounds.x, bounds.y);
        this.mapName = mapName;
        this.direction = direction;
        this.spawnPointName = spawnPointName;
        this.mapController = mapController;
        this.assetManager = Globals.getInstance().getAssetManager();
        this.eventManager = EventManager.getInstance();
    }

    /*
     * Upon colliding with the door, set the new map
     */
    public void loadMap() {
        TiledMap map = loadMap(mapName);
        mapController.setMap(map);
        SpawnPoint spawnPoint = mapController.getSpawnPoint(spawnPointName);
        EntryEventData data = new EntryEventData(mapName, spawnPoint);
        eventManager.emit(Events.MAP_CHANGED.name(), data);
    }

    private TiledMap loadMap(String mapName) {
        String mapAssetsPath = String.format("game/maps/%s.tmx", mapName);
        return assetManager.get(mapAssetsPath, TiledMap.class);
    }
}
