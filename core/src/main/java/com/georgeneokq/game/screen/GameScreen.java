package com.georgeneokq.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.georgeneokq.engine.actor.Actor;
import com.georgeneokq.engine.actor.Direction;
import com.georgeneokq.game.actor.PlayerControls;
import com.georgeneokq.game.dialog.Dialog;
import com.georgeneokq.game.actor.InteractiveEntry;
import com.georgeneokq.game.event.EntryEventData;
import com.georgeneokq.game.event.SelectionRequestEventData;
import com.georgeneokq.game.event.SelectionResponseEventData;
import com.georgeneokq.game.manager.DialogAudioManager;
import com.georgeneokq.game.map.MapStateController;
import com.georgeneokq.game.mapstate.PetalburgRoomController;
import com.georgeneokq.game.actor.npc.PetalburgGymHiraganaTrainer;
import com.georgeneokq.game.actor.npc.PetalburgGymKatakanaTrainer;
import com.georgeneokq.game.actor.npc.PetalburgGymParticlesTrainer;
import com.georgeneokq.game.actor.npc.PetalburgGymSentenceFormingTrainer;
import com.georgeneokq.game.actor.npc.PetalburgGymVocabActionsTrainer;
import com.georgeneokq.game.actor.npc.PetalburgGymVocabGreetingsTrainer;
import com.georgeneokq.game.actor.npc.PetalburgGymVocabWeatherTrainer;
import com.georgeneokq.game.widget.SelectionBox;
import com.georgeneokq.engine.menu.MenuItem;
import com.georgeneokq.engine.save.GameSaver;
import com.georgeneokq.engine.save.LoadRequestReceiver;
import com.georgeneokq.engine.screen.AbstractScreen;
import com.georgeneokq.engine.stage.ExtendedStage;
import com.georgeneokq.game.Globals;
import com.georgeneokq.engine.animation.FadeOutTransition;
import com.georgeneokq.engine.animation.Transitions;
import com.georgeneokq.game.event.Events;
import com.georgeneokq.game.actor.npc.Opponent;
import com.georgeneokq.game.actor.Player;
import com.georgeneokq.game.PlayerStats;
import com.georgeneokq.game.actor.SpawnPoint;
import com.georgeneokq.game.event.BattleEndedEventData;
import com.georgeneokq.game.event.DialogEventData;
import com.georgeneokq.game.actor.npc.PetalburgGymLeader;
import com.georgeneokq.game.manager.MusicManager;
import com.georgeneokq.game.widget.DialogBox;
import com.georgeneokq.game.skin.PokemonSkinGenerator;
import com.georgeneokq.engine.manager.EventManager;
import com.georgeneokq.engine.manager.GameState;
import com.georgeneokq.engine.manager.GameStateManager;
import com.georgeneokq.engine.manager.ScreensManager;
import com.georgeneokq.engine.manager.SettingsManager;
import com.georgeneokq.game.map.MapController;
import com.georgeneokq.engine.map.MapUtil;
import com.georgeneokq.game.widget.MenuWindow;
import com.georgeneokq.engine.settings.BooleanSetting;
import com.georgeneokq.engine.settings.HotkeySetting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameScreen extends AbstractScreen implements LoadRequestReceiver {
    private ExtendedStage stage;
    private Skin skin;
    private MenuWindow pauseWindow;

    private ScreensManager screensManager;
    private SettingsManager settingsManager;
    private AssetManager assetManager;
    private EventManager eventManager;
    private MusicManager musicManager;

    private Globals globals;
    private Player player;

    // For selection box
    private SelectionBox selectionBox;
    private Table selectionBoxRoot;

    // For dialog box
    private Table dialogRoot;

    private DialogBox dialogBox;
    private int dialogLineIndex = 0;
    private Dialog dialog;
    private boolean dialogShown;
    private Actor dialogActor;

    private float characterWidth;
    private float characterHeight;
    private final float characterSpeed = 1;

    private GameStateManager gameStateManager;
    private DialogAudioManager dialogAudioManager;

    private static final int DEFAULT_SAVE_IDENTIFIER = 1;
    private int saveGameHotkey;
    private int interactionKey;
    private int repeatVoiceHotkey;
    private Sound selectSound;
    private MapController mapController;
    private FadeOutTransition fadeOutTransitionRenderer;
    private Transitions transitions;
    private Viewport viewport;
    private OrthographicCamera camera;
    int cameraViewportWidth = 250;
    int cameraViewportHeight = 250;
    private boolean isPaused = false;
    private float fontScale;
    private int baseFontSize = 80;
    private int worldWidth;
    private int worldHeight;
    private int tileWidth;
    private int tileHeight;
    private String mapName = "ashlvl2";
    private SpawnPoint spawnPoint;

    private PlayerControls playerControls;
    private boolean willStartBattle = false;
    private BattleEndedEventData battleEndedEventData;
    private PlayerStats playerStats;
    private Object selectionRequestor;

    public GameScreen() {
        globals = Globals.getInstance();
        screensManager = ScreensManager.getInstance();
        settingsManager = SettingsManager.getInstance();
        this.assetManager = globals.getAssetManager();
        gameStateManager = GameStateManager.getInstance();
        eventManager = EventManager.getInstance();
        musicManager = MusicManager.getInstance();
        dialogAudioManager = DialogAudioManager.getInstance();
        playerStats = new PlayerStats();
    }

    public void initialize() {
        // Get hotkeys
        interactionKey = settingsManager.getSetting("player.controls.interaction_key",
                HotkeySetting.class).getValue();
        saveGameHotkey = settingsManager.getSetting("general.save_key", HotkeySetting.class).getValue();
        repeatVoiceHotkey = settingsManager.getSetting("player.controls.repeat_voice_key",
                HotkeySetting.class).getValue();

        selectSound = assetManager.get("audio/sound_effects/select.mp3");

        // Create map, then create viewport using the map dimensions
        TiledMap map = assetManager.get(String.format("game/maps/%s.tmx", mapName));
        worldWidth = MapUtil.getMapWidth(map);
        worldHeight = MapUtil.getMapHeight(map);
        tileWidth = MapUtil.getTileWidth(map);
        tileHeight = MapUtil.getTileHeight(map);
        characterWidth = tileWidth * 1.5f;
        characterHeight = tileHeight * 1.5f;
        viewport = new ExtendViewport(worldWidth, worldHeight);
        fontScale = (float) worldWidth / globals.resolutionWidth;
        int fontSize = (int) (baseFontSize * (1 - fontScale));
        skin = PokemonSkinGenerator.getInstance().generateSkin(assetManager, fontSize);

        // Prepare pause menu
        createPauseMenu();

        // Initialize stage with the viewport and add actors
        stage = new ExtendedStage(viewport);
        loadNPCs();

        // Initialize map controller and camera viewport
        mapController = new MapController(mapName, map, stage);
        addMapStateControllers();

        if(spawnPoint == null)
            spawnPoint = mapController.getSpawnPoint("whiteout");

        camera = (OrthographicCamera) stage.getCamera();
        buildExtendedStage();

        // For transition effects
        transitions = new Transitions(stage);

        // Start the game
        gameStateManager.setState(GameState.PLAYING);

        transitions.startFadeInTransition(2, 0, (x) -> {
            Gdx.input.setInputProcessor(stage);
        });
    }

    private void createPauseMenu() {
        pauseWindow = null;

        List<MenuItem> menuItems = new ArrayList<>();

        menuItems.add(new MenuItem(getString("resume"), () -> {
            EventManager.getInstance().emit(Events.RESUMED.name());
        }, null));

        menuItems.add(new MenuItem(getString("save"), this::saveGame, null));

        menuItems.add(new MenuItem(getString("quit_game"), this::quitGame, null));

        pauseWindow = new MenuWindow(
                menuItems,
                getString("paused"),
                globals.resolutionWidth,
                globals.resolutionHeight,
                skin,
                fontScale
        );

        if(playerStats != null)
            pauseWindow.updatePlayerStats(playerStats);
    }

    private void buildExtendedStage() {
        playerControls = new PlayerControls(
            settingsManager.getSetting("player.controls.up_key", HotkeySetting.class).getValue(),
            settingsManager.getSetting("player.controls.down_key", HotkeySetting.class).getValue(),
            settingsManager.getSetting("player.controls.left_key", HotkeySetting.class).getValue(),
            settingsManager.getSetting("player.controls.right_key", HotkeySetting.class).getValue(),
            settingsManager.getSetting("player.controls.interaction_key", HotkeySetting.class).getValue()
        );
        player = new Player(
                characterWidth,
                characterHeight,
                spawnPoint.getBounds().x,
                spawnPoint.getBounds().y,
                characterSpeed,
                characterSpeed,
                false,
                playerControls,
                assetManager.get("game/character/character.atlas", TextureAtlas.class),
                assetManager.get("game/battle/player.png", Texture.class)
        );

        player.setDirection(spawnPoint.getDirection());

        stage.addActors(player);


        // Set up dialog box for interactions
        buildDialogRoot();

        // Set up selection box
        buildSelectionBox();
    }

    private void buildDialogRoot() {
        dialogRoot = new Table();
        dialogRoot.setSize(cameraViewportWidth, cameraViewportHeight);
        dialogBox = new DialogBox(skin, fontScale);
        dialogBox.setVisible(false);
        dialogRoot.add(dialogBox).expand().fillX().align(Align.bottom);
    }

    private void buildSelectionBox() {
        String[] choices = new String[] {getString("yes"), getString("no")};
        selectionBox = new SelectionBox(choices, playerControls, fontScale, skin);
        selectionBox.setVisible(false);

        // Attach to a full-screen selection box root table
        selectionBoxRoot = new Table();
        selectionBoxRoot.setSize(cameraViewportWidth, cameraViewportHeight);
        selectionBoxRoot.add(selectionBox).expand().right().padRight(5);
    }

    private void loadNPCs() {
        PetalburgGymHiraganaTrainer petalburgGymHiraganaTrainer = new PetalburgGymHiraganaTrainer(
                characterWidth,
                characterHeight,
                assetManager.get("game/character/character_2.atlas", TextureAtlas.class),
                assetManager.get("game/battle/gym_leader.png", Texture.class)
        );

        PetalburgGymKatakanaTrainer petalburgGymKatakanaTrainer = new PetalburgGymKatakanaTrainer(
                characterWidth,
                characterHeight,
                assetManager.get("game/character/character_2.atlas", TextureAtlas.class),
                assetManager.get("game/battle/gym_leader.png", Texture.class)
        );

        PetalburgGymVocabWeatherTrainer petalburgGymVocabWeatherTrainer = new PetalburgGymVocabWeatherTrainer(
                characterWidth,
                characterHeight,
                assetManager.get("game/character/character_2.atlas", TextureAtlas.class),
                assetManager.get("game/battle/gym_leader.png", Texture.class)
        );

        PetalburgGymVocabGreetingsTrainer petalburgGymVocabGreetingsTrainer = new PetalburgGymVocabGreetingsTrainer(
                characterWidth,
                characterHeight,
                assetManager.get("game/character/character_2.atlas", TextureAtlas.class),
                assetManager.get("game/battle/gym_leader.png", Texture.class)
        );

        PetalburgGymVocabActionsTrainer petalburgGymVocabActionsTrainer = new PetalburgGymVocabActionsTrainer(
                characterWidth,
                characterHeight,
                assetManager.get("game/character/character_2.atlas", TextureAtlas.class),
                assetManager.get("game/battle/gym_leader.png", Texture.class)
        );

        PetalburgGymSentenceFormingTrainer petalburgGymSentenceFormingTrainer = new PetalburgGymSentenceFormingTrainer(
                characterWidth,
                characterHeight,
                assetManager.get("game/character/character_2.atlas", TextureAtlas.class),
                assetManager.get("game/battle/gym_leader.png", Texture.class)
        );

        PetalburgGymParticlesTrainer petalburgGymParticlesTrainer = new PetalburgGymParticlesTrainer(
                characterWidth,
                characterHeight,
                assetManager.get("game/character/character_2.atlas", TextureAtlas.class),
                assetManager.get("game/battle/gym_leader.png", Texture.class)
        );

        PetalburgGymLeader petalburgGymLeader = new PetalburgGymLeader(
                characterWidth,
                characterHeight,
                assetManager.get("game/character/gym_leader.atlas", TextureAtlas.class),
                assetManager.get("game/battle/gym_leader.png", Texture.class)
        );

        // Interactive entries
        InteractiveEntry petalburgGymL1Entry1 = new InteractiveEntry(
                new Rectangle(),
                "petalburggymlvl2l",
                "petalburggymlvl2lspawn",
                true,
                "petalburg_gym_hiragana_room",
                mapController
        );

        InteractiveEntry petalburgGymL1Entry2 = new InteractiveEntry(
                new Rectangle(),
                "petalburggymlvl2r",
                "petalburggymlvl2rspawn",
                true,
                "petalburg_gym_katakana_room",
                mapController
        );

        InteractiveEntry petalburgGymL2lEntry1 = new InteractiveEntry(
                new Rectangle(),
                "petalburggymlvl3l",
                "petalburggymlvl3lspawn",
                false,
                "petalburg_gym_weather_room",
                mapController
        );

        InteractiveEntry petalburgGymL2lEntry2 = new InteractiveEntry(
                new Rectangle(),
                "petalburggymlvl3m",
                "petalburggymlvl3mspawn_1",
                false,
                "petalburg_gym_greetings_room",
                mapController
        );

        InteractiveEntry petalburgGymL2rEntry1 = new InteractiveEntry(
                new Rectangle(),
                "petalburggymlvl3m",
                "petalburggymlvl3mspawn_2",
                false,
                "petalburg_gym_greetings_room",
                mapController
        );

        InteractiveEntry petalburgGymL2rEntry2 = new InteractiveEntry(
                new Rectangle(),
                "petalburggymlvl3r",
                "petalburggymlvl3rspawn",
                false,
                "petalburg_gym_verbs_room",
                mapController
        );

        InteractiveEntry petalburgGymL3lEntry1 = new InteractiveEntry(
                new Rectangle(),
                "petalburggymlvl4l",
                "petalburggymlvl4lspawn",
                false,
                "petalburg_gym_sentence_forming_room",
                mapController
        );

        InteractiveEntry petalburgGymL3mEntry1 = new InteractiveEntry(
                new Rectangle(),
                "petalburggymlvl4l",
                "petalburggymlvl4lspawn",
                false,
                "petalburg_gym_sentence_forming_room",
                mapController
        );

        InteractiveEntry petalburgGymL3mEntry2 = new InteractiveEntry(
                new Rectangle(),
                "petalburggymlvl4r",
                "petalburggymlvl4rspawn",
                false,
                "petalburg_gym_particles_room",
                mapController
        );

        InteractiveEntry petalburgGymL3rEntry1 = new InteractiveEntry(
                new Rectangle(),
                "petalburggymlvl4r",
                "petalburggymlvl4rspawn",
                false,
                "petalburg_gym_particles_room",
                mapController
        );

        InteractiveEntry petalburgGymL4lEntry1 = new InteractiveEntry(
                new Rectangle(),
                "petalburggymlvl5",
                "petalburggymlvl5spawn_1",
                false,
                "petalburg_gym_leader_room",
                mapController
        );

        InteractiveEntry petalburgGymL4rEntry1 = new InteractiveEntry(
                new Rectangle(),
                "petalburggymlvl5",
                "petalburggymlvl5spawn_2",
                false,
                "petalburg_gym_leader_room",
                mapController
        );

        globals.addNPC("petalburg_gym_hiragana_trainer", petalburgGymHiraganaTrainer);
        globals.addNPC("petalburg_gym_katakana_trainer", petalburgGymKatakanaTrainer);
        globals.addNPC("petalburg_gym_vocabulary_weather_trainer", petalburgGymVocabWeatherTrainer);
        globals.addNPC("petalburg_gym_vocabulary_greetings_trainer", petalburgGymVocabGreetingsTrainer);
        globals.addNPC("petalburg_gym_vocabulary_actions_trainer", petalburgGymVocabActionsTrainer);
        globals.addNPC("petalburg_gym_sentence_forming_trainer", petalburgGymSentenceFormingTrainer);
        globals.addNPC("petalburg_gym_particles_trainer", petalburgGymParticlesTrainer);
        globals.addNPC("petalburg_gym_leader", petalburgGymLeader);
        globals.addNPC("petalburg_gym_l1_entry_1", petalburgGymL1Entry1);
        globals.addNPC("petalburg_gym_l1_entry_2", petalburgGymL1Entry2);
        globals.addNPC("petalburg_gym_l2l_entry_1", petalburgGymL2lEntry1);
        globals.addNPC("petalburg_gym_l2l_entry_2", petalburgGymL2lEntry2);
        globals.addNPC("petalburg_gym_l2r_entry_1", petalburgGymL2rEntry1);
        globals.addNPC("petalburg_gym_l2r_entry_2", petalburgGymL2rEntry2);
        globals.addNPC("petalburg_gym_l3l_entry_1", petalburgGymL3lEntry1);
        globals.addNPC("petalburg_gym_l3m_entry_1", petalburgGymL3mEntry1);
        globals.addNPC("petalburg_gym_l3m_entry_2", petalburgGymL3mEntry2);
        globals.addNPC("petalburg_gym_l3r_entry_1", petalburgGymL3rEntry1);
        globals.addNPC("petalburg_gym_l4l_entry_1", petalburgGymL4lEntry1);
        globals.addNPC("petalburg_gym_l4r_entry_1", petalburgGymL4rEntry1);
    }

    private void addMapStateControllers() {
        Map<String, MapStateController> mapStateControllerMap = new HashMap<>();
        mapStateControllerMap.put("petalburggymlvl1", new PetalburgRoomController());
        mapStateControllerMap.put("petalburggymlvl2l", new PetalburgRoomController());
        mapStateControllerMap.put("petalburggymlvl2r", new PetalburgRoomController());
        mapStateControllerMap.put("petalburggymlvl3l", new PetalburgRoomController());
        mapStateControllerMap.put("petalburggymlvl3m", new PetalburgRoomController());
        mapStateControllerMap.put("petalburggymlvl3r", new PetalburgRoomController());
        mapStateControllerMap.put("petalburggymlvl4l", new PetalburgRoomController());
        mapStateControllerMap.put("petalburggymlvl4r", new PetalburgRoomController());
        mapStateControllerMap.put("petalburggymlvl5", new PetalburgRoomController());
        mapController.setMapStateControllers(mapStateControllerMap);
    }

    private void handleKeyPressed() {
        if (Gdx.input.isKeyJustPressed(saveGameHotkey)) {
            saveGame();
        } else if(Gdx.input.isKeyJustPressed(interactionKey)) {
            // Give selection box 1st priority
            if(selectionBox.isShown() && selectionRequestor != null) {
                String selection = selectionBox.getSelection();
                SelectionResponseEventData data = new SelectionResponseEventData(selection, selectionRequestor);
                eventManager.emit(Events.SELECTION_RESPONSE.name(), data);
                selectionRequestor = null;
                selectionBox.setVisible(false);
                selectSound.play();

                // Don't return here, go on to hide the dialog if needed
            }

            // Give dialog box 2nd priority
            if(dialogShown) {
                if(dialogLineIndex + 1 >= dialog.getLines().length) {
                    // Dialog ended
                    if(selectionRequestor != null) {
                        // Show the request box
                        selectionBox.setVisible(true);
                        selectionBoxRoot.remove();
                        stage.addActor(selectionBoxRoot);
                        selectSound.play();
                        return;
                    }

                    dialog = null;
                    dialogLineIndex = 0;
                    dialogShown = false;
                    dialogBox.setVisible(false);
                    eventManager.emit(Events.DIALOG_ENDED.name(), dialogActor);
                    musicManager.restorePreviousVolume();

                    if(willStartBattle && dialogActor instanceof Opponent) {
                        stage.setInputsEnabled(false);

                        Opponent opponent = (Opponent) dialogActor;
                        BattleScreen battleScreen = new BattleScreen(player, opponent, this);
                        battleScreen.initialize();

                        String musicFilePath = String.format("%s/%s", getBattleMusicPath(), opponent.getBattleMusicName());
                        musicManager.playMusic(musicFilePath);
                        transitions.startFlashTransition(4, 0.5f, 0, (x) -> {
                            transitions.startFadeOutTransition(1.5f, 0, (transitionRenderer) -> {
                                screensManager.getGame().setScreen(battleScreen);
                                fadeOutTransitionRenderer = (FadeOutTransition) transitionRenderer;
                            }, false);
                        });
                        willStartBattle = false;
                    }
                } else {
                    dialogRoot.remove();
                    stage.addActor(dialogRoot);
                    dialogLineIndex += 1;
                    Dialog.Line line = dialog.getLines()[dialogLineIndex];
                    // Play the dialog audio, if available
                    if(line.getAudio() != null) {
                        dialogAudioManager.playAudio(line.getAudio());
                    }
                    selectSound.play();
                    dialogBox.animateTextWithSubtitle(line.getOriginalText(), line.getSubtitle(),
                            0.03f, null);
                }
            }
        } else if(Gdx.input.isKeyJustPressed(repeatVoiceHotkey)) {
            if(dialog != null && dialogLineIndex < dialog.getLines().length) {
                dialogAudioManager.playAudio(dialog.getLines()[dialogLineIndex].getAudio());
            }
        }
    }

    @Override
    public String[] getSubscribedEvents() {
        return new String[] {
                Events.PAUSED.name(),
                Events.RESUMED.name(),
                Events.DIALOG_STARTED.name(),
                Events.MAP_CHANGED.name(),
                Events.BATTLE_STARTED.name(),
                Events.BATTLE_ENDED.name(),
                Events.SELECTION_REQUEST.name()
        };
    }

    @Override
    public void eventReceived(String eventName, Object data) {
        if(eventName.equals(Events.DIALOG_STARTED.name())) {
            // A dialog string is passed in as the data.
            // Break the string by newline and put them in dialogLines variable
            musicManager.limitVolume(15 / 100f);
            player.idle();
            DialogEventData dialogEventData = (DialogEventData) data;
            willStartBattle = dialogEventData.willStartBattle();
            dialog = dialogEventData.getDialog();
            dialogActor = dialogEventData.getActor();
            Dialog.Line line = dialog.getLines()[0];
            dialogLineIndex = 0;

            // Begin animating text
            dialogRoot.remove();
            stage.addActor(dialogRoot);
            dialogShown = true;
            dialogBox.setVisible(true);
            if(line.getAudio() != null)
                dialogAudioManager.playAudio(line.getAudio());
            dialogBox.animateTextWithSubtitle(line.getOriginalText(), line.getSubtitle(), 0.03f, null);
        }
        else if(eventName.equals(Events.PAUSED.name())) {
            isPaused = true;
            pauseWindow.show(stage);
            stage.pauseActors();
        }
        else if(eventName.equals(Events.RESUMED.name())) {
            isPaused = false;
            pauseWindow.hide();
            stage.resumeActors();
        } else if(eventName.equals(Events.MAP_CHANGED.name())) {
            EntryEventData entryEventData = (EntryEventData) data;
            mapName = entryEventData.getMapName();
            spawnPoint = entryEventData.getSpawnPoint();
            stage.dispose();
            initialize();
        } else if(eventName.equals(Events.BATTLE_ENDED.name())) {
            musicManager.playMusic(mapController.getBGMName());
            battleEndedEventData = (BattleEndedEventData) data;

            // Save data.
            if(battleEndedEventData.isPerfectRun())
                playerStats.setPerfectRuns(playerStats.getPerfectRuns() + 1);
            if(battleEndedEventData.getHighestCombo() > playerStats.getHighestCombo())
                playerStats.setHighestCombo(battleEndedEventData.getHighestCombo());
            pauseWindow.updatePlayerStats(playerStats);
            saveGame();

            if(!battleEndedEventData.playerWon()) {
                // If lost, move player to whiteout point
                String whiteoutMap = mapController.getWhiteoutMap();
                mapController.setMap(assetManager.get(String.format("game/maps/%s.tmx", whiteoutMap)));
                SpawnPoint whiteoutSpawn = mapController.getWhiteoutSpawnPoint();
                if(whiteoutMap != null && whiteoutSpawn != null) {
                    mapName = whiteoutMap;
                    spawnPoint = whiteoutSpawn;
                    stage.dispose();
                    initialize();
                    return;
                }
            }
            fadeOutTransitionRenderer.removeOverlay();
            transitions.startFadeInTransition(2, 0, (renderer) -> {
                battleEndedEventData = null;
                updateMapState();

                // TODO: Move this logic somewhere into gymleader. Redirect after some dialog
                if(!(dialogActor instanceof PetalburgGymLeader)) return;
                playerStats.setGymClearCount(playerStats.getGymClearCount() + 1);
                mapName = "petalburggymlvl1";
                mapController.setMap(assetManager.get(String.format("game/maps/%s.tmx", mapName)));
                spawnPoint = mapController.getSpawnPoint("victory_spawn");
                stage.dispose();
                initialize();
            });
        } else if(eventName.equals(Events.SELECTION_REQUEST.name())) {
            SelectionRequestEventData requestEventData = (SelectionRequestEventData) data;
            dialog = requestEventData.getDialog();
            selectionRequestor = requestEventData.getRequestor();
            musicManager.limitVolume(15 / 100f);
            // Animate text
            Dialog.Line line = dialog.getLines()[0];
            dialogRoot.remove();
            stage.addActor(dialogRoot);
            dialogShown = true;
            dialogBox.setVisible(true);
            if(line.getAudio() != null)
                dialogAudioManager.playAudio(line.getAudio());
            dialogBox.animateTextWithSubtitle(line.getOriginalText(), line.getSubtitle(), 0.03f, null);
        }
    }

    /**
     * Updates map state depending on the current event that just happened
     * TODO: This structure only accounts for battle ended events at the moment
     */
    private void updateMapState() {
        if(mapName.startsWith("petalburggymlvl")) {
            mapController.updateMapState(globals.getMapState(mapName) + 1);
        }
    }

    private void saveGame() {
        globals.getGameSaver().getKryo().register(PlayerStats.class);
        Map<String, Object> saveData = new HashMap<>();
        saveData.put("player_position", new Vector2(player.getX(), player.getY()));
        saveData.put("player_direction", player.getDirection());
        saveData.put("mapName", mapName);
        saveData.put("player_stats_perfect_runs", playerStats.getPerfectRuns());
        saveData.put("player_stats_highest_combo", playerStats.getHighestCombo());
        saveData.put("player_stats_gym_clear_count", playerStats.getGymClearCount());
        globals.getGameSaver().save(DEFAULT_SAVE_IDENTIFIER, saveData);
    }

    private void quitGame() {
        pauseWindow.hide();
        screensManager.changeScreen(MainMenuScreen.class);
        if(settingsManager.getSetting("save_data.autosave", BooleanSetting.class).getValue())
            saveGame();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        if(transitions.isRunning()) {
            transitions.render(delta);
        }

        // Black background
        ScreenUtils.clear(0, 0, 0, 1);

        // Call to mapController.setView() probably resets these values every frame.
        // Reassign these values every frame to keep the camera zoomed
        camera.viewportWidth = cameraViewportWidth;
        camera.viewportHeight = cameraViewportHeight;
        mapController.setOrthoCameraViewport(cameraViewportWidth, cameraViewportHeight);
        if(isPaused){
            // Comment either to see effect
            // This line to make window fill screen (by changing stage size)
            resize(globals.resolutionWidth, globals.resolutionHeight);

            // This line to zoom out, fixes resolution but does not fill whole screen
            // camera.setToOrtho(false, globals.resolutionWidth, globals.resolutionHeight);
        }

        // Update camera and map controller
        mapController.setCameraPosition(player.getX(), player.getY());
        mapController.setView(camera);
        mapController.renderVisibleLayers();
        dialogRoot.setWidth(camera.viewportWidth);
        dialogRoot.setPosition(camera.position.x - camera.viewportWidth / 2f,
                camera.position.y - camera.viewportHeight / 2f);

        selectionBoxRoot.setSize(camera.viewportWidth, camera.viewportHeight);
        selectionBoxRoot.setPosition(camera.position.x - camera.viewportWidth / 2f,
                camera.position.y - camera.viewportHeight / 2f);

        // If dialog is shown, disable input handling in stage
        if(dialogShown) {
            stage.setInputsEnabled(false);
        }

        // Handle inputs
        handleKeyPressed();

        // Draw
        stage.updateAndDraw(delta);

        if(!dialogShown && !transitions.isRunning()) {
            stage.setInputsEnabled(true);
        }
    }

    @Override
    public void onLoadRequestReceived(int saveIdentifier) {
        GameSaver gameSaver = globals.getGameSaver();
        Map<String, Object> saveData = gameSaver.load(saveIdentifier);
        if(saveData == null)
            return;

        Vector2 playerPosition = (Vector2) saveData.get("player_position");
        Direction playerDirection = (Direction) saveData.get("player_direction");
        int perfectRuns = (int) saveData.get("player_stats_perfect_runs");
        int highestCombo = (int) saveData.get("player_stats_highest_combo");
        int gymClearCount = (int) saveData.get("player_stats_gym_clear_count");
        this.mapName = (String) saveData.get("mapName");
        this.spawnPoint = new SpawnPoint(new Rectangle(playerPosition.x, playerPosition.y, 0, 0), playerDirection);
        this.playerStats = new PlayerStats(perfectRuns, highestCombo, gymClearCount);
        pauseWindow.updatePlayerStats(playerStats);

        initialize();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
        stage.getCamera().update();
        dialogRoot.setWidth(camera.viewportWidth);
    }

    @Override
    public void onResolutionChanged(int width, int height) {

    }

    @Override
    public AbstractScreen clone() {
        return new GameScreen();
    }
}
