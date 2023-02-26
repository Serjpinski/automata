package com.serjpinski.automata.logic.cellular.core;

import com.serjpinski.automata.util.MathUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Neighbour {

  private final int x;
  private final int y;
  private final int type;

  private Neighbour(int x, int y, int type) {
    this.x = x;
    this.y = y;
    this.type = type;
  }

  public static void iterate(int[][] cells, int x, int y, int radius, Consumer<Neighbour> consumer) {

    for (int i = x - radius; i <= x + radius; i++) {
      for (int j = y - radius; j <= y + radius; j++) {
        if ((i != x || j != y)
            && MathUtils.isWithinBounds(i, 0, cells.length)
            && MathUtils.isWithinBounds(j, 0, cells[0].length)) {
          consumer.accept(new Neighbour(i, j, cells[i][j]));
        }
      }
    }
  }

  public static Map<Integer, Integer> getByType(int[][] cells, int x, int y, int radius) {

    Map<Integer, Integer> neighboursByType = new HashMap<>();
    neighboursByType.put(0, 0); // This is convenient for some checks

    iterate(cells, x, y, radius, neighbour ->
        neighboursByType.put(neighbour.type, neighboursByType.getOrDefault(neighbour.type, 0) + 1));

    return neighboursByType;
  }

  public static int getForType(int[][] cells, int x, int y, int radius, int type) {
    return getByType(cells, x, y, radius).getOrDefault(type, 0);
  }

  public static Map<Integer, Double> getByTypeWeighted(int[][] cells, int x, int y, int radius) {

    Map<Integer, Double> neighboursByType = new HashMap<>();

    iterate(cells, x, y, radius, neighbour ->
        neighboursByType.put(neighbour.type, neighboursByType.getOrDefault(neighbour.type, 0.0)
            + 1 / Math.sqrt(Math.pow(x - neighbour.x, 2) + Math.pow(y - neighbour.y, 2))));

    return neighboursByType;
  }

  public static double getForTypeWeighted(int[][] cells, int x, int y, int radius, int type) {
    return getByTypeWeighted(cells, x, y, radius).getOrDefault(type, 0.0);
  }

  public static List<Integer> getList(int[][] cells, int x, int y, int radius) {

    List<Integer> neighbours = new ArrayList<>();
    iterate(cells, x, y, radius, neighbour -> neighbours.add(neighbour.type));
    return neighbours;
  }

  public static int getMostPopularTypeAround(int[][] cells, int x, int y, int radius, boolean shuffle) {

    Map<Integer, Integer> neighboursByType = getByType(cells, x, y, radius);
    List<Map.Entry<Integer, Integer>> neighboursByTypeList = neighboursByType.entrySet().stream()
        .filter(k -> k.getKey() != 0)
        .collect(Collectors.toList());

    if (shuffle) {
      Collections.shuffle(neighboursByTypeList);
    }

    int max = 0;
    int type = 0;

    for (Map.Entry<Integer, Integer> k : neighboursByTypeList) {
      if (k.getValue() > max) {
        max = k.getValue();
        type = k.getKey();
      }
    }

    return type;
  }
}
