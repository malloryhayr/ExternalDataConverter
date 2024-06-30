package ca.spottedleaf.dataconverter.util;

public class Mth {

    public static int roundToward(int value, int divisor) {
        return positiveCeilDiv(value, divisor) * divisor;
    }

    public static int positiveCeilDiv(int a, int b) {
        return -Math.floorDiv(-a, b);
    }

    public static int clamp(int value, int min, int max) {
        return Math.min(Math.max(value, min), max);
    }

    public static int floor(double value) {
        return (int) Math.floor(value);
    }
}
