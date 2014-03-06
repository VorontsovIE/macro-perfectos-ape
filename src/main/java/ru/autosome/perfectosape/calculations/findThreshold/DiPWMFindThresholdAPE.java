package ru.autosome.perfectosape.calculations.findThreshold;

import ru.autosome.perfectosape.BoundaryType;
import ru.autosome.perfectosape.backgroundModels.DiBackgroundModel;
import ru.autosome.perfectosape.calculations.CountingDiPWM;
import ru.autosome.perfectosape.calculations.HashOverflowException;
import ru.autosome.perfectosape.motifModels.DiPWM;

public class DiPWMFindThresholdAPE implements CanFindThreshold {
  public static class Builder implements CanFindThreshold.DiPWMBuilder {
    Double discretization;
    DiBackgroundModel dibackground;
    Integer maxHashSize;
    DiPWM dipwm;

    public Builder(DiBackgroundModel dibackground, Double discretization, Integer maxHashSize) {
      this.dibackground = dibackground;
      this.discretization = discretization;
      this.maxHashSize = maxHashSize;
    }

    @Override
    public CanFindThreshold.Builder applyMotif(DiPWM dipwm) {
      this.dipwm = dipwm;
      return this;
    }

    @Override
    public CanFindThreshold build() {
      if (dipwm != null) {
        return new DiPWMFindThresholdAPE(dipwm, dibackground, discretization, maxHashSize);
      } else {
        return null;
      }
    }
  }


  DiBackgroundModel dibackground;
  Double discretization; // if discretization is null - it's not applied
  Integer maxHashSize; // if maxHashSize is null - it's not applied
  DiPWM dipwm;

  public DiPWMFindThresholdAPE(DiPWM dipwm, DiBackgroundModel dibackground,
                          Double discretization, Integer max_hash_size) {
    this.dipwm = dipwm;
    this.dibackground = dibackground;
    this.discretization = discretization;
    this.maxHashSize = max_hash_size;
  }

  CountingDiPWM countingPWM(DiPWM dipwm) {
    return new CountingDiPWM(dipwm, dibackground, maxHashSize);
  }

  @Override
  public CanFindThreshold.ThresholdInfo weakThresholdByPvalue(double pvalue) throws HashOverflowException {
    return countingPWM(dipwm.discrete(discretization)).weak_threshold(pvalue).downscale(discretization);
  }

  @Override
  public CanFindThreshold.ThresholdInfo strongThresholdByPvalue(double pvalue) throws HashOverflowException {
    return countingPWM(dipwm.discrete(discretization)).strong_threshold(pvalue).downscale(discretization);
  }

  @Override
  public CanFindThreshold.ThresholdInfo thresholdByPvalue(double pvalue, BoundaryType boundaryType) throws HashOverflowException {
    return countingPWM(dipwm.discrete(discretization)).threshold(pvalue, boundaryType).downscale(discretization);
  }

  @Override
  public CanFindThreshold.ThresholdInfo[] weakThresholdsByPvalues(double[] pvalues) throws HashOverflowException {
    CanFindThreshold.ThresholdInfo[] result = new CanFindThreshold.ThresholdInfo[pvalues.length];
    for (int i = 0; i < pvalues.length; ++i) {
      result[i] = weakThresholdByPvalue(pvalues[i]);
    }
    return result;
  }

  @Override
  public CanFindThreshold.ThresholdInfo[] strongThresholsdByPvalues(double[] pvalues) throws HashOverflowException {
    CanFindThreshold.ThresholdInfo[] result = new CanFindThreshold.ThresholdInfo[pvalues.length];
    for (int i = 0; i < pvalues.length; ++i) {
      result[i] = strongThresholdByPvalue(pvalues[i]);
    }
    return result;
  }

  @Override
  public CanFindThreshold.ThresholdInfo[] thresholdsByPvalues(double[] pvalues, BoundaryType boundaryType) throws HashOverflowException {
    CanFindThreshold.ThresholdInfo[] result = new CanFindThreshold.ThresholdInfo[pvalues.length];
    for (int i = 0; i < pvalues.length; ++i) {
      result[i] = thresholdByPvalue(pvalues[i], boundaryType);
    }
    return result;
  }
}