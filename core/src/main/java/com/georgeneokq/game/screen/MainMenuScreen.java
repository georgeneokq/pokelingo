package com.georgeneokq.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.georgeneokq.engine.screen.AbstractScreen;
import com.georgeneokq.engine.screen.SettingsScreen;
import com.georgeneokq.game.Globals;
import com.georgeneokq.engine.save.LoadRequestReceiver;
import com.georgeneokq.game.skin.PokemonSkinGenerator;
import com.georgeneokq.game.manager.MusicManager;
import com.georgeneokq.engine.manager.ScreensManager;
import com.georgeneokq.engine.manager.SettingsManager;
import com.georgeneokq.engine.menu.MenuItem;

public class MainMenuScreen extends AbstractScreen {

    private Stage stage;

    private Sound sound;
    private Array<MenuItem> menuItems;

    private ScreensManager screensManager;
    private SettingsManager settingsManager;
    private AssetManager assetManager;
    private MusicManager musicManager;

    private Globals globals;

    public MainMenuScreen() {}

    @Override
    public void initialize() {
        globals = Globals.getInstance();
        this.screensManager = ScreensManager.getInstance();
        this.settingsManager = SettingsManager.getInstance();
        this.assetManager = globals.getAssetManager();
        this.musicManager = MusicManager.getInstance();

        setMenuItems();

        sound = assetManager.get("audio/sound_effects/select.mp3", Sound.class);

        stage = new Stage(new FitViewport(globals.resolutionWidth, globals.resolutionHeight));
        buildStage();

        // Play music
        musicManager = MusicManager.getInstance();
        musicManager.playMusic("audio/bgm/titletheme.mp3");
    }

    private void setMenuItems() {
        menuItems = new Array<>();
        menuItems.add(new MenuItem(getString("play_game"),
                () -> {
                    screensManager.changeScreen(GameScreen.class);
                    AbstractScreen screen = screensManager.getCurrentScreen();

                    int saveIdentifier = 1;
                    LoadRequestReceiver loadRequestReceiver = (LoadRequestReceiver) screen;
                    loadRequestReceiver.onLoadRequestReceived(saveIdentifier);
                }, sound));

        menuItems.add(new MenuItem(getString("settings"),
                () -> screensManager.changeScreen(SettingsScreen.class), sound));

        menuItems.add(new MenuItem(getString("quit_game"),
                () -> Gdx.app.exit(), sound));
    }

    private void buildStage() {
        // Create a table that fills the screen. Everything else will go inside this table.
        Table table = new Table();

        PokemonSkinGenerator generator = PokemonSkinGenerator.getInstance();
        Skin skin = generator.generateSkin(assetManager);

        // Dynamically load buttons and attach click listeners based on menuItems array
        for(int i = 0; i < menuItems.size; i++) {
            final MenuItem menuItem = menuItems.get(i);
            TextButton textButton = new TextButton(menuItem.getTitle(), skin);
            textButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    menuItem.playSound();
                    menuItem.getSelectListener().onSelect();
                }
            });

            table.row().fillX().padTop(10).padBottom(10).width(200).height(100);
            table.add(textButton);
        }

        ScrollPane scrollPane = new ScrollPane(table);
        scrollPane.setScrollBarPositions(false, true);
        scrollPane.setFillParent(true);
        stage.addActor(scrollPane);
        stage.setScrollFocus(scrollPane);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        // ScreenUtils.clear(new Color(0x40F5BC00));
        ScreenUtils.clear(1, 1, 1, 1);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public AbstractScreen clone() throws CloneNotSupportedException {
        return new MainMenuScreen();
    }
}