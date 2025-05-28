package io.github.example.UIComponents;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.example.*;

public class AutoPilotModeDataUI extends FlightDataUI {
    AutoPilot autoPilot;

    public AutoPilotModeDataUI(Aircraft aircraft, Viewport viewport, ShapeRenderer shape, SpriteBatch batch, float x, float y) {
        super(aircraft, viewport, shape, batch, x, y);
        this.width = 100;
        this.height = 100;
        this.autoPilot = aircraft.getAutoPilot();
    }

    public void draw(){
        if (FlightSimulation.getSteeringMode() == SteeringMode.AUTO_PILOT && autoPilot.getMode() != AutoPilotMode.PITCH_HOLD) {
            shape.circle(x + 25, y + 15, 10);
            shape.circle(x + 75, y + 15, 10);
        }
    }

    public void writeValues() {
        if (FlightSimulation.getSteeringMode() == SteeringMode.AUTO_PILOT) {
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
                float setClimbRate = autoPilot.getSetClimbRate();
                setValue = setClimbRate * UnitConversionUtils.getMps2Feetpmin();
                if (autoPilot.isClimbAndHold()) {
                    mode = "CLH";
                    if (setClimbRate != 0 && (autoPilot.getSetAltitude() < aircraft.getPosition().y == setClimbRate > 0)) {
                        font.setColor(Color.RED);
                    }
                    font.draw(batch, Math.round(autoPilot.getSetAltitude() * UnitConversionUtils.getM2Feet()) + " feet", x, y + 50);
                } else {
                    mode = "VS";
                }
            } else if (autoPilotMode == AutoPilotMode.ALTITUDE_HOLD) {
                mode = "ALT";
                unit = "feet";
                setValue = autoPilot.getSetAltitude() * UnitConversionUtils.getM2Feet();
            }
            font.draw(batch, Math.round(setValue) + " " + unit, x, y + 70);
            font.setColor(FlightDataUI.color);
            font.draw(batch, "AP: " + mode, x, y + 90);
        }
    }

    public boolean mouseAboveLeftButton(float xMouse) {
        return xMouse < x + 0.5f * width;
    }
}
