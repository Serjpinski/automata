package com.serjpinski.automata.logic;

import java.awt.Color;

public interface CellMatrixGame {

  int getWidth();

  int getHeight();

  int[][] getCells();

  Color[][] getColors();

  void init();

  void step();
}
