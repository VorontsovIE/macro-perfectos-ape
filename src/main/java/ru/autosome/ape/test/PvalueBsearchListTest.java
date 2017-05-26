package ru.autosome.ape.test;

import junit.framework.TestCase;
import org.junit.Test;
import ru.autosome.ape.model.PvalueBsearchList;
import ru.autosome.commons.model.BoundaryType;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.sqrt;

public class PvalueBsearchListTest extends TestCase {
  @Test
  public void testPvalueByThreshold(){
    List<PvalueBsearchList.ThresholdPvaluePair> pairs = new ArrayList<>();
    for (double threshold = 0.0; threshold < 10.0; threshold += 1) {
      pairs.add(new PvalueBsearchList.ThresholdPvaluePair(threshold, (10-threshold)/10));
    }
    PvalueBsearchList bsearchList = new PvalueBsearchList(pairs);
    assertEquals(new PvalueBsearchList.ThresholdPvaluePair(5.0, 0.5), bsearchList.thresholdInfoByPvalue(0.55, BoundaryType.STRONG));
    assertEquals(new PvalueBsearchList.ThresholdPvaluePair(4.0, 0.6), bsearchList.thresholdInfoByPvalue(0.55, BoundaryType.WEAK));
    assertEquals(new PvalueBsearchList.ThresholdPvaluePair(4.0, 0.6), bsearchList.thresholdInfoByPvalue(0.6, BoundaryType.STRONG));
    assertEquals(new PvalueBsearchList.ThresholdPvaluePair(4.0, 0.6), bsearchList.thresholdInfoByPvalue(0.6, BoundaryType.WEAK));
    assertEquals(0.4, bsearchList.pvalue_by_threshold(6));
    assertEquals(sqrt(0.4 * 0.5), bsearchList.pvalue_by_threshold(5.3));
    assertEquals(sqrt(0.4 * 0.5), bsearchList.pvalue_by_threshold(5.5));
    assertEquals(sqrt(0.4 * 0.5), bsearchList.pvalue_by_threshold(5.8));
  }
}
