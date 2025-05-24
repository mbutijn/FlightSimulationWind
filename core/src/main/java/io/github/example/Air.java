package io.github.example;

public class Air {
    final float rho0 = 1.225f; // sea level air density (kg/m³)
    final float L = 0.0065f; // temperature lapse rate (K/m)
    final float T0 = 288.15f; // sea level standard temperature (K)
    final float g = 9.80665f; // acceleration due to gravity (m/s²)
    final float M = 0.0289644f; // molar mass of air (kg/mol)
    final float R = 8.3144598f; // universal gas constant (J/(mol·K))
    final float P0 = 101325; // sea-level standard pressure (Pa)
    final float gamma = 1.4f; // specific heat ratio air (-)
    final float constant1, constant2;
    float density; // air density (kg/m³)
    float temperature; // air temperature (deg)
    float pressure; // air pressure (Pa)
    float speedOfSound; // speed of sound (m/s)

    public Air(){
        this.constant1 = L / T0;
        this.constant2 = (g * M) / (R * L);
    }

    public void updateProperties(float height) {
        float constant = 1 - constant1 * height;
        density = (float) (rho0 * Math.pow(constant, constant2 - 1));
        temperature = T0 - L * height;
        pressure = (float) (P0 * Math.pow(constant, constant2));
        speedOfSound = (float) Math.sqrt(gamma * R * temperature / M);
    }

    public float getDensity(){
        return density;
    }

    public float getTemperature() {
        return temperature;
    }

    public float getPressure(){
        return pressure;
    }

    public float getRho0(){
        return rho0;
    }

    public float getSpeedOfSound(){
        return speedOfSound;
    }

    public float getDensityRatio(){
        return density / rho0;
    }

    public float getInvertedDensityRatio(){
        return rho0 / density;
    }
}
