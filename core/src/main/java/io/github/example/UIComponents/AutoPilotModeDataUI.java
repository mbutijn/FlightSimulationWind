package io.github.example.UIComponents;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.example.*;

public class AutoPilotModeDataUI extends FlightDataUI {
    public AutoPilotModeDataUI(Aircraft aircraft, Viewport viewport, ShapeRenderer shape, SpriteBatch batch, float x, float y) {
        super(aircraft, viewport, shape, batch, x, y);
        this.width = 100;
        this.height = 60;
    }

    public void writeValues() {
        if (FlightSimulation.getSteeringMode() == SteeringMode.AUTO_PILOT) {
            AutoPilot autoPilot = aircraft.getAutoPilot();
            AutoPilotMode autoPilotMode = autoPilot.getMode();
            String mode = "";
            String unit = "";
            float setValue = 0;

            if (autoPilotMode == AutoPilotMode.PITCH_HOLD) {
                mode = "PIT";
                unit = "deg";
                setValue = autoPilot.getSetPitchAngle();
            } else if (autoPilotMode == AutoPilotMode.VERTICAL_SPEED) {
                unit = "feet/min";
                setValue = autoPilot.getSetClimbRate() * UnitConversionUtils.getMps2Feetpmin();
                if (autoPilot.climbAndHold) {
                    mode = "CLH";
                    font.draw(batch, Math.round(autoPilot.getSetAltitude() * UnitConversionUtils.getM2Feet()) + " feet", x, y + 10);
                } else {
                    mode = "VS";
                }
            } else if (autoPilotMode == AutoPilotMode.ALTITUDE_HOLD) {
                mode = "ALT";
                unit = "feet";
                setValue = autoPilot.getSetAltitude() * UnitConversionUtils.getM2Feet();
            }
            font.draw(batch, "AP: " + mode, x, y + 50);
            font.draw(batch, Math.round(setValue) + " " + unit, x, y + 30);
        }
    }
}
