package com.serjpinski.automata.logic.cellular;

import com.serjpinski.automata.logic.cellular.core.CellularAutomataGame;
import com.serjpinski.automata.logic.cellular.core.Neighbour;
import com.serjpinski.automata.logic.cellular.core.Utils;

import java.util.Map;
import java.util.Random;
import java.util.Set;

// TODO merge with GameOfWar?
public class TotalWar extends CellularAutomataGame {

  private static final Random RANDOM = new Random();

  private final int factions;
  private final Set<Integer> warzone;

  private boolean converged;

  public TotalWar(int width, int height, int factions, Set<Integer> warzone) {

    super(width, height);
    this.factions = Math.max(2, factions);
    this.warzone = warzone;
    this.converged = false;
  }

  @Override
  public void initializeCells() {
    Utils.populateRandomCells(cells, factions, 0.5);
  }

  @Override
  public void computeNextCells() {

    boolean[] survived = new boolean[factions + 1];

    for (int i = 0; i < width; i++) {

      for (int j = 0; j < height; j++) {

        int maxNeighbours = -1;
        int typeMaxNeighbours = 0;
        boolean draw = false;

        Map<Integer, Integer> neighboursByType = Neighbour.getByType(cells, i, j, 1);

        for (int k = 1; k <= factions; k++) {

          int neighbours = neighboursByType.getOrDefault(k, 0);

          if (maxNeighbours == neighbours) {
            draw = true;
          } else if (maxNeighbours < neighbours) {
            draw = false;
            maxNeighbours = neighbours;
            typeMaxNeighbours = k;
          }
        }

        int type = cells[i][j];
        int emptyNum = Neighbour.getForType(cells, i, j, 1, 0);

        if (type == 0) {

          if (!draw && maxNeighbours > 0 && warzone.contains(emptyNum)) {
            nextCells[i][j] = typeMaxNeighbours;
          }
        }
        else {

          if (emptyNum < 5 || emptyNum > 6) {
            nextCells[i][j] = 0;
          } else if (draw) {
            nextCells[i][j] = 0;
          } else {
            nextCells[i][j] = typeMaxNeighbours;
          }
        }

        survived[nextCells[i][j]] = true;
      }
    }

    int survivors = 0;

    for (int i = 1; i < survived.length; i++) {
      survivors += survived[i] ? 1 : 0;
    }

    if (survivors == 1) {
      converged = true;
    }
  }

  @Override
  public boolean hasConverged() {
    return converged;
  }

  public static TotalWar standard(int width, int height, int factions) {
    return new TotalWar(width, height, factions, Set.of(5, 6));
  }

  public static TotalWar gameOfLife(int width, int height, int factions) {
    return new TotalWar(width, height, factions, Set.of(5));
  }

  public static TotalWar labyrinth(int width, int height, int factions) {
    return new TotalWar(width, height, factions, Set.of(6));
  }
}
