package com.serjpinski.automata.logic.cellular.core;

import com.serjpinski.automata.logic.CellMatrixGame;
import com.serjpinski.automata.util.MatrixUtils;

import java.awt.Color;

public abstract class CellularAutomataGame implements CellMatrixGame {

  public final int width;
  public final int height;

  private Color[][] colors;

  public int[][] cells;
  public int[][] nextCells;

  private int generation;

  public CellularAutomataGame(int width, int height) {

    this.width = width;
    this.height = height;
  }

  public abstract void initializeCells();

  public abstract void computeNextCells();

  public abstract boolean hasConverged();

  public int getGeneration() {
    return generation;
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

    if (Constants.COLORIZE_BLACK) {
      return getColorsColorizeBlack();
    }

    for (int i = 0; i < width; i++) {
      for (int j = 0; j < height; j++) {
        colors[i][j] = getCellColor(cells[i][j]);
      }
    }

    return colors;
  }

  @Override
  public void init() {
    reset();
    initializeCells();
    MatrixUtils.copyIntMatrix(cells, nextCells);
  }

  @Override
  public void step() {

    computeNextCells();
    MatrixUtils.copyIntMatrix(nextCells, cells);
    generation++;
  }

  public void reset() {

    colors = new Color[width][height];
    cells = new int[width][height];
    nextCells = new int[width][height];
    generation = 0;
  }

  public Color getCellColor(int cell) {

    if (cell <= 0 || cell > Constants.PALETTE.size()) {
      return Color.BLACK;
    } else {
      return Constants.PALETTE.get(cell - 1);
    }
  }

  private Color[][] getColorsColorizeBlack() {

    int[][] colorizedCells = new int[width][height];
    MatrixUtils.copyIntMatrix(cells, colorizedCells);

    int[][] nextColorizedCells = new int[width][height];
    MatrixUtils.copyIntMatrix(nextCells, nextColorizedCells);

    while (true) {

      int numBlack = 0;

      for (int i = 0; i < width; i++) {

        for (int j = 0; j < height; j++) {

          if (colorizedCells[i][j] == 0) {
            numBlack++;
            nextColorizedCells[i][j] = Neighbour.getMostPopularTypeAround(colorizedCells, i, j, 1, false);
          }

          colors[i][j] = getCellColor(colorizedCells[i][j]);
        }
      }

      if (numBlack == 0) {
        return colors;
      }

      MatrixUtils.copyIntMatrix(nextColorizedCells, colorizedCells);
    }
  }
}
