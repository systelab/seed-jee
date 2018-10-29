package com.systelab.seed.util;

import java.util.Arrays;
import java.util.List;

public class FormulasUtil {
	 
	public static List<Double> invalidDoubleValues = Arrays.asList(Double.POSITIVE_INFINITY,
			Double.NEGATIVE_INFINITY, null, Double.NaN);

	  /*
	   * Coefficient of Variation Target_CV = Target_SD / Target_Mean
	   */
	  public static Double getCVCalculation(Double pMean, Double pSD)
	  {
	    if (!invalidDoubleValues.contains(pMean) && !invalidDoubleValues.contains(pSD) && pMean.doubleValue() != 0)
	    {
	      return new Double((pSD.doubleValue() * 100 / pMean.doubleValue()));
	    }
	    return null;
	  }
	  
}
