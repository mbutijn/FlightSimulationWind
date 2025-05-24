package io.github.example.UIComponents;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.example.Air;
import io.github.example.Aircraft;

public class AirPropertiesDataUI extends FlightDataUI{
    private Air air;

    public AirPropertiesDataUI(Aircraft aircraft, Viewport viewport, ShapeRenderer shape, SpriteBatch batch, float x, float y) {
        super(aircraft, viewport, shape, batch, x, y);
    }

    public void setAir(Air air){
        this.air = air;
    }

    public void writeValues(){
        font.draw(batch, "rho = " + formatTwoDecimals(air.getDensity()) + " kg/m^3", x, y);
        font.draw(batch, "T = " + formatOneDecimal(air.getTemperature() - 273.15f) + " deg", x, y - 20);
        font.draw(batch, "P = " + formatTwoDecimals(1E-5f * air.getPressure()) + " bar", x, y - 40);
    }
}
