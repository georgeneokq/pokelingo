package com.georgeneokq.engine.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.georgeneokq.engine.factory.DrawableFactory;
import com.georgeneokq.game.Globals;
import com.georgeneokq.game.screen.MainMenuScreen;
import com.georgeneokq.game.skin.PokemonSkinGenerator;
import com.georgeneokq.engine.manager.ScreensManager;
import com.georgeneokq.engine.manager.SettingsManager;
import com.georgeneokq.engine.settings.BooleanSetting;
import com.georgeneokq.engine.settings.DropdownSelectSetting;
import com.georgeneokq.engine.settings.HotkeySetting;
import com.georgeneokq.engine.settings.NumberSetting;
import com.georgeneokq.engine.settings.Setting;
import com.georgeneokq.engine.settings.SettingsGroup;
import com.georgeneokq.engine.settings.TextSetting;

import java.util.List;

import com.georgeneokq.engine.font.FontEnum;
import com.georgeneokq.engine.font.FontGenerator;

public class SettingsScreen extends AbstractScreen {

    private ScreensManager screensManager;
    private SettingsManager settingsManager;
    private AssetManager assetManager;

    private Globals globals;

    private Stage stage;
    private Skin pokemonSkin;
    private Skin engineDefaultSkin;

    private HotkeySetting hotkeySetting;
    private boolean settingHotkeyMode = false;
    private TextButton hotkeyTextButton = null;

    private List<SettingsGroup> settings;

    public SettingsScreen() {}

    @Override
    public void initialize() {
        globals = Globals.getInstance();
        this.screensManager = ScreensManager.getInstance();
        this.settingsManager = SettingsManager.getInstance();
        this.assetManager = globals.getAssetManager();

        Viewport viewport = new FitViewport(globals.resolutionWidth, globals.resolutionHeight);
        viewport.setScreenPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
        viewport.setScreenWidth(globals.resolutionWidth);
        viewport.setScreenHeight(globals.resolutionHeight);
        stage = new Stage(viewport);
        pokemonSkin = PokemonSkinGenerator.getInstance().generateSkin(assetManager);
        engineDefaultSkin = assetManager.get("skin/cloud-form-ui.json");

        settings = settingsManager.getSettingsCopy();

        buildStage();
    }

