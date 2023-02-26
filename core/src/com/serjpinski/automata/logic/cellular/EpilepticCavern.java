package com.serjpinski.automata.logic.cellular;

import com.serjpinski.automata.logic.cellular.core.CellularAutomataGame;
import com.serjpinski.automata.logic.cellular.core.Neighbour;
import com.serjpinski.automata.logic.cellular.core.Utils;

import java.util.Set;

// Weird cavern pattern that appears when using a specific neighbour formula with a labyrinth-type GameOfLife
public class EpilepticCavern extends CellularAutomataGame {

  private final Set<Integer> creation;
  private final Set<Integer> survival;

  public EpilepticCavern(int width, int height) {

    super(width, height);
    this.creation = Set.of(1);
    this.survival = Set.of(1, 2, 3, 4);
  }

  @Override
  public void initializeCells() {
    Utils.populateRandomCells(cells, 1);
  }

  @Override
  public void computeNextCells() {

    for (int i = 0; i < width; i++) {

      for (int j = 0; j < height; j++) {

        int neighbours = (int) Math.round(0.5 + Neighbour.getForTypeWeighted(cells, i, j, 1, 1));

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
}
