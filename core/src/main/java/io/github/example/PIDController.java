package io.github.example;

public class PIDController {
    private final float P, I, D;
    private final float minValue, maxValue;
    private float error;
    private float errorIntegral;
    private float errorDot;

    public PIDController(float P, float I, float D, float min, float max){
        this.P = P;
        this.I = I;
        this.D = D;
        this.minValue = min;
        this.maxValue = max;
    }

    public void updateValues(float setValue, float realValue, float errorDot, float errorIntegral){
        this.error = setValue - realValue;
        this.errorDot = errorDot;
        this.errorIntegral = errorIntegral;
    }

    public void updateValues(float setValue, float realValue, float errorDot){
        this.error = setValue - realValue;
        this.errorDot = errorDot;
        this.errorIntegral += error;
    }

    public float calculateOutput(){
        float output = P * error + I * errorIntegral + D * errorDot;

        // limit values
        if (output < minValue){
            return minValue;
        }
        return Math.min(output, maxValue);
    }

    public void resetErrorIntegral() {
        this.errorIntegral = 0;
    }
}
