package ru.autosome.perfectosape;

import gnu.trove.iterator.TDoubleDoubleIterator;
import gnu.trove.iterator.TDoubleObjectIterator;
import gnu.trove.map.TDoubleDoubleMap;
import gnu.trove.map.TDoubleObjectMap;
import gnu.trove.map.hash.TDoubleDoubleHashMap;
import gnu.trove.map.hash.TDoubleObjectHashMap;
import ru.autosome.perfectosape.calculations.findThreshold.CanFindThreshold;

import java.util.ArrayList;
import java.util.List;

// Top part of score distribution
public class ScoreDistributionTop {
  public static class NotRepresentativeDistribution extends Exception {
    public NotRepresentativeDistribution() { super(); }
    public NotRepresentativeDistribution(String msg) { super(msg); }
  }

  private double left_score_boundary; // score distribution left boundary. `-INF` if distribution is full.
                                      // or `threshold` if distribution if above threshold
  private TDoubleDoubleMap score_count_hash; // score --> count mapping
  private double total_count; // sum of all counts under score distribution (not only under top part)

  private Double best_score;  // best score and worst score are used to estimate score when it is
  private Double worst_score;


  public double getWorstScore() {
    if (worst_score != null) {
      return worst_score;
    } else {
      return Double.NEGATIVE_INFINITY;
    }
  }
  public void setWorstScore(double value) { worst_score = value; }

  public double getBestScore() {
    if (best_score == null) { // cache
      double max_score = Double.NEGATIVE_INFINITY;
      TDoubleDoubleIterator iterator = score_count_hash.iterator();
      while (iterator.hasNext()) {
        iterator.advance();
        double score = iterator.key();
        max_score = Math.max(score, max_score);
      }
      best_score = max_score;
    }
    return best_score;
  }
  public void setBestScore(double value) { best_score = value; }

  public ScoreDistributionTop(TDoubleDoubleMap score_count_hash, double total_count, double left_score_boundary) {
    this.score_count_hash = score_count_hash;
    this.total_count = total_count;
    this.left_score_boundary = left_score_boundary;
  }

  // returns map threshold --> count
  public TDoubleDoubleMap counts_above_thresholds(double[] thresholds) throws NotRepresentativeDistribution {
    TDoubleDoubleMap result = new TDoubleDoubleHashMap();
    for (double threshold : thresholds) {
      result.put(threshold, count_above_threshold(threshold));
    }
    return result;
  }

  public double count_above_threshold(double threshold) throws NotRepresentativeDistribution {
    if (threshold < left_score_boundary) {
      throw new NotRepresentativeDistribution("Score distribution left boundary " + left_score_boundary + " is greater than requested threshold " + threshold);
    }

    double accum = 0.0;
    TDoubleDoubleIterator iterator = score_count_hash.iterator();
    while(iterator.hasNext()) {
      iterator.advance();
      double score = iterator.key();
      double count = iterator.value();
      if (score >= threshold) {
        accum += count;
      }
    }
    return accum;
  }

  ThresholdsRange thresholdsRangeByCount(double[] scores, List<Double> partial_sums, double look_for_count) {
    int[] range_indices = ArrayExtensions.indices_of_range(partial_sums, look_for_count);
    if (range_indices[0] == -1) {
      return new ThresholdsRange(scores[0], getBestScore() + 1,
                                 partial_sums.get(0), 0);
    } else if (range_indices[0] == partial_sums.size()) {
      return new ThresholdsRange(getWorstScore() - 1, scores[scores.length - 1],
                                 total_count, partial_sums.get(scores.length - 1));
    } else {
      return new ThresholdsRange(scores[range_indices[1]], scores[range_indices[0]],
                                 partial_sums.get(range_indices[1]), partial_sums.get(range_indices[0]));
    }
  }

  // count under given part of distribution
  public double top_part_count() {
    double accum = 0.0;
    TDoubleDoubleIterator iterator = score_count_hash.iterator();
    while(iterator.hasNext()) {
      iterator.advance();
      double count = iterator.value();
      accum += count;
    }
    return accum;
  }

