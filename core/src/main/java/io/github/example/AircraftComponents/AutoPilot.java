package io.github.example.AircraftComponents;

import io.github.example.Aircraft;
import io.github.example.AutoPilotMode;
import io.github.example.PIDController;
import io.github.example.UIComponents.ElevatorDataUI;
import io.github.example.utils.MathUtils;
import io.github.example.utils.UnitConversionUtils;

public class AutoPilot {
    private final Aircraft aircraft;
    private float setPitchAngle;
    private float setClimbRate;
    private float setAltitude;
    private float setAirspeed;
    private AutoPilotMode mode;
    private boolean autoThrottle;
    private boolean climbAndHold;
    private final Wing wing;
    public PIDController pitchController, verticalSpeedController, altitudeController;

    public AutoPilot (Aircraft aircraft){
        this.aircraft = aircraft;
        this.wing = aircraft.getWing();
        this.mode = AutoPilotMode.PITCH_HOLD;
        this.setPitchAngle = 0;
        this.setClimbRate = 0;
        this.setAltitude = aircraft.getPosition().y;
        this.setAirspeed = wing.getTrueAirspeed();
        this.climbAndHold = false;
        this.pitchController = new PIDController(1, 0.005f, -0.3f, -1, 1);
        this.verticalSpeedController = new PIDController(0.1f, 0.001f, -0.2f, -1, 1);
        this.altitudeController = new PIDController(0.2f, 0.0f, -0.05f, -4, 4);
    }

    public void calculateElevatorDeflection(){
        float Cm_deltaE = 0;
        float altitude = aircraft.getPosition().y;
        if (this.mode == AutoPilotMode.PITCH_HOLD) {
            pitchController.updateValues(setPitchAngle, aircraft.getPitchAngle(), aircraft.getPitchRate());
            Cm_deltaE = pitchController.calculateOutput();
        } else if (mode == AutoPilotMode.VERTICAL_SPEED) {
            verticalSpeedController.updateValues(setClimbRate, aircraft.getClimbRate(), aircraft.getAcceleration().y);
            Cm_deltaE = verticalSpeedController.calculateOutput();

            if (climbAndHold && Math.abs(setAltitude - altitude) < 20){
                aircraft.getAutoPilot().setMode(AutoPilotMode.ALTITUDE_HOLD);
            }
        } else if (mode == AutoPilotMode.ALTITUDE_HOLD) {
            altitudeController.updateValues(setAltitude, aircraft.getPosition().y, aircraft.getClimbRate());
            float setClimb = altitudeController.calculateOutput();
            verticalSpeedController.updateValues(setClimb, aircraft.getClimbRate(), aircraft.getAcceleration().y);
            Cm_deltaE = verticalSpeedController.calculateOutput();

            if (Math.abs(setAltitude - altitude) > 20){
                climbAndHold = true;
                aircraft.getAutoPilot().setMode(AutoPilotMode.VERTICAL_SPEED);
            }
        }

        aircraft.setCm_deltaE(Cm_deltaE);
        ElevatorDataUI.setDeflection(Cm_deltaE); // multiply with -25 degrees per C_m
    }

    public void changeSetPitchAngle(float amount){
        setPitchAngle -= amount;
        setPitchAngle = MathUtils.putInDomain(setPitchAngle);
    }

    public float getSetPitchAngle(){
        return setPitchAngle;
    }

    public void setMode(AutoPilotMode mode){
        this.mode = mode;
    }

    public AutoPilotMode getMode(){
        return mode;
    }

    public int calculateAutoThrottle() {
        float airspeed = wing.getTrueAirspeed();
        float powerReq = (wing.getDrag() * airspeed + aircraft.getWeight() * airspeed * (float) Math.sin(wing.getFlightPathAngle()))
                * aircraft.getAir().getInvertedDensityRatio();

        float power = powerReq + 10000 * (setAirspeed - airspeed);
        int throttle = Math.round(100 * power / aircraft.getEngine().getMaxPower());

        // limit values
        int minValue = 0;
        int maxValue = 100;
        if (throttle < minValue){
            return minValue;
        }
        return Math.min(throttle, maxValue);
    }

    public void changeSetClimbRate(float amount, boolean coarseTuning) {
        setClimbRate -= ((coarseTuning ? 100 : 10) * amount / UnitConversionUtils.getMps2Feetpmin());
    }

    public float getSetClimbRate(){
        return setClimbRate;
    }

    public void changeSetAltitude(float amount, boolean coarseTuning) {
        setAltitude -= (coarseTuning ? 100 : 10) * amount / UnitConversionUtils.getM2Feet();
    }

    public void setSetAltitude(float setAltitude) {
        this.setAltitude = setAltitude;
    }

    public float getSetAltitude() {
        return setAltitude;
    }

    public void toggleClimbAndHold() {
        this.climbAndHold = !climbAndHold;
    }

    public void setClimbAndHold(boolean climbAndHold){
        this.climbAndHold = climbAndHold;
    }

    public boolean drawSetAltitude(){
        return mode == AutoPilotMode.ALTITUDE_HOLD || (mode == AutoPilotMode.VERTICAL_SPEED && climbAndHold);
    }

    public boolean drawSetAirspeed(){
        return autoThrottle;
    }

    public void toggleAutoThrottle() {
        autoThrottle = !autoThrottle;
        this.setAirspeed = Math.round(wing.getTrueAirspeed() / UnitConversionUtils.getMps2Knts()) * UnitConversionUtils.getMps2Knts();
        System.out.println("Auto throttle: " + autoThrottle);
    }

    public void changeSetAirspeed(float amount) {
        setAirspeed -= amount / UnitConversionUtils.getMps2Knts();
    }

    public float getSetAirspeed() {
        return setAirspeed;
    }

    public boolean getAutoThrottle() {
        return autoThrottle;
    }

    public boolean isClimbAndHold() {
        return climbAndHold;
    }
}
