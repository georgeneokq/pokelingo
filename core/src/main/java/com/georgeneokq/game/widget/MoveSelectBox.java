package com.georgeneokq.game.widget;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Scaling;
import com.georgeneokq.game.Globals;

/**
 * This selection box has four labels, enumerated like so:
 * 		0	1
 * 		2	3
 *
 */
public class MoveSelectBox extends Table {

    private float longestLabelWidth = 0;
    private int selectorIndex = 0;

    private Label[] labels = new Label[4];
    private Image[] arrows = new Image[4];

    private Table table;
    private MoveSelectOptions options;
    private Sound selectSound;
    private AssetManager assetManager;

    // Workaround for table growing bigger upon option re-render
    float originalWidth = 0;
    float originalHeight = 0;

    public MoveSelectBox(Skin skin, MoveSelectOptions options) {
        super(skin);
        this.options = options;
        this.setBackground("optionbox");
        assetManager = Globals.getInstance().getAssetManager();

        selectSound = assetManager.get("audio/sound_effects/select.mp3");
    }

    public MoveSelectBox(Skin skin) {
        this(skin, new MoveSelectOptions(
                "-",
                "-",
                "-",
                "-"
        ));
    }

    public void renderOptions() {
        if(table != null) {
            Cell cell = getCell(table);
            table.remove();
            getCells().removeValue(cell, true);
            invalidate();
            if(originalWidth != 0 && originalHeight != 0) {
                setWidth(originalWidth);
                setHeight(originalHeight);
            }
        }

        longestLabelWidth = 0;
        table = new Table();
        table.setSize(getWidth(), getHeight());
        if(getWidth() > 0 && getHeight() > 0) {
            originalWidth = getWidth();
            originalHeight = getHeight();
        }

        row();
        add(table).expand().fill().pad(5);

        Skin skin = getSkin();

        for(int i = 0; i < 4; i++) {
            Label label = new Label(options.getOption(i), skin);
            Image arrow = new Image(skin, "arrow");
            labels[i] = label;
            arrows[i] = arrow;
            arrows[i].setScaling(Scaling.none);

            Table wrapper = new Table();
            wrapper.add(arrow).padLeft(50);
            wrapper.add(label).spaceLeft(10).expand().fill().left();
            table.add(wrapper).width(table.getWidth() / 2).height(table.getHeight() / 2).fill();

            if((i + 1) % 2 == 0) {
                table.row();
            }
        }

        setSelection(0);
    }

    public void setLabel(int index, String text) {
        labels[index].setText(text);
    }

    public int getSelection() {
        return selectorIndex;
    }

    public String getSelectedOption() {
        return labels[selectorIndex].getText().toString();
    }

    public void moveUp() {
        if (selectorIndex == 0) {
            return;
        }
        if (selectorIndex == 1) {
            return;
        }
        if (selectorIndex == 2) {
            selectSound.play();
            setSelection(0);
            return;
        }
        if (selectorIndex == 3) {
            selectSound.play();
            setSelection(1);
            return;
        }
    }

    public void moveDown() {
        if (selectorIndex == 0) {
            selectSound.play();
            setSelection(2);
            return;
        }
        if (selectorIndex == 1) {
            selectSound.play();
            setSelection(3);
            return;
        }
        if (selectorIndex == 2) {
            return;
        }
        if (selectorIndex == 3) {
            return;
        }
    }

    public void moveLeft() {
        if (selectorIndex == 0) {
            return;
        }
        if (selectorIndex == 1) {
            selectSound.play();
            setSelection(0);
            return;
        }
        if (selectorIndex == 2) {
            return;
        }
        if (selectorIndex == 3) {
            selectSound.play();
            setSelection(2);
            return;
        }
    }

    public void moveRight() {
        if (selectorIndex == 0) {
            selectSound.play();
            setSelection(1);
            return;
        }
        if (selectorIndex == 1) {
            return;
        }
        if (selectorIndex == 2) {
            selectSound.play();
            setSelection(3);
            return;
        }
        if (selectorIndex == 3) {
            return;
        }
    }

    private void setSelection(int index) {
        selectorIndex = index;
        for (int i = 0; i < labels.length; i++) {
            if (i == index) {
                arrows[i].setVisible(true);
            } else {
                arrows[i].setVisible(false);
            }
        }
    }

    public int getSelectorIndex() {
        return selectorIndex;
    }

    public void setOptions(MoveSelectOptions options) {
        this.options = options;
    }
}
