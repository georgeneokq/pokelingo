package com.georgeneokq.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.georgeneokq.engine.actor.Controls;
import com.georgeneokq.engine.factory.DrawableFactory;
import com.georgeneokq.game.dialog.Dialog;
import com.georgeneokq.game.event.Events;
import com.georgeneokq.game.manager.DialogAudioManager;
import com.georgeneokq.engine.screen.AbstractScreen;
import com.georgeneokq.engine.image.ImageUtil;
import com.georgeneokq.game.Globals;
import com.georgeneokq.engine.animation.Animations;
import com.georgeneokq.engine.animation.MoveAnimation;
import com.georgeneokq.engine.animation.ShakeAnimation;
import com.georgeneokq.engine.animation.Transitions;
import com.georgeneokq.engine.font.FontGenerator;
import com.georgeneokq.game.event.BattleEndedEventData;
import com.georgeneokq.game.actor.npc.Opponent;
import com.georgeneokq.game.actor.Player;
import com.georgeneokq.game.question.Choice;
import com.georgeneokq.game.question.Question;
import com.georgeneokq.game.question.QuestionType;
import com.georgeneokq.engine.manager.EventManager;
import com.georgeneokq.game.manager.MusicManager;
import com.georgeneokq.engine.manager.ScreensManager;
import com.georgeneokq.engine.manager.SettingsManager;
import com.georgeneokq.engine.settings.HotkeySetting;
import com.georgeneokq.game.skin.MyFontEnum;
import com.georgeneokq.game.widget.DialogBox;
import com.georgeneokq.game.widget.HPBar;
import com.georgeneokq.game.widget.MoveSelectBox;
import com.georgeneokq.game.widget.MoveSelectOptions;
import com.georgeneokq.game.widget.ComboDisplayWidget;
import com.georgeneokq.game.skin.PokemonSkinGenerator;

public class BattleScreen extends AbstractScreen {

    private Question currentQuestion;
    private boolean awaitingNextQuestion = false;
    private int playerMultiplier = 1;
    private int opponentMultiplier = 1;

    private Container topLeftContainer;
    private Container topRightContainer;
    private Table optionBoxTable;
    private Table dialogBoxTable;
    private MoveSelectBox moveSelectBox;
    private Table dialogTable;
    private DialogBox dialogBox;
    private HPBar opponentHPBar;
    private HPBar playerHPBar;
    private Label repeatVoiceTipLabel;
    private ComboDisplayWidget playerComboWidget;
    private ComboDisplayWidget opponentComboWidget;

    private Animations animations;
    private ShakeAnimation playerShakeAnimation;
    private ShakeAnimation opponentShakeAnimation;
    private MoveAnimation playerMoveAnimation;
    private MoveAnimation opponentMoveAnimation;
    private Transitions transitions;
    private Viewport viewport;
    private Stage stage;
    private Batch batch;
    private Skin skin;
    private BitmapFont comboFont;
    private int comboFontSize;
    private Controls controls;
    private int interactionKey;
    private int repeatVoiceHotkey;
    private Sound selectSound;
    private Sound defeatedSound;
    private Sound attackSound;

    private Player player;
    private Opponent opponent;
    private boolean initialAnimationEnded = false;
    private float initialAnimationTimeElapsed = 0;
    private float initialAnimationTotalTime = 3;
    private float initialPlayerX;
    private float initialOpponentX;
    private boolean battleFinished = false;

    // Battle stats
    private boolean playerWon = false;
    private boolean perfectRun = true;
    private int totalCorrect = 0;
    private int totalWrong = 0;
    private int highestCombo = 0;

    private Dialog dialog;
    private int dialogLineIndex;
    private String dialogText = "";

    private AbstractScreen redirectScreen;

    private EventManager eventManager;
    private MusicManager musicManager;
    private AssetManager assetManager;
    private SettingsManager settingsManager;
    private ScreensManager screensManager;
    private DialogAudioManager dialogAudioManager;

    private Table table;
    private Drawable whiteBackgroundDrawable;

