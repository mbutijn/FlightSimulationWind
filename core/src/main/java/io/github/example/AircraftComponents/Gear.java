package io.github.example.AircraftComponents;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.example.Aircraft;
import io.github.example.BrakeCommand;
import io.github.example.GearPosition;

public class Gear {
    private final Aircraft aircraft;
    private final Wheel frontWheel, rearWheel;
    private float moment;
    private BrakeCommand brakeCommand;
    private final Color frameColor = new Color(0.345f, 0.392f, 0.404f, 1f);
    private GearPosition gearPosition = GearPosition.DOWN;
    private long beginTimeRetracting, beginTimeExtending;
    private float extensionFactor = 1.0f; // 1.0 means fully extended, 0.0 means fully retracted

    public Gear(Aircraft aircraft) {
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

    public void update(float dt) {
        // step 1 Update gear position
        //Polygon hitBox = aircraft.getHitBox();
        if (gearPosition == GearPosition.RETRACTING) {
            float time = System.currentTimeMillis() - beginTimeRetracting;
            float RETRACTING_TIME = 3000; // milliseconds
            extensionFactor = 1 - (time / RETRACTING_TIME);
            processGearWheels();
            //float [] vertices = hitBox.getVertices();

//            aircraft.updateHitBox(1 - (time / RETRACTING_TIME));

            if (time >= RETRACTING_TIME) {
                gearPosition = GearPosition.UP;
            }
        }

        if (gearPosition == GearPosition.EXTENDING) {
            float time = System.currentTimeMillis() - beginTimeExtending;
            float EXTENDING_TIME = 3000; // milliseconds
            extensionFactor = time / EXTENDING_TIME;
            processGearWheels();
            if (time >= EXTENDING_TIME) {
                gearPosition = GearPosition.DOWN;
            }
        }

        // step 2 Update normal forces and moment
        frontWheel.updateReactionForceAndMoment(dt, frontWheel.isOnGround() && brakeCommand == BrakeCommand.BRAKE);
        rearWheel.updateReactionForceAndMoment(dt, rearWheel.isOnGround() && brakeCommand == BrakeCommand.BRAKE);

        moment = frontWheel.getMoment();
        moment += rearWheel.getMoment();
    }

    private void processGearWheels() {
        //float position = 1.07f + 0.68f * extensionFactor;
//        frontWheel.updateShape(extensionFactor);
        frontWheel.updateFrameDistanceAndPosition(extensionFactor);
//        rearWheel.updateShape(extensionFactor);
        rearWheel.updateFrameDistanceAndPosition(extensionFactor);
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

    public void renderFrame(ShapeRenderer shape) {
        if (gearPosition != GearPosition.UP) {
            shape.setColor(frameColor);
            frontWheel.renderFrame(shape);
            rearWheel.renderFrame(shape);
        }
    }

    public void toggleGearPosition() {
        if (gearPosition == GearPosition.UP) {
            beginTimeExtending = System.currentTimeMillis();
            gearPosition = GearPosition.EXTENDING;
        } else if (gearPosition == GearPosition.DOWN && !frontWheel.isOnGround() && !rearWheel.isOnGround()) {
            beginTimeRetracting = System.currentTimeMillis();
            gearPosition = GearPosition.RETRACTING;
        }
    }

    public GearPosition getGearPosition() {
        return gearPosition;
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

    public void renderWheels(SpriteBatch batch) {
        if (gearPosition != GearPosition.UP) {
            frontWheel.render(batch);
            rearWheel.render(batch);
        }
    }

    public float getExtensionFactor() {
        return extensionFactor;
    }
}
