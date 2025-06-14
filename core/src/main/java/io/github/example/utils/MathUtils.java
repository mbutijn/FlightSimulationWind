package io.github.example.utils;

public class MathUtils {
    public static float putInDomain(float angle){
        while (angle < -180){
            angle += 360;
        }
        while (angle > 180){
            angle -= 360;
        }
        return angle;
    }
}
