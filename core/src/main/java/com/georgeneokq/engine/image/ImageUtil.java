package com.georgeneokq.engine.image;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class ImageUtil {

    public final static float SCALE = -1;

    public static float getScaledDimension(Texture texture, float width, float height) {
        if((width == SCALE && height == SCALE) || (width != SCALE && height != SCALE))
            throw new IllegalArgumentException("One and only one of the dimensions should be specified as SCALE");

        // Get sprite original dimensions
        float originalWidth = texture.getWidth();
        float originalHeight = texture.getHeight();

        // Perform scaling according to which was specified as SCALE
        if(width == SCALE) {
            float ratio = originalHeight / height;
            return originalWidth / ratio;
        } else {
            float ratio = originalWidth / width;
            return originalHeight / ratio;
        }
    }

    public static float getScaledDimension(Sprite sprite, float width, float height) {
        if((width == SCALE && height == SCALE) || (width != SCALE && height != SCALE))
            throw new IllegalArgumentException("One and only one of the dimensions should be specified as SCALE");

        // Get sprite original dimensions
        float originalWidth = sprite.getWidth();
        float originalHeight = sprite.getHeight();

        // Perform scaling according to which was specified as SCALE
        if(width == SCALE) {
            float ratio = originalHeight / height;
            return originalWidth / ratio;
        } else {
            float ratio = originalWidth / width;
            return originalHeight / ratio;
        }
    }
}