    public BattleScreen(Player player, Opponent opponent, AbstractScreen redirectScreen) {
        this.player = player;
        this.opponent = opponent;
        this.redirectScreen = redirectScreen;
        this.eventManager = EventManager.getInstance();
        this.musicManager = MusicManager.getInstance();
        this.assetManager = Globals.getInstance().getAssetManager();
        this.settingsManager = SettingsManager.getInstance();
        this.screensManager = ScreensManager.getInstance();
        this.dialogAudioManager = DialogAudioManager.getInstance();
    }

    @Override
    public void initialize() {
        skin = PokemonSkinGenerator.getInstance().generateSkin(assetManager, scaleFontSizeByResolution(40));
        comboFontSize = scaleFontSizeByResolution(55);
        comboFont = FontGenerator.getInstance().createFont(MyFontEnum.NOTOSANS_COMBO.name(), comboFontSize);

        defeatedSound = assetManager.get("audio/sound_effects/defeated_enemy.mp3");
        selectSound = assetManager.get("audio/sound_effects/select.mp3");
        attackSound = assetManager.get("audio/sound_effects/slash_attack.mp3");
        interactionKey = settingsManager.getSetting("player.controls.interaction_key", HotkeySetting.class)
                .getValue();
        repeatVoiceHotkey = settingsManager.getSetting("player.controls.repeat_voice_key",
                HotkeySetting.class).getValue();
        controls = player.getControls();

        this.viewport = new ScreenViewport();
        this.stage = new Stage(viewport);
        this.batch = stage.getBatch();
        transitions = new Transitions(stage);
        animations = new Animations();

        // Use table as a 2x4 grid.
        // Format: (row, column)
        // (1, 1): Player sprite
        // (1, 2): Opponent sprite
        // (2, 1): Player HP
        // (2, 2): Opponent HP + tip
        // (3, 1): Player option box
        // (3, 2): Dialog box for displaying questions
        // (4, 1): Player damage multiplier
        // (4, 2): Opponent damage multiplier

        table = new Table();
        table.setSize(viewport.getScreenWidth(), viewport.getScreenHeight());

        // Player sprite
        topLeftContainer = new Container();
        table.add(topLeftContainer).fill().width(table.getWidth() / 2f).height(table.getHeight() / 2f);

        // Opponent sprite
        topRightContainer = new Container();
        table.add(topRightContainer).fill().width(table.getWidth() / 2f);

        whiteBackgroundDrawable = DrawableFactory.fromColor(new Color(1, 1, 1, 1));

        table.row();

        // HP labels
        playerHPBar = new HPBar(player.getMaxHP(), skin);
        playerHPBar.padLeft(15);
        playerHPBar.setVisible(false);
        opponentHPBar = new HPBar(opponent.getMaxHP(), skin);
        opponentHPBar.padLeft(15);
        opponentHPBar.setVisible(false);

        // Place tip label beside opponent HP bar
        String tipLabelText = getString("repeat_voice_tip")
                .replace("[repeat_voice_key]", Input.Keys.toString(repeatVoiceHotkey));
        repeatVoiceTipLabel = new Label(tipLabelText, skin);
        repeatVoiceTipLabel.setVisible(false);

        Table tipTable = new Table();
        tipTable.add(opponentHPBar).left();
        tipTable.add(repeatVoiceTipLabel).spaceLeft(15).expand().fill().right();

        table.add(playerHPBar).width(table.getWidth() / 2).left();
        table.add(tipTable).expand().fill();

        table.row();

        // Populate option box with choices
        currentQuestion = opponent.getNextQuestion();
        Choice[] choices = currentQuestion.getChoices();
        MoveSelectOptions moveSelectOptions = new MoveSelectOptions();
        for(int i = 0; i < choices.length; i++) {
            Choice choice = choices[i];
            moveSelectOptions.setOption(i, choice.getValue());
        }

        optionBoxTable = new Table();

        // Display option box
        moveSelectBox = new MoveSelectBox(
                skin,
                moveSelectOptions
        );
        moveSelectBox.setVisible(false);

        optionBoxTable.add(moveSelectBox).expand().fill().padLeft(10).padRight(10);

        table.add(optionBoxTable).width(table.getWidth() / 2).expand().fill();

        dialogBoxTable = new Table();

        // Display dialog box for displaying question
        dialogBox = new DialogBox(skin, 1f);
        dialogTable = new Table();
        dialogTable.add(dialogBox)
                .expand()
                .fill();

        dialogBoxTable.add(dialogTable).expand().fill().padLeft(10).padRight(10);
        dialogBox.setVisible(false);
        table.add(dialogBoxTable).expand().fill().align(Align.top);

        table.row();

        playerComboWidget = new ComboDisplayWidget(1, new Label.LabelStyle(comboFont, null), comboFontSize);
        playerComboWidget.setVisible(false);
        table.add(playerComboWidget).width(table.getWidth() / 2).fill();

        opponentComboWidget = new ComboDisplayWidget(1, new Label.LabelStyle(comboFont, null), comboFontSize);
        opponentComboWidget.setVisible(false);
        table.add(opponentComboWidget).expand().fill();

        stage.addActor(table);
        stage.draw();
    }

