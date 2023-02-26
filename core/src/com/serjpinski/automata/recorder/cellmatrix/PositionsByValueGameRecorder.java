package com.serjpinski.automata.recorder.cellmatrix;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.serjpinski.automata.util.MatrixUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PositionsByValueGameRecorder implements CellMatrixGameRecorder {

  private static final ObjectWriter OBJECT_WRITER = new ObjectMapper()
      .writerFor(new TypeReference<List<Map<Byte, byte[]>>>() {});

  private final int width;
  private final int height;
  private final String fileName;
  private final int[][] lastCells;
  private final List<Map<Byte, byte[]>> deltas;

  public PositionsByValueGameRecorder(int width, int height, String fileName) {

    this.width = width;
    this.height = height;
    this.fileName = fileName;
    this.lastCells = new int[width][height];
    this.deltas = new ArrayList<>();

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
    deltas.add(getDelta(cells, lastCells));
    MatrixUtils.copyIntMatrix(cells, lastCells);
  }

  private Map<Byte, byte[]> getDelta(int[][] cells, int[][] lastCells) {

    Map<Byte, List<Integer>> deltaCells = new HashMap<>();

    for (int i = 0; i < width; i++) {
      for (int j = 0; j < height; j++) {
        int value = cells[i][j];
        if (value != lastCells[i][j]) {
          int position = width * i + j;
          deltaCells.computeIfAbsent((byte) value, k -> new ArrayList<>()).add(position);
        }
      }
    }

    return deltaCells.entrySet().stream().collect(Collectors.toMap(
        Map.Entry::getKey,
        k -> {
          ByteArrayOutputStream bytes = new ByteArrayOutputStream();
          k.getValue().forEach(integer -> bytes.writeBytes(ByteBuffer.allocate(4).putInt(integer).array()));
          return bytes.toByteArray();
        }));
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
      Files.writeString(Path.of(fileName), OBJECT_WRITER.writeValueAsString(deltas) + "\n", StandardOpenOption.APPEND);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
