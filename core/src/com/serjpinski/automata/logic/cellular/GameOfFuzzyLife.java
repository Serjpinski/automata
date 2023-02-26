package com.serjpinski.automata.logic.cellular;

import com.serjpinski.automata.logic.cellular.core.CellularAutomataGame;
import com.serjpinski.automata.logic.cellular.core.Neighbour;
import com.serjpinski.automata.logic.cellular.core.Utils;

import java.awt.Color;
import java.util.Set;

public class GameOfFuzzyLife extends CellularAutomataGame {

  private final Set<Integer> creation;
  private final Set<Integer> survival;
  private final int maxLife;
  private final Double spawnRatio;

  public GameOfFuzzyLife(int width, int height, Set<Integer> creation, Set<Integer> survival,
                         int maxLife, Double spawnRatio) {

    super(width, height);
    this.creation = creation;
    this.survival = survival;
    this.maxLife = maxLife;
    this.spawnRatio = spawnRatio;
  }

  @Override
  public Color getCellColor(int cell) {

    if (cell == 0) {
      return super.getCellColor(0);
    } else {
      Color color = super.getCellColor(1);
      // TODO this is a dirty trick because current UI doesn't have transparency
      float alpha = cell / (float) maxLife;
      int red = Math.round(color.getRed() * alpha);
      int green = Math.round(color.getGreen() * alpha);
      int blue = Math.round(color.getBlue() * alpha);
      return new Color(red, green, blue);
    }
  }

  @Override
  public void initializeCells() {
    if (spawnRatio == null) {
      Utils.populateRandomCells(cells, maxLife);
    }
    else {
      Utils.populateRandomCells(cells, maxLife, spawnRatio);
    }
  }

  @Override
  public void computeNextCells() {

    for (int i = 0; i < width; i++) {

      for (int j = 0; j < height; j++) {

        int life = cells[i][j];
        int neighboursLife = Math.round(
            Neighbour.getList(cells, i, j, 1).stream().mapToInt(Integer::intValue).sum() / (float) maxLife);

        if (life < maxLife / 2.0) {
          if (creation.contains(neighboursLife)) {
            nextCells[i][j] = life + 1;
          } else {
            nextCells[i][j] = Math.max(0, life - 1);
          }
        } else {
          if (!survival.contains(neighboursLife)) {
            nextCells[i][j] = life - 1;
          } else {
            nextCells[i][j] = Math.min(maxLife, life + 1);
          }
        }
      }
    }
  }

  @Override
  public boolean hasConverged() {
    return false;
  }

  public static GameOfFuzzyLife standard(int width, int height) {
    return new GameOfFuzzyLife(width, height, Set.of(3), Set.of(2, 3), 3, null);
  }

  public static GameOfFuzzyLife standardSlow(int width, int height) {
    return new GameOfFuzzyLife(width, height, Set.of(3), Set.of(2, 3), 30, null);
  }

  public static GameOfFuzzyLife bacteria(int width, int height) {
    return new GameOfFuzzyLife(width, height, Set.of(2, 3), Set.of(0, 1, 4), 6, 0.05);
  }

  public static GameOfFuzzyLife cheese(int width, int height) {
    return new GameOfFuzzyLife(width, height, Set.of(3), Set.of(2, 3, 4, 5, 6, 7), 9, 0.07);
  }

  public static GameOfFuzzyLife fiber(int width, int height) {
    return new GameOfFuzzyLife(width, height, Set.of(1, 3), Set.of(2), 6, null);
  }

  public static GameOfFuzzyLife capa8(int width, int height) {
    return new GameOfFuzzyLife(width, height, Set.of(1), Set.of(2), 100, 0.01);
  }

  public static GameOfFuzzyLife cars(int width, int height) {
    return new GameOfFuzzyLife(width, height, Set.of(2), Set.of(3), 5, 0.1);
  }

  public static GameOfFuzzyLife criticalMass(int width, int height) {
    return new GameOfFuzzyLife(width, height, Set.of(2), Set.of(3), 17, 0.13);
  }
}
