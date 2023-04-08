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
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.georgeneokq.game.Globals;
import com.georgeneokq.game.screen.MainMenuScreen;
import com.georgeneokq.game.skin.PokemonSkinGenerator;
import com.georgeneokq.engine.manager.ScreensManager;

public class InstructionScreen extends AbstractScreen {
    private Stage stage;
    private Skin skin;
    private String instructions;
    private Label instructionLabel;
    private ScrollPane scrollPane;

    private AssetManager assetManager;
    private ScreensManager screensManager;
    private Globals globals;

    public InstructionScreen() {
    }

    @Override
    public void initialize() {

        globals = Globals.getInstance();
        assetManager = globals.getAssetManager();
        screensManager = ScreensManager.getInstance();
        stage = new Stage(new FitViewport(globals.resolutionWidth, globals.resolutionHeight));
        skin = PokemonSkinGenerator.getInstance().generateSkin(assetManager);

        // Read instructions file
        FileHandle handle = Gdx.files.local("instructions.txt");
        instructions = handle.readString();

        buildStage();
    }

    private void buildStage() {
        // Create instruction text
        instructionLabel = new Label(instructions, skin);
        instructionLabel.setWrap(true);

        // Add instruction text to table
        Table table = new Table();
        table.add(instructionLabel).fill().expand().pad(50);

        // Attach table to scroll pane
        scrollPane = new ScrollPane(table);
        scrollPane.setScrollBarPositions(false, true);
        scrollPane.setFillParent(true);
        stage.addActor(scrollPane);
        stage.setScrollFocus(scrollPane);
    }

    private void handleKeyPressed() {
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)){
            screensManager.changeScreen(MainMenuScreen.class);
        }
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.57f, 0.77f, 0.85f, 1);

        handleKeyPressed();

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void dispose() { }

    @Override
    public AbstractScreen clone() throws CloneNotSupportedException {
        return new InstructionScreen();
    }
}
