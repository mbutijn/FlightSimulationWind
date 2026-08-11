package io.github.example.AircraftComponents;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import io.github.example.Aircraft;

public class Wheel {
    private final Aircraft aircraft;
    private final Vector2 position, reactionForce, suspensionPoint, suspensionMoving;
    private final float angle;
    private float moment;
    private final float stiffness, damping;
    private float previousDisplacement;
    private boolean onGround;
    private final Sprite sprite;
    private final float radius = 0.325f; // radius of the wheel

    public Wheel(Aircraft aircraft, float x, float y, float stiffness, float damping) {
        this.aircraft = aircraft;
        this.position = new Vector2(x, y); // front: 1.5, -1.7; rear: -0.9, -1.7
        this.angle = position.angleRad(); // front: -0.847817; rear: -2.0576956
        this.suspensionPoint = new Vector2(x, 0); // front: 1.5, 0; rear: -0.9, 0
        this.suspensionMoving = new Vector2(0,0);
        this.reactionForce = new Vector2(0, 0);
        this.moment = 0;
        this.stiffness = stiffness;
        this.damping = damping;
        this.onGround = false;
        this.sprite = new Sprite(new Texture("wheel.png"));
        sprite.setSize(2 * this.radius, 2 * this.radius);
    }

    public void updateReactionForceAndMoment(float dt, boolean isBraking) {
        float pitchAngle = aircraft.getPitchAngleInRadians();
        suspensionMoving.x = aircraft.getPosition().x + suspensionPoint.x * (float) Math.cos(pitchAngle) - suspensionPoint.y * (float) Math.sin(pitchAngle);
        suspensionMoving.y = aircraft.getPosition().y + suspensionPoint.x * (float) Math.sin(pitchAngle) + suspensionPoint.y * (float) Math.cos(pitchAngle);

        position.setAngleRad(pitchAngle + angle);
        float displacement = position.y + aircraft.getPosition().y;

        if (displacement < 0) {
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

        float suspensionLength = Math.min(displacement, 0) + 1.75f - radius;
        float suspensionAngle = aircraft.getPitchAngleInRadians() - 0.5f * (float) (Math.PI);

        sprite.setPosition(
            suspensionMoving.x - radius + suspensionLength * (float) (Math.cos(suspensionAngle)),
            suspensionMoving.y - radius + suspensionLength * (float) (Math.sin(suspensionAngle)));
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

    public void renderStructure(ShapeRenderer shape) {
        shape.setColor(Color.GRAY);
        shape.line(suspensionMoving.x, suspensionMoving.y, sprite.getX() + radius, sprite.getY() + radius);
    }
}
