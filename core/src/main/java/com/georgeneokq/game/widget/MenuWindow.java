package com.georgeneokq.game.widget;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.georgeneokq.engine.menu.MenuItem;
import com.georgeneokq.game.Globals;
import com.georgeneokq.game.PlayerStats;

import java.util.List;

public class MenuWindow {

    private Globals globals;
    private List<MenuItem> menuItems;
    private String title;

    // For building window
    private Window window;
    private Label perfectRunsLabel;
    private Label highestComboLabel;
    private Label gymClearCountLabel;
    private PlayerStats playerStats;
    private Skin skin;
    private float width;
    private float height;
    private float fontScale;

    private boolean attached = false;

    public MenuWindow(List<MenuItem> menuItems, String title, float width, float height, Skin skin,
                      float fontScale) {
        this.globals = Globals.getInstance();
        this.menuItems = menuItems;
        this.skin = skin;
        this.title = title;
        this.width = width;
        this.height = height;
        this.fontScale = fontScale;

        buildWindow();
    }

    public void updatePlayerStats(PlayerStats playerStats) {
        this.playerStats = playerStats;
        perfectRunsLabel.setText(String.format("%s: %d", globals.getString("perfect_runs"), playerStats.getPerfectRuns()));
        highestComboLabel.setText(String.format("%s: %d", globals.getString("highest_combo"), playerStats.getHighestCombo()));
        gymClearCountLabel.setText(String.format("%s: %d", globals.getString("gym_clear_count"), playerStats.getGymClearCount()));
    }

    private void buildWindow() {
        window = new Window(title, skin);
        window.setFillParent(true);
        window.setMovable(false);
        window.setResizable(false);
        window.getTitleLabel().setVisible(false);

        perfectRunsLabel = new Label(String.format("%s: 0", globals.getString("perfect_runs")), skin);
        perfectRunsLabel.setFontScale(fontScale);
        highestComboLabel = new Label(String.format("%s: 0", globals.getString("highest_combo")), skin);
        highestComboLabel.setFontScale(fontScale);
        gymClearCountLabel = new Label(String.format("%s: 0", globals.getString("gym_clear_count")), skin);
        gymClearCountLabel.setFontScale(fontScale);

        window.add(perfectRunsLabel);
        window.row();
        window.add(highestComboLabel);
        window.row();
        window.add(gymClearCountLabel);

        int numItems = 3 + menuItems.size();

        // Display menu items in a column
        for(MenuItem menuItem : menuItems) {
            window.row();
            TextButton button = new TextButton(menuItem.getTitle(), skin);
            button.getLabel().setFontScale(fontScale);
            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    menuItem.playSound();
                    menuItem.getSelectListener().onSelect();
                }
            });
            Value height = Value.percentHeight(1f / (numItems + 2), window);
            window.add(button).expand().padTop(5).padBottom(5).width(window.getWidth() - 10).height(height);
        }
    }

    public void show(Stage stage) {
        if(attached)
            return;
        stage.addActor(window);
        attached = true;
    }

    public void hide() {
        if(!attached)
            return;
        window.remove();
        attached = false;
    }
}
