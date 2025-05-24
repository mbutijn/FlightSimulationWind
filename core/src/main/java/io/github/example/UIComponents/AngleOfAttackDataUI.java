package io.github.example.UIComponents;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.example.Aircraft;

public class AngleOfAttackDataUI extends FlightDataUI {
    private final Polygon indicator;
    private final Array<Vector2> arcPositions;

    public AngleOfAttackDataUI(Aircraft aircraft, Viewport viewport, ShapeRenderer shape, SpriteBatch batch, float x, float y) {
        super(aircraft, viewport, shape, batch, x, y);
        indicator = new Polygon(new float[]{
                -40, -6,
                12, 7,
                30, 8,
                37, 5,
                40, 2,
                40, 0,
                35, -4,
                26, -6});
        indicator.setPosition(viewport.getScreenWidth() + x, y);

        arcPositions = new Array<>();
        float radius = 60;
        for (int angle = -15; angle <= 30; angle += 5){
            arcPositions.add(new Vector2(x + (float) (radius * Math.cos(Math.toRadians(angle))),
                    y + (float) (radius * Math.sin(Math.toRadians(angle)))));
        }
    }

    public void writeValues() {
        font.draw(batch, "AoA: " + formatOneDecimal(aircraft.getAngleOfAttack()) + " deg", x - 40, y - 40);
    }

    public void draw(){
        shape.setColor(color);
        indicator.setRotation(aircraft.getAngleOfAttack());
        shape.polygon(indicator.getTransformedVertices());
    }

    public void drawStaticPart(){
        shape.setColor(Color.GREEN); // normal angle of attack
        for (int i = 1; i < arcPositions.size - 2; i++){
            shape.rectLine(arcPositions.get(i - 1), arcPositions.get(i), 4); // -15 -> 15
        }

        shape.setColor(Color.YELLOW); // caution
        shape.rectLine(arcPositions.get(6), arcPositions.get(7), 4); // 15 -> 20

        shape.setColor(Color.RED); // stall
        shape.rectLine(arcPositions.get(7), arcPositions.get(8), 4); // 20 -> 25
        shape.rectLine(arcPositions.get(8), arcPositions.get(9), 4); // 25 -> 30
    }
}
