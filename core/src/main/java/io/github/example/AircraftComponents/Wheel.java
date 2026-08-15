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
    private final Vector2 normalForce, drag, position, suspensionPoint, suspensionMoving, startFrame;
    private final float stiffness, damping, angle, fuselageSectionDistance;
    private float momentNormalForce, momentDrag, previousDisplacement, frameDistance;
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
        this.normalForce = new Vector2(0, 0);
        this.drag = new Vector2(0, 0);
        this.momentNormalForce = 0;
        this.stiffness = stiffness;
        this.damping = damping;
        this.onGround = false;
        this.sprite = new Sprite(new Texture("wheel.png"));
        this.fuselageSectionDistance = fuselageSectionDistance;
        this.sprite.setSize(2 * this.radius, 2 * this.radius);
        this.frameDistance = 1.75f;
    }

    public void updateAllForcesAndMoment(float dt, boolean isBraking) {
        float pitchAngleRad = aircraft.getPitchAngleInRadians();
        suspensionMoving.x = aircraft.getPosition().x + suspensionPoint.x * (float) Math.cos(pitchAngleRad) - suspensionPoint.y * (float) Math.sin(pitchAngleRad);
        suspensionMoving.y = aircraft.getPosition().y + suspensionPoint.x * (float) Math.sin(pitchAngleRad) + suspensionPoint.y * (float) Math.cos(pitchAngleRad);

        position.setAngleRad(pitchAngleRad + angle);
        float displacement = position.y + aircraft.getPosition().y;

        if (displacement < 0 && aircraft.getGear().getGearPosition() == GearPosition.DOWN) {
            normalForce.y = -stiffness * displacement - damping * (displacement - previousDisplacement) / dt; // normal force
            float frictionCoefficient = aircraft.isMovingForward() ? 0.05f : 0.1f;

            if (aircraft.isMovingForward()) {
                normalForce.x = -frictionCoefficient * normalForce.y + (isBraking ? -1000 : 0);
            } else {
                normalForce.x = frictionCoefficient * normalForce.y + (isBraking ? 1000 : 0);
            }

            momentNormalForce = (position.x * normalForce.y) - (position.y * normalForce.x); // cross product
            onGround = true;
            previousDisplacement = displacement;
        } else {
            normalForce.y = 0;
            onGround = false;
        }

        if (aircraft.getGear().getGearPosition() == GearPosition.UP) {
            drag.setZero();
        } else {
            updateDragAndMomentContribution(pitchAngleRad);
        }

        float suspensionLength = Math.min(displacement, 0) + frameDistance - radius;
        float suspensionAngle = pitchAngleRad - 0.5f * (float) (Math.PI);
        float cosSuspension = (float) Math.cos(suspensionAngle);
        float sinSuspension = (float) Math.sin(suspensionAngle);

        sprite.setPosition(
            suspensionMoving.x - radius + suspensionLength * cosSuspension,
            suspensionMoving.y - radius + suspensionLength * sinSuspension);

        startFrame.x = suspensionMoving.x + fuselageSectionDistance * cosSuspension;
        startFrame.y = suspensionMoving.y + fuselageSectionDistance * sinSuspension;
    }

    public void updateDragAndMomentContribution(float pitchAngleRad) {
        float dragCoefficient = aircraft.getGear().getExtensionFactor() * 0.05f;
        float dynamicPressure = aircraft.getDynamicPressure();
        drag.x = -dragCoefficient * dynamicPressure * (float) Math.cos(pitchAngleRad);
        drag.y = -dragCoefficient * dynamicPressure * (float) Math.sin(pitchAngleRad);
        momentDrag = (position.x * drag.y) - (position.y * drag.x); // cross product
    }

    public Vector2 getNormalForce() {
        return normalForce;
    }

    public Vector2 getDragForce() {
        return drag;
    }

    public float getMomentNormalForce() {
        return momentNormalForce;
    }

    public float getMomentDragContribution() {
        return momentDrag;
    }

    public void reset() {
        normalForce.setZero();
        momentNormalForce = 0;
        momentDrag = 0;
    }

    public void updateFrameDistanceAndPosition(float extensionFactor) {
        sprite.setOriginCenter();
        sprite.setRotation(aircraft.getPitchAngleInDegrees());
        sprite.setScale(1.0f, extensionFactor);

        this.frameDistance = 1.07f + 0.68f * extensionFactor;
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
