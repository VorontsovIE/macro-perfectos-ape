package ru.autosome.perfectosape.api;

import ru.autosome.perfectosape.BoundaryType;
import ru.autosome.perfectosape.backgroundModels.BackgroundModel;
import ru.autosome.perfectosape.calculations.HashOverflowException;
import ru.autosome.perfectosape.calculations.findThreshold.CanFindThreshold;
import ru.autosome.perfectosape.motifModels.PWM;

public class FindThresholdAPE extends SingleTask<CanFindThreshold.ThresholdInfo[]> {
  public static class Parameters {
    public BackgroundModel background;
    public Double discretization; // if discretization is null - it's not applied
    public BoundaryType pvalue_boundary;
    public Integer max_hash_size; // if max_hash_size is null - it's not applied
    public PWM pwm;
    public double[] pvalues;

    public Parameters() { }
    public Parameters(PWM pwm, double[] pvalues, BackgroundModel background,
                      Double discretization, BoundaryType pvalue_boundary, Integer max_hash_size) {
      this.pwm = pwm;
      this.pvalues = pvalues;
      this.background = background;
      this.discretization = discretization;
      this.pvalue_boundary = pvalue_boundary;
      this.max_hash_size = max_hash_size;
    }
  }

  Parameters parameters;
  public FindThresholdAPE(Parameters parameters) {
    super();
    this.parameters = parameters;
  }

  @Override
  public CanFindThreshold.ThresholdInfo[] launchSingleTask() throws HashOverflowException {
    return new ru.autosome.perfectosape.calculations.findThreshold.FindThresholdAPE<PWM, BackgroundModel>(parameters.pwm,
                                                                                                            parameters.background,
                                                                                                            parameters.discretization,
                                                                                                            parameters.max_hash_size)
     .thresholdsByPvalues(parameters.pvalues, parameters.pvalue_boundary);
  }
}
