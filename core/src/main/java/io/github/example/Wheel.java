package io.github.example;

import com.badlogic.gdx.math.Vector2;

public class Wheel {
    private final Vector2 position, reactionForce;
    private final float angle;
    private float moment;
    private final float stiffness;
    private boolean onGround;

    public Wheel(float x, float y, float stiffness){
        this.position = new Vector2(x, y);
        this.reactionForce = new Vector2(0, 0);
        this.angle = position.angleDeg();
        this.moment = 0;
        this.stiffness = stiffness;
        this.onGround = false;
    }

    public void updateReactionForceAndMoment(float pitchAngle, float y){
        position.setAngleDeg(pitchAngle + angle);

        float displacement = position.y + y;

        if (displacement < 0){
            reactionForce.y = -stiffness * displacement;
            moment = (position.x * reactionForce.y) - (position.y * reactionForce.x); // cross product
            onGround = true;
        } else {
            reactionForce.y = 0;
            onGround = false;
        }
    }

    public void brake(){
        reactionForce.x = -1000;
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
