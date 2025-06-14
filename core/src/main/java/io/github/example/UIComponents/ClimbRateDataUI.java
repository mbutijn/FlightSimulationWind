package io.github.example.UIComponents;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.example.Aircraft;
import io.github.example.utils.UnitConversionUtils;

public class ClimbRateDataUI extends FlightDataUI {

    float radius, mps2Degree;

    public ClimbRateDataUI(Aircraft aircraft, Viewport viewport, ShapeRenderer shape, SpriteBatch batch, float x, float y) {
        super(aircraft, viewport, shape, batch, x, y);
        radius = 40;
        width = 2 * radius;
        height = 2 * radius;
        mps2Degree = 10;
    }

    public void writeValues(){
        font.draw(batch, "Climb rate: " + (Math.round(aircraft.getClimbRate() * UnitConversionUtils.getMps2Feetpmin())) + " feet/min", x - 80, y - 50);
        font.draw(batch, "up", x - radius + 4, y + 17);
        font.draw(batch, "down", x - radius + 4, y - 5);
    }

    public void draw() {
        // draw static part
        shape.circle(x, y, radius);
        shape.rectLine(x - radius, y, x - radius + 10, y, 0.1f);

        // draw moving part

        float angle = mps2Degree * aircraft.getClimbRate();
        float x_tip = (float) (x + radius * Math.cos(Math.toRadians(180 - angle)));
        float y_tip = (float) (y + radius * Math.sin(Math.toRadians(180 - angle)));
        shape.rectLine(x, y, x_tip, y_tip, 0.1f);

    }

    public void drawSetValue(){
        shape.setColor(Color.RED);
        float angleSet = mps2Degree * aircraft.getAutoPilot().getSetClimbRate();
        float cos = (float) Math.cos(Math.toRadians(180 - angleSet));
        float sin = (float) Math.sin(Math.toRadians(180 - angleSet));
        float x_tip2 = x + radius * cos;
        float y_tip2 = y + radius * sin;
        float x_tipSet = x + (radius + 10) * cos;
        float y_tipSet = y + (radius + 10) * sin;
        shape.rectLine(x_tip2, y_tip2, x_tipSet, y_tipSet, 0.1f);
    }

    public float getRadius() {
        return radius;
    }
}
