package com.georgeneokq.game.widget;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Scaling;
import com.georgeneokq.game.Globals;
import com.georgeneokq.engine.actor.Controls;

import java.util.ArrayList;
import java.util.List;

public class SelectionBox extends Table {

    private List<Label> labels;
    private List<Image> arrows;
    private Controls controls;
    private String[] choices;
    private Table table;

    private String selection;
    private int selectionIndex;

    private Sound selectSound;

    private AssetManager assetManager;

    private float fontScale;

    public SelectionBox(String[] choices, Controls controls, float fontScale, Skin skin) {
        super(skin);
        this.assetManager = Globals.getInstance().getAssetManager();
        this.choices = choices;
        this.controls = controls;
        this.fontScale = fontScale;
        this.setBackground("optionbox");
        selectSound = assetManager.get("audio/sound_effects/select.mp3");
        renderSelectionBox();
        setSelection(0);
    }

    public void renderSelectionBox() {
        if(table != null) {
            table.remove();
        }
        table = new Table();
        labels = new ArrayList<>();
        arrows = new ArrayList<>();

        Skin skin = getSkin();
        for(String choice : choices) {
            Table wrapper = new Table();
            Label label = new Label(choice, skin);
            label.setFontScale(fontScale);
            labels.add(label);
            table.add(label).fill().expand();

            Image arrow = new Image(skin, "arrow");
            arrow.setScaling(Scaling.none);
            arrows.add(arrow);

            wrapper.add(arrow).padLeft(5);
            wrapper.add(label).spaceLeft(5).expand().fill().left();

            table.add(wrapper);
            table.row().space(10, 0, 10, 0);
        }

        add(table).pad(0, 15, 0, 15);
    }

    @Override
    public void act(float delta) {
        handleKeyPress();
    }

    public String getSelection() {
        return choices[selectionIndex];
    }

    public void clearSelection() {
        selection = null;
    }

    private void handleKeyPress() {
        if(!isShown())
            return;

        if(Gdx.input.isKeyJustPressed(controls.getUpKey())) {
            moveUp();
        } else if(Gdx.input.isKeyJustPressed(controls.getDownKey())) {
            moveDown();
        }

        setSelection(selectionIndex);
    }

    private void moveUp() {
        if(selectionIndex == 0)
            return;

        selectionIndex -= 1;
        selectSound.play();
    }
    private void moveDown() {
        if(selectionIndex + 1 == choices.length)
            return;

        selectionIndex += 1;
        selectSound.play();
    }

    private void setSelection(int index) {
        selectionIndex = index;
        for (int i = 0; i < labels.size(); i++) {
            Image arrow = arrows.get(i);
            if (i == index) {
                arrow.setVisible(true);
            } else {
                arrow.setVisible(false);
            }
        }
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        setSelection(0);
    }

    public boolean isShown() {
        return isVisible();
    }
}
