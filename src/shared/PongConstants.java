package shared;

public final class PongConstants {
  private PongConstants() {
  }

  public static final String HOST = "localhost";
  public static final int DEFAULT_PORT = 5050;
  public static final String PORT_ENV = "PONG_PORT";

  public static final int MAX_PLAYERS = 2;
  public static final int NETWORK_TICK_RATE = 60;
  public static final long NETWORK_TICK_SLEEP_MILLIS = 1000L / NETWORK_TICK_RATE;
  public static final char PLAYER_ONE_MARKER = '1';
  public static final char PLAYER_TWO_MARKER = '2';

  public static final int BOARD_WIDTH = 650;
  public static final int BOARD_HEIGHT = 480;
  public static final int WALL_X = 10;
  public static final int WALL_WIDTH = BOARD_WIDTH - 20;
  public static final int WALL_THICKNESS = 6;
  public static final int TOP_WALL_Y = 0;
  public static final int BOTTOM_WALL_Y = BOARD_HEIGHT - WALL_THICKNESS;

  public static final int LEFT_PADDLE_X = 30;
  public static final int RIGHT_PADDLE_X = 590;
  public static final int PADDLE_WIDTH = 16;
  public static final int PADDLE_COLLISION_WIDTH = 17;
  public static final int PADDLE_HEIGHT = 77;
  public static final int PADDLE_MOVE_STEP = 10;
  public static final int INITIAL_PADDLE_Y = 250;

  public static final int BALL_SIZE = 15;
  public static final int INITIAL_BALL_SPEED = 5;
  public static final int BALL_RESET_X = BOARD_WIDTH / 2;
  public static final int BALL_RESET_Y = BOARD_HEIGHT / 2;

  public static final int CENTER_LINE_X = BOARD_WIDTH / 2;
  public static final int CENTER_LINE_STEP = 50;
  public static final int CENTER_LINE_LENGTH = 25;

  public static final int OPPONENT_VIEW_BALL_RESET_X = 600 / 2;
}
