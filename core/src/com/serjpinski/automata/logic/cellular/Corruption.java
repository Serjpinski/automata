package com.serjpinski.automata.logic.cellular;

import com.serjpinski.automata.logic.cellular.core.CellularAutomataGame;
import com.serjpinski.automata.logic.cellular.core.Neighbour;

import java.util.Random;

public class Corruption extends CellularAutomataGame {

  private static final Random RANDOM = new Random();

  private final int radius;

  private boolean converged;

  private final Mode mode;

  public enum Mode {
    STANDARD, BLOSSOM
  }

  public Corruption(int width, int height, int radius, Mode mode) {

    super(width, height);
    this.radius = Math.max(1, radius);
    this.converged = false;
    this.mode = mode;
  }

  @Override
  public void initializeCells() {
    cells[width / 2][height / 2] = 5;
  }

  @Override
  public void computeNextCells() {

    converged = true;

    for (int i = 0; i < width; i++) {

      for (int j = 0; j < height; j++) {

        int type = cells[i][j];

        if (type == 0) {

          converged = false;

          int neighbours1 = Neighbour.getForType(cells, i, j, radius, 5);

          if (neighbours1 > 0) {

            int neighbours2 = Neighbour.getForType(cells, i, j, radius, 42);

            int maxNeighbours = (radius + 1) * (radius + 1) - 1;

            double prob1;
            double prob2;

            if (mode == Mode.BLOSSOM) {
              prob1 = (1 + neighbours1) / (maxNeighbours * 3.0);
              prob2 = (1 + neighbours2) / (maxNeighbours * 2.0);
            } else {
              prob1 = (1 + neighbours1) / (maxNeighbours * 5.0);
              prob2 = neighbours1 > 1 ? (1 + neighbours2) / (maxNeighbours * 2.0) : 0;
            }

            double p = RANDOM.nextDouble();

            if (p < prob1) {
              nextCells[i][j] = 5;
            } else if (p < prob1 + prob2) {
              nextCells[i][j] = 42;
            } else {
              nextCells[i][j] = 0;
            }
          }
        } else {
          nextCells[i][j] = type;
        }
      }
    }
  }

  @Override
  public boolean hasConverged() {
    return converged;
  }

  public static Corruption standard(int width, int height, int radius) {
    return new Corruption(width, height, radius, Mode.STANDARD);
  }

  public static Corruption blossom(int width, int height, int radius) {
    return new Corruption(width, height, radius, Mode.BLOSSOM);
  }
}
