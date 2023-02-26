package com.serjpinski.automata.logic.cellular;

import com.serjpinski.automata.logic.cellular.core.CellularAutomataGame;
import com.serjpinski.automata.logic.cellular.core.Neighbour;
import com.serjpinski.automata.logic.cellular.core.Utils;

import java.util.Map;
import java.util.Set;

public class Symbiosis extends CellularAutomataGame {

  private final int factions;

  private final Set<Integer> creation;
  private final Set<Integer> survival;

  private final Mode mode;

  public enum Mode {
    STANDARD, LABYRINTH, ROADS, CHEESE
  }

  public Symbiosis(int width, int height, int factions, Set<Integer> creation, Set<Integer> survival, Mode mode) {

    super(width, height);
    this.factions = Math.max(1, factions);
    this.creation = creation;
    this.survival = survival;
    this.mode = mode;
  }

  @Override
  public void initializeCells() {

    if (mode == Mode.ROADS) {
      Utils.populateRandomCells(cells, factions, Math.pow(factions - 1, 0.3) * 0.2);
    } else if (mode == Mode.CHEESE) {
      Utils.populateRandomCells(cells, factions, Math.pow(factions, 0.3) * 0.1);
    } else {
      Utils.populateRandomCells(cells, factions, Math.pow(factions - 1, 0.3) * 0.15);
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

        if (type == 0) {
          if (!draw && creation.contains(neighboursByType.getOrDefault(typeMaxNeighbours, 0))) {
            nextCells[i][j] = typeMaxNeighbours;
          }
        } else {
          if ((neighboursByType.size() <= 2 || neighboursByType.get(0) > 4)
              && !survival.contains(neighboursByType.getOrDefault(type, 0))) {
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

  public static Symbiosis standard(int width, int height, int factions) {
    return new Symbiosis(width, height, factions, Set.of(3), Set.of(2, 3), Mode.STANDARD);
  }

  public static Symbiosis labyrinth(int width, int height, int factions) {
    return new Symbiosis(width, height, factions, Set.of(1), Set.of(1, 2, 3, 4), Mode.LABYRINTH);
  }

  public static Symbiosis roads(int width, int height, int factions) {
    return new Symbiosis(width, height, factions, Set.of(3, 5), Set.of(2, 4), Mode.ROADS);
  }

  public static Symbiosis cheese(int width, int height, int factions) {
    return new Symbiosis(width, height, factions, Set.of(3), Set.of(2, 4, 5, 6, 7), Mode.CHEESE);
  }
}
