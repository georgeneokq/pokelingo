package com.georgeneokq.game.map;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.georgeneokq.engine.actor.CollidableActor;
import com.georgeneokq.engine.actor.Direction;
import com.georgeneokq.engine.actor.InteractableActor;
import com.georgeneokq.game.actor.Entry;
import com.georgeneokq.game.actor.InteractableObject;
import com.georgeneokq.game.actor.SpawnPoint;
import com.georgeneokq.engine.stage.ExtendedStage;
import com.georgeneokq.game.Globals;
import com.georgeneokq.game.manager.MusicManager;
import com.georgeneokq.engine.map.MapUtil;

import java.util.HashMap;
import java.util.Map;

public class MapController {

    private Map<String, MapStateController> mapStateControllerMap;

    private String mapName;
    private TiledMap map;
    private MapProperties props;
    private OrthogonalTiledMapRenderer renderer;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private ExtendedStage stage;

    private Globals globals;
    private MusicManager musicManager;
    private AssetManager assetManager;

    // Pixels
    private int tileWidth;
    private int tileHeight;

    public MapController(String mapName, TiledMap map, ExtendedStage stage) {
        this.mapName = mapName;
        this.map = map;
        this.stage = stage;
        this.camera = (OrthographicCamera) stage.getCamera();
        this.musicManager = MusicManager.getInstance();
        globals = Globals.getInstance();
        this.assetManager = globals.getAssetManager();
        batch = (SpriteBatch) stage.getBatch();
        mapStateControllerMap = new HashMap<>();
        renderer = new OrthogonalTiledMapRenderer(map, batch);
        renderer.setView(camera);

        props = map.getProperties();
        tileWidth = props.get("tilewidth", Integer.class);
        tileHeight = props.get("tileheight", Integer.class);

        renderInteractables();
        renderCollidables();
        renderEntryPoints();
        setBGM();
        updateMapState(globals.getMapState(mapName));
    }

    public void renderInteractables() {
        MapLayer interactableLayer = map.getLayers().get("interactables");

        if(interactableLayer == null)
            return;

        MapObjects mapObjects = interactableLayer.getObjects();

        for(MapObject mapObject : mapObjects) {
            MapProperties properties = mapObject.getProperties();
            RectangleMapObject interactableObject = (RectangleMapObject) mapObject;
            Rectangle interactableObjectRectangle = interactableObject.getRectangle();

            InteractableActor interactableActor;

            if(properties.containsKey("npc_name")) {
                String npcName = properties.get("npc_name").toString();
                Direction direction = properties.get("direction") != null ?
                        Direction.valueOf(properties.get("direction").toString()) : Direction.DOWN;
                interactableActor = globals.getNPC(npcName);
                interactableActor.setDirection(direction);
                interactableActor.setX(interactableObjectRectangle.getX());
                interactableActor.setY(interactableObjectRectangle.getY());
                if(interactableActor.getWidth() == 0) {
                    interactableActor.setWidth(interactableObjectRectangle.getWidth());
                }
                if(interactableActor.getHeight() == 0) {
                    interactableActor.setHeight(interactableObjectRectangle.getHeight());
                }
            } else {
                interactableActor = new InteractableObject(interactableObjectRectangle, mapObject.getName());
            }
            stage.addActors(interactableActor);
        }
    }

    public void renderCollidables() {
        MapLayer collidableLayer = map.getLayers().get("collidables");

        if(collidableLayer == null)
            return;

        MapObjects mapObjects = collidableLayer.getObjects();

        for(MapObject mapObject : mapObjects) {
            RectangleMapObject entryObject = (RectangleMapObject) mapObject;
            Rectangle entryObjectRectangle = entryObject.getRectangle();
            CollidableActor collidable = new CollidableActor(entryObjectRectangle);
            stage.addActors(collidable);
        }
    }

    public void renderEntryPoints() {
        MapLayer entryLayer = map.getLayers().get("entry");

        if(entryLayer == null)
            return;

        MapObjects mapObjects = entryLayer.getObjects();

        for(MapObject mapObject : mapObjects) {
            MapProperties properties = mapObject.getProperties();
            if(!properties.containsKey("map") ||
                !properties.containsKey("spawn_at_object") ||
                !properties.containsKey("direction"))
                continue;
            RectangleMapObject entryObject = (RectangleMapObject) mapObject;
            Rectangle entryObjectRectangle = entryObject.getRectangle();

            // Get destination map name and spawn point name
            String mapName = properties.get("map").toString();
            String spawnPointName = properties.get("spawn_at_object").toString();
            Direction direction = Direction.valueOf(properties.get("direction").toString());

            Entry entry = new Entry(entryObjectRectangle, direction, mapName, spawnPointName, this);
            stage.addActors(entry);
        }
    }

