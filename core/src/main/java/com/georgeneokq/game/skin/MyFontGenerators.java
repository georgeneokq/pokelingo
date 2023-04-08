package com.georgeneokq.game.skin;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.georgeneokq.engine.font.FontGenerator;

public class MyFontGenerators {

    public static BitmapFont notosansComboFont(int size, String characters) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("font/NotoSansJP-Light.otf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = size;
        parameter.color = new Color(0x61E8A0FF);
        parameter.shadowColor = new Color(0, 0, 0, 1);
        parameter.borderColor = new Color(0, 0, 0, 1);
        parameter.borderWidth = 5;
        parameter.shadowOffsetX = 1;
        parameter.shadowOffsetY = 1;

        if(characters != null) {
            parameter.characters = FontGenerator.getUniqueCharacters(characters);
        }
        else {
            parameter.characters = FontGenerator.asciiCharacters;
        }

        BitmapFont font = generator.generateFont(parameter);
        generator.dispose();

        return font;
    }
}
