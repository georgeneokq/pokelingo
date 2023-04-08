package com.georgeneokq.game.tests.save;

import static com.georgeneokq.engine.save.GameSaver.NEW_SAVE;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.georgeneokq.game.GdxTestRunner;
import com.georgeneokq.engine.save.GameSaver;

import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

@RunWith(GdxTestRunner.class)
public class GameSaverTest {
    @Test
    public void saveAndLoadGame() {
        // Create a score
        int score = 100;

        // Store car positions into a vector (test object serialization)
        Vector2 position = new Vector2(100, 0);

        // Prepare a mapping of objects to be saved
        Map<String, Object> saveObjects = new HashMap<>();
        saveObjects.put("position", position);
        saveObjects.put("score", score);

        // Register classes with kryo
        Map<String, Class> nameClassMapping = new HashMap<>();
        nameClassMapping.put("position", Vector2.class);
        nameClassMapping.put("score", int.class);

        String savesPath = "src/test/resources/tmp/test/saves";

        GameSaver gameSaver = new GameSaver(savesPath, nameClassMapping);

        // Save the object
        int saveIdentifier = gameSaver.save(NEW_SAVE, saveObjects);

        // Load objects
        Map<String, Object> loadedObjects = gameSaver.load(saveIdentifier);
        Vector2 loadedPosition = (Vector2) loadedObjects.get("position");
        int loadedScore = (int) loadedObjects.get("score");
        assertEquals(loadedPosition.x, 100f, 0);
        assertEquals(loadedPosition.y, 0, 0);
        assertEquals(loadedScore, 100);

        // Ensure timestamp is generated
        String _timestamp = (String) loadedObjects.getOrDefault("_timestamp", null);
        assertNotNull(_timestamp);
        Gdx.app.log("GameSaverTest.saveAndLoadGame", String.format("Game saved at %s", _timestamp));

        // Tests passed, delete generated files
        FileHandle saveFile = Gdx.files.local(String.format("%s/%s", savesPath,
                gameSaver.getFileNameByIdentifier(saveIdentifier)));

        // If the file can't be deleted, that means that either
        // Input or Output object is not closed
        boolean deletedSaveFile = saveFile.delete();
        assertTrue(deletedSaveFile);
    }

    @Test
    public void loadAllSaveData() {
        // Register classes with kryo
        Map<String, Class> nameClassMapping = new HashMap<>();
        nameClassMapping.put("position", Vector2.class);
        nameClassMapping.put("score", int.class);

        String savesPath = "src/test/resources/tmp/loadAllSaveData";

        GameSaver gameSaver = new GameSaver(savesPath, nameClassMapping);

        // Create 3 saves
        int numSaves = 3;
        Array<Integer> identifiers = new Array<>();
        for(int i = 0; i < numSaves; i++) {
            Vector2 vector2 = new Vector2(100 * (i + 1), 0);
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
        assertEquals(100, ((Vector2) firstSave.get("position")).x, 0);
        assertEquals(100 * numSaves, ((Vector2) lastSave.get("position")).x, 0);
        assertEquals(identifiers.get(0).intValue(), ((Integer) firstSave.get("_id")).intValue());
        assertEquals(identifiers.get(numSaves - 1).intValue(), ((Integer) lastSave.get("_id")).intValue());


        // Cleanup
        for(int i = 0; i < numSaves; i++) {
            String saveFileName = gameSaver.getFileNameByIdentifier(identifiers.get(i));
            String savePath = String.format("%s/%s", savesPath, saveFileName);
            FileHandle saveFile = Gdx.files.local(savePath);
            boolean deleted = saveFile.delete();
            assertTrue(deleted);
        }
    }

}