    private void playVoice(Music voice, float bgmVolumeLimit) {
        if(voice == null) return;

        // TODO: Account for sound volume setting
        musicManager.limitVolume(bgmVolumeLimit);
        repeatVoiceTipLabel.setVisible(false);
        voice.setOnCompletionListener((x) -> {
            repeatVoiceTipLabel.setVisible(true);
            musicManager.restorePreviousVolume();
        });
        dialogAudioManager.playAudio(voice);
    }

    private void handleKeyPress() {
        // Press the interaction key to go on to the next question
        if(awaitingNextQuestion && !Gdx.input.isKeyJustPressed(interactionKey))
            return;

        if(Gdx.input.isKeyJustPressed(interactionKey)) {
            if(awaitingNextQuestion) {
                currentQuestion = opponent.getNextQuestion();
                dialogBox.animateText(currentQuestion.getQuestion(), 0.03f, null);
                playVoice(currentQuestion.getVoice(), getBGMVolumeLimit(currentQuestion));
                MoveSelectOptions options = new MoveSelectOptions();
                Choice[] choices = currentQuestion.getChoices();
                for(int i = 0; i < choices.length; i++) {
                    Choice choice = choices[i];
                    options.setOption(i, choice.getValue());
                }
                selectSound.play();
                moveSelectBox.setOptions(options);
                moveSelectBox.renderOptions();
                awaitingNextQuestion = false;
            }
            else if(!dialogBox.isFinished()) {
                dialogBox.animateText(dialogText, 0.03f, null);
                awaitingNextQuestion = true;
            } else if(battleFinished) {
                if(dialog != null && dialogLineIndex + 1 != dialog.getLines().length) {
                    // If there is another dialog line to be displayed, display it
                    dialogLineIndex += 1;
                    Dialog.Line line = dialog.getLines()[dialogLineIndex];
                    playVoice(line.getAudio(), 15 / 100f);
                    dialogBox.animateTextWithSubtitle(line.getOriginalText(), line.getSubtitle(), 0.03f, null);
                } else {
                    // If not, end the battle and send the results back to game screen
                    transitions.startFadeOutTransition(2, 0, (x) -> {
                        BattleEndedEventData data = new BattleEndedEventData();
                        data.setPerfectRun(perfectRun);
                        data.setTotalCorrect(totalCorrect);
                        data.setTotalWrong(totalWrong);
                        data.setHighestCombo(highestCombo);
                        data.setPlayerWon(playerWon);

                        screensManager.getGame().setScreen(redirectScreen);
                        eventManager.emit(Events.BATTLE_ENDED.name(), data);
                    });
                }
            } else {
                repeatVoiceTipLabel.setVisible(false);
                handleOptionSelected();
            }
        } else if(Gdx.input.isKeyJustPressed(repeatVoiceHotkey)) {
            if(dialog != null && dialogLineIndex < dialog.getLines().length) {
               playVoice(dialog.getLines()[dialogLineIndex].getAudio(), 15 / 100f);
            } else {
                playVoice(currentQuestion.getVoice(), getBGMVolumeLimit(currentQuestion));
            }
        }
        // Select Box conditions
        if(Gdx.input.isKeyJustPressed(controls.getUpKey())) moveSelectBox.moveUp();
        else if (Gdx.input.isKeyJustPressed(controls.getDownKey())) moveSelectBox.moveDown();
        else if (Gdx.input.isKeyJustPressed(controls.getLeftKey())) moveSelectBox.moveLeft();
        else if (Gdx.input.isKeyJustPressed(controls.getRightKey())) moveSelectBox.moveRight();
    }

