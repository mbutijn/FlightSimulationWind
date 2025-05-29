package io.github.example.UIComponents;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.example.*;

public class AutoPilotModeDataUI extends FlightDataUI {
    private final AutoPilot autoPilot;
    private final float radius;
    private final float xMid;
    private final Array<Float> x_bases, y_bases, x_tips, y_tips;
    private final float yCourseButton;
    private final float yFineButton;

    public AutoPilotModeDataUI(Aircraft aircraft, Viewport viewport, ShapeRenderer shape, SpriteBatch batch, float x, float y) {
        super(aircraft, viewport, shape, batch, x, y);
        this.width = 100;
        this.height = 200;
        this.autoPilot = aircraft.getAutoPilot();
        this.radius = 10;
        this.xMid = 0.5f * width;
        this.yCourseButton = 35;
        this.yFineButton = 105;

        x_bases = new Array<>();
        y_bases = new Array<>();
        x_tips = new Array<>();
        y_tips = new Array<>();

        for (int i = 0; i < 10; i++) {
            float angle = (float) (Math.PI * (0.5 - 0.2 * i));
            float cos = (float) Math.cos(angle);
            float sin = (float) Math.sin(angle);
            x_bases.add(x + xMid + radius * cos);
            y_bases.add(y + radius * sin);
            x_tips.add(x + xMid + (radius + 5) * cos);
            y_tips.add(y + (radius + 5) * sin);
        }
    }

    public void draw(){
        AutoPilotMode mode = autoPilot.getMode();
        if (FlightSimulation.getSteeringMode() == SteeringMode.AUTO_PILOT && mode != AutoPilotMode.PITCH_HOLD) {
            shape.circle(x + xMid, y + yFineButton, radius);
            shape.circle(x + xMid, y + yCourseButton, radius);

            for (int i = 0; i < x_bases.size; i += 2) {
                shape.rectLine(x_bases.get(i), y_bases.get(i) + yFineButton, x_tips.get(i), y_tips.get(i) + yFineButton, 0.1f);
                shape.rectLine(x_bases.get(i), y_bases.get(i) + yCourseButton, x_tips.get(i), y_tips.get(i) + yCourseButton, 0.1f);
            }

            int setValue = 0;
            if (mode == AutoPilotMode.ALTITUDE_HOLD){
                setValue = Math.round(autoPilot.getSetAltitude() * UnitConversionUtils.getM2Feet());
            } else if (mode == AutoPilotMode.VERTICAL_SPEED){
//                if (autoPilot.isClimbAndHold()) {
//                    setValue = Math.round(autoPilot.getSetAltitude() * UnitConversionUtils.getM2Feet());
//                } else {
//                    setValue = Math.round(autoPilot.getSetClimbRate() * UnitConversionUtils.getMps2Feetpmin());
//                }
                setValue = getSetValue();
            }
            int index100;
            int index10;

            if (setValue > 0) {
                index100 = (int) Math.floor((double) (setValue % 1000) / 100);
                index10 = (int) Math.floor((double) (setValue % 100) / 10);
            } else {
                index100 = (int) Math.ceil((double) (setValue % 1000) / 100);
                index10 = (int) Math.ceil((double) (setValue % 100) / 10);
            }

            if (index100 < 0) {
                index100 += 10;
            }
            if (index10 < 0) {
                index10 += 10;
            }

            shape.rectLine(x + xMid, y + yCourseButton, x_bases.get(index100), y_bases.get(index100) + yCourseButton, 0.1f);
            shape.rectLine(x + xMid, y + yFineButton, x_bases.get(index10), y_bases.get(index10) + yFineButton, 0.1f);
        }
    }

    public int getSetValue(){
        if (autoPilot.isClimbAndHold()) {
            return Math.round(autoPilot.getSetAltitude() * UnitConversionUtils.getM2Feet());
        } else {
            return Math.round(autoPilot.getSetClimbRate() * UnitConversionUtils.getMps2Feetpmin());
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
                    font.draw(batch, Math.round(autoPilot.getSetAltitude() * UnitConversionUtils.getM2Feet()) + " feet", x, y + 160);
                } else {
                    mode = "VS";
                }
            } else if (autoPilotMode == AutoPilotMode.ALTITUDE_HOLD) {
                mode = "ALT";
                unit = "feet";
                setValue = autoPilot.getSetAltitude() * UnitConversionUtils.getM2Feet();
            }
            font.draw(batch, Math.round(setValue) + " " + unit, x, y + 180);
            font.setColor(FlightDataUI.color);
            font.draw(batch, "AP: " + mode, x, y + 200);

            if (autoPilot.getMode() != AutoPilotMode.PITCH_HOLD) {
                for (int i = 0; i < x_bases.size; i += 2) {
                    writeDialValue(i, 100, yCourseButton, getSetValue() >= 0);
                    writeDialValue(i, 10, yFineButton, getSetValue() >= 0);
                }
            }
        }
    }

    public void writeDialValue(int i, int factor, float yDistance, boolean positive){
        String value = positive ? String.valueOf(i * factor) : i == 0 ? "0" : "-" + factor * (10 - i);
        layout.setText(font, value);
        float textWidth = (i == 6 || i == 8) ? layout.width : (i == 0) ? 0.5f * layout.width : 0;
        float textHeight = (i == 2 || i == 8) ? layout.height : (i == 0) ? 1.2f * layout.height : 0;
        font.draw(batch, layout, x_tips.get(i) - textWidth, y_tips.get(i) + textHeight + yDistance);
    }

    public boolean mouseAboveCourseButton(float yMouse) {
        return yMouse < y + 0.5f * (yCourseButton + yFineButton);
    }
}
