package com.georgeneokq.game.skin;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.GdxRuntimeException;

import com.georgeneokq.engine.font.FontEnum;
import com.georgeneokq.engine.font.FontGenerator;

public class PokemonSkinGenerator {

    private static PokemonSkinGenerator skinGenerator;

    private PokemonSkinGenerator() { }

    public static PokemonSkinGenerator getInstance() {
        if(skinGenerator == null)
            skinGenerator = new PokemonSkinGenerator();

        return skinGenerator;
    }

    public Skin generateSkin(AssetManager assetManager) {
        return generateSkin(assetManager, 18);
    }

    public Skin generateSkin(AssetManager assetManager, int fontSize) {
        Skin skin = new Skin();

        if (!assetManager.isLoaded("game/uipack.atlas")) {
            throw new GdxRuntimeException("uipack.atlas was not loaded");
        }

        TextureAtlas uiAtlas = assetManager.get("game/uipack.atlas");

        NinePatch buttonSquareBlue = new NinePatch(uiAtlas.findRegion("dialoguebox"), 10, 10, 5, 5);
        skin.add("dialoguebox", buttonSquareBlue);

        NinePatch optionbox = new NinePatch(uiAtlas.findRegion("optionbox"),6, 6, 6, 6);
        skin.add("optionbox", optionbox);

        NinePatch battleinfobox = new NinePatch(uiAtlas.findRegion("battleinfobox"),14, 14, 5, 8);
        battleinfobox.setPadLeft((int)battleinfobox.getTopHeight());
        skin.add("battleinfobox", battleinfobox);

        skin.add("arrow", uiAtlas.findRegion("arrow"), TextureRegion.class);
        skin.add("hpbar_side", uiAtlas.findRegion("hpbar_side"), TextureRegion.class);
        skin.add("hpbar_bar", uiAtlas.findRegion("hpbar_bar"), TextureRegion.class);
        skin.add("green", uiAtlas.findRegion("green"), TextureRegion.class);
        skin.add("yellow", uiAtlas.findRegion("yellow"), TextureRegion.class);
        skin.add("red", uiAtlas.findRegion("red"), TextureRegion.class);
        skin.add("background_hpbar", uiAtlas.findRegion("background_hpbar"), TextureRegion.class);

        FontGenerator fontGenerator = FontGenerator.getInstance();
        BitmapFont font = fontGenerator.createFont(FontEnum.DEFAULT.name(), fontSize);
        skin.add("font", font);

        BitmapFont smallFont = assetManager.get("font/small_letters_font.fnt", BitmapFont.class);
        skin.add("small_letters_font", smallFont);

        // Label style
        LabelStyle labelStyle = new LabelStyle();
        labelStyle.font = skin.getFont("font");
        skin.add("default", labelStyle);

        LabelStyle labelStyleSmall = new LabelStyle();
        labelStyleSmall.font = skin.getFont("small_letters_font");
        skin.add("smallLabel", labelStyleSmall);

        // Text button style
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = skin.getFont("font");
        textButtonStyle.up = skin.getDrawable("dialoguebox");
        skin.add("default", textButtonStyle);

        // Window style
        Window.WindowStyle windowStyle = new Window.WindowStyle();
        windowStyle.titleFont = skin.getFont("font");
        windowStyle.titleFontColor = new Color(0xFFFFFFFF);
        windowStyle.background = new NinePatchDrawable(buttonSquareBlue);
        skin.add("default", windowStyle);

        return skin;
    }

}
