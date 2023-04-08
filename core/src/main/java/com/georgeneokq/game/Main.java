package com.georgeneokq.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.georgeneokq.engine.actor.Direction;
import com.georgeneokq.engine.font.FontGenerator;
import com.georgeneokq.game.screen.GameScreen;
import com.georgeneokq.engine.menu.MenuItem;
import com.georgeneokq.engine.save.GameSaver;
import com.georgeneokq.engine.screen.CreditsScreen;
import com.georgeneokq.engine.screen.InstructionScreen;
import com.georgeneokq.engine.screen.LeaderboardScreen;
import com.georgeneokq.engine.screen.LoadingScreen;
import com.georgeneokq.game.screen.MainMenuScreen;
import com.georgeneokq.engine.screen.SelectSaveScreen;
import com.georgeneokq.engine.screen.SettingsScreen;
import com.georgeneokq.game.manager.MusicManager;
import com.georgeneokq.engine.manager.ScreensManager;
import com.georgeneokq.engine.manager.SettingsManager;
import com.georgeneokq.game.skin.MyFontEnum;
import com.georgeneokq.game.skin.MyFontGenerators;

import java.io.File;
import java.io.FileFilter;
import java.util.HashMap;
import java.util.Map;

public class Main extends Game implements LoadingScreen.OnLoadListener {

	private Globals globals;
	private ScreensManager screensManager;
	private SettingsManager settingsManager;
	private AssetManager assetManager;

	/*
	 * create() is only used to initialize the loading screen.
	 * All other operations should happen in the onLoad function,
	 * which is called by the loading screen after loading is complete.
	 */
	@Override
	public void create() {
		initializeGlobals();
		screensManager = ScreensManager.getInstance();
		settingsManager = SettingsManager.getInstance();
		assetManager = globals.getAssetManager();
		queueAssets();
		screensManager.cacheScreens(new LoadingScreen(assetManager, this));
		screensManager.changeScreen(LoadingScreen.class);

		// Custom fonts
		FontGenerator.getInstance().addFontGenerator(MyFontEnum.NOTOSANS_COMBO.name(), MyFontGenerators::notosansComboFont);
	}

	/*
	 * Called when loading is complete
	 */
	@Override
	public void onLoad() {
		settingsManager.applySettings();

		// Create screens
		GameScreen gameScreen = new GameScreen();
		SettingsScreen settingsScreen = new SettingsScreen();
		MainMenuScreen mainMenuScreen = new MainMenuScreen();
		SelectSaveScreen selectSaveScreen = new SelectSaveScreen(GameScreen.class);
		LeaderboardScreen leaderboardScreen = new LeaderboardScreen();
		CreditsScreen creditsScreen = new CreditsScreen();
		InstructionScreen instructionScreen = new InstructionScreen();

		screensManager.cacheScreens(
				gameScreen,
				settingsScreen,
				mainMenuScreen,
				selectSaveScreen,
				leaderboardScreen,
				creditsScreen,
				instructionScreen
		);

		// Change to the transition screen
		screensManager.changeScreen(MainMenuScreen.class);
	}

	private void queueAssetsFromDir(String dir, Class loadAs) {
		queueAssetsFromDir(dir, loadAs, null, true);
	}

	private void queueAssetsFromDir(String dir, Class loadAs, FileFilter fileFilter) {
		queueAssetsFromDir(dir, loadAs, fileFilter, true);
	}

	private void queueAssetsFromDir(String dir, Class loadAs, FileFilter fileFilter, boolean recursive) {
		FileHandle dirHandle = Gdx.files.local(dir);
		FileHandle[] fileHandles = dirHandle.list(file -> file.isFile() && (fileFilter == null || fileFilter.accept(file)));
		for(FileHandle fileHandle: fileHandles) {
			assetManager.load(String.format("%s/%s", dir, fileHandle.name()), loadAs);
		}

		if(recursive) {
			FileHandle[] dirHandles = dirHandle.list(File::isDirectory);
			for(FileHandle subDirHandle : dirHandles) {
				queueAssetsFromDir(subDirHandle.path(), loadAs, fileFilter, true);
			}
		}
	}

	/*
	 * Queue assets to be loaded by the assets manager in the loading screen
	 */
	private void queueAssets() {
		assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
		queueAssetsFromDir("game/maps", TiledMap.class, file -> file.getName().endsWith(".tmx"), false);
		queueAssetsFromDir("audio/sound_effects", Sound.class);
		queueAssetsFromDir("audio/bgm", Music.class);
		queueAssetsFromDir("audio/voice", Music.class);
		queueAssetsFromDir("game/character", TextureAtlas.class, file -> file.getName().endsWith(".atlas"));
		queueAssetsFromDir("game/battle", Texture.class);

		assetManager.load("skin/cloud-form-ui.json", Skin.class);
		assetManager.load("font/small_letters_font.fnt", BitmapFont.class);
		assetManager.load("CreditsBackground.png", Texture.class);
		assetManager.load("game/uipack.atlas", TextureAtlas.class);
	}

	private void initializeGlobals() {
		AssetManager assetManager = new AssetManager();
		globals = Globals.getInstance();
		globals.setAssetManager(assetManager);
		ScreensManager.getInstance().initialize(this);
		MusicManager.getInstance().initialize(assetManager);

		Map<String, Class> nameClassMapping = new HashMap<>();

		// TODO: Replace/Register fields to be saved here
		nameClassMapping.put("player_position", Vector2.class);
		nameClassMapping.put("player_direction", Direction.class);
		nameClassMapping.put("mapName", String.class);
		nameClassMapping.put("score", int.class);

		globals.setGameSaver(new GameSaver(nameClassMapping));
	}

	@Override
	public void render () {
		super.render();
	}

	@Override
	public void dispose () { }
}
