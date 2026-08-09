package io.github.example.UIComponents;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.example.Aircraft;
import io.github.example.utils.UnitConversionUtils;
import io.github.example.AircraftComponents.Wing;

public class SpeedDataUI extends FlightDataUI{
    public SpeedDataUI(Aircraft aircraft, Viewport viewport, ShapeRenderer shape, SpriteBatch batch, float x, float y) {
        super(aircraft, viewport, shape, batch, x, y);
    }

    public void writeValues() {
        Wing wing = aircraft.getWing();
        font.draw(batch, "IAS: " + formatOneDecimal(wing.getIndicatedAirspeed() * UnitConversionUtils.getMps2Knts()) + " knts", x + 2, y);
        font.draw(batch, "GS: " + formatOneDecimal(aircraft.getSpeed() * UnitConversionUtils.getMps2Knts()) + " knts", x + 2, y - 20);
        font.draw(batch, "Headwind: " + formatOneDecimal(-wing.getWind().getVelocity().x * UnitConversionUtils.getMps2Knts()) + " knts", x + 2, y - 40);

        font.draw(batch, "Mach: " + formatTwoDecimals(aircraft.getMachNumber()), x, y - 80);
        font.draw(batch, "Acceleration: " + formatOneDecimal(aircraft.getVerticalAcceleration()) + " g", x, y - 100);
    }
}
