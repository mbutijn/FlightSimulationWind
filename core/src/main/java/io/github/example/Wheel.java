package io.github.example;

import com.badlogic.gdx.math.Vector2;

public class Wheel {
    private final Vector2 position, reactionForce;
    private final float angle;
    private float moment;
    private final float stiffness, damping;
    private float previousDisplacement;
    private boolean onGround;

    public Wheel(float x, float y, float stiffness, float damping){
        this.position = new Vector2(x, y);
        this.reactionForce = new Vector2(0, 0);
        this.angle = position.angleDeg();
        this.moment = 0;
        this.stiffness = stiffness;
        this.damping = damping;
        this.onGround = false;
    }

    public void updateReactionForceAndMoment(float pitchAngle, float yAircraft, float dt){
        position.setAngleDeg(pitchAngle + angle);

        float displacement = position.y + yAircraft;

        if (displacement < 0){
            reactionForce.y = -stiffness * displacement - damping * (displacement - previousDisplacement) / dt;
            moment = (position.x * reactionForce.y) - (position.y * reactionForce.x); // cross product
            onGround = true;
            previousDisplacement = displacement;
        } else {
            reactionForce.y = 0;
            onGround = false;
        }
    }

    public void brake(float velocity){
        if (velocity > 0) {
            reactionForce.x = -1000;
        } else {
            reactionForce.x = 1000;
        }
    }

    public void releaseBrake(){
        reactionForce.x = 0;
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
