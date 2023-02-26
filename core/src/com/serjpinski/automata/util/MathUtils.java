package com.serjpinski.automata.util;

public class MathUtils {

  public static int bounded(int i, int min, int max) {
    return Math.min(max - 1, Math.max(i, min));
  }

  public static boolean isWithinBounds(int i, int min, int max) {
    return i >= min && i < max;
  }
}
