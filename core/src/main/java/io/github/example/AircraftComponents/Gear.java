package io.github.example.AircraftComponents;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.example.Aircraft;
import io.github.example.BrakeCommand;

public class Gear {
    private final Aircraft aircraft;
    private final Wheel frontWheel, rearWheel;
    private float moment;
    private BrakeCommand brakeCommand;
    private final Color frameColor = new Color(0.345f, 0.392f, 0.404f, 1f);

    public Gear(Aircraft aircraft){
        this.aircraft = aircraft;

        float [] vertices = aircraft.getHitBox().getVertices();
        float xFront = vertices[14]; // = 8.0 front gear (right)
        float yFront = vertices[15]; // = 3.3

        float xRear = vertices[16]; // = 5.6 rear gear (left)
        float yRear = vertices[17]; // = 3.3

        float x_cg = aircraft.getSprite().getX() + aircraft.getCgPosition().x * aircraft.getSprite().getWidth(); // = 6.5
        float y_cg = aircraft.getSprite().getY() + aircraft.getCgPosition().y * aircraft.getSprite().getHeight(); // = 5.0

        this.frontWheel = new Wheel(aircraft, xFront - x_cg, yFront - y_cg, 0.7f * 37634.7f, 5000, 0.8f);
        this.rearWheel = new Wheel(aircraft, xRear - x_cg, yRear - y_cg, 1.3f * 37634.7f, 5000, 0.85f);
        this.brakeCommand = BrakeCommand.RELEASE_BRAKE;
    }

    public void updateNormalForcesAndMoment(float dt) {
        frontWheel.updateReactionForceAndMoment(dt, frontWheel.isOnGround() && brakeCommand == BrakeCommand.BRAKE);
        rearWheel.updateReactionForceAndMoment(dt, rearWheel.isOnGround() && brakeCommand == BrakeCommand.BRAKE);

        moment = frontWheel.getMoment();
        moment += rearWheel.getMoment();
    }

//    public void brake() {
//
//    }

//    public void releaseBrake(){
//        frontWheel.releaseBrake();
//        rearWheel.releaseBrake();
//    }

    public Wheel getFrontWheel() {
        return frontWheel;
    }

    public Wheel getRearWheel() {
        return rearWheel;
    }

    public float getMoment() {
        return moment;
    }

    public void reset() {
        frontWheel.reset();
        rearWheel.reset();
        moment = 0;
    }

    public void renderFrame(ShapeRenderer shape) {
        shape.setColor(frameColor);
        frontWheel.renderFrame(shape);
        rearWheel.renderFrame(shape);
    }

    public void setBrakeCommand(BrakeCommand brakeCommand) {
        this.brakeCommand = brakeCommand;
    }

    public boolean bothWheelsOnGround(){
        return frontWheel.isOnGround() && rearWheel.isOnGround();
    }

    public boolean isBraking() {
        return bothWheelsOnGround() && brakeCommand == BrakeCommand.BRAKE;
    }
}
