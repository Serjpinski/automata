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
import java.util.List;

public class DeltaBlocksGameRecorder implements CellMatrixGameRecorder {

  private static final ObjectWriter OBJECT_WRITER = new ObjectMapper()
      .writerFor(new TypeReference<List<byte[]>>() {});

  private final int width;
  private final int height;
  private final String fileName;
  private final int[][] lastCells;
  private final List<byte[]> deltas;

  public DeltaBlocksGameRecorder(int width, int height, String fileName) {

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

  private byte[] getDelta(int[][] cells, int[][] lastCells) {

    ByteArrayOutputStream delta = new ByteArrayOutputStream();

    byte blockDiff = (byte) (cells[0][0] - lastCells[0][0]);
    int blockSize = 0;

    for (int i = 0; i < width; i++) {
      for (int j = 0; j < height; j++) {
        byte diff = (byte) (cells[i][j] - lastCells[i][j]);
        if (blockDiff == diff) {
          blockSize++;
        } else {
          delta.writeBytes(ByteBuffer.allocate(5).putInt(blockSize).put(blockDiff).array());
          blockDiff = diff;
          blockSize = 1;
        }
      }
    }

    delta.writeBytes(ByteBuffer.allocate(5).putInt(blockSize).put(blockDiff).array());

    return delta.toByteArray();
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
