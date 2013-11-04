package ru.autosome.perfectosape;

import java.util.ArrayList;

import static java.lang.Math.ceil;

public class PWM extends PM {
  private double[] cache_best_suffices;
  private double[] cache_worst_suffices;

  public PWM(double[][] matrix, String name) throws IllegalArgumentException {
    super(matrix, name);
  }

  public static PWM fromParser(PMParser parser) {
    double[][] matrix = parser.matrix();
    String name = parser.name();
    return new PWM(matrix, name);
  }

  private static PWM new_from_text(ArrayList<String> input_lines) {
    return fromParser(new PMParser(input_lines));
  }

  double score(String word, BackgroundModel background) throws IllegalArgumentException {
    word = word.toUpperCase();
    if (word.length() != length()) {
      throw new IllegalArgumentException("word in PWM#score(word) should have the same length as matrix");
    }
    double sum = 0.0;
    for (int pos_index = 0; pos_index < length(); ++pos_index) {
      char letter = word.charAt(pos_index);
      Integer letter_index = indexByLetter.get(letter);
      if (letter_index != null) {
        sum += matrix[pos_index][letter_index];
      } else if (letter == 'N') {
        sum += background.mean_value(matrix[pos_index]);
      } else {
        throw new IllegalArgumentException("word in PWM#score(#{word}) should have only ACGT or N letters, but have '" + letter + "' letter");
      }
    }
    return sum;
  }

  public double score(Sequence word, BackgroundModel background) throws IllegalArgumentException {
    return score(word.sequence, background);
  }

  public double score(Sequence word) throws IllegalArgumentException {
    return score(word, new WordwiseBackground());
  }

  public double best_score() {
    return best_suffix(0);
  }

  public double worst_score() {
    return worst_suffix(0);
  }

  // best score of suffix s[i..l]
  public double best_suffix(int i) {
    return best_suffices()[i];
  }

  double worst_suffix(int i) {
    return worst_suffices()[i];
  }

  double[] worst_suffices() {
    if (cache_worst_suffices == null) {
      double[] result = new double[length() + 1];
      result[length()] = 0;
      for (int pos_index = length() - 1; pos_index >= 0; --pos_index) {
        result[pos_index] = ArrayExtensions.min(matrix[pos_index]) + result[pos_index + 1];
      }
      cache_worst_suffices = result;
    }
    return cache_worst_suffices;
  }

  double[] best_suffices() {
    if (cache_best_suffices == null) {
      double[] result = new double[length() + 1];
      result[length()] = 0;
      for (int pos_index = length() - 1; pos_index >= 0; --pos_index) {
        result[pos_index] = ArrayExtensions.max(matrix[pos_index]) + result[pos_index + 1];
      }
      cache_best_suffices = result;
    }
    return cache_best_suffices;
  }

  public PWM discrete(Double rate) {
    if (rate == null) {
      return this;
    }
    double[][] mat_result;
    mat_result = new double[length()][];
    for (int i = 0; i < length(); ++i) {
      mat_result[i] = new double[4];
      for (int j = 0; j < 4; ++j) {
        mat_result[i][j] = ceil(matrix[i][j] * rate);
      }
    }
    return new PWM(mat_result, name);
  }
}
