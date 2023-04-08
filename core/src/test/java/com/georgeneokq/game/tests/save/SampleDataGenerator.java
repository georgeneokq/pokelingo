package com.georgeneokq.game.tests.save;

import static com.georgeneokq.engine.save.GameSaver.NEW_SAVE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.georgeneokq.game.GdxTestRunner;
import com.georgeneokq.engine.save.GameSaver;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

@RunWith(GdxTestRunner.class)
public class SampleDataGenerator {
    @Test
    public void generate() {
        // Register classes with kryo
        Map<String, Class> nameClassMapping = new HashMap<>();
        nameClassMapping.put("position", Vector2.class);
        nameClassMapping.put("score", int.class);

        String savesPath = "src/test/resources/tmp/samples/saves";

        GameSaver gameSaver = new GameSaver(savesPath, nameClassMapping);

        // Create 3 saves
        int numSaves = 3;
        Array<Integer> identifiers = new Array<>();
        for(int i = 0; i < numSaves; i++) {
            Vector2 vector2 = new Vector2(200 * (i + 1), 200 * (i + 1));
            Map<String, Object> saveObjects = new HashMap<>();
            saveObjects.put("position", vector2);
            saveObjects.put("score", 20 * i);
            int saveIdentifier = gameSaver.save(NEW_SAVE, saveObjects);
            identifiers.add(saveIdentifier);
        }

        assertEquals(identifiers.size, numSaves);

        Map<String, Object>[] saveData = gameSaver.loadAll();
        assertEquals(numSaves, saveData.length);

        // Perform attribute checks on first and last element
        Map<String, Object> firstSave = saveData[0];
        Map<String, Object> lastSave = saveData[numSaves - 1];
        assertNotNull(firstSave.getOrDefault("position", null));
        assertNotNull(firstSave.getOrDefault("score", null));
        assertNotNull(firstSave.getOrDefault("_id", null));
        assertNotNull(firstSave.getOrDefault("_timestamp", null));
        assertNotNull(lastSave.getOrDefault("position", null));
        assertNotNull(lastSave.getOrDefault("score", null));
        assertNotNull(lastSave.getOrDefault("_id", null));
        assertNotNull(lastSave.getOrDefault("_timestamp", null));
        assertEquals(200, ((Vector2) firstSave.get("position")).x, 0);
        assertEquals(200 * numSaves, ((Vector2) lastSave.get("position")).x, 0);
        assertEquals(200, ((Vector2) firstSave.get("position")).y, 0);
        assertEquals(200 * numSaves, ((Vector2) lastSave.get("position")).y, 0);
        assertEquals(identifiers.get(0).intValue(), ((Integer) firstSave.get("_id")).intValue());
        assertEquals(identifiers.get(numSaves - 1).intValue(), ((Integer) lastSave.get("_id")).intValue());
    }
}
