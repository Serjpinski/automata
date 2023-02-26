package com.serjpinski.automata.logic.cellular;

import com.serjpinski.automata.logic.cellular.core.CellularAutomataGame;
import com.serjpinski.automata.logic.cellular.core.Neighbour;

import java.util.Map;
import java.util.Random;

// TODO merge with GameOfWar?
public class Spawners extends CellularAutomataGame {

  private static final Random RANDOM = new Random();

  private final int factions;
  private final int spawnSize;

  public Spawners(int width, int height, int factions, int spawnSize) {

    super(width, height);
    this.factions = Math.max(2, factions);
    this.spawnSize = spawnSize;
  }

  @Override
  public void initializeCells() {

    for (int f = 1; f <= factions; f++) {

      int x = RANDOM.nextInt(width - spawnSize);
      int y = RANDOM.nextInt(height - spawnSize);

      for (int i = 0; i < spawnSize; i++) {
        for (int j = 0; j < spawnSize; j++) {
          cells[x + i][y + j] = RANDOM.nextBoolean() ? 0 : -f;
        }
      }
    }
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
        int emptyNum = Neighbour.getForType(cells, i, j, 1, 0);

        if (type == 0) {

          if (!draw && maxNeighbours > 0 && emptyNum == 5) {
            nextCells[i][j] = typeMaxNeighbours;
          }
        }
        else if (type < 0) {

          if (draw || type == -typeMaxNeighbours) {
            nextCells[i][j] = type;
          } else {
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
      }
    }
  }

  @Override
  public boolean hasConverged() {
    return false;
  }
}
