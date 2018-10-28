package com.systelab.seed.util;

import java.util.ArrayList;

public class FormulasUtil {
	 
	public static ArrayList<Double> invalidDoubleValues = new ArrayList<Double>()
	  {
	    {
	      add(Double.POSITIVE_INFINITY);
	      add(Double.NEGATIVE_INFINITY);
	      add(null);
	      add(Double.NaN);
	    }
	  };
	  
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
