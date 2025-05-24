package io.github.example;

public class UnitConversionUtils {
    private final static float MPS2KNTS = 1.94384f;
    private final static float M2FEET = 3.2808399f;
    private final static float MPS2FEETPMIN = 196.85f;

    public static float getMps2Knts(){
        return MPS2KNTS;
    }

    public static float getM2Feet(){
        return M2FEET;
    }

    public static float getMps2Feetpmin(){
        return MPS2FEETPMIN;
    }
}
