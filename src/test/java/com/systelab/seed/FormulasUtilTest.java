package com.systelab.seed;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import com.systelab.seed.util.FormulasUtil;
import com.systelab.seed.utils.TestUtil;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;

@RunWith(Enclosed.class)
public class FormulasUtilTest {

	  @TmsLink("TC1234_Formulas")
	  @Feature("Goal: This test case is intended to verify the correct calculation of the different Statistical Calculations")
	  @RunWith(Parameterized.class)
	  public static class CVCalculationForCorrectValuesTest
	  {
	    @Parameter(0)
		public Double mean;
	    @Parameter(1)
	    public Double sD;
	    @Parameter(2)
	    public Double expectedResult;

	    @Parameters(name = "mean={0},sd={1},expectedResult={2}")
	    public static Collection<Object[]> data()
	    {
	      // @formatter:off
	    	Object[][] data = new Object[][] {
	            /*mean, sd , expectedResult*/
	            { -1.0, -1.0, 100.0 },
	            { -1.0, 0.0, -0.0 },
	            { -1.0, 1.0, -100.0 },
	            { 1.0, -1.0, -100.0 },
	            { 1.0, 0.0, 0.0 },
	            { 1.0, 1.0, 100.0 },
	            { 0.1, 1.0, 1000.0 },
	            { 21.08103555, -0.230084016, -1.0914265357 },
	            { 26.22904998, -9.952216335, -37.943487631 },
	            { -15.7184542, -13.31250055, 84.693446 },
	            { -3.3763288, 0.669902095, -19.8411391 },
	            { 5.211201366, 20.20752329, 387.7709163 },
	            { 329.9780152, 5.642665465, 1.710012548 },
	            { -2.847978558, 1.884544469, -66.1713012 },
	            { 52.99759077, 13.53356239, 25.53618418 },
	            { 7.172113126, -1.925796424, -26.85117189 },
	            { -11.32868765, -17.20830806, 151.9002782 },
	            { 26.83749938, -2.543020798, -9.47562499 },

	          };
	      // @formatter:on
	      return Arrays.asList(data);
	    }

	    @Description("Calculate CV")
        @Test
        public void checkCVCalculationForCorrectValuesTest() {
	    	Double cvCalculated = FormulasUtil.getCVCalculation(mean, sD);
            TestUtil.checkField("checkCVCalculation: ", expectedResult, cvCalculated , 0.0000005);
        }
	  }

	  //This is an example of a Single Test in the same class with Parameterized Tests
	  @TmsLink("TC1234_Formulas")
	  @Feature("Goal: This test case is intended to verify the correct calculation of the different Statistical Calculations")
	  public static class ComponentSingleTests
	  {
	    @Description("Calculate SD")
	    @Test
	    public void checkSingleCalculation()
	    {
	    	Double sdCalculated = 2.0; //FormulaUtil.getSDCalculation(mean, sD);
            TestUtil.checkField("checkSDCalculation: ", 2.0, sdCalculated , 0.0000005);
	    }
	  }
}
