package com.georgeneokq.game.widget;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.georgeneokq.game.Globals;

/**
 * Widget that displays HP.
 *
 */
public class HPBar extends Table {

    private Skin skin;
    private Label hpLabel;

    private boolean animating;
    private int hpBeforeAnimation;
    private int displayedHP;
    private float timeElapsed;
    private float totalAnimationTime;
    private float timeBeforeAnimation;
    private int maxHP;
    private int hp;
    private Sound hpDecreaseSound;
    private boolean animationScheduled = false;
    private int newHP;

    public HPBar(int maxHP, Skin skin) {
        super();
        this.maxHP = maxHP;
        this.skin = skin;
        hpLabel = new Label("", skin);
        add(hpLabel).fillX().expandX().align(Align.left);
        hpDecreaseSound = Globals.getInstance().getAssetManager()
                .get("audio/sound_effects/hp_decrease.mp3", Sound.class);
    }

    public void updateHP(int newHP) {
        hp = newHP;
        displayedHP = hp;
    }

    public void updateHPAnimated(int newHP, float timeBeforeAnimation) {
        updateHPAnimated(newHP, timeBeforeAnimation, 2.2f);
    }

    public void updateHPAnimated(int newHP, float timeBeforeAnimation, float animationTime) {
        if(newHP < 0)
            newHP = 0;

        this.newHP = newHP;
        this.timeBeforeAnimation = timeBeforeAnimation;
        this.totalAnimationTime = animationTime;
        timeElapsed = 0;
        animationScheduled = true;
    }

    private void startAnimation(int newHP, float animationTime) {
        hpDecreaseSound.play();
        displayedHP = hp;
        hpBeforeAnimation = displayedHP;
        hp = newHP;
        timeElapsed = 0;
        totalAnimationTime = animationTime;
        animating = true;
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if(animationScheduled) {
            timeElapsed += delta;
            if (timeElapsed >= timeBeforeAnimation) {
                timeElapsed = 0;
                animationScheduled = false;
                startAnimation(newHP, totalAnimationTime);
            }
            return;
        }

        // Animate HP
        if(animating) {
            timeElapsed += delta;
            if(displayedHP == hp && timeElapsed >= totalAnimationTime) {
                animating = false;
            } else {
                // Calculate amount of HP to decrease
                float ratio;
                try {
                    ratio = timeElapsed / totalAnimationTime;
                } catch (ArithmeticException e) {
                    ratio = 1;
                }

                float diff = Math.abs(hpBeforeAnimation - hp);
                float ratioDiff = diff * ratio;
                displayedHP = Math.round(hpBeforeAnimation - ratioDiff);

                if(displayedHP <= hp) {
                    displayedHP = hp;
                }
            }
        }
        hpLabel.setText(String.format("HP: %d/%d", displayedHP, maxHP));
    }

    public int getHP() {
        return hp;
    }

    public boolean isAnimating() {
        return animating;
    }
}