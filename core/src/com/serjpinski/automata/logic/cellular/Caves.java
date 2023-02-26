package com.serjpinski.automata.logic.cellular;

import com.serjpinski.automata.logic.cellular.core.CellularAutomataGame;
import com.serjpinski.automata.logic.cellular.core.Neighbour;
import com.serjpinski.automata.logic.cellular.core.Utils;

import java.util.Map;
import java.util.Random;

public class Caves extends CellularAutomataGame {

  private static final Random RANDOM = new Random();

  private final int factions;
  private final Mode mode;

  public enum Mode {
    BALANCED, UNBALANCED, CRISP
  }

  public Caves(int width, int height, int factions, Mode mode) {

    super(width, height);
    this.factions = Math.max(2, factions);
    this.mode = mode;
  }

  @Override
  public void initializeCells() {
    Utils.populateRandomCells(cells, factions, 0.5);
  }

  @Override
  public void computeNextCells() {

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

        if (mode == Mode.CRISP) {
          if (type == 0) {
            if (maxNeighbours > 0) {
              nextCells[i][j] = typeMaxNeighbours;
            }
          } else if (!draw && typeMaxNeighbours != type) {
            nextCells[i][j] = typeMaxNeighbours;
          }
        } else {
          if (type == 0) {
            if (!draw && maxNeighbours > 0) {
              nextCells[i][j] = typeMaxNeighbours;
            }
          } else if ((!draw || mode == Mode.UNBALANCED) && typeMaxNeighbours != type) {
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

  public static Caves balanced(int width, int height, int factions) {
    return new Caves(width, height, factions, Mode.BALANCED);
  }

  public static Caves unbalanced(int width, int height, int factions) {
    return new Caves(width, height, factions, Mode.UNBALANCED);
  }

  public static Caves sharp(int width, int height, int factions) {
    return new Caves(width, height, factions, Mode.CRISP);
  }
}
