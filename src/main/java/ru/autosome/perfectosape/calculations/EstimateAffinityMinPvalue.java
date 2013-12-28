package ru.autosome.perfectosape.calculations;

import ru.autosome.perfectosape.calculations.findPvalue.CanFindPvalue;
import ru.autosome.perfectosape.motifModels.PWM;
import ru.autosome.perfectosape.Position;
import ru.autosome.perfectosape.Sequence;

import java.util.ArrayList;

public class EstimateAffinityMinPvalue implements EstimateAffinity {
  PWM pwm;
  Sequence sequence;
  CanFindPvalue pvalueCalculator;
  ArrayList<Position> positions_to_check;
  public EstimateAffinityMinPvalue(PWM pwm, Sequence sequence, CanFindPvalue pvalueCalculator, ArrayList<Position> positions_to_check) {
    this.pwm = pwm;
    this.sequence = sequence;
    this.pvalueCalculator = pvalueCalculator;
    this.positions_to_check = positions_to_check;
  }
  EstimateAffinityMinPvalue(PWM pwm, Sequence sequence, CanFindPvalue pvalueCalculator) {
    this(pwm, sequence, pvalueCalculator, sequence.subsequence_positions(pwm.length()));
  }

  ScanSequence scanSequence() {
    return new ScanSequence(sequence, pwm, positions_to_check);
  }

  @Override
  public double affinity() throws HashOverflowException {
    double score = scanSequence().best_score();
    return pvalueCalculator.pvalueByThreshold(score).pvalue;
  }
  public Position bestPosition() {
    return scanSequence().best_position();
  }
}