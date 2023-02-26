package com.serjpinski.automata.logic.cellular;

import com.serjpinski.automata.logic.cellular.core.CellularAutomataGame;
import com.serjpinski.automata.logic.cellular.core.Neighbour;
import com.serjpinski.automata.logic.cellular.core.Utils;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class Cities extends CellularAutomataGame {

  private static final Random RANDOM = new Random();

  private static final int NUMBER_OF_CITIES = 100;
  private static final int CITY_SIZE = 2;
  private static final int SPAWN_RADIUS = 3;
  private static final int SPAWN_SIZE = CITY_SIZE + 2 * SPAWN_RADIUS;

  private final int factions;
  private final int neutralFaction;

  private final Set<Integer> creation;
  private final Set<Integer> survival;

  public Cities(int width, int height, int factions, Set<Integer> creation, Set<Integer> survival) {

    super(width, height);
    this.factions = Math.max(2, factions);
    this.neutralFaction = this.factions + 1; // An additional "neutral" faction
    this.creation = creation;
    this.survival = survival;
  }

  @Override
  public Color getCellColor(int cell) {

    if (cell == neutralFaction) {
      return Color.WHITE;
    } else {
      return super.getCellColor(cell);
    }
  }

  @Override
  public void initializeCells() {

    // Assign one city to each faction

    Map<Integer, Integer> assignedCities = new HashMap<>();
    List<Integer> cities = new ArrayList<>();
    for (int i = 0; i < NUMBER_OF_CITIES; i++) {
      cities.add(i);
    }

    for (int i = 1; i <= factions; i++) {
      assignedCities.put(cities.remove(RANDOM.nextInt(cities.size())), i);
    }

    // Spawn cities

    for (int c = 0; c < NUMBER_OF_CITIES; c++) {

      int x = RANDOM.nextInt(width - SPAWN_SIZE);
      int y = RANDOM.nextInt(height - SPAWN_SIZE);

      if (assignedCities.containsKey(c)) {
        // Spawn faction controlling the city
        Utils.populateSquare(cells, x, y, SPAWN_SIZE, assignedCities.get(c), 0.5);
      }

      // Spawn city
      for (int i = SPAWN_RADIUS; i < SPAWN_RADIUS + CITY_SIZE; i++) {
        for (int j = SPAWN_RADIUS; j < SPAWN_RADIUS + CITY_SIZE; j++) {
          cells[x + i][y + j] = neutralFaction;
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
        Map<Integer, Integer> neighboursByType2 = Neighbour.getByType(cells, i, j, 2);

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

        if (type != neutralFaction) {

          if (type == 0) {
            if (!draw && (creation.contains(neighboursByType.getOrDefault(typeMaxNeighbours, 0))
                || neighboursByType2.getOrDefault(neutralFaction, 0) > 0)) {
              nextCells[i][j] = typeMaxNeighbours;
            }
          } else {
            if (!survival.contains(neighboursByType.getOrDefault(type, 0))
                || (type != maxNeighbours && !survival.contains(8 - neighboursByType.getOrDefault(0, 0)))) {
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

  public static Cities standard(int width, int height, int factions) {
    return new Cities(width, height, factions, Set.of(3), Set.of(2, 3));
  }
}
