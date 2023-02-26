package com.serjpinski.automata;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.TimeUtils;
import com.serjpinski.automata.logic.CellMatrixGame;
import com.serjpinski.automata.logic.CloudPainter;
import com.serjpinski.automata.logic.cellular.Caves;
import com.serjpinski.automata.logic.cellular.Cities;
import com.serjpinski.automata.logic.cellular.Corruption;
import com.serjpinski.automata.logic.cellular.EpilepticCavern;
import com.serjpinski.automata.logic.cellular.GameOfFuzzyLife;
import com.serjpinski.automata.logic.cellular.GameOfLife;
import com.serjpinski.automata.logic.cellular.GameOfWar;
import com.serjpinski.automata.logic.cellular.GameOfWar2;
import com.serjpinski.automata.logic.cellular.Spawners;
import com.serjpinski.automata.logic.cellular.Symbiosis;
import com.serjpinski.automata.logic.cellular.TotalWar;

import java.util.List;

public class Automata extends ApplicationAdapter {

  public static final int WIDTH = 1280;
  public static final int HEIGHT = 720;
  public static final int PIXEL_SIZE = 4;

  public static final int MAX_GAME_EXECUTIONS = 1;
  public static final int MAX_ITERATIONS = Integer.MAX_VALUE;

  private static final List<CellMatrixGame> GAME_LIST = List.of(
      new CloudPainter(WIDTH / PIXEL_SIZE, HEIGHT / PIXEL_SIZE),
      GameOfLife.standard(WIDTH / PIXEL_SIZE, HEIGHT / PIXEL_SIZE),
      GameOfLife.labyrinth(WIDTH / PIXEL_SIZE, HEIGHT / PIXEL_SIZE),
      Caves.balanced(WIDTH / PIXEL_SIZE, HEIGHT / PIXEL_SIZE, 2),
      Caves.unbalanced(WIDTH / PIXEL_SIZE, HEIGHT / PIXEL_SIZE, 2),
      Caves.sharp(WIDTH / PIXEL_SIZE, HEIGHT / PIXEL_SIZE, 2),
      GameOfWar.standard(WIDTH / PIXEL_SIZE, HEIGHT / PIXEL_SIZE, 2),
      GameOfWar2.standard(WIDTH / PIXEL_SIZE, HEIGHT / PIXEL_SIZE, 2),
      Corruption.standard(WIDTH / PIXEL_SIZE, HEIGHT / PIXEL_SIZE, 1),
      Corruption.blossom(WIDTH / PIXEL_SIZE, HEIGHT / PIXEL_SIZE, 1),
      new EpilepticCavern(WIDTH / PIXEL_SIZE, HEIGHT / PIXEL_SIZE),
      new Spawners(WIDTH / PIXEL_SIZE, HEIGHT / PIXEL_SIZE, 2, 5),
      TotalWar.standard(WIDTH / PIXEL_SIZE, HEIGHT / PIXEL_SIZE, 2),
      TotalWar.labyrinth(WIDTH / PIXEL_SIZE, HEIGHT / PIXEL_SIZE, 2),
      Symbiosis.standard(WIDTH / PIXEL_SIZE, HEIGHT / PIXEL_SIZE, 2),
      Symbiosis.labyrinth(WIDTH / PIXEL_SIZE, HEIGHT / PIXEL_SIZE, 6),
      Symbiosis.roads(WIDTH / PIXEL_SIZE, HEIGHT / PIXEL_SIZE, 2),
      Symbiosis.cheese(WIDTH / PIXEL_SIZE, HEIGHT / PIXEL_SIZE, 1),
      Cities.standard(WIDTH / PIXEL_SIZE, HEIGHT / PIXEL_SIZE, 2),
      GameOfFuzzyLife.standard(WIDTH / PIXEL_SIZE, HEIGHT / PIXEL_SIZE),
      GameOfFuzzyLife.standardSlow(WIDTH / PIXEL_SIZE, HEIGHT / PIXEL_SIZE),
      GameOfFuzzyLife.bacteria(WIDTH / PIXEL_SIZE, HEIGHT / PIXEL_SIZE),
      GameOfFuzzyLife.criticalMass(WIDTH / PIXEL_SIZE, HEIGHT / PIXEL_SIZE)
  );

  private final CellMatrixGame game = Symbiosis.roads(WIDTH / PIXEL_SIZE, HEIGHT / PIXEL_SIZE, 2);

  //private final CellMatrixGameRecorder gameRecorder = new PositionsByValueGameRecorder(
  //    game.getWidth(), game.getHeight(), String.join(
  //        "-", "games", game.getWidth() + "", game.getHeight() + "", Instant.now().toEpochMilli() + "") + ".jsonl");

  private Pixmap gridPixmap;
  private Texture gridTexture;
  private SpriteBatch batch;
  private OrthographicCamera camera;

  private long gameExecution;
  private long iteration;
  private long lastIterationMs;

  @Override
  public void create() {

    gameExecution = -1;
    newGameExecution();
  }

  private void newGameExecution() {

    gameExecution++;

    gridPixmap = new Pixmap(game.getWidth() * PIXEL_SIZE, game.getHeight() * PIXEL_SIZE, Pixmap.Format.RGBA8888);
    gridTexture = new Texture(gridPixmap);

    // create the camera and the SpriteBatch
    camera = new OrthographicCamera();
    camera.setToOrtho(false, game.getWidth() * PIXEL_SIZE, game.getHeight() * PIXEL_SIZE);
    batch = new SpriteBatch();

    game.init();

    iteration = 0;

    //gameRecorder.newGame();
    //gameRecorder.addIteration(game.getCells());
  }

  @Override
  public void render() {

    // check if we need to iterate
    // TODO if I set it to a big number like 100000000, increases GPU usage a lot, why?
    if (TimeUtils.nanoTime() - lastIterationMs > 0) {
      iterate();
    }

    // clear the screen
    //ScreenUtils.clear(0, 0, 0, 1);

    // tell the camera to update its matrices.
    camera.update();

    // tell the SpriteBatch to render in the
    // coordinate system specified by the camera.
    batch.setProjectionMatrix(camera.combined);

    // begin a new batch and draw the bucket and
    // all drops
    batch.begin();
    batch.draw(gridTexture, 0, 0);
    batch.end();
  }

  @Override
  public void dispose() {
    // dispose of all the native resources
    gridTexture.dispose();
    batch.dispose();
  }

  private void iterate() {

    if (iteration == MAX_ITERATIONS) {
      newGameExecution();
    }

    if (gameExecution == MAX_GAME_EXECUTIONS) {
      System.exit(0);
    }

    game.step();
    //gameRecorder.addIteration(game.getCells());
    java.awt.Color[][] pixels = game.getColors();

    for (int i = 0; i < game.getWidth(); i++) {
      for (int j = 0; j < game.getHeight(); j++) {
        java.awt.Color color = pixels[i][j];
        gridPixmap.setColor(Color.rgba8888(
            color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, 1));
        gridPixmap.fillRectangle(i * PIXEL_SIZE, j * PIXEL_SIZE, PIXEL_SIZE, PIXEL_SIZE);
      }
    }

    gridTexture.dispose();
    gridTexture = new Texture(gridPixmap);

    iteration++;
    lastIterationMs = TimeUtils.nanoTime();
  }
}
