package com.serjpinski.automata.recorder.cellmatrix;

public interface CellMatrixGameRecorder {

  void newGame();

  void addIteration(int[][] cells);
}
