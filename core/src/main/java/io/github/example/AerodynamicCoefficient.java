package io.github.example;

public class AerodynamicCoefficient {
    float[] alphas;
    float[] coefficients;

    public AerodynamicCoefficient(float[] alphas, float[] coefficients){
        this.alphas = alphas;
        this.coefficients = coefficients;
    }

    protected float calculateCoefficient(float angleOfAttack){
        for (int i = 1; i < alphas.length; i++){
            float alpha = alphas[i];
            float previousAlpha = alphas[i-1];
            if (angleOfAttack >= previousAlpha && angleOfAttack <= alpha){
                float dA = alpha - previousAlpha;
                float dC = coefficients[i] - coefficients[i-1];
                float slope = dC / dA;
                return coefficients[i-1] + (angleOfAttack - previousAlpha) * slope;
            }
        }
        return 0;
    }
}
