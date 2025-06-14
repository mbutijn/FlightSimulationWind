package io.github.example.AircraftComponents;

import com.badlogic.gdx.math.Vector2;
import io.github.example.Aircraft;
import io.github.example.ChangeThrottle;
import io.github.example.utils.Config;

public class Engine {
    private final Aircraft aircraft;
    private final float powerPerThrottle, maxPower;
    private final Vector2 thrust;
    private int throttle;
    private ChangeThrottle changeThrottle;

    public Engine(Aircraft aircraft){
        this.thrust = new Vector2(0, 0);
        this.aircraft = aircraft;
        this.maxPower = Config.getFloat("aircraft1.maxPower") * Config.getFloat("aircraft1.propEfficiency"); // at sea level
        this.powerPerThrottle = 0.01f * maxPower;
        this.changeThrottle = ChangeThrottle.NONE;
    }

    public void update(){
        if (changeThrottle == ChangeThrottle.DOWN){
            throttleDown();
        }
        if (changeThrottle == ChangeThrottle.UP){
            throttleUp();
        }
        updateThrust();
    }

    public void updateThrust(){
        thrust.x = throttle * powerPerThrottle * aircraft.getAir().getDensityRatio() / aircraft.getWing().getTrueAirspeed();
        thrust.y = 0;
        thrust.rotateDeg(aircraft.getPitchAngle()); // engine is attached to the vehicle
    }

    public Vector2 getThrust() {
        return thrust;
    }

    public void updateAutoThrottle(int throttle) {
        this.throttle = throttle;
        updateThrust();
    }

    public void reset() {
        this.throttle = 25; // 75
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

    public int getThrottle(){
        return throttle;
    }

    public float getMaxPower() {
        return maxPower;
    }

    public void setChangeThrottle(ChangeThrottle change){
        changeThrottle = change;
    }

}
