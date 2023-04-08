package com.georgeneokq.game.actor;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.georgeneokq.game.Globals;
import com.georgeneokq.engine.actor.Actor;
import com.georgeneokq.game.dialog.Dialog;
import com.georgeneokq.game.event.Events;
import com.georgeneokq.game.event.EntryEventData;
import com.georgeneokq.game.event.SelectionRequestEventData;
import com.georgeneokq.game.event.SelectionResponseEventData;
import com.georgeneokq.engine.manager.EventManager;
import com.georgeneokq.game.map.MapController;
import com.georgeneokq.game.actor.npc.NPC;


public class InteractiveEntry extends NPC implements EventManager.Subscriber {

    private String mapName;
    private String spawnPointName;
    private boolean enabled;
    private String dialogName;

    private MapController mapController;
    private Globals globals;
    private EventManager eventManager;
    private AssetManager assetManager;

    private String[] events = new String[] {
            Events.SELECTION_RESPONSE.name()
    };

    private boolean changeMap = false;

    public InteractiveEntry(Rectangle bounds, String mapName, String spawnPointName, boolean enabled,
                            String dialogName, MapController mapController) {
        super(bounds, "interactive_entry", null);
        this.mapName = mapName;
        this.spawnPointName = spawnPointName;
        this.enabled = enabled;
        this.dialogName = dialogName;
        this.mapController = mapController;
        this.globals = Globals.getInstance();
        this.eventManager = EventManager.getInstance();
        this.assetManager = globals.getAssetManager();
        eventManager.subscribe(this, events);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public void interact(Actor actor) {
        if(!(actor instanceof Player))
            return;

        if(enabled) {
            // Start interactive dialog
            Dialog dialog = globals.getDialog(dialogName);
            SelectionRequestEventData data = new SelectionRequestEventData(dialog, this);
            eventManager.emit(Events.SELECTION_REQUEST.name(), data);
        }
    }

    @Override
    public void eventReceived(String eventName, Object data) {
        if(eventName.equals(Events.SELECTION_RESPONSE.name())) {
            SelectionResponseEventData selectionResponse = (SelectionResponseEventData) data;
            if(selectionResponse.getRequestor() != this)
                return;

            String response = selectionResponse.getResponse();

            if(response.equalsIgnoreCase(globals.getString("yes"))) {
                changeMap = true;
            }
        }
    }

    @Override
    public void act(float delta) {
        if(changeMap)
            loadMap();
    }

    private void loadMap() {
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
