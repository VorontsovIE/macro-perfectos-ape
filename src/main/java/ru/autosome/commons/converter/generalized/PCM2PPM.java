package ru.autosome.commons.converter.generalized;

import ru.autosome.commons.motifModel.Named;
import ru.autosome.commons.motifModel.types.PositionCountModel;
import ru.autosome.commons.motifModel.types.PositionFrequencyModel;

public abstract class PCM2PPM<ModelTypeFrom extends PositionCountModel & Named,
                              ModelTypeTo extends PositionFrequencyModel & Named>
                              implements MotifConverter<ModelTypeFrom, ModelTypeTo> {

  protected abstract ModelTypeTo createMotif(double[][] matrix, String name);

  public PCM2PPM() { }

  public ru.autosome.commons.model.Named<ModelTypeTo> convert(ru.autosome.commons.model.Named<ModelTypeFrom> namedModel) {
    return new ru.autosome.commons.model.Named<>(convert(namedModel.getObject()),
                                                 namedModel.getName());
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

  private double[] convert_position(double[] pos) {
    double count = count(pos);

    double[] converted_pos = new double[pos.length];
    for (int letter = 0; letter < pos.length; ++letter) {
      converted_pos[letter] = pos[letter] / count;
    }
    return converted_pos;
  }
}
