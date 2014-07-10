package ru.autosome.commons.converter.generalized;

import ru.autosome.commons.backgroundModel.GeneralizedBackgroundModel;
import ru.autosome.commons.motifModel.Named;
import ru.autosome.commons.motifModel.types.PositionCountModel;
import ru.autosome.commons.motifModel.types.PositionWeightModel;

// TODO: extract interface for converter
public abstract class PCM2PWM<ModelTypeFrom extends PositionCountModel & Named,
                              ModelTypeTo extends PositionWeightModel & Named,
                              BackgroundType extends GeneralizedBackgroundModel>
                              implements MotifConverter<ModelTypeFrom, ModelTypeTo> {
  public final GeneralizedBackgroundModel background;
  public final Double const_pseudocount;

  protected abstract BackgroundType defaultBackground();
  protected abstract ModelTypeTo createMotif(double[][] matrix, String name);

  public PCM2PWM(BackgroundType background, double pseudocount) {
    this.background = background;
    this.const_pseudocount = pseudocount;
  }

  public PCM2PWM(BackgroundType background) {
    this.background = background;
    this.const_pseudocount = null;
  }

  public PCM2PWM(double pseudocount) {
    this.background = defaultBackground();
    this.const_pseudocount = pseudocount;
  }

  public PCM2PWM() {
    this.background = defaultBackground();
    this.const_pseudocount = null; // to be calculated automatically as logarithm of count
  }

  public ModelTypeTo convert(ModelTypeFrom pcm) {
    double new_matrix[][] = new double[pcm.getMatrix().length][];
    for (int pos = 0; pos < pcm.getMatrix().length; ++pos) {
      new_matrix[pos] = convert_position(pcm.getMatrix()[pos]);
    }
    return createMotif(new_matrix, pcm.getName());
  }

  // columns can have different counts for some PCMs
  private double count(double[] pos) {
    double count = 0.0;
    for(double element: pos) {
      count += element;
    }
    return count;
  }

  private double pseudocount(double count) {
    return (const_pseudocount != null) ? const_pseudocount : Math.log(count);
  }

  private double[] convert_position(double[] pos) {
    double count = count(pos);
    double pseudocount = pseudocount(count);

    double[] converted_pos = new double[pos.length];

    for (int letter = 0; letter < pos.length; ++letter) {
      double numerator = pos[letter] + background.probability(letter) * pseudocount;
      double denominator = background.probability(letter) * (count + pseudocount);
      converted_pos[letter] = Math.log(numerator / denominator);
    }
    return converted_pos;
  }
}