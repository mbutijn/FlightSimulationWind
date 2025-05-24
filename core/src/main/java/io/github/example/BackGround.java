package io.github.example;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class BackGround {
    private final Texture groundTexture, gradientAirTexture, upperAirTexture;
    private final Sprite groundSprite, gradientAirSprite, upperAirSprite;
    private final float border;

    public BackGround(){
        // generate the texture for ground, lower gradient and upper atmosphere
        groundTexture = generateConstantTexture(new Color(0.2f, 0.5f, 0.4f, 1));
        gradientAirTexture = generateLowerAirTexture();
        upperAirTexture = generateConstantTexture(new Color(0, 0, 0.54f, 1));

        // create sprites that covers the screen height
        groundSprite = new Sprite(groundTexture);
        gradientAirSprite = new Sprite(gradientAirTexture);
        upperAirSprite = new Sprite(upperAirTexture);

        border = 5000; // the height at which the color of the atmosphere above does not change
    }

    private Texture generateConstantTexture(Color color){
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.drawPixel(0, 0);

        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    private Texture generateLowerAirTexture() {
        Pixmap pixmap = new Pixmap(1, 100, Pixmap.Format.RGBA8888);

        for (int y = 0; y < 100; y++) {
            float alpha = 1 - 0.01f * y;
            Color color = new Color(0.53f * (1 - alpha) + 0f * alpha,
                    0.81f * (1 - alpha) + 0f * alpha,
                    0.98f * (1 - alpha) + 0.54f * alpha,
                    1);
            pixmap.setColor(color);
            pixmap.drawPixel(0, y);
        }

        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    public void render(SpriteBatch batch, OrthographicCamera camera, float screenWidth, float screenHeight) {
        float xLeft = camera.position.x - 0.5f * screenWidth * camera.zoom;
        float width = screenWidth * camera.zoom;

        // renderPolygon ground
        float lowerBound = camera.position.y - 0.5f * screenHeight * camera.zoom - border;
        if (lowerBound < 0) {
            groundSprite.setSize(width, lowerBound);
            groundSprite.setPosition(xLeft, 0);
            groundSprite.draw(batch);
        }

        // renderPolygon lower atmosphere
        gradientAirSprite.setSize(width, border);
        gradientAirSprite.setPosition(xLeft, 0);
        gradientAirSprite.draw(batch);

        // renderPolygon upper atmosphere
        float upperBound = camera.position.y + 0.5f * screenHeight * camera.zoom - border;
        if (upperBound > 0) {
            upperAirSprite.setSize(width, upperBound);
            upperAirSprite.setPosition(xLeft, border);
            upperAirSprite.draw(batch);
        }
    }

    public void dispose() {
        groundTexture.dispose();
        gradientAirTexture.dispose();
        upperAirTexture.dispose();
    }
}