  // pvalue of given part of distribution
  public double top_part_pvalue() {
    return top_part_count() / total_count;
  }

  public TDoubleObjectMap<ThresholdsRange> thresholds_by_pvalues(double[] pvalues) throws NotRepresentativeDistribution {
    if (top_part_pvalue() < ArrayExtensions.max(pvalues)) {
      throw new NotRepresentativeDistribution("Score distribution covers values up to pvalue " + top_part_pvalue() +
                                               " but pvalue " + ArrayExtensions.max(pvalues)  + " was requested");
    }

    double[] scores = ArrayExtensions.descending_sorted_hash_keys(score_count_hash);

    double counts[] = new double[scores.length];
    for (int i = 0; i < scores.length; ++i) {
      counts[i] = score_count_hash.get(scores[i]);
    }
    List<Double> partial_sums = ArrayExtensions.partial_sums(counts, 0.0);

    TDoubleObjectMap<ThresholdsRange> results = new TDoubleObjectHashMap<ThresholdsRange>();
    for (double pvalue : pvalues) {
      double look_for_count = pvalue * total_count;
      results.put(pvalue, thresholdsRangeByCount(scores, partial_sums, look_for_count));
    }
    return results;
  }


  // "strong" means that threshold has real pvalue not more than requested one
  public CanFindThreshold.ThresholdInfo[] strong_thresholds(double[] pvalues) throws NotRepresentativeDistribution {
    return thresholds(pvalues, BoundaryType.LOWER);
  }

  public CanFindThreshold.ThresholdInfo strong_threshold(double pvalue) throws NotRepresentativeDistribution {
    return strong_thresholds(new double[]{pvalue})[0];
  }

  // "strong" means that threshold has real pvalue not less than requested one
  public CanFindThreshold.ThresholdInfo[] weak_thresholds(double[] pvalues) throws NotRepresentativeDistribution {
    return thresholds(pvalues, BoundaryType.UPPER);
  }

  public CanFindThreshold.ThresholdInfo weak_threshold(double pvalue) throws NotRepresentativeDistribution {
    return weak_thresholds(new double[]{pvalue})[0];
  }

  public CanFindThreshold.ThresholdInfo[] thresholds(double[] pvalues, BoundaryType pvalueBoundary) throws NotRepresentativeDistribution {
    ArrayList<CanFindThreshold.ThresholdInfo> results = new ArrayList<CanFindThreshold.ThresholdInfo>();
    TDoubleObjectMap<ThresholdsRange> thresholds_by_pvalues = thresholds_by_pvalues(pvalues);
    TDoubleObjectIterator<ThresholdsRange> iterator = thresholds_by_pvalues.iterator();
    while (iterator.hasNext()) {
      iterator.advance();
      double pvalue = iterator.key();
      ThresholdsRange range = iterator.value();
      double threshold, real_pvalue;
      if (pvalueBoundary == BoundaryType.LOWER) { // strong threshold
        threshold = range.first_threshold + 0.1 * (range.second_threshold - range.first_threshold);
        real_pvalue = range.second_count / total_count;
      } else { // weak threshold
        threshold = range.first_threshold;
        real_pvalue = range.first_count / total_count;
      }
      results.add(new CanFindThreshold.ThresholdInfo(threshold, real_pvalue, pvalue));
    }
    return results.toArray(new CanFindThreshold.ThresholdInfo[results.size()]);
  }

  public CanFindThreshold.ThresholdInfo threshold(double pvalue, BoundaryType pvalueBoundary) throws NotRepresentativeDistribution {
    return thresholds(new double[]{pvalue}, pvalueBoundary)[0];
  }


  // Container for a range of thresholds and appropriate counts.
  // Following inequations are assumed
  // first threshold < second threshold
  // first count > second count
  public static class ThresholdsRange {
    double first_threshold, second_threshold;
    double first_count, second_count;
    ThresholdsRange(double first_threshold, double second_threshold, double first_count, double second_count) {
      this.first_threshold = first_threshold;
      this.second_threshold = second_threshold;
      this.first_count = first_count;
      this.second_count = second_count;
    }
  }
}