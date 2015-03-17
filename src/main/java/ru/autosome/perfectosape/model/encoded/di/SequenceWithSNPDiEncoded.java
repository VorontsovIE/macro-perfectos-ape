package ru.autosome.perfectosape.model.encoded.di;

import ru.autosome.perfectosape.model.encoded.EncodedSequenceWithSNVType;

import java.util.List;

public class SequenceWithSNPDiEncoded implements EncodedSequenceWithSNVType<SequenceDiEncoded> {
  final private List<SequenceDiEncoded> sequenceVariants;
  final private int length;
  public SequenceWithSNPDiEncoded(List<SequenceDiEncoded> sequenceVariants) {
    if(sequenceVariants.size() < 2) {
      throw new IllegalArgumentException("There should be at least two sequences in SequenceWithSNVMonoEncoded");
    }
    this.length = sequenceVariants.get(0).length();
    for (int i = 1; i < sequenceVariants.size(); ++i) {
      if (sequenceVariants.get(i).length() != this.length) {
        throw new IllegalArgumentException("All sequences should be of equal length");
      }
    }
    this.sequenceVariants = sequenceVariants;
  }
  @Override
  public SequenceDiEncoded sequenceVariant(int alleleNumber) {
    return sequenceVariants.get(alleleNumber);
  }
  public int length() {
    return this.length + 1;
  }
}
