package io.github.example.UIComponents;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.example.Aircraft;
import io.github.example.SteeringMode;

public class SteeringModeDataUI extends FlightDataUI{
    private long changeSteeringModeTime;

    public SteeringModeDataUI(Aircraft aircraft, Viewport viewport, ShapeRenderer shape, SpriteBatch batch, float x, float y) {
        super(aircraft, viewport, shape, batch, x, y);
        changeSteeringModeTime = System.currentTimeMillis();
    }

    public void writeSteeringMode(SteeringMode mode){
        if (System.currentTimeMillis() - changeSteeringModeTime < 2000) {
            font.draw(batch, "Steering mode: " + mode.toString(), x, y);
        }
    }

    public void reset() {
        changeSteeringModeTime = System.currentTimeMillis();
    }
}
