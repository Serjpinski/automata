package com.serjpinski.automata.logic;

import com.serjpinski.automata.util.MathUtils;

import java.awt.Color;
import java.util.Random;

public class CloudPainter implements CellMatrixGame {

  private static final Random RANDOM = new Random();

  private final int width;
  private final int height;

  private final Color[][] colors;

  private final int[][] cells;

  int currentX;
  int currentY;

  public CloudPainter(int width, int height) {

    this.width = width;
    this.height = height;

    cells = new int[width][height];
    colors = new Color[width][height];
  }

  @Override
  public int getWidth() {
    return width;
  }

  @Override
  public int getHeight() {
    return height;
  }

  @Override
  public int[][] getCells() {
    return cells;
  }

  @Override
  public Color[][] getColors() {

    for (int i = 0; i < width; i++) {
      for (int j = 0; j < height; j++) {
        colors[i][j] = getCellColor(cells[i][j]);
      }
    }

    return colors;
  }

  @Override
  public void init() {

    for (int i = 0; i < width; i++) {
      for (int j = 0; j < height; j++) {
        cells[i][j] = 0;
      }
    }

    currentX = width / 2;
    currentY = height / 2;
  }

  @Override
  public void step() {

    for (int i = 0; i < width; i++) {
      for (int j = 0; j < height; j++) {
        cells[i][j] = cells[i][j] > 0 ? cells[i][j] + 1 : 0;
      }
    }

    for (int k = 0; k < 50; k++) {
      currentX = MathUtils.bounded(currentX + 4 - RANDOM.nextInt(9), 0, width);
      currentY = MathUtils.bounded(currentY + 4 - RANDOM.nextInt(9), 0, height);
      cells[currentX][currentY] = cells[currentX][currentY] > 0 ? cells[currentX][currentY] : 1;
    }
  }

  private static Color getCellColor(int cell) {

    return cell == 0 ? Color.black : new Color(0.5f + 0.5f * (1.0f / cell), 0.5f, 0.5f);
  }
}
