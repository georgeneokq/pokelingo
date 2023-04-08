package com.georgeneokq.game.widget;

import com.badlogic.gdx.scenes.scene2d.ui.Table;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;

/**
 * A dialogue box that can animate text.
 */
public class DialogBox extends Table {

    private float fontScale;
    private String targetOriginalText = "";
    private String targetSubtitleText = "";
    private float originalTextAnimTimer = 0f;
    private float subtitleTextAnimTimer = 0f;
    private float originalTextAnimationTotalTime = 0f;
    private float subtitleTextAnimationTotalTime = 0f;
    private STATE state = STATE.IDLE;
    private Runnable onFinish = null;

    private Label originalTextLabel;
    private Label subtitleTextLabel;

    private enum STATE {
        ANIMATING,
        IDLE,
    }

    public DialogBox(Skin skin, float fontScale) {
        super(skin);
        this.fontScale = fontScale;
        this.setBackground("dialoguebox");
        originalTextLabel = new Label("", skin);
        originalTextLabel.setAlignment(Align.topLeft);
        originalTextLabel.setWrap(true);
        subtitleTextLabel = new Label("", skin);
        subtitleTextLabel.setAlignment(Align.topLeft);
        subtitleTextLabel.setWrap(true);

        originalTextLabel.setFontScale(fontScale);
        subtitleTextLabel.setFontScale(fontScale);
        this.add(originalTextLabel).expandX().fillX().align(Align.topLeft).pad(0f);
        this.row();
    }

    /**
     * Animate the specified text.
     * @param text The text to animate.
     * @param timePerCharacter Time it takes for each character to print.
     * @param onFinish A callback that will be called when the animation finishes.
     */
    public void animateText(String text, float timePerCharacter, Runnable onFinish) {
        subtitleTextLabel.remove();
        targetOriginalText = text;
        originalTextAnimationTotalTime = text.length() * timePerCharacter;
        state = STATE.ANIMATING;
        originalTextAnimTimer = 0f;
        subtitleTextAnimTimer = 0f;
        this.onFinish = onFinish;
    }

    public void animateTextWithSubtitle(String originalText, String subtitleText, float timePerCharacter, Runnable onFinish) {
        subtitleTextLabel.remove();
        row();
        this.add(subtitleTextLabel).expandX().fillX().align(Align.topLeft).pad(0f);
        targetOriginalText = originalText;
        targetSubtitleText = subtitleText;

        originalTextAnimationTotalTime = originalText.length() * timePerCharacter;

        // Calculate time per character for subtitles based on original text animation time
        float subtitleTimePerCharacter = originalTextAnimationTotalTime / subtitleText.length();
        subtitleTextAnimationTotalTime = subtitleText.length() * subtitleTimePerCharacter;

        state = STATE.ANIMATING;
        originalTextAnimTimer = 0f;
        subtitleTextAnimTimer = 0f;
        this.onFinish = onFinish;
    }

    /**
     * Check if the animation is finished.
     * @return True if the animation is finished, false otherwise.
     */
    public boolean isFinished() {
        return state == STATE.IDLE;
    }

    private void setOriginalText(String text) {
        if (!text.contains("\n")) {
            text += "\n";
        }
        this.originalTextLabel.setText(text);
    }

    private void setSubtitleText(String text) {
        if (!text.contains("\n")) {
            text += "\n";
        }
        this.subtitleTextLabel.setText(text);
    }

    @Override
    public void act(float delta) {
        if (state == STATE.ANIMATING) {
            originalTextAnimTimer += delta;
            subtitleTextAnimTimer += delta;
            if (originalTextAnimTimer > originalTextAnimationTotalTime) {
                originalTextAnimTimer = originalTextAnimationTotalTime;
            }

            if (subtitleTextAnimTimer > subtitleTextAnimationTotalTime) {
                subtitleTextAnimTimer = subtitleTextAnimationTotalTime;
            }

            if(originalTextAnimTimer >= originalTextAnimationTotalTime &&
                    subtitleTextAnimTimer >= subtitleTextAnimationTotalTime) {
                state = STATE.IDLE;
                if (onFinish != null) {
                    onFinish.run();
                }
            }

            String actuallyDisplayedOriginalText = "";
            int originalTextCharsToDisplay = (int) ((originalTextAnimTimer / originalTextAnimationTotalTime) * targetOriginalText.length());
            for (int i = 0; i < originalTextCharsToDisplay; i++) {
                actuallyDisplayedOriginalText += targetOriginalText.charAt(i);
            }
            if (!actuallyDisplayedOriginalText.equals(originalTextLabel.getText().toString())) {
                setOriginalText(actuallyDisplayedOriginalText);
            }

            String actuallyDisplayedSubtitleText = "";
            int subtitleTextCharsToDisplay = (int) ((subtitleTextAnimTimer / subtitleTextAnimationTotalTime) * targetSubtitleText.length());
            for (int i = 0; i < subtitleTextCharsToDisplay; i++) {
                actuallyDisplayedSubtitleText += targetSubtitleText.charAt(i);
            }
            if (!actuallyDisplayedSubtitleText.equals(subtitleTextLabel.getText().toString())) {
                setSubtitleText(actuallyDisplayedSubtitleText);
            }
        }
    }
}
