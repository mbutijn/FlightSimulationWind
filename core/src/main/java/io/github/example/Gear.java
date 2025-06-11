package io.github.example;

public class Gear {
    private final Aircraft aircraft;
    private final Wheel frontWheel, rearWheel;
    private float moment;
    private BrakeCommand brakeCommand;

    public Gear(Aircraft aircraft){
        this.aircraft = aircraft;

        float [] vertices = aircraft.getHitBox().getVertices();
        float xFront = vertices[10];
        float yFront = vertices[11];

        float xRear = vertices[12];
        float yRear = vertices[13];

        float x_cg = aircraft.getSprite().getX() + aircraft.getCgPosition().x * aircraft.getSprite().getWidth();
        float y_cg = aircraft.getSprite().getY() + aircraft.getCgPosition().y * aircraft.getSprite().getHeight();

        this.frontWheel = new Wheel(xFront - x_cg, yFront - y_cg, 0.7f * 37634.7f, 5000);
        this.rearWheel = new Wheel(xRear - x_cg, yRear - y_cg, 1.3f * 37634.7f, 5000);
        this.brakeCommand = BrakeCommand.RELEASE_BRAKE;
    }

    public void updateNormalForcesAndMoment(float dt) {
        frontWheel.updateReactionForceAndMoment(aircraft.getPitchAngle(), aircraft.getPosition().y, dt);
        rearWheel.updateReactionForceAndMoment(aircraft.getPitchAngle(), aircraft.getPosition().y, dt);

        if (brakeCommand == BrakeCommand.BRAKE){
            brake();
        } else if (brakeCommand == BrakeCommand.RELEASE_BRAKE) {
            releaseBrake();
        }

        moment = frontWheel.getMoment();
        moment += rearWheel.getMoment();
    }

    public void brake(){
        if (aircraft.getVelocity().x > 0) {
            if (frontWheel.isOnGround()) {
                frontWheel.brake();
            }
            if (rearWheel.isOnGround()) {
                rearWheel.brake();
            }
        } else {
            releaseBrake();
        }
    }

    public void releaseBrake(){
        frontWheel.releaseBrake();
        rearWheel.releaseBrake();
    }

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

    public void setBrakeCommand(BrakeCommand brakeCommand) {
        this.brakeCommand = brakeCommand;
    }

    public boolean bothWheelsOnGround(){
        return frontWheel.isOnGround() && rearWheel.isOnGround();
    }
}
