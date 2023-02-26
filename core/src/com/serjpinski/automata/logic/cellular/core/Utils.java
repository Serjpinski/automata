package com.serjpinski.automata.logic.cellular.core;

import java.util.Random;

public class Utils {

  private static final Random RANDOM = new Random();

  public static void populateRandomCells(int[][] cells, int factions, double ratio) {

    for (int i = 0; i < cells.length; i++) {
      for (int j = 0; j < cells[0].length; j++) {
        cells[i][j] = RANDOM.nextFloat() < ratio ? (1 + RANDOM.nextInt(factions)) : 0;
      }
    }
  }

  public static void populateRandomCells(int[][] cells, int factions) {
    populateRandomCells(cells, factions, factions * Constants.DEFAULT_POPULATE_RATIO);
  }

  public static void populateSquare(int[][] cells, int x, int y, int size, int value, double ratio) {

    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        int xi = x + i;
        int yj = y + j;
        if (xi < cells.length && yj < cells[0].length && cells[xi][yj] == 0) {
          cells[xi][yj] = RANDOM.nextFloat() <= ratio ? value : 0;
        }
      }
    }
  }
}
