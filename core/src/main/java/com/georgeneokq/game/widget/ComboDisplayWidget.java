package com.georgeneokq.game.widget;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.georgeneokq.game.Globals;

public class ComboDisplayWidget extends Table {
    private int combo;

    private float maxFontScale = 2f;

    private float totalAnimationTime;
    private float elapsedTime;
    private boolean animating;
    private boolean peaked;
    private float originalFontScaleX = 1;
    private float originalFontScaleY = 1;
    private float currentFontScaleX;
    private float currentFontScaleY;
    private Label prefixLabel;
    private Label comboLabel;
    private Label.LabelStyle style;
    private BitmapFont font;
    private float fontSize;

    public ComboDisplayWidget(int initialCombo, Label.LabelStyle style, float fontSize) {
        this.style = style;
        this.fontSize = fontSize;
        font = style.font;

        prefixLabel = new Label(String.format("%s: x", getString("combo")), style);

        // The combo label content will be drawn using batch, so set the actual widget to invisible.
        // If the size increase happens by increasing the font size of the actual label,
        // surrounding widgets will be affected by the size change.
        comboLabel = new Label(String.valueOf(combo), style);
        comboLabel.setVisible(false);
        currentFontScaleX = comboLabel.getFontScaleX();
        currentFontScaleY = comboLabel.getFontScaleY();

        updateCombo(initialCombo);

        add(prefixLabel);
        add(comboLabel);
    }

    public void updateCombo(int combo) {
        this.combo = combo;
        comboLabel.setText(String.valueOf(combo));
    }

    public void animate() {
        animate(0.3f);
    }

    public void animate(float animationTime) {
        originalFontScaleX = comboLabel.getFontScaleX();
        originalFontScaleY = comboLabel.getFontScaleY();
        currentFontScaleX = originalFontScaleX;
        currentFontScaleY = originalFontScaleY;
        totalAnimationTime = animationTime;
        elapsedTime = 0;
        animating = true;
    }

    public void stopAnimation() {
        currentFontScaleX = originalFontScaleX;
        currentFontScaleY = originalFontScaleY;
        animating = false;
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if(animating && elapsedTime >= totalAnimationTime) {
            stopAnimation();
        }

        if(animating) {
            elapsedTime += delta;
            float halfAnimationTime = totalAnimationTime / 2;
            float fontScaleX = originalFontScaleX;
            float fontScaleY = originalFontScaleY;
            if(elapsedTime <= halfAnimationTime) {
                float ratio = elapsedTime / halfAnimationTime;
                float diffRatioX = (maxFontScale - originalFontScaleX) * ratio;
                float diffRatioY = (maxFontScale - originalFontScaleY) * ratio;
                fontScaleX += diffRatioX;
                fontScaleY += diffRatioY;
                peaked = true;
            } else if(peaked) {
                fontScaleX = maxFontScale;
                fontScaleY = maxFontScale;
                peaked = false;
            } else {
                float ratio = 1 / (elapsedTime / halfAnimationTime);
                float diffRatioX = (maxFontScale - originalFontScaleX) * ratio;
                float diffRatioY = (maxFontScale - originalFontScaleY) * ratio;
                fontScaleX += diffRatioX;
                fontScaleY += diffRatioY;
            }

            currentFontScaleX = fontScaleX;
            currentFontScaleY = fontScaleY;
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        float fontOriginalScaleX = font.getData().scaleX;
        float fontOriginalScaleY = font.getData().scaleY;

        // Scale up the font size before drawing the combo multiplier,
        // then revert it back after done drawing
        font.getData().setScale(currentFontScaleX, currentFontScaleY);
        font.draw(batch, String.valueOf(combo), getX() + comboLabel.getX(), getY() + comboLabel.getY() + fontSize);
        font.getData().setScale(fontOriginalScaleX, fontOriginalScaleY);
    }

    private String getString(String stringName) {
        return Globals.getInstance().getString(stringName);
    }

    public int getCombo() {
        return combo;
    }
}
