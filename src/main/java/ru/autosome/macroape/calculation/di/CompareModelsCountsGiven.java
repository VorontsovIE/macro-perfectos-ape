package ru.autosome.macroape.calculation.di;

import ru.autosome.commons.backgroundModel.di.DiBackgroundModel;
import ru.autosome.commons.model.Discretizer;
import ru.autosome.commons.motifModel.di.DiPWM;
import ru.autosome.macroape.model.PairAligned;

public class CompareModelsCountsGiven extends ru.autosome.macroape.calculation.generalized.CompareModelsCountsGiven<DiPWM, DiBackgroundModel> {

  public CompareModelsCountsGiven(DiPWM firstPWM, DiPWM secondPWM,
                                  DiBackgroundModel firstBackground,
                                  DiBackgroundModel secondBackground,
                                  Discretizer discretizer) {
    super(firstPWM, secondPWM, firstBackground, secondBackground, discretizer);
  }

  @Override
  protected AlignedModelIntersection calculator(PairAligned<DiPWM> alignment) {
    return new AlignedModelIntersection(alignment, firstBackground, secondBackground);
  }
}
