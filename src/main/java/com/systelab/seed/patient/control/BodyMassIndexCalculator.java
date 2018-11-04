package com.systelab.seed.patient.control;

import java.util.Arrays;
import java.util.List;

public class BodyMassIndexCalculator {

    public static List<Double> invalidDoubleValues = Arrays.asList(Double.POSITIVE_INFINITY,
            Double.NEGATIVE_INFINITY, null, Double.NaN);

    /*
     * BMI = weight (kg) ÷ height2 (m2)
     */
    public static Double getBMI(Double weight, Double height) {
        if (!invalidDoubleValues.contains(weight) && !invalidDoubleValues.contains(height) && weight > 1 && height > 0.1) {
            return (double)Math.round(weight / Math.pow(height, 2) * 10) / 10;
        }
        return null;
    }
}
