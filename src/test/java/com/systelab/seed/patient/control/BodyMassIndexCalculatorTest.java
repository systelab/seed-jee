package com.systelab.seed.patient.control;

import com.systelab.seed.utils.TestUtil;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

public class BodyMassIndexCalculatorTest {

    @Step("Action: Calculate the BMI with a weight of {0} kg and a height of {1} meters")
    public void printScenario(Double weight, Double height) {
    }

    @TmsLink("TC1234_Formulas")
    @Feature("Goal: This test case is intended to verify the correct calculation of the Body Mass Index")
    @ParameterizedTest
    @CsvFileSource(resources = "/bodyMassIndexCalculator.csv")
    public void checkBMICalculationForCorrectValuesTest(Double weight, Double height, String expectedResultStr) {
        Double expectedResult = expectedResultStr.equals("null") ? null : Double.parseDouble(expectedResultStr);
        printScenario(weight, height);
        Double bmiCalculated = BodyMassIndexCalculator.getBMI(weight, height);
        if (expectedResult == null)
            TestUtil.checkObjectIsNull("BMI", bmiCalculated);
        else
            TestUtil.checkField("BMI", expectedResult, bmiCalculated);
    }

    //This is an example of a Single Test in the same class with Parameterized Tests
    @TmsLink("TC1234_Formulas")
    @Feature("Goal: This test case is intended to verify the correct calculation of the Body Mass Index")
    public static class ComponentSingleTests {
        @Description("Calculate BMI")
        @Test
        public void checkSingleCalculation() {
            Double bmiCalculated = BodyMassIndexCalculator.getBMI(73.5, 1.786);
            TestUtil.checkField("BMI", 23.0, bmiCalculated);
        }
    }
}
