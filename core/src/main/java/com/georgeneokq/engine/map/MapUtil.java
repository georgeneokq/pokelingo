package com.georgeneokq.engine.map;

import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.maps.MapProperties;

public class MapUtil {
    public static int getMapWidth(Map map) {
        return map.getProperties().get("width", Integer.class) * map.getProperties().get("tilewidth", Integer.class);
    }

    public static int getMapHeight(Map map) {
        return map.getProperties().get("height", Integer.class) * map.getProperties().get("tileheight", Integer.class);
    }

    public static int getTileWidth(Map map) {
        MapProperties props = map.getProperties();
        return props.get("tilewidth", Integer.class);
    }

    public static int getTileHeight(Map map) {
        MapProperties props = map.getProperties();
        return props.get("tileheight", Integer.class);
    }
}