    private void playerCorrect() {
        totalCorrect += 1;
        dialogText = getString("correct");

        float shakePixels = 15;
        if(playerMultiplier > 3) {
            shakePixels *= 2;
        }

        attackSound.play();
        opponentShakeAnimation = new ShakeAnimation(topRightContainer, 2.5f, 0.7f, shakePixels);
        animations.startAnimation(topRightContainer, opponentShakeAnimation);

        opponentHPBar.updateHPAnimated(opponentHPBar.getHP() - currentQuestion.getWeightage() * playerMultiplier, 0.7f);
        opponentMultiplier = 1;
        playerMultiplier += 1;
        if(playerMultiplier - 1 > highestCombo)
            highestCombo = playerMultiplier;
        updateMultiplierLabels();
        playerComboWidget.animate();
    }

    private void playerWrong() {
        perfectRun = false;
        totalWrong += 1;
        dialogText = String.format("%s", getString("wrong"));

        float shakePixels = 15;
        if(opponentMultiplier > 3) {
            shakePixels *= 2;
        }

        attackSound.play();
        playerShakeAnimation = new ShakeAnimation(topLeftContainer, 2.5f, 0.7f, shakePixels);
        animations.startAnimation(topLeftContainer, playerShakeAnimation);

        playerHPBar.updateHPAnimated(playerHPBar.getHP() - currentQuestion.getWeightage() * opponentMultiplier, 0.7f);
        playerMultiplier = 1;
        opponentMultiplier += 1;
        updateMultiplierLabels();
        opponentComboWidget.animate();
    }

    private void updateMultiplierLabels() {
        playerComboWidget.updateCombo(playerMultiplier);
        opponentComboWidget.updateCombo(opponentMultiplier);
    }

    private void handleOptionSelected() {
        int optionIndex = moveSelectBox.getSelectorIndex();

        if (optionIndex == currentQuestion.getCorrectChoiceIndex()) {
            playerCorrect();
        } else {
            playerWrong();
        }
        dialogBox.animateText(dialogText, 0.03f, () -> awaitingNextQuestion = true);
    }

    private float getBGMVolumeLimit(Question question) {
        if(question.getType() == QuestionType.LISTENING) {
            return 0;
        }
        return 15 / 100f;
    }

