package ru.autosome.jMacroape;

import java.util.Comparator;
import java.util.HashMap;

public class ThresholdPvaluePair implements Comparable {
  public double threshold;
  public double pvalue;
  ThresholdPvaluePair(double threshold, double pvalue) {
    this.threshold = threshold;
    this.pvalue = pvalue;
  }
  ThresholdPvaluePair(ThresholdInfo info) {
    this.threshold = info.threshold;
    this.pvalue = info.real_pvalue;
  }
  public int compareTo(Object other) {
    double other_value;
    if (other instanceof ThresholdPvaluePair) {
      other_value = ((ThresholdPvaluePair)other).threshold;
    } else if (other instanceof Double) {
      other_value = (Double)other;
    } else {
      throw new ClassCastException("Incorrect type for comparison");
    }

    if (threshold > other_value)  {
      return 1;
    } else if (threshold < other_value) {
      return -1;
    } else {
      return 0;
    }
  }
  public int compareTo(Double other) {
    if (threshold > other) {
      return 1;
    } else if (threshold < other) {
      return -1;
    } else {
      return 0;
    }
  }

  @Override
  public String toString() {
    return threshold + "\t" + pvalue;
  }

  public static Comparator double_comparator(){
    return new Comparator<Object>(){
      Double val(Object obj){
        double value;
        if (obj instanceof ThresholdPvaluePair) {
          value = ((ThresholdPvaluePair)obj).threshold;
        } else if (obj instanceof Double) {
          value = (Double)obj;
        } else {
          throw new ClassCastException("Incorrect type for comparison");
        }
        return value;
      }
      @Override
      public int compare(Object o1, Object o2) {
        if (val(o1) < val(o2)) {
          return -1;
        } else if (val(o1) > val(o2)) {
          return 1;
        } else return 0;
      }
    };

  }
}