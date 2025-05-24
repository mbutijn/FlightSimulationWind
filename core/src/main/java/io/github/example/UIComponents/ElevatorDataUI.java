package io.github.example.UIComponents;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.example.Aircraft;

public class ElevatorDataUI extends FlightDataUI {
    private final Polygon staticPart;

    public ElevatorDataUI(Aircraft aircraft, Viewport viewport, ShapeRenderer shape, SpriteBatch batch, float x, float y) {
        super(aircraft, viewport, shape, batch, x, y);
        this.staticPart = new Polygon(new float[]{
                -2, 4,
                12, 7,
                30, 8,
                37, 5,
                40, 2,
                40, 0,
                35, -4,
                26, -6,
                0, -6
        });
        staticPart.setPosition(viewport.getScreenWidth() + x, y);
    }

    public static void setDeflection(float Cm_deltaE) {
        deflection = -25 * Cm_deltaE; // angle in degrees
    }

    public void writeValues(){
        font.draw(batch, "elevator: " + formatOneDecimal(deflection) + " deg", x - 60, y - 40);
    }

    public void draw(){
        shape.polygon(staticPart.getTransformedVertices());

        // draw moving part
        float x_trailingEdge = x + (float) (40.45f * Math.cos(Math.toRadians(deflection + 188.65)));
        float y_trailingEdge = y + (float) (40.45f * Math.sin(Math.toRadians(deflection + 188.65)));

        shape.rectLine(x_trailingEdge, y_trailingEdge, x - 2, y + 4, 0.1f);
        shape.rectLine(x_trailingEdge, y_trailingEdge, x, y - 6, 0.1f);
    }
}
