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

    public void writeValues(){
        Wing wing = aircraft.getWing();
        font.draw(batch, "TAS: " + formatOneDecimal(wing.getTrueAirspeed() * UnitConversionUtils.getMps2Knts()) + " knts", x, y);
        font.draw(batch, "IAS: " + formatOneDecimal(wing.getIndicatedAirspeed() * UnitConversionUtils.getMps2Knts()) + " knts", x + 2, y - 20);
        font.draw(batch, "Mach: " + formatTwoDecimals(aircraft.getMachNumber()), x + 2, y - 40);
        font.draw(batch, "acceleration: " + formatOneDecimal(aircraft.getVerticalAcceleration()) + " g", x, y - 80);
    }
}
