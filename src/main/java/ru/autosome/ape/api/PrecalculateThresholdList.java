package ru.autosome.ape.api;

import ru.autosome.ape.model.PvalueBsearchList;
import ru.autosome.ape.model.exception.HashOverflowException;
import ru.autosome.commons.api.SingleTask;
import ru.autosome.commons.backgroundModel.mono.BackgroundModel;
import ru.autosome.commons.model.BoundaryType;
import ru.autosome.commons.model.Discretizer;
import ru.autosome.commons.motifModel.mono.PWM;

public class PrecalculateThresholdList extends SingleTask<PvalueBsearchList> {
  public static class Parameters {
    public Discretizer discretizer;
    public BackgroundModel background;
    public BoundaryType pvalue_boundary;
    public Integer max_hash_size;
    public double[] pvalues;
    public PWM pwm;

    public Parameters() {}
    public Parameters(PWM pwm, double[] pvalues, Discretizer discretizer, BackgroundModel background, BoundaryType pvalue_boundary, Integer max_hash_size) {
      this.pwm = pwm;
      this.pvalues = pvalues;
      this.discretizer = discretizer;
      this.background = background;
      this.pvalue_boundary = pvalue_boundary;
      this.max_hash_size = max_hash_size;
    }
  }
  Parameters parameters;
  public PrecalculateThresholdList(Parameters parameters) {
    super();
    this.parameters = parameters;
  }

  ru.autosome.ape.calculation.PrecalculateThresholdList<PWM,BackgroundModel> calculator() {
    return new ru.autosome.ape.calculation.PrecalculateThresholdList<PWM, BackgroundModel>(parameters.pvalues,
                                                                          parameters.discretizer,
                                                                          parameters.background,
                                                                          parameters.pvalue_boundary,
                                                                          parameters.max_hash_size);
  }


  @Override
  public PvalueBsearchList launchSingleTask() {
    try {
      return calculator().bsearch_list_for_pwm(parameters.pwm);
    } catch (HashOverflowException e) {
      e.printStackTrace();
      return null;
    }
  }

}