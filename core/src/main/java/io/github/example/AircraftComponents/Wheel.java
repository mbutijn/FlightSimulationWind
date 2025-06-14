package io.github.example.AircraftComponents;

import com.badlogic.gdx.math.Vector2;
import io.github.example.Aircraft;

public class Wheel {
    private final Aircraft aircraft;
    private final Vector2 position, reactionForce;
    private final float angle;
    private float moment;
    private final float stiffness, damping;
    private float previousDisplacement;
    private boolean onGround;

    public Wheel(Aircraft aircraft, float x, float y, float stiffness, float damping){
        this.aircraft = aircraft;
        this.position = new Vector2(x, y);
        this.reactionForce = new Vector2(0, 0);
        this.angle = position.angleDeg();
        this.moment = 0;
        this.stiffness = stiffness;
        this.damping = damping;
        this.onGround = false;
    }

    public void updateReactionForceAndMoment(float dt, boolean isBraking){
        position.setAngleDeg(aircraft.getPitchAngle() + angle);

        float displacement = position.y + aircraft.getPosition().y;

        if (displacement < 0){
            reactionForce.y = -stiffness * displacement - damping * (displacement - previousDisplacement) / dt; // normal force
            float frictionCoefficient = aircraft.isMovingForward() ? 0.05f : 0.1f;

            if (aircraft.isMovingForward()) {
                reactionForce.x = -frictionCoefficient * reactionForce.y + (isBraking ? -1000 : 0);
            } else {
                reactionForce.x = frictionCoefficient * reactionForce.y + (isBraking ? 1000 : 0);
            }

            moment = (position.x * reactionForce.y) - (position.y * reactionForce.x); // cross product
            onGround = true;
            previousDisplacement = displacement;
        } else {
            reactionForce.y = 0;
            onGround = false;
        }
    }

    public Vector2 getReactionForce(){
        return reactionForce;
    }

    public float getMoment(){
        return moment;
    }

    public void reset() {
        reactionForce.setZero();
    }

    public boolean isOnGround() {
        return onGround;
    }
}
