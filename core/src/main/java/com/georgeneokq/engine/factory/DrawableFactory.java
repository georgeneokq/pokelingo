package com.georgeneokq.engine.factory;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class DrawableFactory {
    public static Drawable fromTextureAtlas(TextureAtlas textureAtlas, String spriteName) {
        return new TextureRegionDrawable(textureAtlas.findRegion(spriteName));
    }

    public static Drawable fromTexture(Texture texture) {
        return new TextureRegionDrawable(texture);
    }

    public static Drawable fromPixmap(Pixmap pixmap) {
        return new TextureRegionDrawable(new TextureRegion(new Texture(pixmap)));
    }

    public static Drawable fromColor(Color color) {
        Pixmap pixmap = new Pixmap(1,1, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Drawable drawable = fromPixmap(pixmap);
        pixmap.dispose();
        return drawable;
    }
}
