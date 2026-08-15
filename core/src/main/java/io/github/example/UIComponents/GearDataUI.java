package io.github.example.UIComponents;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.example.Aircraft;
import io.github.example.AircraftComponents.Gear;

public class GearDataUI extends FlightDataUI {
    private final Gear gear;

    public GearDataUI(Aircraft aircraft, Viewport viewport, ShapeRenderer shape, SpriteBatch batch, float x, float y) {
        super(aircraft, viewport, shape, batch, x, y);
        gear = aircraft.getGear();
    }

    @Override
    public void writeValues() {
        if (gear.isBraking()) {
            font.draw(batch, "Braking", x, y + 20);
        }

        if (gear.getGearPosition() == io.github.example.GearPosition.UP) {
            font.draw(batch, "Gear: Up", x, y);
        } else if (gear.getGearPosition() == io.github.example.GearPosition.DOWN) {
            font.draw(batch, "Gear: Down", x, y);
        } else if (gear.getGearPosition() == io.github.example.GearPosition.EXTENDING) {
            font.draw(batch, "Gear: Extending", x, y);
        } else if (gear.getGearPosition() == io.github.example.GearPosition.RETRACTING) {
            font.draw(batch, "Gear: Retracting", x, y);
        }

    }
}
