package com.systelab.seed.patient.control;

public class BodyMassIndexCalculator {

    private BodyMassIndexCalculator()
    {
    }

    private static boolean isValidWeight(Double value) {
        return value != null && Double.isFinite(value) && value > 1;
    }

    private static boolean isValidHeight(Double value) {
        return value != null && Double.isFinite(value) && value > 0.1;
    }

    /*
     * BMI = weight (kg) ÷ height2 (m2)
     */
    public static Double getBMI(Double weight, Double height) {
        if (isValidWeight(weight) && isValidHeight(height)) {
            return (double) Math.round(weight / Math.pow(height, 2) * 10) / 10;
        }
        return null;
    }
}
