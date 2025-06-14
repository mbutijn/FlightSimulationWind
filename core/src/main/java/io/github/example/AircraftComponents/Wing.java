package io.github.example.AircraftComponents;

import com.badlogic.gdx.math.Vector2;
import io.github.example.AerodynamicCoefficient;
import io.github.example.Air;
import io.github.example.Aircraft;
import io.github.example.utils.Config;
import io.github.example.utils.MathUtils;

public class Wing {
    private final Aircraft aircraft;
    private final float area;
    private final Vector2 wind, aerodynamicForce;
    private final AerodynamicCoefficient Cl, Cd, Cm;
    private final float chordLength = Config.getFloat("aircraft1.chordLength");
    private float angleOfAttack, airspeed, indicatedAirspeed, drag, flightPathAngle;

    public Wing(Aircraft aircraft){
        this.aircraft = aircraft;
        this.area = Config.getFloat("aircraft1.wingArea"); // wing surface area [m²]
        this.aerodynamicForce = new Vector2(0, 0);
        this.wind = new Vector2(-20, 0); // -20, 0

        // Initialize aerodynamic coefficients
        Cl = new AerodynamicCoefficient(new float[]{-180, -90, -30, -20, -10, 0, 8, 10, 12, 15, 18, 21, 26, 32, 60, 90, 135, 180},
            new float[]{0, -0.07f, -0.29f, -0.79f, -0.64f, 0.2f, 1.3f, 1.4f, 1.45f, 1.4f, 1.1f, 0.6f, 0.3f, 0.15f, 0.05f, -0.05f, -0.2f, 0});
        Cd = new AerodynamicCoefficient(new float[]{-180, -90, -50, -20, -15, -10, -5, 0, 5, 8, 10, 12, 15, 20, 50, 90, 180},
            new float[]{0.05f, 1.35f, 0.6f,  0.22f,  0.16f, 0.08f, 0.045f, 0.02f, 0.025f, 0.032f, 0.045f, 0.07f, 0.12f,  0.22f, 0.6f, 1.35f, 0.05f});
        Cm = new AerodynamicCoefficient(new float[]{-180, -135, -90, -60, -30, -20, -10, 0, 10, 20, 30, 60, 90, 135, 180},
            new float[]{0, 0.15f, 0.2f, 0.18f, 0.1f, 0.06f, 0.02f, -0.05f, -0.08f, -0.1f, -0.12f, -0.18f, -0.2f, -0.15f, 0}); // approx neg sin wave

    }

    public void updateAerodynamics(Air air, float Cm_deltaE) {
        Vector2 windRelativeToAircraft = aircraft.getVelocity().cpy().sub(wind);
        airspeed = windRelativeToAircraft.len();
        indicatedAirspeed = (float) (airspeed * Math.sqrt(air.getDensityRatio()));
        flightPathAngle = MathUtils.putInDomain(windRelativeToAircraft.angleDeg()); // angle between aircraft velocity vector and wind velocity vector
        angleOfAttack = MathUtils.putInDomain(aircraft.getPitchAngle() - flightPathAngle);

        float dynamicPressure = 0.5f * air.getDensity() * windRelativeToAircraft.len2();
        float totalCm = Cm.calculateCoefficient(angleOfAttack) -0.007f * aircraft.getPitchRate() + Cm_deltaE; // pitch moment coefficient

        aircraft.setPitchMoment(totalCm * dynamicPressure * area * chordLength); // pitch moment

        aerodynamicForce.y = Cl.calculateCoefficient(angleOfAttack) * dynamicPressure * area; // lift
        drag = Cd.calculateCoefficient(angleOfAttack) * dynamicPressure * area;
        aerodynamicForce.x = -drag; // drag
//        System.out.println("L over D: " + aerodynamicForce.y / -aerodynamicForce.x);
        aerodynamicForce.rotateDeg(flightPathAngle); // put in airspeed frame
    }

    public AerodynamicCoefficient getCl() {
        return Cl;
    }

    public AerodynamicCoefficient getCd(){
        return Cd;
    }

    public AerodynamicCoefficient getCm(){
        return Cm;
    }

    public float getArea() {
        return area;
    }

    public Vector2 getAerodynamicForce() {
        return aerodynamicForce;
    }

    public float getAngleOfAttack(){
        return angleOfAttack;
    }

    public float getDrag(){
        return drag;
    }

    public double getFlightPathAngle() {
        return Math.toRadians(flightPathAngle);
    }

    public float getTrueAirspeed(){
        return airspeed;
    }

    public float getIndicatedAirspeed(){
        return indicatedAirspeed;
    }

}
