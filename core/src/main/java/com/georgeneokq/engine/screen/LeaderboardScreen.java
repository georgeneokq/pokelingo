package com.georgeneokq.engine.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.georgeneokq.game.Globals;
import com.georgeneokq.game.screen.MainMenuScreen;
import com.georgeneokq.game.skin.PokemonSkinGenerator;
import com.georgeneokq.engine.leaderboard.LeaderboardItem;
import com.georgeneokq.engine.manager.ScreensManager;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardScreen extends AbstractScreen {

    private Stage stage;
    private Globals globals;
    private ScreensManager screensManager;
    private AssetManager assetManager;
    private Skin skin;

    private String leaderboardFilePath;

    private final static String LEADERBOARD_FILE_PATH = "internal/leaderboard";

    private List<LeaderboardItem> leaderboardItems;

    public LeaderboardScreen() {
        this(LEADERBOARD_FILE_PATH);
    }

    public LeaderboardScreen(String leaderboardFilePath) {
        this.leaderboardFilePath = leaderboardFilePath;
    }

    @Override
    public void initialize() {
        globals = Globals.getInstance();
        screensManager = ScreensManager.getInstance();
        assetManager = globals.getAssetManager();
        Viewport viewport = new FitViewport(globals.resolutionWidth, globals.resolutionHeight);
        viewport.setScreenPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
        viewport.setScreenWidth(globals.resolutionWidth);
        viewport.setScreenHeight(globals.resolutionHeight);
        stage = new Stage(viewport);

        // Get skin
        skin = PokemonSkinGenerator.getInstance().generateSkin(assetManager);

        // Read the leaderboard file
        leaderboardItems = loadLeaderboard(leaderboardFilePath);

        // Fill the table with 10 top ranked names
        Table table = new Table(skin);

        // Table headers: rank, name, score, timestamp
        Label rankHeaderLabel = new Label("Rank", skin);
        Label nameHeaderLabel = new Label("Name", skin);
        Label scoreHeaderLabel = new Label("Score", skin);
        Label timestampHeaderLabel = new Label("Timestamp", skin);
        table.row().pad(25);
        table.add(rankHeaderLabel).expandX();
        table.add(nameHeaderLabel).expandX();
        table.add(scoreHeaderLabel).expandX();
        table.add(timestampHeaderLabel).expandX();

        for(int i = 0; i < leaderboardItems.size(); i++) {
            LeaderboardItem item = leaderboardItems.get(i);
            table.row().pad(25);

            Label rankLabel = new Label(String.valueOf(i + 1), skin);
            table.add(rankLabel).expandX();

            Label nameLabel = new Label(item.getName(), skin);
            table.add(nameLabel).expandX();

            Label scoreLabel = new Label(String.valueOf(item.getScore()), skin);
            table.add(scoreLabel).expandX();

            Label timestampLabel = new Label(item.getTimestamp(), skin);
            table.add(timestampLabel).expandX();
        }

        ScrollPane scrollPane = new ScrollPane(table);
        scrollPane.setFillParent(true);
        stage.addActor(scrollPane);
    }

    private List<LeaderboardItem> loadLeaderboard(String leaderboardFilePath) {
        FileHandle inputFile = Gdx.files.local(leaderboardFilePath);
        if(!inputFile.exists())
            return new ArrayList<>();

        Json json = new Json();
        List<LeaderboardItem> leaderboardItems = json.fromJson(ArrayList.class, inputFile);
        leaderboardItems.sort((leaderboardItem, leaderboardItem2) -> {
            if(leaderboardItem.getScore() < leaderboardItem2.getScore()) {
                return 1;
            } else if(leaderboardItem.getScore() > leaderboardItem2.getScore()) {
                return -1;
            } else {
                return 0;
            }
        });

        return leaderboardItems;
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(1, 1, 1, 1);

        handleInputs();

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    private void handleInputs() {
        if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            screensManager.changeScreen(MainMenuScreen.class);
        }
    }

    @Override
    public AbstractScreen clone() throws CloneNotSupportedException {
        return new LeaderboardScreen();
    }
}
