package io.github.example.UIComponents;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.example.Aircraft;

public class BrakeDataUI extends FlightDataUI{
    public BrakeDataUI(Aircraft aircraft, Viewport viewport, ShapeRenderer shape, SpriteBatch batch, float x, float y) {
        super(aircraft, viewport, shape, batch, x, y);
    }

    @Override
    public void writeValues() {
        if (aircraft.getGear().isBraking()) {
            font.draw(batch, "Braking", x, y);
        }
    }
}
