package ru.autosome.commons.model;

import ru.autosome.commons.motifModel.HasLength;
import ru.autosome.commons.motifModel.ScoringModel;
import ru.autosome.perfectosape.calculation.ScanSequence;

public class PositionInterval {
  public final int left; // [left; right]
  public final int right; // both left and right included
  PositionInterval(int left, int right) {
    if (left>= right) {
      throw new IllegalArgumentException("Left should be less than right");
    }
    this.left = left;
    this.right = right;
  }


  public <SequenceType extends HasLength> ScanSequence.BestPositionWithScore findBestPosition(SequenceType sequence, ScoringModel<SequenceType> scoringModel) {
    ScanSequence.BestPositionWithScore bestPos = new ScanSequence.BestPositionWithScore();

    for (int pos = Math.max(left, 0); pos <= Math.min(right, sequence.length() - scoringModel.length()); ++pos) {
      bestPos.updateBestScore(pos, Orientation.direct, scoringModel.score(sequence, Orientation.direct, pos));
      bestPos.updateBestScore(pos, Orientation.revcomp, scoringModel.score(sequence, Orientation.revcomp, pos));
    }
    return bestPos;
  }

  @Override
  public String toString() {
    return "[" + left + ";" + right + "]";
  }

  public PositionInterval expand(int expandRegionLength) {
    return new PositionInterval(left - expandRegionLength, right + expandRegionLength);
  }

}