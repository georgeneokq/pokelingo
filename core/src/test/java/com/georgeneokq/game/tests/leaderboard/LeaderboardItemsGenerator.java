package com.georgeneokq.game.tests.leaderboard;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.georgeneokq.game.GdxTestRunner;
import com.georgeneokq.engine.leaderboard.LeaderboardItem;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(GdxTestRunner.class)
public class LeaderboardItemsGenerator {
    @Test
    public void generate() {
        LeaderboardItem[] leaderboardItemsArray = new LeaderboardItem[] {
                new LeaderboardItem("Player 1", 100, getCurrentTimestamp()),
                new LeaderboardItem("Player 2", 15, getCurrentTimestamp()),
                new LeaderboardItem("Player 3", 80, getCurrentTimestamp()),
                new LeaderboardItem("Player 4", 89, getCurrentTimestamp()),
                new LeaderboardItem("Player 5", 69, getCurrentTimestamp()),
                new LeaderboardItem("Player 6", 52, getCurrentTimestamp()),
                new LeaderboardItem("Player 7", 48, getCurrentTimestamp()),
                new LeaderboardItem("Player 8", 36, getCurrentTimestamp()),
                new LeaderboardItem("Player 9", 78, getCurrentTimestamp()),
                new LeaderboardItem("Player 10", 200, getCurrentTimestamp()),
        };

        String path = "src/test/resources/tmp/samples/leaderboard/leaderboard";

        FileHandle outputFile = Gdx.files.local(path);

        // Create arraylist for serialization
        List<LeaderboardItem> leaderboardItems = new ArrayList<>(Arrays.asList(leaderboardItemsArray));

        Json json = new Json();
        json.toJson(leaderboardItems, outputFile);
    }

    private String getCurrentTimestamp() {
        return new Timestamp(System.currentTimeMillis()).toString();
    }
}
