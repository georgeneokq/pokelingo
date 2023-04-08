package com.georgeneokq.engine.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.georgeneokq.game.Globals;
import com.georgeneokq.game.screen.MainMenuScreen;
import com.georgeneokq.game.skin.PokemonSkinGenerator;
import com.georgeneokq.engine.manager.ScreensManager;
import com.georgeneokq.engine.manager.SettingsManager;
import com.georgeneokq.engine.save.GameSaver;
import com.georgeneokq.engine.save.LoadRequestReceiver;
import com.georgeneokq.engine.save.LoadingNotSupportedException;

import java.util.Map;

public class SelectSaveScreen extends AbstractScreen {

    private ScreensManager screensManager;
    private SettingsManager settingsManager;
    private AssetManager assetManager;
    private Globals globals;

    private GameSaver gameSaver;
    private Class<? extends AbstractScreen> handleSaveDataScreenClass;

    private Stage stage;

    Skin skin;
    Drawable itemBackground;

    public SelectSaveScreen(Class<? extends AbstractScreen> handleSaveDataScreenClass) {
        this.handleSaveDataScreenClass = handleSaveDataScreenClass;
    }

    @Override
    public void initialize() {
        globals = Globals.getInstance();
        this.screensManager = ScreensManager.getInstance();
        this.settingsManager = SettingsManager.getInstance();
        this.assetManager = globals.getAssetManager();

        globals = Globals.getInstance();
        gameSaver = globals.getGameSaver();

        stage = new Stage(new StretchViewport(globals.resolutionWidth, globals.resolutionHeight));

        // Background for each save item
        skin = PokemonSkinGenerator.getInstance().generateSkin(assetManager);
        itemBackground = new NinePatchDrawable(skin.getPatch("dialoguebox"));

        buildStage();
    }

    private void buildStage() {
        Table table = new Table();

        table.setDebug(false);
        stage.addActor(table);

        // Empty array for testing
        Map<String, Object>[] saves = gameSaver.loadAll();

        if(saves.length == 0) {
            table.row();
            Label label = new Label("Nothing to load. Start a new game!", skin);
            label.setAlignment(Align.center);
            table.add(label);
        }

        // Lay out maximum of 3 boxes per row
        int maxCols = 3;
        float spaceX = 20;
        float spaceY = 20;
        float containerWidth = globals.resolutionWidth / maxCols - spaceX;
        float containerHeight = 400;

        for(int row = 1; row <= saves.length; row++) {
            table.row().padTop(spaceY / 2).padBottom(spaceY / 2);

            int numContainersPlaced = (row - 1) * maxCols;
            int numCols = maxCols * row > saves.length ?
                    saves.length - numContainersPlaced : maxCols;

            for(int col = 1; col <= numCols; col++) {
                int currentContainerIndex = numContainersPlaced + col - 1;
                Map<String, Object> saveData = saves[currentContainerIndex];

                // Create the container, which will handle clicks
                Container container = new Container();
                container.setFillParent(true);

                // TODO: Fix background being colorless / always black
                container.setBackground(itemBackground);

                // Create the label
                Label label = new Label((String) saveData.get("_timestamp"), skin);
                label.setAlignment(Align.center);

                // Stack the widgets
                Stack stack = new Stack(container, label);
                stack.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        screensManager.changeScreen(handleSaveDataScreenClass);
                        AbstractScreen screen = screensManager.getCurrentScreen();
                        try {
                            int saveIdentifier = (int) saves[currentContainerIndex].get("_id");
                            LoadRequestReceiver loadRequestReceiver = (LoadRequestReceiver) screen;
                            loadRequestReceiver.onLoadRequestReceived(saveIdentifier);
                        } catch(ClassCastException e) {
                            throw new LoadingNotSupportedException("Your screen class must implement the LoadRequestReceiver interface!");
                        }
                    };
                });
                table.add(stack).expandX().width(containerWidth).height(containerHeight);
            }
        }
        // Finished attaching to table, initialize scrollpane with table
        ScrollPane scrollPane = new ScrollPane(table);
        scrollPane.setFillParent(true);
        scrollPane.setScrollingDisabled(true, false);
        stage.addActor(scrollPane);
        stage.setScrollFocus(scrollPane);
    }

    private void handleInputs() {
        if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            screensManager.changeScreen(MainMenuScreen.class);
        }
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(new Color(0x40F5BC00));

        handleInputs();

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public AbstractScreen clone() {
        return new SelectSaveScreen(handleSaveDataScreenClass);
    }
}
