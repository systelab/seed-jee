package com.systelab.seed.patient;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import com.systelab.seed.patient.control.BodyMassIndexCalculator;
import com.systelab.seed.utils.TestUtil;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;

@RunWith(Enclosed.class)
public class BodyMassIndexCalculatorTest {

	  @TmsLink("TC1234_Formulas")
	  @Feature("Goal: This test case is intended to verify the correct calculation of the different Statistical Calculations")
	  @RunWith(Parameterized.class)
	  public static class BMICalculationForCorrectValuesTest
	  {
	    @Parameter(0)
		public Double weight;
	    @Parameter(1)
	    public Double height;
	    @Parameter(2)
	    public Double expectedResult;

	    @Parameters(name = "weight={0},height={1},expectedResult={2}")
	    public static Collection<Object[]> data()
	    {
	      // @formatter:off
	    	Object[][] data = new Object[][] {
	            { 0.0, 0.0, null },
	            { -1.0, 0.0, null },
	            { -1.0, 1.0, null },
	            { 1.0, -1.0, null },
	            { 1.0, 0.0, null },
	            { 5.0, 0.1, null },
				{ 30.0, 0.5, 120.0 },
				{ 45.0, 1.0, 45.0 },
	            { 65.0, 1.40, 33.2 },
	            { 75.0, 1.8, 23.1 },
	            { 95.0, 1.8, 29.3 },
	            { 78.5, 1.805, 24.1 },
	          };
	      // @formatter:on
	      return Arrays.asList(data);
	    }

	    @Description("Calculate BMI")
        @Test
        public void checkBMICalculationForCorrectValuesTest() {
	    	Double bmiCalculated = BodyMassIndexCalculator.getBMI(weight, height);
	    	if (expectedResult==null)
				TestUtil.checkObjectIsNull("checkBMICalculation: ", bmiCalculated);
	    	else
            	TestUtil.checkField("checkBMICalculation: ", expectedResult, bmiCalculated , 0.0000005);
        }
	  }

	  //This is an example of a Single Test in the same class with Parameterized Tests
	  @TmsLink("TC1234_Formulas")
	  @Feature("Goal: This test case is intended to verify the correct calculation of the different Statistical Calculations")
	  public static class ComponentSingleTests
	  {
	    @Description("Calculate BMI")
	    @Test
	    public void checkSingleCalculation()
	    {
	    	Double bmiCalculated = BodyMassIndexCalculator.getBMI(73.5, 1.786);
            TestUtil.checkField("checkBMICalculation: ", 23.0, bmiCalculated , 0.0000005);
	    }
	  }
}
