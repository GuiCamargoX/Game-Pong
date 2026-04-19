import java.awt.Rectangle;
import java.io.*;
import java.net.*;
import java.util.*;

class Servidor {
  static final int MAX_PLAYERS = 2;

  private static final int DEFAULT_PORT = 5050;
  private static final String PORT_ENV = "PONG_PORT";

  public static void main(String[] args) {
    ServerSocket serverSocket = null;
    int port = resolvePort();

    try {
      serverSocket = new ServerSocket(port);
    } catch (IOException e) {
      System.out.println("Could not listen on port: " + port + ", " + e);
      System.exit(1);
    }

    for (int i = 0; i < MAX_PLAYERS; i++) {
      Socket clientSocket = null;
      try {
        clientSocket = serverSocket.accept();
      } catch (IOException e) {
        System.out.println("Accept failed: " + port + ", " + e);
        System.exit(1);
      }

      System.out.println("Client connected");
      new ClientSession(clientSocket).start();
    }

    try {
      serverSocket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static int resolvePort() {
    String envPort = System.getenv(PORT_ENV);
    if (envPort == null || envPort.trim().isEmpty()) {
      return DEFAULT_PORT;
    }

    try {
      return Integer.parseInt(envPort.trim());
    } catch (NumberFormatException e) {
      System.err.println("Invalid " + PORT_ENV + " value: " + envPort + ". Using " + DEFAULT_PORT + ".");
      return DEFAULT_PORT;
    }
  }
}

class ClientSession extends Thread {
  private static final int BOARD_WIDTH = 650;
  private static final int BOARD_HEIGHT = 480;
  private static final int WALL_X = 10;
  private static final int WALL_WIDTH = BOARD_WIDTH - 20;
  private static final int WALL_THICKNESS = 6;
  private static final int TOP_WALL_Y = 0;
  private static final int BOTTOM_WALL_Y = BOARD_HEIGHT - WALL_THICKNESS;
  private static final int LEFT_PADDLE_X = 30;
  private static final int RIGHT_PADDLE_X = 590;
  private static final int PADDLE_COLLISION_WIDTH = 17;
  private static final int PADDLE_HEIGHT = 77;
  private static final int BALL_SIZE = 15;
  private static final int PADDLE_MOVE_STEP = 10;
  private static final int INITIAL_PADDLE_Y = 250;
  private static final int INITIAL_BALL_SPEED = 5;
  private static final int LOOP_RATE = 60;
  private static final long LOOP_SLEEP_MILLIS = 1000L / LOOP_RATE;
  private static final int BALL_RESET_X = BOARD_WIDTH / 2;
  private static final int BALL_RESET_Y = BOARD_HEIGHT / 2;
  private static final int OPPONENT_VIEW_BALL_RESET_X = 600 / 2;
  private static final char PLAYER_ONE_MARKER = '1';
  private static final char PLAYER_TWO_MARKER = '2';

  private final Socket clientSocket;
  static DataOutputStream[] clientOutputs = new DataOutputStream[Servidor.MAX_PLAYERS];
  static int connectedClients = 0;
  public boolean moveUp;
  public boolean moveDown;

  static int ballX = BALL_RESET_X;
  static int mirroredBallX = BALL_RESET_X;
  static int ballY = BALL_RESET_Y;
  static int playerOneY = INITIAL_PADDLE_Y;
  static int playerTwoY = INITIAL_PADDLE_Y;
  static int ballStepX = INITIAL_BALL_SPEED;
  static int ballStepY = INITIAL_BALL_SPEED;
  static int playerOneScore = 0;
  static int playerTwoScore = 0;
  static boolean showTitleScreen = true;

  Rectangle topWall = new Rectangle(WALL_X, TOP_WALL_Y, WALL_WIDTH, WALL_THICKNESS);
  Rectangle bottomWall = new Rectangle(WALL_X, BOTTOM_WALL_Y, WALL_WIDTH, WALL_THICKNESS);

  Rectangle playerOnePaddle = new Rectangle(LEFT_PADDLE_X, playerOneY, PADDLE_COLLISION_WIDTH, PADDLE_HEIGHT);
  Rectangle playerTwoPaddle = new Rectangle(RIGHT_PADDLE_X, playerTwoY, PADDLE_COLLISION_WIDTH, PADDLE_HEIGHT);
  Rectangle ballRect = new Rectangle(ballX, ballY, BALL_SIZE, BALL_SIZE);

  ClientSession(Socket clientSocket) {
    this.clientSocket = clientSocket;
  }

  public void run() {
    try {
      DataInputStream inputStream = new DataInputStream(clientSocket.getInputStream());
      clientOutputs[connectedClients++] = new DataOutputStream(clientSocket.getOutputStream());

      new StateWriter().start();

      if (connectedClients == Servidor.MAX_PLAYERS) {
        showTitleScreen = false;
        new BallLoop().start();
      }

      char playerMarker = inputStream.readChar();

      do {
        moveUp = inputStream.readBoolean();
        moveDown = inputStream.readBoolean();

        if (moveUp && !playerOnePaddle.intersects(topWall) && playerMarker == PLAYER_ONE_MARKER) {
          playerOneY -= PADDLE_MOVE_STEP;
        }

        if (moveDown && !playerOnePaddle.intersects(bottomWall) && playerMarker == PLAYER_ONE_MARKER) {
          playerOneY += PADDLE_MOVE_STEP;
        }

        if (moveUp && !playerTwoPaddle.intersects(topWall) && playerMarker == PLAYER_TWO_MARKER) {
          playerTwoY -= PADDLE_MOVE_STEP;
        }

        if (moveDown && !playerTwoPaddle.intersects(bottomWall) && playerMarker == PLAYER_TWO_MARKER) {
          playerTwoY += PADDLE_MOVE_STEP;
        }

        playerOnePaddle.setLocation(LEFT_PADDLE_X, playerOneY);
        playerTwoPaddle.setLocation(RIGHT_PADDLE_X, playerTwoY);

      } while (true);

    } catch (IOException e) {
      e.printStackTrace();
    } catch (NoSuchElementException e) {
      System.out.println("Connection closed by client");
    }
  }

  class BallLoop extends Thread {
    private boolean canBounceFromPlayerOne = true;

    public void run() {
      do {
        ballRect.setLocation(ballX, ballY);

        if (ballX <= 0 || ballX >= BOARD_WIDTH) {
          if (ballX <= 0) {
            playerTwoScore++;
          }

          if (ballX >= BOARD_WIDTH) {
            playerOneScore++;
          }

          ballX = BALL_RESET_X;
          mirroredBallX = OPPONENT_VIEW_BALL_RESET_X;
          ballY = BALL_RESET_Y;
        }

        if (playerOnePaddle.intersects(ballRect) && canBounceFromPlayerOne) {
          ballStepX = ballStepX * (-1);
          canBounceFromPlayerOne = !canBounceFromPlayerOne;
        }

        if (playerTwoPaddle.intersects(ballRect) && !canBounceFromPlayerOne) {
          ballStepX = ballStepX * (-1);
          canBounceFromPlayerOne = !canBounceFromPlayerOne;
        }

        if (ballRect.intersects(topWall) || ballRect.intersects(bottomWall)) {
          ballStepY = ballStepY * (-1);
        }

        mirroredBallX -= ballStepX * (-1);
        ballX -= ballStepX;
        ballY -= ballStepY;

        try {
          sleep(LOOP_SLEEP_MILLIS);
        } catch (InterruptedException e) {
        }

      } while (true);
    }
  }

  class StateWriter extends Thread {
    int clientNumber;

    public void run() {
      clientNumber = connectedClients - 1;

      try {
        clientOutputs[clientNumber].writeInt(clientNumber);

      } catch (IOException e) {
        System.err.println("IOException:  " + e);
      }

      do {
        try {
          clientOutputs[clientNumber].writeBoolean(showTitleScreen);
          clientOutputs[clientNumber].writeInt(playerOneScore);
          clientOutputs[clientNumber].writeInt(playerTwoScore);

          clientOutputs[clientNumber].writeInt(playerOneY);
          clientOutputs[clientNumber].writeInt(playerTwoY);

          if (clientNumber == 0) {
            clientOutputs[clientNumber].writeInt(ballX);
          }

          if (clientNumber == 1) {
            clientOutputs[clientNumber].writeInt(mirroredBallX);
          }

          clientOutputs[clientNumber].writeInt(ballY);
          clientOutputs[clientNumber].flush();

        } catch (IOException e) {
          System.err.println("IOException:  " + e);
        }

        try {
          sleep(LOOP_SLEEP_MILLIS);
        } catch (InterruptedException e) {
        }

      } while (true);

    }

  }

}