    @Override
    public void show() {
        transitions.startFadeInTransition(1.5f, 0.5f, null);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(1, 1, 1, 1);

        if(!transitions.isRunning() &&
                initialAnimationEnded &&
                !(playerShakeAnimation != null && playerShakeAnimation.isRunning()) &&
                !(opponentShakeAnimation != null && opponentShakeAnimation.isRunning()) &&
                !(playerMoveAnimation != null && playerMoveAnimation.isRunning()) &&
                !(opponentMoveAnimation != null && opponentMoveAnimation.isRunning()) &&
                dialogBox.isFinished() &&
                !playerHPBar.isAnimating() &&
                !opponentHPBar.isAnimating()) {

            // Check if game over. If opponent won, show dialog
            if(!battleFinished && (playerHPBar.getHP() <= 0 || opponentHPBar.getHP() <= 0)) {
                battleFinished = true;
                playerWon = playerHPBar.getHP() > 0;
                awaitingNextQuestion = false;
                defeatedSound.play();

                // TODO: Display statistics before closing battle screen
                String stats = String.format("[%s]\n%s%s: %d\n%s: %d\n%s: %d",
                        getString("battle_statistics"),
                        perfectRun ? String.format("%s\n", getString("perfect")) : "",
                        getString("total_correct"), totalCorrect,
                        getString("highest_combo"),highestCombo,
                        getString("total_wrong"), totalWrong);

                if(playerWon) {
                    dialog = null;
                    opponentMoveAnimation = new MoveAnimation(topRightContainer,
                            topRightContainer.getX(), 0 - topRightContainer.getHeight(), 0.7f, 0, null, false);
                    animations.startAnimation(topRightContainer, opponentMoveAnimation);
                } else {
                    playerMoveAnimation = new MoveAnimation(topLeftContainer,
                            topLeftContainer.getX(), 0 - topLeftContainer.getHeight(), 0.7f, 0,
                            (animationRenderer) -> {
                                dialog = opponent.getWinDialog();
                                Dialog.Line firstLine = dialog.getLines()[0];
                                playVoice(firstLine.getAudio(), 15 / 100f);
                                dialogLineIndex = 0;
                                dialogBox.animateTextWithSubtitle(firstLine.getOriginalText(),
                                        firstLine.getSubtitle(), 0.03f, null);
                            }, false);
                    animations.startAnimation(topLeftContainer, playerMoveAnimation);
                }
            }

            handleKeyPress();
        }

        batch.begin();

        // Draw the player and opponent sprites
        Texture playerSprite = player.getBattleSprite();
        float playerHeight = topLeftContainer.getHeight();
        float playerWidth = ImageUtil.getScaledDimension(playerSprite, ImageUtil.SCALE, playerHeight);
        float playerX = topLeftContainer.getX() + topLeftContainer.getWidth() / 2 - playerWidth / 2;

        Texture opponentSprite = opponent.getBattleSprite();
        float opponentHeight = topRightContainer.getHeight();
        float opponentWidth = ImageUtil.getScaledDimension(opponentSprite, ImageUtil.SCALE, opponentHeight);
        float opponentX = topRightContainer.getX() + topRightContainer.getWidth() / 2 - opponentWidth / 2;

        // On the very first frame, set the initial position of player and opponent
        if(!initialAnimationEnded) {
            if(initialAnimationTimeElapsed == 0) {
                initialPlayerX = topLeftContainer.getX() - playerWidth;
                initialOpponentX = topRightContainer.getX() + topRightContainer.getWidth() + opponentWidth;
                playerX = initialPlayerX;
                opponentX = initialOpponentX;
            } else if(initialAnimationTimeElapsed < initialAnimationTotalTime) {
                float timeElapsedRatio = initialAnimationTimeElapsed / initialAnimationTotalTime;
                float playerTargetPositionDiff = playerX - initialPlayerX;
                playerX = initialPlayerX + timeElapsedRatio * playerTargetPositionDiff;
                float opponentTargetPositionDiff = Math.abs(opponentX - initialOpponentX);
                opponentX = initialOpponentX - timeElapsedRatio * opponentTargetPositionDiff;
            } else if(initialAnimationTimeElapsed >= initialAnimationTotalTime) {
                // Called once, when the initial transition ends
                Gdx.input.setInputProcessor(stage);
                initialAnimationEnded = true;
                playerHPBar.setVisible(true);
                playerHPBar.updateHP(player.getMaxHP());
                opponentHPBar.setVisible(true);
                opponentHPBar.updateHP(opponent.getMaxHP());
                dialogBox.setVisible(true);
                moveSelectBox.renderOptions();
                moveSelectBox.setVisible(true);
                playerComboWidget.setVisible(true);
                opponentComboWidget.setVisible(true);
                dialogBox.animateText(currentQuestion.getQuestion(), 0.03f, null);
                playVoice(currentQuestion.getVoice(), getBGMVolumeLimit(currentQuestion));
            }
            initialAnimationTimeElapsed += delta;
        }

        // For defeat animation
        float playerY = playerMoveAnimation == null ? topLeftContainer.getY() : playerMoveAnimation.getCurrentY();
        float opponentY = opponentMoveAnimation == null ? topRightContainer.getY() : opponentMoveAnimation.getCurrentY();
        batch.draw(playerSprite, playerX, playerY,
                playerWidth, playerHeight);
        batch.draw(opponentSprite, opponentX, opponentY,
                opponentWidth, opponentHeight);

        whiteBackgroundDrawable.draw(batch, 0, 0, table.getWidth(), topLeftContainer.getY());

        batch.end();

        stage.draw();
        stage.act();

        if(transitions.isRunning())
            transitions.render(delta);

        animations.render(delta);
    }

    @Override
    public AbstractScreen clone() throws CloneNotSupportedException {
        return new BattleScreen(player, opponent, redirectScreen);
    }
}
