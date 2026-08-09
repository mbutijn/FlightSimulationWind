package io.github.example.UIComponents;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.example.Aircraft;
import io.github.example.utils.UnitConversionUtils;

public class SpeedDataTape extends DataTape {
    public SpeedDataTape(Aircraft aircraft, Viewport viewport, ShapeRenderer shape, SpriteBatch batch, float x, float y) {
        super(aircraft, viewport, shape, batch, x, y);
    }

    public void drawStaticPart() {
        float xBar = x + width - 20;
        shape.rectLine(xBar, under, xBar, upper, 2); // static elements
        shape.rectLine(xBar, center, xBar + 15, center - 15, 2);
        shape.rectLine(xBar, center, xBar + 15, center + 15, 2);
    }

    public void drawTape(float value, boolean drawSetValue, float setValue) {
        float xBar = x + width - 20;
        int currentValue = Math.round(value);

        for (int i = currentValue - bound; i <= currentValue + bound; i++) {
            if (i >= 0 && i % spacing == 0) {
                float yPosition = (i - value) * pixelSpacing;
                float reference = center + yPosition;
                float xBarRight = i == 0 ? xBar - 20 : i % 100 == 0 ? xBar - 40 : i % 50 == 0 ? xBar - 30 : xBar - 20;
                shape.rectLine(xBar, reference, xBarRight, reference, 2);
            }
        }

        if (drawSetValue) {
            float difference = value - setValue;
            if (difference > -bound && difference < bound) {
                shape.setColor(Color.MAGENTA);
                float setAltitude = center - difference * pixelSpacing;
                shape.rectLine(x + width - 20, setAltitude, x + width - 30, setAltitude, 2);
                shape.setColor(FlightDataUI.color);
            }
        }
    }

    public void writeValues(float value) {
        float xBarEnd = x + 10;
        int currentValue = Math.round(value);

        font.draw(batch, String.format("%7.1f", value), x + width, center + 7);
        for (int i = currentValue - bound; i <= currentValue + bound; i++) {
            if (i >= 0 && i % spacing == 0) {
                float yPosition = (i - value) * pixelSpacing;
                float reference = center + yPosition;
                float xBarRight = i == 0 ? xBarEnd + 20 : i % 100 == 0 ? xBarEnd : i % 50 == 0 ? xBarEnd + 10 : xBarEnd + 20;

                font.draw(batch, Integer.toString(i), xBarRight, reference + 5);
            }
        }
    }

    public void drawStallRegionsWarning(float stallSpeed) {
        if (!aircraft.getGear().bothWheelsOnGround()) {
            float airspeed = aircraft.getWing().getTrueAirspeed() * UnitConversionUtils.getMps2Knts();
            drawRegion(stallSpeed, airspeed, Color.YELLOW);
            drawRegion(0.9f * stallSpeed, airspeed, Color.RED);
        }
    }

    public void drawRegion(float borderSpeed, float airspeed, Color color) {
        float lowerBound = airspeed - bound;
        float upperBound = airspeed + bound;
        float xPosition = x + 15;

        if (lowerBound < borderSpeed) {
            shape.setColor(color);
            float barHeigth = borderSpeed - lowerBound;
            if (upperBound < borderSpeed){
                barHeigth = upperBound - lowerBound;
            }
            shape.rectLine(xPosition, under, xPosition, under + barHeigth * pixelSpacing, 2);
        }
    }
}
