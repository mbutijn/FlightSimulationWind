package io.github.example.utils;

public class UnitConversionUtils {
    private final static float MPS2KNTS = 1.94384f;
    private final static float KNTS2MPS = 0.51444f;
    private final static float M2FEET = 3.2808399f;
    private final static float FEET2M = 0.3048f;
    private final static float MPS2FEETPMIN = 196.85f;
    private final static float FEETPMIN2MPS = 0.00508f;

    public static float getMps2Knts() {
        return MPS2KNTS;
    }

    public static float getKnts2Mps() {
        return KNTS2MPS;
    }

    public static float convertMps2Knts(float velocityInMps) {
        return MPS2KNTS * velocityInMps;
    }

    public static float convertKnts2Mps(float velocityInKnts) {
        return KNTS2MPS * velocityInKnts;
    }

    public static float convertM2Feet(float distanceInMeter) {
        return M2FEET * distanceInMeter;
    }

    public static float convertFeet2M(float distanceInFeet) {
        return FEET2M * distanceInFeet;
    }

    public static float getMps2Feetpmin(float climbSpeedInMps) {
        return MPS2FEETPMIN * climbSpeedInMps;
    }

    public static float convertFeetpmin2Mps(float climbSpeedInFPMin) {
        return FEETPMIN2MPS * climbSpeedInFPMin;
    }
}
