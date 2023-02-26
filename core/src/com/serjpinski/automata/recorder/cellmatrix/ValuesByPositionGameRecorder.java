package com.serjpinski.automata.recorder.cellmatrix;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.serjpinski.automata.util.MatrixUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ValuesByPositionGameRecorder implements CellMatrixGameRecorder {

  private static final ObjectWriter OBJECT_WRITER = new ObjectMapper()
      .writerFor(new TypeReference<Map<Integer, byte[]>>() {});

  private final int width;
  private final int height;
  private final String fileName;
  private final int[][] lastCells;
  private final Map<Integer, List<Byte>> deltas;

  public ValuesByPositionGameRecorder(int width, int height, String fileName) {

    this.width = width;
    this.height = height;
    this.fileName = fileName;
    this.lastCells = new int[width][height];
    this.deltas = new HashMap<>();

    createNewFile();
  }

  @Override
  public void newGame() {

    if (!deltas.isEmpty()) {
      writeGameToFile();
    }

    deltas.clear();

    MatrixUtils.copyIntMatrix(new int[width][height], lastCells);
  }

  @Override
  public void addIteration(int[][] cells) {
    addDelta(cells, lastCells);
    MatrixUtils.copyIntMatrix(cells, lastCells);
  }

  private void addDelta(int[][] cells, int[][] lastCells) {

    Map<Integer, Byte> deltaCells = new HashMap<>();

    for (int i = 0; i < width; i++) {
      for (int j = 0; j < height; j++) {
        int value = cells[i][j];
        if (value != lastCells[i][j]) {
          int position = width * i + j;
          deltaCells.put(position, (byte) value);
        }
      }
    }

    deltaCells.keySet().forEach(position -> deltas.computeIfAbsent(position, k -> new ArrayList<>()));
    deltas.forEach((k, v) -> {
      Byte value = deltaCells.get(k);
      v.add(value != null ? value: v.get(v.size() - 1));
    });
  }

  private void createNewFile() {

    try {
      Files.writeString(Path.of(fileName), "");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void writeGameToFile() {

    try {
      Map<Integer, byte[]> deltasAsByteArray = deltas.entrySet().stream()
          .collect(Collectors.toMap(
              Map.Entry::getKey,
              k -> {
                List<Byte> list = k.getValue();
                byte[] array = new byte[list.size()];
                for (int i = 0; i < list.size(); i++) {
                  array[i] = list.get(i);
                }
                return array;
              }));

      Files.writeString(
          Path.of(fileName),
          OBJECT_WRITER.writeValueAsString(deltasAsByteArray) + "\n",
          StandardOpenOption.APPEND);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
