package io.github.example;

import com.badlogic.gdx.Gdx;
import io.github.example.UIComponents.ElevatorDataUI;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Aircraft {
    private final int mass;
    private final float momentOfInertia, wingArea, powerPerThrottle, maxPower;
    private Vector2 position, velocity, acceleration, resultantForce, aerodynamicForce;
    private final Vector2 wind, weight, thrust;
    private float angleOfAttack, airspeed, indicatedAirspeed, climbRate, drag, pitchAngle, pitchRate, pitchAcceleration, pitchMoment, Cm_deltaE, flightPathAngle;
    private final AerodynamicCoefficient Cl, Cd, Cm;
    private final AutoPilot autoPilot;
    private final Air air;
    private final Sprite sprite;
    private int throttle;
    private ChangeThrottle changeThrottle;
//    private boolean noseUp;
//    private long time;

    public Aircraft(int mass, Air air) {
        this.mass = mass;
        this.momentOfInertia = 2100; // kg * m^4
        this.weight = new Vector2(0, -9.807f * mass); // Weight => 9807 N
//        this.time = System.currentTimeMillis();

        // disturbance
        this.wind = new Vector2(-20, 0); // -20, 0

        this.thrust = new Vector2(0, 0);
        this.changeThrottle = ChangeThrottle.NONE;
        this.reset();

        this.sprite = new Sprite(new Texture("aircraft.png"));

        sprite.setSize(10, 10);
        sprite.setOrigin(0.6f * sprite.getWidth(), 0.5f * sprite.getHeight());

        // Initialize aerodynamic coefficients
        Cl = new AerodynamicCoefficient(new float[] {-180, -90,  -30,  -20,  -10,   8,  10,  12,  15,  18,  21,   26,   32,   60,  90, 135, 180},
                new float[] {0, -0.07f, -0.29f, -0.79f, -0.64f, 1.36f, 1.5f, 1.5f, 1.43f, 1.0f, 0.5f, 0.21f, 0.14f, 0.05f, -0.05f, -0.2f, 0});
        Cd = new AerodynamicCoefficient(new float[] {-180,  -90, -50, -20,   -15, -10,   -5,    0,     5,      8,     10,    12,    15,   20, 50,  90, 180},
                new float[] {0.05f, 1.35f, 0.6f, 0.225f, 0.175f, 0.1f, 0.05f, 0.03f, 0.035f, 0.0425f, 0.0525f, 0.075f, 0.125f, 0.2f, 0.6f, 1.35f, 0.05f});
        Cm = new AerodynamicCoefficient(new float[]{-180, -135, -90, -60, -30, -20, -10, 0, 10, 20, 30, 60, 90, 135, 180},
                new float[] {0, 0.15f, 0.2f, 0.18f, 0.1f, 0.06f, 0.02f, -0.05f, -0.08f, -0.1f, -0.12f, -0.18f, -0.2f, -0.15f, 0}); // approx neg sin wave

        this.wingArea = 16.2f; // wing surface area [m²]

        this.air = air;
        this.autoPilot = new AutoPilot(this);

        Cm_deltaE = 0.05f;
        ElevatorDataUI.setDeflection(Cm_deltaE);

        maxPower = 0.8f * 134225.977f; // at sea level
        powerPerThrottle = 0.01f * maxPower;
    }

    private float integrate(float output, float integrand, float timeDelta) {
        return output + integrand * timeDelta;
    }

    public void update(float timeStep) {
        updateAerodynamics(); // update forces and moment

        // rotation
        pitchAcceleration = pitchMoment / momentOfInertia;
        pitchRate = integrate(pitchRate, pitchAcceleration, timeStep);
        pitchAngle = putInDomain(integrate(pitchAngle, pitchRate, timeStep));
//        if (noseUp != pitchAngle > 0){
//            long now = System.currentTimeMillis();
//            System.out.println(now - time);
//            this.time = now;
//        }
//        noseUp = pitchAngle > 0;

        // update the throttle
        if (changeThrottle == ChangeThrottle.DOWN){
            throttleDown();
        }
        if (changeThrottle == ChangeThrottle.UP){
            throttleUp();
        }
        updateThrust();

        // translation
        resultantForce.setZero();

        resultantForce.add(aerodynamicForce);
        resultantForce.add(weight);
        resultantForce.add(thrust);

        acceleration.y = resultantForce.y / mass;
        acceleration.x = resultantForce.x / mass;

        velocity.x = integrate(velocity.x, acceleration.x, timeStep);
        velocity.y = integrate(velocity.y, acceleration.y, timeStep);
        climbRate = velocity.y;

        position.x = integrate(position.x, velocity.x, timeStep);
        position.y = integrate(position.y, velocity.y, timeStep);

        if (position.y < 1) {
            System.out.println("aircraft crashed");
            reset();
        }
    }

    public void updateAutoPilot(){
        autoPilot.calculateElevatorDeflection();

        if (autoPilot.getAutoThrottle()) {
            this.throttle = autoPilot.calculateAutoThrottle();
            updateThrust();
        }
    }

    private void updateAerodynamics() {
        Vector2 windRelativeToAircraft = velocity.cpy().sub(wind);
        airspeed = windRelativeToAircraft.len();
        flightPathAngle = putInDomain(windRelativeToAircraft.angleDeg()); // angle between aircraft velocity vector and wind velocity vector
        angleOfAttack = putInDomain(pitchAngle - flightPathAngle);
        air.updateProperties(position.y);
        float airDensity = air.getDensity();
        indicatedAirspeed = (float) (airspeed * Math.sqrt(airDensity / air.getRho0()));

        float dynamicPressure = 0.5f * airDensity * windRelativeToAircraft.len2();
        float totalCm = Cm.calculateCoefficient(angleOfAttack) -0.007f * pitchRate + Cm_deltaE; // pitch moment coefficient
        float chordLength = 1.48f;
        pitchMoment = totalCm * dynamicPressure * wingArea * chordLength; // pitch moment

        aerodynamicForce.y = Cl.calculateCoefficient(angleOfAttack) * dynamicPressure * wingArea; // lift
        drag = Cd.calculateCoefficient(angleOfAttack) * dynamicPressure * wingArea;
        aerodynamicForce.x = -drag; // drag
//        System.out.println("L over D: " + aerodynamicForce.y / -aerodynamicForce.x);
        aerodynamicForce.rotateDeg(flightPathAngle); // put in airspeed frame
    }

    public void updateThrust(){
        thrust.x = throttle * powerPerThrottle * air.getDensityRatio() / airspeed;
        thrust.y = 0;
        thrust.rotateDeg(pitchAngle); // engine is attached to the vehicle
    }

    private float putInDomain(float angle){
        while (angle < -180){
            angle += 360;
        }
        while (angle > 180){
            angle -= 360;
        }
        return angle;
    }

    public void reset() {
        System.out.println("reset");

        this.position = new Vector2(0, 4267.2f); // 0, 2500 (service ceiling = 4267.2f)
        this.velocity = new Vector2(55, 0); // 55, 0
        this.acceleration = new Vector2(0, 0);
        this.throttle = 75; // 75

        // forces
        this.resultantForce = new Vector2(0, 0);
        this.aerodynamicForce = new Vector2(0, 0);

        // angle dynamics
        this.pitchAngle = 0; // -15
        this.pitchRate = 0; // 0
        this.pitchAcceleration = 0; // 0

        if (this.autoPilot != null) {
            autoPilot.pitchController.resetErrorIntegral();
            autoPilot.altitudeController.resetErrorIntegral();
            autoPilot.verticalSpeedController.resetErrorIntegral();
        }
    }

    public Vector2 getPosition(){
        return position;
    }

    public void moveElevatorFromMousePosition(int yPosition){
        float up = 668;
        float down = 100;
        float minValue = -1f;
        float maxValue = 1f;
        if (yPosition < down){
            Cm_deltaE = minValue;
            ElevatorDataUI.setDeflection(Cm_deltaE);
            return;
        }
        if (yPosition > up) {
            Cm_deltaE = maxValue;
            ElevatorDataUI.setDeflection(Cm_deltaE);
            return;
        }
        Cm_deltaE = minValue + (maxValue - minValue) * (yPosition - down) / (up - down);
        ElevatorDataUI.setDeflection(Cm_deltaE);
    }

    public void setCm_deltaE(float Cm_deltaE){
        this.Cm_deltaE = Cm_deltaE;
    }

    public float getSpeed(){
        return velocity.len();
    }

    public Vector2 getVelocity(){
        return velocity;
    }

    public float getPitchAngle(){
        return pitchAngle;
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

    public float getTrueAirspeed(){
        return airspeed;
    }

    public float getAngleOfAttack(){
        return angleOfAttack;
    }

    public float getIndicatedAirspeed(){
        return indicatedAirspeed;
    }

    public void setChangeThrottle(ChangeThrottle change){
        changeThrottle = change;
    }

    public void throttleDown() {
        if (throttle > 0) {
            throttle--;
        }
    }

    public void throttleUp() {
        if (throttle < 100) {
            throttle++;
        }
    }

    public float getWeight(){
        return weight.len();
    }

    public Vector2 getAcceleration(){
        return acceleration;
    }

    public float getPitchRate(){
        return pitchRate;
    }

    public AutoPilot getAutoPilot(){
        return autoPilot;
    }

    public int getThrottle(){
        return throttle;
    }

    public float getClimbRate(){
        return climbRate;
    }

    public Air getAir(){
        return air;
    }

    public void render(SpriteBatch batch) {
        sprite.setRotation(pitchAngle);
        sprite.setPosition(position.x - sprite.getWidth() * 0.6f, position.y - sprite.getHeight() / 2);

        sprite.draw(batch);
    }

    public float getStallSpeed() {
        return (float) Math.sqrt(2 * weight.len() / (getAir().getDensity() * wingArea * 1.5f));
    }

    public float getMachNumber(){
        return airspeed / getAir().speedOfSound;
    }

    public float getVerticalAcceleration(){
        return getAcceleration().y / 9.807f + 1;
    }

    public float getMaxPower() {
        return maxPower;
    }

    public float getDrag(){
        return drag;
    }

    public double getFlightPathAngle() {
        return Math.toRadians(flightPathAngle);
    }
}
