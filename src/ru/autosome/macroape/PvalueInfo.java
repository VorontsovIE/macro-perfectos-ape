package ru.autosome.macroape;

public class PvalueInfo extends ResultInfo {
  public final double threshold;
  public final double pvalue;
  public final int number_of_recognized_words;

  public PvalueInfo(double threshold, double pvalue, int number_of_recognized_words) {
    this.threshold = threshold;
    this.pvalue = pvalue;
    this.number_of_recognized_words = number_of_recognized_words;
  }
}