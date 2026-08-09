package io.github.example.AircraftComponents;

import com.badlogic.gdx.math.Vector2;

public class Wind {
    public Vector2 velocity;
    float[] heights;
    float[] windSpeeds;
    float[] alpha;

    public Wind() {
        velocity = new Vector2(0, 0);
        heights =    new float[] {  0, 100, 500, 1000, 2000, 5000, 8000, 10000, 12000, 15000, 18000, 20000};
        windSpeeds = new float[] {-20, -23, -29,  -35,  -45,  -52,  -59,   -83,  -130,  -122,   -70,   -53};
        alpha =      new float[] {0.4f, 0.41f, 0.43f, 0.48f, 0.59f, 0.78f, 0.89f, 0.94f, 0.99f, 1f, 1f, 1f}; // rough terrain
    }

    public void updateWind(float inputHeight) {
        for (int i = 1; i < heights.length; i++) {
            float currentHeight = heights[i];
            float previousHeight = heights[i-1];
            if (inputHeight >= previousHeight && inputHeight <= currentHeight){
                float dH = currentHeight - previousHeight;
                float dW = windSpeeds[i] - windSpeeds[i-1];
                float slope = dW / dH;
                velocity.x = alpha[i] * (windSpeeds[i-1] + (inputHeight - previousHeight) * slope);
            }
        }
    }

    public Vector2 getVelocity() {
        return velocity;
    }
}
