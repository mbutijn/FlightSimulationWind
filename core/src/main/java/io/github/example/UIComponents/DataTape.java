package io.github.example.UIComponents;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.example.Aircraft;

public abstract class DataTape extends FlightDataUI {
    protected int bound;
    protected float pixelSpacing, under, spacing, upper, center;

    public DataTape(Aircraft aircraft, Viewport viewport, ShapeRenderer shape, SpriteBatch batch, float x, float y) {
        super(aircraft, viewport, shape, batch, x, y);
    }

    public void setProperties(int bound, float pixelSpacing, float spacing) {
        this.bound = bound;
        this.pixelSpacing = pixelSpacing;
        this.spacing = spacing;
        this.center = y + 150;
        this.under = center - bound * pixelSpacing;
        this.upper = center + bound * pixelSpacing;
        this.width = 100;
        this.height = 300;
    }
}
