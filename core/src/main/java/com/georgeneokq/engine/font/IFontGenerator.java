package com.georgeneokq.engine.font;

import com.badlogic.gdx.graphics.g2d.BitmapFont;

public interface IFontGenerator {
    BitmapFont generate(int size, String characters);
}
