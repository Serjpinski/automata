package com.serjpinski.automata.util;

public class MatrixUtils {

  public static void copyIntMatrix(int[][] from, int[][] to) {

    for (int i = 0; i < to.length; i++) {
      System.arraycopy(from[i], 0, to[i], 0, to[0].length);
    }
  }
}