    /**
     * Get bounds of a specified spawn point in the current map.
     * @param spawnPointName Name of the spawn point
     * @return Bounds of the spawn point
     */
    public SpawnPoint getSpawnPoint(String spawnPointName) {
        MapLayer layer = map.getLayers().get("spawn");

        if(layer == null)
            return null;

        MapObjects mapObjects = layer.getObjects();

        for(MapObject mapObject : mapObjects) {
            MapProperties properties = mapObject.getProperties();
            RectangleMapObject spawnPoint = (RectangleMapObject) mapObject;
            String name = spawnPoint.getName();
            if(name.equals(spawnPointName)) {
                Direction direction = Direction.valueOf(properties.get("direction").toString());
                return new SpawnPoint(spawnPoint.getRectangle(), direction);
            }
        }

        return null;
    }

    public void renderVisibleLayers() {
        batch.begin();
        for (int i = 0; i < map.getLayers().getCount(); i++) {
            MapLayer layer = map.getLayers().get(i);
            if (layer instanceof TiledMapTileLayer && !layer.getName().equals("collidables")) {
                renderer.renderTileLayer((TiledMapTileLayer) layer);
            }
        }
        batch.end();
    }

    public void setMapStateControllers(Map<String, MapStateController> mapStateControllerMap) {
        this.mapStateControllerMap = mapStateControllerMap;
    }

    public void updateMapState(int state) {
        // Invokes the map state controller for the current map
        MapStateController mapStateController = mapStateControllerMap.get(mapName);
        if(mapStateController == null)
            return;

        mapStateController.updateByState(state, stage.getAllActors());
    }

    public void render() {
        renderer.render();
    }

    public void dispose() {
        renderer.dispose();
        map.dispose();
    }

    public void setOrthoCameraViewport(int viewportWidth, int viewportHeight) {
        if(viewportWidth > getMapWidth()) {
            viewportWidth = getMapWidth();
        }
        if(viewportHeight > getMapHeight()) {
            viewportHeight = getMapHeight();
        }
        camera.setToOrtho(false, viewportWidth, viewportHeight);
        camera.update();
    }

    public void setCameraPosition(float x, float y) {
        float halfViewportWidth = camera.viewportWidth / 2;
        float halfViewportHeight = camera.viewportHeight / 2;

        // Handle x-axis
        if (x - halfViewportWidth < 0) {
            camera.position.x = halfViewportWidth;
        }
        else if (x + halfViewportWidth > getMapWidth()) {
            camera.position.x = getMapWidth() - halfViewportWidth;
        }
        else {
            camera.position.x = x;
        }

        // Handle y-axis
        if (y - halfViewportHeight < 0) {
            camera.position.y = halfViewportHeight;
        }
        else if (y + halfViewportHeight > getMapHeight()) {
            camera.position.y = getMapHeight() - halfViewportHeight;
        }
        else {
            camera.position.y = y;
        }

        // Finally update camera
        camera.update();
    }

    public String getWhiteoutMap() {
        return map.getProperties().get("whiteout_map", String.class);
    }

    public SpawnPoint getWhiteoutSpawnPoint() {
        return getSpawnPoint(map.getProperties().get("whiteout_spawn", String.class));
    }

    public void setView(OrthographicCamera camera) {
        renderer.setView(camera);
    }

    public int getMapWidth() {
        return MapUtil.getMapWidth(map);
    }

    public int getMapHeight() {
        return MapUtil.getMapHeight(map);
    }

    public TiledMap getMap() {
        return map;
    }

    public void setBGM() {
        String musicName = map.getProperties().get("music", String.class);
        if (musicName != null){
            String musicFilePath = String.format("audio/bgm/%s", musicName);
            musicManager.playMusic(musicFilePath);
        }
    }

    public String getBGMName() {
        String musicName = map.getProperties().get("music", String.class);
        return String.format("audio/bgm/%s", musicName);
    }

    public void setMap(TiledMap map) {
        this.map = map;
    }
}


