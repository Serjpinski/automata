package com.serjpinski.automata.ui;

import java.awt.Color;
import java.util.List;

public class ColorPalettes {

  public static final List<Color> DEFAULT = List.of(
      Color.YELLOW,
      Color.RED,
      Color.GREEN,
      Color.CYAN,
      Color.MAGENTA,
      Color.WHITE
  );

  public static final List<Color> BLANCA = List.of(
      new Color(184, 199, 35),
      new Color(122, 66, 255),
      new Color(15, 111, 139),
      new Color(0, 188, 212),
      new Color(64, 77, 185),
      new Color(100, 255, 218)
  );

  public static final List<Color> GREYSCALE = List.of(
      new Color(225, 225, 225),
      new Color(150, 150, 150),
      new Color(100, 100, 100),
      new Color(75, 75, 75),
      new Color(200, 200, 200),
      new Color(125, 125, 125)
  );

  public static final List<Color> RED_AND_GREY = List.of(
      new Color(150, 75, 75),
      new Color(175, 175, 175),
      new Color(250, 175, 175),
      new Color(75, 75, 75),
      new Color(200, 125, 125),
      new Color(125, 125, 125)
  );
}
