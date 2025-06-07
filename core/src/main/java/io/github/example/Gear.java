package io.github.example;

import com.badlogic.gdx.math.Vector2;

public class Gear {
    private final Aircraft aircraft;
    private float moment;
    private final Vector2 front, rear;
    private final Vector2 normalFront, normalRear;
    private final float angleFront, angleRear;
//    private boolean onGround;

    public Gear(Aircraft aircraft){
        this.aircraft = aircraft;
        this.normalFront = new Vector2();

        float [] vertices = aircraft.getHitBox().getVertices();
        float x_cg = aircraft.getSprite().getX() + aircraft.getCgPosition().x * aircraft.getSprite().getWidth();
        float y_cg = aircraft.getSprite().getY() + aircraft.getCgPosition().y * aircraft.getSprite().getHeight();

        float xFront = vertices[10];
        float yFront = vertices[11];

        float xRear = vertices[12];
        float yRear = vertices[13];

        float xFrontDiff = xFront - x_cg;
        float xRearDiff = xRear - x_cg;

        float yFrontDiff = yFront - y_cg;
        float yRearDiff = yRear - y_cg;

        System.out.println("xFrontDiff:" + xFrontDiff);
        System.out.println("xRearDiff:" + xRearDiff);

        System.out.println("yFrontDiff:" + yFrontDiff);
        System.out.println("yRearDiff:" + yRearDiff);

        this.front = new Vector2(xFrontDiff, yFrontDiff);
        this.angleFront = front.angleDeg();
        this.normalRear = new Vector2();
        this.rear = new Vector2(xRearDiff, yRearDiff);
        this.angleRear = rear.angleDeg();
//        this.onGround = false;
    }

    public void updateNormalForcesAndMoment() {
        front.setAngleDeg(aircraft.getPitchAngle() + angleFront);
        rear.setAngleDeg(aircraft.getPitchAngle() + angleRear);

        float xFront = front.x;
        float yFront = front.y + aircraft.getPosition().y;

        if (yFront < 0){
            float stiffnessFront = 0.7f * 37634.7f;
            normalFront.y = stiffnessFront * -yFront;
            System.out.println("front:" + xFront);
            moment = xFront * normalFront.y;
        } else {
            normalFront.y = 0;
        }

        float xRear = rear.x;
        float yRear = rear.y + aircraft.getPosition().y;
        if (yRear < 0){
            float stiffnessRear = 1.3f * 37634.7f;
            normalRear.y = stiffnessRear * -yRear;
            System.out.println("rear:" + xRear);
            moment += xRear * normalRear.y;
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
