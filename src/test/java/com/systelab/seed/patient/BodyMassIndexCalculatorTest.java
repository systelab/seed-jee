package com.systelab.seed.patient;

import com.systelab.seed.patient.control.BodyMassIndexCalculator;
import com.systelab.seed.utils.TestUtil;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class BodyMassIndexCalculatorTest {

    static class Scenario {
        public Double weight;
        public Double height;
        public Double expectedResult;

        public Scenario(Double weight, Double height, Double expectedResult) {
            this.weight = weight;
            this.height = height;
            this.expectedResult = expectedResult;
        }
    }

    static Scenario[] scenarios() {
        // @formatter:off
        return new Scenario[]{
                new Scenario(0.0, 0.0, null),
                new Scenario(-1.0, 0.0, null),
                new Scenario(-1.0, 1.0, null),
                new Scenario(1.0, -1.0, null),
                new Scenario(1.0, 0.0, null),
                new Scenario(5.0, 0.1, null),
                new Scenario(30.0, 0.5, 120.0),
                new Scenario(45.0, 1.0, 45.0),
                new Scenario(65.0, 1.40, 33.2),
                new Scenario(75.0, 1.8, 23.1),
                new Scenario(95.0, 1.8, 29.3),
                new Scenario(78.5, 1.805, 24.1)
        };
        // @formatter:on
    }

    @Step("Action: With a weight of {0} kg and a height of {1} meters")
    public void printScenario(Double weight, Double height) {
    }

    @TmsLink("TC1234_Formulas")
    @Feature("Goal: This test case is intended to verify the correct calculation of the different Statistical Calculations")
    @ParameterizedTest
    @MethodSource("scenarios")
    public void checkBMICalculationForCorrectValuesTest(Scenario scenario) {
        printScenario(scenario.weight, scenario.height);
        Double bmiCalculated = BodyMassIndexCalculator.getBMI(scenario.weight, scenario.height);
        if (scenario.expectedResult == null)
            TestUtil.checkObjectIsNull("BMI", bmiCalculated);
        else
            TestUtil.checkField("BMI", scenario.expectedResult, bmiCalculated);
    }

    //This is an example of a Single Test in the same class with Parameterized Tests
    @TmsLink("TC1234_Formulas")
    @Feature("Goal: This test case is intended to verify the correct calculation of the different Statistical Calculations")
    public static class ComponentSingleTests {
        @Description("Calculate BMI")
        @Test
        public void checkSingleCalculation() {
            Double bmiCalculated = BodyMassIndexCalculator.getBMI(73.5, 1.786);
            TestUtil.checkField("BMI", 23.0, bmiCalculated);
        }
    }
}
