package com.serjpinski.automata.logic.cellular;

import com.serjpinski.automata.logic.cellular.core.CellularAutomataGame;
import com.serjpinski.automata.logic.cellular.core.Neighbour;
import com.serjpinski.automata.logic.cellular.core.Utils;

import java.util.Set;

public class GameOfLife extends CellularAutomataGame {

  private final Set<Integer> creation;
  private final Set<Integer> survival;

  public GameOfLife(int width, int height, Set<Integer> creation, Set<Integer> survival) {

    super(width, height);
    this.creation = creation;
    this.survival = survival;
  }

  @Override
  public void initializeCells() {
    Utils.populateRandomCells(cells, 1);
  }

  @Override
  public void computeNextCells() {

    for (int i = 0; i < width; i++) {

      for (int j = 0; j < height; j++) {

        int neighbours = Neighbour.getForType(cells, i, j, 1, 1);

        if (cells[i][j] == 0) {
          if (creation.contains(neighbours)) {
            nextCells[i][j] = 1;
          }
        } else {
          if (!survival.contains(neighbours)) {
            nextCells[i][j] = 0;
          }
        }
      }
    }
  }

  @Override
  public boolean hasConverged() {
    return false;
  }

  public static GameOfLife standard(int width, int height) {
    return new GameOfLife(width, height, Set.of(3), Set.of(2, 3));
  }

  public static GameOfLife labyrinth(int width, int height) {
    return new GameOfLife(width, height, Set.of(1), Set.of(1, 2, 3, 4));
  }

  public static GameOfLife labyrinth2(int width, int height) {
    return new GameOfLife(width, height, Set.of(3), Set.of(2, 3, 4, 5));
  }

  public static GameOfLife cheese(int width, int height) {
    return new GameOfLife(width, height, Set.of(3), Set.of(2, 3, 4, 5, 6, 7));
  }
}
