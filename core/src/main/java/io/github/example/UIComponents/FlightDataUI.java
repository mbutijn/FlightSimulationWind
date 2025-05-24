package io.github.example.UIComponents;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.example.Aircraft;

public abstract class FlightDataUI {
    protected final Aircraft aircraft;
    protected static final BitmapFont font = new BitmapFont();
    protected GlyphLayout layout = new GlyphLayout();
    protected final ShapeRenderer shape;
    protected final SpriteBatch batch;
    protected final Viewport viewport;
    public static float deflection;
    protected float x, y, width, height;
    public static Color color;

    public FlightDataUI(Aircraft aircraft, Viewport viewport, ShapeRenderer shape, SpriteBatch batch, float x, float y) {
        this.aircraft = aircraft;
        this.shape = shape;
        this.batch = batch;
        this.viewport = viewport;
        this.x = x;
        this.y = y;
        updateUIColor(aircraft.getPosition().y);
    }

    public static void updateUIColor(float height){
        color = height > 1500 ? Color.WHITE : Color.BLACK;
        font.setColor(color);
    }

    public float getX(){
        return x;
    }

    public float getY(){
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public boolean mouseAboveUI(float xMouse, float yMouse){
        return xMouse > x && xMouse < x + this.width && yMouse > y && yMouse < y + this.height;
    }

    public void writeValues(){
    }

    public void draw(){
    }

    public void writeValues(float value){
    }

    public static String formatOneDecimal(float value) {
        String out = "" + (Math.round(10 * value) / 10f);
        return out.contains(".") ? out : out + ".0";
    }

    public static String formatTwoDecimals(float value){
        String out = "" + (Math.round(100 * value) / 100f);
        return out.contains(".") ? out : out + ".0";
    }

}
