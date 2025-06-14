package io.github.example;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Polygon;
import io.github.example.AircraftComponents.AutoPilot;
import io.github.example.AircraftComponents.Engine;
import io.github.example.AircraftComponents.Gear;
import io.github.example.AircraftComponents.Wing;
import io.github.example.UIComponents.ElevatorDataUI;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import io.github.example.utils.Config;
import io.github.example.utils.MathUtils;
import io.github.example.utils.UnitConversionUtils;

public class Aircraft {
    private final int mass;
    private final float momentOfInertia;
    private Vector2 position, velocity, acceleration, resultantForce;
    private final Vector2 weight;
    private float climbRate, pitchAngle, pitchRate, pitchAcceleration, pitchMoment, Cm_deltaE;
    private final AutoPilot autoPilot;
    private final Air air;
    private final Engine engine;
    private final Sprite sprite;
    private final Polygon hitBox;
    private final Gear gear;
    private final Wing wing;
    private final Vector2 cgPosition;
//    private boolean noseUp;
//    private long time;

    public Aircraft(Air air) {
        this.mass = Config.getInt("aircraft1.mass");
        this.momentOfInertia = Config.getFloat("aircraft1.momentOfInertia"); // kg * m^4
        this.weight = new Vector2(0, -9.807f * this.mass); // Weight => 11346.699 N
        this.wing = new Wing(this);
        this.air = air;
        this.engine = new Engine(this);
        this.sprite = new Sprite(new Texture("aircraft.png"));
        this.cgPosition = new Vector2(0.65f, 0.5f);
        sprite.setSize(10, 10);
        sprite.setOrigin(cgPosition.x * sprite.getWidth(), cgPosition.y * sprite.getHeight());
        hitBox = new Polygon(new float[]{
            1.1f, 4.8f, // bottom tail (left)
            0.9f, 6.8f, // tail top (left)
            4.3f, 6.8f, // top
            9.5f, 5.0f, // front (right)
            8.2f, 4.1f, // bottom front fuselage (right)
            8.0f, 3.3f, // front gear (right)
            5.6f, 3.3f, // rear gear (left)
            4.9f, 4.1f // bottom rear fuselage (left)
        });

        this.reset();
        this.gear = new Gear(this);
        this.gear.reset();
        this.autoPilot = new AutoPilot(this);

        Cm_deltaE = 0.05f;
        ElevatorDataUI.setDeflection(Cm_deltaE);

//        this.time = System.currentTimeMillis();
    }

    private float integrate(float output, float integrand, float timeDelta) {
        return output + integrand * timeDelta;
    }

    public void update(float timeStep) {
        air.updateProperties(position.y);

        wing.updateAerodynamics(air, Cm_deltaE); // update Aerodynamic forces and moment
        gear.updateNormalForcesAndMoment(timeStep);

        // rotation
        pitchAcceleration = (pitchMoment + gear.getMoment()) / momentOfInertia;
        pitchRate = integrate(pitchRate, pitchAcceleration, timeStep);
        pitchAngle = MathUtils.putInDomain(integrate(pitchAngle, pitchRate, timeStep));
//        if (noseUp != pitchAngle > 0){
//            long now = System.currentTimeMillis();
//            System.out.println(now - time);
//            this.time = now;
//        }
//        noseUp = pitchAngle > 0;

        // update the throttle
        engine.update();

        // translation
        resultantForce.setZero();
        resultantForce.add(wing.getAerodynamicForce());
        resultantForce.add(weight);
        resultantForce.add(engine.getThrust());
        resultantForce.add(gear.getFrontWheel().getReactionForce());
        resultantForce.add(gear.getRearWheel().getReactionForce());

        acceleration.y = resultantForce.y / mass;
        acceleration.x = resultantForce.x / mass;

        velocity.x = integrate(velocity.x, acceleration.x, timeStep);
        velocity.y = integrate(velocity.y, acceleration.y, timeStep);
        climbRate = velocity.y;

        position.x = integrate(position.x, velocity.x, timeStep);
        position.y = integrate(position.y, velocity.y, timeStep);

        if (checkCrashed()){
            System.out.println("aircraft crashed");
            reset();
        }
    }

    public boolean checkCrashed() {
        hitBox.setPosition(sprite.getX(), sprite.getY());
        hitBox.setOrigin(sprite.getOriginX(), sprite.getOriginY());
        hitBox.setRotation(sprite.getRotation());
        float [] vertices = hitBox.getTransformedVertices();
        for (int i = 1; i < vertices.length; i += 2){
            if (i != 11 && i != 13) { // skip the landing gear y positions
                if (vertices[i] < 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public void updateAutoPilot(){
        autoPilot.calculateElevatorDeflection();

        if (autoPilot.getAutoThrottle()) {
            engine.updateAutoThrottle(autoPilot.calculateAutoThrottle());
        }
    }

    public void reset() {
        System.out.println("reset");

        this.position = new Vector2(0, 50 / UnitConversionUtils.getM2Feet()); // 0, 2500 (service ceiling = 4267.2f)
        this.velocity = new Vector2(35, -3); // 55, 0
        this.acceleration = new Vector2(0, 0);
        this.engine.reset();

        this.resultantForce = new Vector2(0, 0);

        // angle dynamics
        this.pitchAngle = 3; // -15
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

    public float getClimbRate(){
        return climbRate;
    }

    public Air getAir(){
        return air;
    }

    public void render(SpriteBatch batch) {
        sprite.setPosition(position.x - sprite.getWidth() * cgPosition.x, position.y - cgPosition.y * sprite.getHeight());
        sprite.setRotation(pitchAngle);

        sprite.draw(batch);
    }

    public float getStallSpeed() {
        return (float) Math.sqrt(2 * weight.len() / (getAir().getDensity() * wing.getArea() * 1.45f));
    }

    public float getMachNumber(){
        return wing.getTrueAirspeed() / getAir().speedOfSound;
    }

    public float getVerticalAcceleration(){
        return getAcceleration().y / 9.807f + 1;
    }

    public void renderHitBox(ShapeRenderer shape) {
        shape.polygon(hitBox.getTransformedVertices());
    }

    public Polygon getHitBox() {
        return hitBox;
    }

    public Gear getGear() {
        return gear;
    }

    public void renderCenterOfGravity(ShapeRenderer shape){
//        shape.circle(position.x, position.y, 0.1f, 20);
//        shape.circle(sprite.getOriginX(), sprite.getOriginY(), 0.1f, 20);
        shape.circle(sprite.getX() + cgPosition.x * sprite.getWidth(), sprite.getY() + cgPosition.y * sprite.getHeight(), 0.1f, 20);
    }

    public Sprite getSprite(){
        return sprite;
    }

    public Vector2 getCgPosition(){
        return cgPosition;
    }

    public Engine getEngine(){
        return engine;
    }

    public Wing getWing(){
        return wing;
    }

    public void setPitchMoment(float pitchMoment) {
        this.pitchMoment = pitchMoment;
    }

    public boolean isMovingForward(){
        return velocity.x > 0;
    }
}
