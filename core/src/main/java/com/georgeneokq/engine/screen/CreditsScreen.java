package com.georgeneokq.engine.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.georgeneokq.game.Globals;
import com.georgeneokq.game.screen.MainMenuScreen;
import com.georgeneokq.game.skin.PokemonSkinGenerator;
import com.georgeneokq.engine.manager.ScreensManager;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

public class CreditsScreen extends AbstractScreen {
    private Stage stage;
    private Skin skin;
    private Texture background;
    private Texture background2;
    private SpriteBatch batch;

    //Sets the speed of the background movement
    private float backgroundVelocity = 3;
    private float backgroundX = 0;
    float worldWidth = 960;
    float screenWidth;
    float screenHeight;
    float worldHeight = 540;
    private String creditsArray[];
    private Label creditsText;

    //Time for the action
    private int actionDuration = 50;

    private AssetManager assetManager;
    private ScreensManager screensManager;
    private Globals globals;

    public CreditsScreen() {
        globals = Globals.getInstance();
        screensManager = ScreensManager.getInstance();
        assetManager = globals.getAssetManager();
        batch = new SpriteBatch();
        stage = new Stage(new StretchViewport(worldWidth, worldHeight, new OrthographicCamera()));
        skin = PokemonSkinGenerator.getInstance().generateSkin(assetManager);
        background = assetManager.get("CreditsBackground.png", Texture.class);
        background2 = assetManager.get("CreditsBackground.png", Texture.class);
        screenHeight = globals.resolutionHeight;
        screenWidth = globals.resolutionWidth;
    }

    @Override
    public void initialize() {
        //Reading the credits.txt file
        FileHandle handle = Gdx.files.local("credits.txt");
        String text = handle.readString();
        creditsArray = text.split("\\r?\\n");

        buildStage();
    }

    private void moveScreen() {
        batch.draw(background, backgroundX, 0, screenWidth, screenHeight);
        batch.draw(background2, backgroundX + screenWidth, 0, screenWidth, screenHeight);
        backgroundX -= backgroundVelocity;
        if(backgroundX + screenWidth == 0){
            backgroundX = 0;
        }
    }

    private void buildStage() {
        Table credits = new Table();
        credits.setFillParent(true);

        for(String credit : creditsArray) {
            creditsText = new Label(credit, skin);
            credits.add(creditsText);
            credits.row();
        }

        stage.addActor(credits);

        //Sets the table to below the screen, table location is dependent on size for now, so can't get a definite size
        credits.setPosition(0,-1800);

        //Moves the table out of view of the screen
        Action move = Actions.moveTo(0, Gdx.graphics.getHeight() * 2, actionDuration);

        //New runnable action to run code after the first action is done
        RunnableAction end = new RunnableAction();
        end.setRunnable(() -> screensManager.changeScreen(MainMenuScreen.class));

        //Action sequence to allow one to run after the other
        credits.addAction(sequence(move, end));
        credits.row().fillX().width(200).height(1000);
        credits.setDebug(true); // turn on all debug lines (table, cell, and widget)
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        batch.begin();
        moveScreen();
        batch.end();
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void dispose() {
        batch.dispose();
        background.dispose();
        background2.dispose();
    }

    @Override
    public AbstractScreen clone() throws CloneNotSupportedException {
        return new CreditsScreen();
    }
}
