package ru.autosome.perfectosape.calculations.findPvalue;

import ru.autosome.perfectosape.backgroundModels.BackgroundModel;
import ru.autosome.perfectosape.formatters.OutputInformation;
import ru.autosome.perfectosape.motifModels.PWM;
import ru.autosome.perfectosape.PvalueBsearchList;

import java.io.File;
import java.io.FileNotFoundException;

// Looks for rough pValue of motif under given threshold
// using a sorted list of predefined threshold-pvalues pairs
// by performing binary search

public class FindPvalueBsearch implements CanFindPvalue {
  public static class Builder implements CanFindPvalue.Builder {
    File pathToThresholds;
    PWM pwm;

    public Builder(File pathToThresholds) {
      this.pathToThresholds = pathToThresholds;
    }

    @Override
    public CanFindPvalue.Builder applyMotif(PWM pwm) {
      this.pwm = pwm;
      return this;
    }

    @Override
    public CanFindPvalue build() {
      if (pwm != null) {
        try {
          File thresholds_file = new File(pathToThresholds, pwm.name + ".thr");
          PvalueBsearchList pvalueBsearchList = PvalueBsearchList.load_from_file(thresholds_file);
          return new FindPvalueBsearch(pwm, pvalueBsearchList);
        } catch (FileNotFoundException e) {
          return null;
        }
      } else {
        return null;
      }
    }
  }

  PWM pwm;
  PvalueBsearchList bsearchList;

  public FindPvalueBsearch(PWM pwm, PvalueBsearchList bsearchList) {
    this.pwm = pwm;
    this.bsearchList = bsearchList;
  }

  @Override
  public PvalueInfo[] pvaluesByThresholds(double[] thresholds) {
    PvalueInfo[] results = new PvalueInfo[thresholds.length];
    for (int i = 0; i < thresholds.length; ++i) {
      results[i] = pvalueByThreshold(thresholds[i]);
    }
    return results;
  }

  @Override
  public PvalueInfo pvalueByThreshold(double threshold) {
    double pvalue = bsearchList.pvalue_by_threshold(threshold);
    return new PvalueInfo(threshold, pvalue);
  }

  // TODO: decide which parameters are relevant
  @Override
  public OutputInformation report_table_layout() {
    OutputInformation infos = new OutputInformation();

    infos.add_table_parameter("T", "threshold", "threshold");
    infos.add_table_parameter("P", "P-value", "pvalue");

    return infos;
  }
}
