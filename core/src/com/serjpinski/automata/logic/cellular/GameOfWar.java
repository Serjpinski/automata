package com.serjpinski.automata.logic.cellular;

import com.serjpinski.automata.logic.cellular.core.CellularAutomataGame;
import com.serjpinski.automata.logic.cellular.core.Neighbour;
import com.serjpinski.automata.logic.cellular.core.Utils;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class GameOfWar extends CellularAutomataGame {

  private static final Random RANDOM = new Random();

  private final int factions;

  private final Set<Integer> creation;
  private final Set<Integer> survival;

  public GameOfWar(int width, int height, int factions, Set<Integer> creation, Set<Integer> survival) {

    super(width, height);
    this.factions = Math.max(2, factions);
    this.creation = creation;
    this.survival = survival;
  }

  @Override
  public void initializeCells() {
    Utils.populateRandomCells(cells, factions);
  }

  @Override
  public void computeNextCells() {

    for (int i = 0; i < width; i++) {

      for (int j = 0; j < height; j++) {

        Map<Integer, Integer> neighboursByType = Neighbour.getByType(cells, i, j, 1);

        if (neighboursByType.size() > 2) { // More than one faction nearby
          List<Integer> neighbours = Neighbour.getList(cells, i, j, 1);
          nextCells[i][j] = neighbours.get(RANDOM.nextInt(neighbours.size()));
        } else {
          int type = cells[i][j];
          if (type == 0) {
            for (int k = 1; k <= factions; k++) {
              if (creation.contains(neighboursByType.getOrDefault(k, 0))) {
                nextCells[i][j] = k;
              }
            }
          } else {
            if (!survival.contains(neighboursByType.getOrDefault(type, 0))) {
              nextCells[i][j] = 0;
            }
          }
        }
      }
    }
  }

  @Override
  public boolean hasConverged() {
    return false;
  }

  public static GameOfWar standard(int width, int height, int factions) {
    return new GameOfWar(width, height, factions, Set.of(3), Set.of(2, 3));
  }

  public static GameOfWar labyrinth(int width, int height, int factions) {
    return new GameOfWar(width, height, factions, Set.of(1), Set.of(1, 2, 3, 4));
  }
}
