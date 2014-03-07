package ru.autosome.perfectosape.calculations.findThreshold;

import ru.autosome.perfectosape.BoundaryType;
import ru.autosome.perfectosape.calculations.HashOverflowException;
import ru.autosome.perfectosape.calculations.ScoringModelDistibutions;

public abstract class FindThresholdByDiscretization implements CanFindThreshold {
  Double discretization; // if discretization is null - it's not applied

  public FindThresholdByDiscretization(Double discretization) {
    this.discretization = discretization;
  }

  abstract ScoringModelDistibutions discretedModel();

  @Override
  public CanFindThreshold.ThresholdInfo weakThresholdByPvalue(double pvalue) throws HashOverflowException {
    return discretedModel().weak_threshold(pvalue).downscale(discretization);
  }

  @Override
  public CanFindThreshold.ThresholdInfo strongThresholdByPvalue(double pvalue) throws HashOverflowException {
    return discretedModel().strong_threshold(pvalue).downscale(discretization);
  }

  @Override
  public CanFindThreshold.ThresholdInfo thresholdByPvalue(double pvalue, BoundaryType boundaryType) throws HashOverflowException {
    return discretedModel().threshold(pvalue, boundaryType).downscale(discretization);
  }

  @Override
  public CanFindThreshold.ThresholdInfo[] weakThresholdsByPvalues(double[] pvalues) throws HashOverflowException {
    return downscale_all(discretedModel().weak_thresholds(pvalues), discretization);
  }

  @Override
  public CanFindThreshold.ThresholdInfo[] strongThresholsdByPvalues(double[] pvalues) throws HashOverflowException {
    return downscale_all(discretedModel().strong_thresholds(pvalues), discretization);
  }

  @Override
  public CanFindThreshold.ThresholdInfo[] thresholdsByPvalues(double[] pvalues, BoundaryType boundaryType) throws HashOverflowException {
    return downscale_all(discretedModel().thresholds(pvalues, boundaryType), discretization);
  }

  private CanFindThreshold.ThresholdInfo[] downscale_all(CanFindThreshold.ThresholdInfo[] thresholdInfos, double discretization) {
    CanFindThreshold.ThresholdInfo[] result = new CanFindThreshold.ThresholdInfo[thresholdInfos.length];
    for (int i = 0; i < thresholdInfos.length; ++i) {
      result[i] = thresholdInfos[i].downscale(discretization);
    }
    return result;
  }

}