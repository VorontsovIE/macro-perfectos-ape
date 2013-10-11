package ru.autosome.macroape.Calculations;

import ru.autosome.macroape.*;

import java.io.File;
import java.util.ArrayList;

public class PrecalculateThresholdList {
  public static class Parameters {
    private double discretization;
    private BackgroundModel background;
    private String pvalue_boundary;
    private int max_hash_size;
    private double[] pvalues;
    public Parameters() {}
    public Parameters(double[] pvalues, double discretization, BackgroundModel background, String pvalue_boundary, int max_hash_size) {
      this.pvalues = pvalues;
      this.discretization = discretization;
      this.background = background;
      this.pvalue_boundary = pvalue_boundary;
      this.max_hash_size = max_hash_size;
    }
  }
  Parameters parameters;
  public PrecalculateThresholdList(Parameters parameters) {
    this.parameters = parameters;
  }

  private ru.autosome.macroape.Calculations.FindThresholdAPE.Parameters find_threshold_parameters(PWM pwm) {
    return new ru.autosome.macroape.Calculations.FindThresholdAPE.Parameters(pwm,
                                                                             parameters.background,
                                                                             parameters.discretization,
                                                                             parameters.pvalue_boundary,
                                                                             parameters.max_hash_size);
  }

  private ru.autosome.macroape.Calculations.FindThresholdAPE find_threshold_calculator(PWM pwm) {
    return new ru.autosome.macroape.Calculations.FindThresholdAPE(find_threshold_parameters(pwm));
  }

  public ArrayList<ThresholdPvaluePair> calculate_thresholds_for_pwm(PWM pwm) {
    ArrayList<ThresholdPvaluePair> pairs = new ArrayList<ThresholdPvaluePair>();
    for (ThresholdInfo info : find_threshold_calculator(pwm).find_thresholds_by_pvalues(parameters.pvalues)) {
      pairs.add(new ThresholdPvaluePair(info));
    }
    return pairs;
  }

  public PvalueBsearchList bsearch_list_for_pwm(PWM pwm) {
    return new PvalueBsearchList(calculate_thresholds_for_pwm(pwm));
  }


}