package com.georgeneokq.engine.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import com.georgeneokq.engine.font.FontEnum;
import com.georgeneokq.engine.font.FontGenerator;

public class LoadingScreen extends AbstractScreen {

    public interface OnLoadListener {
        void onLoad();
    }

    private AssetManager assetManager;
    private OnLoadListener onLoadListener;
    private FontGenerator fontGenerator;
    private Skin engineDefaultSkin;

    private Stage stage;
    private ProgressBar progressBar;
    private Label percentageLabel;
    private Label statusLabel;

    public LoadingScreen(AssetManager assetManager, OnLoadListener onLoadListener) {
        this.assetManager = assetManager;
        this.onLoadListener = onLoadListener;
    }

    @Override
    public void initialize() {
        stage = new Stage(new ScreenViewport());

        engineDefaultSkin = new Skin(Gdx.files.internal("skin/cloud-form-ui.json"));

        // Generate font
        fontGenerator = FontGenerator.getInstance();
        BitmapFont font = fontGenerator.createFont(FontEnum.DEFAULT.name(), 18);
        font.getData().setLineHeight(18f);

        // Set label style
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;

        progressBar = new ProgressBar(0, 100, 0.1f, false, engineDefaultSkin);
        percentageLabel = new Label("0%", labelStyle);
        statusLabel = new Label("Loading...", labelStyle);

        Table table = new Table();
        table.setFillParent(true);
        table.setDebug(false);
        table.add(statusLabel).padBottom(30);
        table.row();
        table.add(progressBar).padBottom(10);
        table.row();
        table.add(percentageLabel);

        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        // For faster loading, pass a parameter to update() to specify
        // number of milliseconds to block and load before passing control
        // back to the render function
        if(assetManager.update(70)) {
            onLoadListener.onLoad();
        }

        ScreenUtils.clear(1, 1, 1, 1);
        float progress = assetManager.getProgress() * 100;
        progressBar.setValue(progress);
        percentageLabel.setText(String.format("%.2f%%", progress));

        stage.act(delta);
        stage.draw();
    }

    @Override
    public AbstractScreen clone() {
        return new LoadingScreen(assetManager, onLoadListener);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
