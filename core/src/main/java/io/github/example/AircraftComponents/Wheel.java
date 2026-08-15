package io.github.example.AircraftComponents;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import io.github.example.Aircraft;
import io.github.example.GearPosition;

public class Wheel {
    private final Aircraft aircraft;
    private final Vector2 position, reactionForce, suspensionPoint, suspensionMoving, startFrame;
    private final float stiffness, damping, angle, fuselageSectionDistance;
    private float moment, previousDisplacement, frameDistance;
    private boolean onGround;
    private final Sprite sprite;
    private final float radius = 0.325f;

    public Wheel(Aircraft aircraft, float x, float y, float stiffness, float damping, float fuselageSectionDistance) {
        this.aircraft = aircraft;
        this.position = new Vector2(x, y); // front: 1.5, -1.7; rear: -0.9, -1.7
        this.angle = position.angleRad(); // front: -0.847817; rear: -2.0576956
        this.suspensionPoint = new Vector2(x, 0); // front: 1.5, 0; rear: -0.9, 0
        this.suspensionMoving = new Vector2(0,0);
        this.startFrame = new Vector2(0, 0);
        this.reactionForce = new Vector2(0, 0);
        this.moment = 0;
        this.stiffness = stiffness;
        this.damping = damping;
        this.onGround = false;
        this.sprite = new Sprite(new Texture("wheel.png"));
        this.fuselageSectionDistance = fuselageSectionDistance;
        this.sprite.setSize(2 * this.radius, 2 * this.radius);
        this.frameDistance = 1.75f;
    }

    public void updateReactionForceAndMoment(float dt, boolean isBraking) {
        float pitchAngle = aircraft.getPitchAngleInRadians();
        suspensionMoving.x = aircraft.getPosition().x + suspensionPoint.x * (float) Math.cos(pitchAngle) - suspensionPoint.y * (float) Math.sin(pitchAngle);
        suspensionMoving.y = aircraft.getPosition().y + suspensionPoint.x * (float) Math.sin(pitchAngle) + suspensionPoint.y * (float) Math.cos(pitchAngle);

        position.setAngleRad(pitchAngle + angle);
        float displacement = position.y + aircraft.getPosition().y;

        if (displacement < 0 && aircraft.getGear().getGearPosition() == GearPosition.DOWN) {
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

        float suspensionLength = Math.min(displacement, 0) + frameDistance - radius;
        float suspensionAngle = aircraft.getPitchAngleInRadians() - 0.5f * (float) (Math.PI);
        float cosSuspension = (float) Math.cos(suspensionAngle);
        float sinSuspension = (float) Math.sin(suspensionAngle);

        sprite.setPosition(
            suspensionMoving.x - radius + suspensionLength * cosSuspension,
            suspensionMoving.y - radius + suspensionLength * sinSuspension);

        startFrame.x = suspensionMoving.x + fuselageSectionDistance * cosSuspension;
        startFrame.y = suspensionMoving.y + fuselageSectionDistance * sinSuspension;

    }

    public Vector2 getReactionForce() {
        return reactionForce;
    }

    public float getMoment() {
        return moment;
    }

    public void reset() {
        reactionForce.setZero();
        moment = 0;
    }

    public void updateFrameDistanceAndPosition(float extensionFactor) {
        sprite.setOriginCenter();
        sprite.setRotation(aircraft.getPitchAngleInDegrees());
        sprite.setScale(1.0f, extensionFactor);

        float distance = 1.07f + 0.68f * extensionFactor;
        this.frameDistance = aircraft.getGear().getGearPosition() == GearPosition.DOWN ? 1.75f : distance;
    }

    public void render(SpriteBatch batch) {
        sprite.draw(batch);
    }

    public boolean isOnGround() {
        return onGround;
    }

    public Vector2 getSuspensionPoint() {
        return suspensionPoint;
    }

    public void renderSuspensionPoint(ShapeRenderer shape) {
        shape.circle(suspensionMoving.x, suspensionMoving.y, 0.1f, 20);
    }

    public void renderFrame(ShapeRenderer shape) {
        shape.rectLine(startFrame.x, startFrame.y, sprite.getX() + radius, sprite.getY() + radius, 0.15f);
    }
}
