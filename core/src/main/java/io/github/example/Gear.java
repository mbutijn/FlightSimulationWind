package io.github.example;

import com.badlogic.gdx.math.Vector2;

public class Gear {
    private final Aircraft aircraft;
    private float moment;
    private final Vector2 normalFront, normalRear;
//    private boolean onGround;

    public Gear(Aircraft aircraft){
        this.aircraft = aircraft;
        this.normalFront = new Vector2();
        this.normalRear = new Vector2();
//        this.onGround = false;
    }

    public void updateNormalForcesAndMoment() {
        float [] vertices = aircraft.getHitBox().getTransformedVertices();
//        float x_cg = aircraft.getPosition().x;
        float x_cg = aircraft.getSprite().getX() + 0.6f * aircraft.getSprite().getWidth();
        float y_cg = aircraft.getSprite().getY() + 0.5f * aircraft.getSprite().getHeight();

        float xFront = vertices[10];
        float yFront = vertices[11];
        float stiffness = 37634.7f;
        if (yFront < 0){
            normalFront.y = stiffness * -yFront;
            System.out.println("front:" + (xFront - x_cg));
            moment = (xFront - x_cg) * normalFront.y;
        } else {
            normalFront.y = 0;
        }

        float xRear = vertices[12];
        float yRear = vertices[13];
        if (yRear < 0){
            normalRear.y = stiffness * -yRear;
            System.out.println("rear:" + (xRear - x_cg));
            moment += (xRear - x_cg) * normalRear.y;
        } else {
            normalRear.y = 0;
        }
    }

    public void brake(){
        normalFront.x = -1000;
        normalRear.x = -1000;
    }

    public void releaseBrake(){
        normalFront.x = 0;
        normalRear.x = 0;
    }

    public Vector2 getNormalFront(){
        return normalFront;
    }

    public Vector2 getNormalRear() {
        return normalRear;
    }

    public float getMoment() {
        return moment;
    }

    public void reset() {
        normalFront.setZero();
        normalRear.setZero();
        moment = 0;
    }
}
