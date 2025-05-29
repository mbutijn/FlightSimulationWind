package io.github.example.UIComponents;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.example.*;

public class ThrottleDataUI extends FlightDataUI {
    private final float radius;

    public ThrottleDataUI(Aircraft aircraft, Viewport viewport, ShapeRenderer shape, SpriteBatch batch, float x, float y){
        super(aircraft, viewport, shape, batch, x, y);
        this.radius = 40;
        this.width = 2 * radius;
        this.height = 2 * radius;
    }

    public void writeValues(){
        AutoPilot autoPilot = aircraft.getAutoPilot();
        boolean autoThrottle = FlightSimulation.getSteeringMode() == SteeringMode.AUTO_PILOT && autoPilot.getAutoThrottle();
        font.draw(batch, (autoThrottle ? "Autothrottle: " : "Throttle: ") + aircraft.getThrottle() + "%",
                autoThrottle ? x - 60 : x - 50, y - 50);

        if (autoThrottle){
            font.draw(batch, Math.round(autoPilot.getSetAirspeed() * UnitConversionUtils.getMps2Knts()) + " knts", x - 60, y - 70);
        }
    }

    public void draw() {
        float angle = 2.7f * aircraft.getThrottle();
        float x_tip = (float) (x + radius * Math.cos(Math.toRadians(225 - angle)));
        float y_tip = (float) (y + radius * Math.sin(Math.toRadians(225 - angle)));
        shape.rectLine(x, y, x_tip, y_tip, 0.1f); // moving
        shape.arc(x, y, radius, -45, 270); // static
    }

    public float getRadius() {
        return radius;
    }
}