    private void handleInputs() {
        if(settingHotkeyMode)
            return;
        if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            screensManager.changeScreen(MainMenuScreen.class);
        }
    }

    private void buildStage() {
        stage.addListener(new InputListener()
        {
            @Override
            public boolean keyDown(InputEvent event, int keycode)
            {
                if(settingHotkeyMode && hotkeyTextButton != null && hotkeySetting != null) {
                    String hotkeyString = Input.Keys.toString(keycode);
                    hotkeyTextButton.setText(hotkeyString);
                    hotkeySetting.setValue(hotkeyString);
                    settingHotkeyMode = false;
                }
                return true;
            }
        });

        Drawable inputGroupBackground = DrawableFactory.fromColor(new Color(0x22a87e00));

        float labelMaxWidth = 150;
        FontGenerator fontGenerator = FontGenerator.getInstance();
        BitmapFont firstGroupLabelFont = fontGenerator.createFont(FontEnum.DEFAULT.name(), 30);
        BitmapFont secondGroupLabelFont = fontGenerator.createFont(FontEnum.DEFAULT.name(), 26);
        BitmapFont thirdGroupLabelFont = fontGenerator.createFont(FontEnum.DEFAULT.name(), 22);
        BitmapFont normalFont = fontGenerator.createFont(FontEnum.DEFAULT.name(), 18);
        BitmapFont[] fonts = new BitmapFont[] {
                firstGroupLabelFont,
                secondGroupLabelFont,
                thirdGroupLabelFont
        };

        // Create table to place the widgets
        Table table = new Table(engineDefaultSkin);
        table.padLeft(50);
        table.padRight(50);

        // Loop through settings and place widgets for each
        for(int i = 0; i < settings.size(); i++) {
            SettingsGroup settingsGroup = settings.get(i);

            float groupTopPadding = i == 0 ? 0 : 30;

            // --- dynamic generation of widgets ---
            SettingsGroup.StatefulTraverseCallback callback =
                    (currentSettingsGroup, parentGroupNames, depth) -> {

                        // Scale the text down smaller as it goes down the tree.
                        // Scale: 1 - (depth * 0.2)
                        // TODO: At depth 5 and above, the text will not render
                        //  as the formula for scaling causes font scale to become 0.
                        //  Limit the depth to a max of 4 in SettingsParser.

                        // Draw the label for the settings group
                        float groupNameLabelPadding = 10;
                        BitmapFont font = depth >= fonts.length ? fonts[fonts.length - 1] : fonts[depth];
                        Label.LabelStyle labelStyle = new Label.LabelStyle(font, null);
                        Label groupNameLabel = new Label(getString(currentSettingsGroup.getName()), labelStyle);

                        table.row()
                                .padBottom(groupNameLabelPadding)
                                .padTop(groupNameLabelPadding);
                        table.add(groupNameLabel).padTop(groupTopPadding).align(Align.left);
                        table.row();

                        for(Setting setting: currentSettingsGroup.getSettings()) {
                            Label settingNameLabel = new Label(getString(setting.getLabel()),
                                    new Label.LabelStyle(normalFont, null));

                            // Place a dropdown select
                            if(setting.getClass() == DropdownSelectSetting.class) {
                                table.add(settingNameLabel).align(Align.left).padLeft(5);
                                DropdownSelectSetting dropdownSelectSetting =
                                        (DropdownSelectSetting) setting;
                                SelectBox<String> dropdown = new SelectBox<>(engineDefaultSkin);
                                dropdown.setItems(dropdownSelectSetting.getChoices());
                                dropdown.setSelected(dropdownSelectSetting.getValue());
                                dropdown.addListener(new ChangeListener() {
                                    @Override
                                    public void changed(ChangeEvent event, Actor actor) {
                                        dropdownSelectSetting.setValue((String) dropdown.getSelected());
                                    }
                                });
                                table.row();
                                table.add(dropdown).minWidth(labelMaxWidth).align(Align.left);
                            }

                            // Place a checkbox
                            if(setting.getClass() == BooleanSetting.class) {
                                BooleanSetting booleanSetting = (BooleanSetting) setting;
                                CheckBox checkBox = new CheckBox(null, engineDefaultSkin);
                                checkBox.setChecked(booleanSetting.getValue());
                                checkBox.addListener(new ChangeListener() {
                                    @Override
                                    public void changed(ChangeEvent event, Actor actor) {
                                        booleanSetting.setValue((boolean) !booleanSetting.getValue());
                                    }
                                });
                                Table wrapper = new Table();
                                wrapper.add(settingNameLabel).width(labelMaxWidth);
                                wrapper.add(checkBox).width(50).align(Align.right);
                                table.add(wrapper).align(Align.left);
                            }

                            // Place a number slider and a number displayed beside it
                            if(setting.getClass() == NumberSetting.class) {
                                table.add(settingNameLabel).align(Align.left);
                                NumberSetting numberSetting = (NumberSetting) setting;

                                // Label
                                float value = numberSetting.getValue();

                                String labelText = numberSetting.getStepSize() == 1 ?
                                        String.format("%.0f", value) : String.format("%.2f", value);
                                Label numberLabel = new Label(labelText, pokemonSkin);

                                // Slider
                                Slider slider = new Slider(numberSetting.getMin(),
                                        numberSetting.getMax(), numberSetting.getStepSize(), false, engineDefaultSkin);
                                slider.setValue(numberSetting.getValue());
                                slider.addListener(new ChangeListener() {
                                    @Override
                                    public void changed(ChangeEvent event, Actor actor) {
                                        numberSetting.setValue((float) slider.getValue());

                                        float value = numberSetting.getValue();
                                        String labelText = numberSetting.getStepSize() == 1 ?
                                                String.format("%.0f", value) : String.format("%.2f", value);
                                        numberLabel.setText(labelText);
                                    }
                                });

                                // Wrap the slider and label together
                                Table wrapper = new Table();
                                wrapper.add(slider);
                                wrapper.add(numberLabel).padLeft(20);
                                table.row();
                                table.add(wrapper).align(Align.left);
                            }

                            // Place a text box
                            if(setting.getClass() == TextSetting.class) {
                                table.add(settingNameLabel).align(Align.left);
                                TextSetting textSetting = (TextSetting) setting;
                                TextField textField = new TextField(textSetting.getValue(), engineDefaultSkin);
                                textField.setMaxLength(textSetting.getMaxLength());
                                textField.setTextFieldFilter((textField1, c) -> {
                                    if(textSetting.isCharDisallowed(c)) return false;
                                    return true;
                                });
                                textField.addListener(new ChangeListener() {
                                    @Override
                                    public void changed(ChangeEvent event, Actor actor) {
                                        textSetting.setValue(textField.getText());
                                    }
                                });
                                table.row();
                                table.add(textField).align(Align.left);
                            }

                            // Place a button which shows a popup
                            if(setting.getClass() == HotkeySetting.class) {
                                HotkeySetting hotkeySetting = (HotkeySetting) setting;
                                TextButton textButton = new TextButton(hotkeySetting.getValueString(), engineDefaultSkin);
                                textButton.addListener(new ClickListener() {
                                    @Override
                                    public void clicked(InputEvent event, float x, float y) {
                                        settingHotkeyMode = true;
                                        hotkeyTextButton = textButton;
                                        SettingsScreen.this.hotkeySetting = hotkeySetting;
                                    }
                                });
                                Table wrapper = new Table();
                                wrapper.add(settingNameLabel).width(labelMaxWidth);
                                wrapper.add(textButton).center();
                                table.add(wrapper).align(Align.left);
                            }

                            table.row().padTop(10).padBottom(10);
                        }
                    };

            // Execute callback on the current root SettingsGroup
            settingsGroup.statefulTraverse(callback, true);

            if(i < settings.size() - 1) {
                // TODO: Add visible border to separate root groups
                table.row().height(30).expandX();
            }
        }

        // --- finished dynamic generation of widgets ---
        TextButton button = new TextButton(getString("apply_changes"), pokemonSkin);
        button.addListener(new ClickListener() {
                               @Override
                               public void clicked(InputEvent event, float x, float y) {
                                   Globals.getInstance().playSoundEffect("select");
                                   settingsManager.saveSettings(settings);
                                   settingsManager.applySettings();
                                   screensManager.changeScreen(MainMenuScreen.class);
                               }
                           }
        );

        table.add(button)
                .align(Align.left)
                .padTop(50)
                .padBottom(50);

        Container<Table> container = new Container<>(table);

        ScrollPane scrollPane = new ScrollPane(container);
        scrollPane.setFillParent(true);
        scrollPane.setScrollingDisabled(true, false);
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

        handleInputs();

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        // Viewport viewport = stage.getViewport();
        // viewport.update(width, height, true);
    }

    @Override
    public AbstractScreen clone() {
        return new SettingsScreen();
    }
}
