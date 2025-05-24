package io.github.example.UIComponents;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.example.Aircraft;

public class PitchAngleDataUI extends FlightDataUI {
    private final Color skyColor, groundColor;
    private final float middleHeight, middleWidth;

    public PitchAngleDataUI(Aircraft aircraft, Viewport viewport, ShapeRenderer shape, SpriteBatch batch, float x, float y) {
        super(aircraft, viewport, shape, batch, x, y);
        skyColor = new Color(0.47f, 0.64f, 0.9f, 1);
        groundColor = new Color(0.8f, 0.47f, 0.1f, 1);
        width = 60;
        height = 2 * width;
        middleWidth = x + 0.5f * width;
        middleHeight = y + 0.5f * height;
    }

    public void draw() {
        shape.setColor(Color.BLACK);
        shape.rect(x, y, width, 2  * width);
    }

    public void draw(boolean autoPilot) {
        int limit = 30;
        float heightPerDegree = height / (2 * limit); // = 2
        float pitchAngle = aircraft.getPitchAngle();
        float skyHeight, groundHeight;

        if (pitchAngle > -90 && pitchAngle < 90) {
            // draw the sky
            skyHeight = (pitchAngle + limit) * heightPerDegree;
            skyHeight = Math.min(skyHeight, height);
            if (skyHeight > 0) {
                shape.setColor(skyColor);
                shape.rect(x, y + height - skyHeight, width, skyHeight);
            }

            // draw the ground
            groundHeight = (limit - pitchAngle) * heightPerDegree;
            groundHeight = Math.min(groundHeight, height);
            if (groundHeight > 0) {
                shape.setColor(groundColor);
                shape.rect(x, y, width, groundHeight);
            }
        } else { // when the aircraft is upside down
            if (pitchAngle > 0){
                skyHeight = (limit + 180 - pitchAngle) * heightPerDegree;
                groundHeight = (limit - 180 + pitchAngle) * heightPerDegree;
            } else {
                skyHeight = (limit - 180 - pitchAngle) * heightPerDegree;
                groundHeight = (limit + 180 + pitchAngle) * heightPerDegree;
            }

            // draw sky
            if (skyHeight > 0) {
                skyHeight = Math.min(skyHeight, height);
                shape.setColor(skyColor);
                shape.rect(x, y, width, skyHeight);
            }

            // draw the ground
            if (groundHeight > 0) {
                groundHeight = Math.min(groundHeight, height);
                shape.setColor(groundColor);
                shape.rect(x, y + height - groundHeight, width, groundHeight);
            }
        }

        // draw the lines
        shape.setColor(Color.BLACK);
        int pitchAngleInt = Math.round(pitchAngle);
        float xRight = x + width;
        for (int i = pitchAngleInt - limit; i <= pitchAngleInt + limit; i++) {
            if (i % 5 == 0) {
                float reference = middleHeight + (i - pitchAngle) * heightPerDegree;
                float barLength = i % 90 == 0 ? 0.5f * width : i % 10 == 0 ? 10 : 5;
                shape.rectLine(x, reference, x + barLength, reference, 2);
                shape.rectLine(xRight, reference, xRight - barLength, reference, 2);
            }
        }

        // draw static symbol
        float x1 = middleWidth - 4;
        float x2 = middleWidth + 4;
        shape.rectLine(x + 10, middleHeight, x1, middleHeight, 2);
        shape.rectLine(xRight - 10, middleHeight, x2, middleHeight, 2);
        shape.rectLine(x1, middleHeight, x1, middleHeight - 5, 2);
        shape.rectLine(x2, middleHeight, x2, middleHeight - 5, 2);

        if (autoPilot){
            float difference = pitchAngle - aircraft.getAutoPilot().getSetPitchAngle();
            if (difference > -limit && difference < limit) {
                shape.setColor(Color.RED);
                float setPitchAngle = middleHeight - difference * heightPerDegree;
                shape.rectLine(x - 10, setPitchAngle, x, setPitchAngle, 2);
                shape.rectLine(xRight, setPitchAngle, xRight + 10, setPitchAngle, 2);
            }
        }
    }

    public void writeValues(){
        int pitchAngleInt = Math.round(aircraft.getPitchAngle());
        int limit = 30;
        float heightPerDegree = height / (2 * limit); // = 2

        font.setColor(Color.BLACK);
        for (int i = pitchAngleInt - limit + 3; i <= pitchAngleInt + limit - 3; i++) {
            if (i % 10 == 0 && i % 90 != 0) {
                layout.setText(font, Integer.toString(i > 180 ? i - 360 : i < -180 ? i + 360 : i));
                font.draw(batch, layout, middleWidth - 0.5f * layout.width, middleHeight + (i - pitchAngleInt) * heightPerDegree + 0.5f * layout.height);
            }
        }
        font.setColor(FlightDataUI.color);
    }

}
