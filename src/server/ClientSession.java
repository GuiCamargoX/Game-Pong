package server;

import static shared.PongConstants.*;

import java.awt.Rectangle;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.NoSuchElementException;

class ClientSession extends Thread {
  private final Socket clientSocket;

  private static DataOutputStream[] clientOutputs = new DataOutputStream[MAX_PLAYERS];
  private static int connectedClients = 0;

  private static int ballX = BALL_RESET_X;
  private static int mirroredBallX = BALL_RESET_X;
  private static int ballY = BALL_RESET_Y;
  private static int playerOneY = INITIAL_PADDLE_Y;
  private static int playerTwoY = INITIAL_PADDLE_Y;
  private static int ballStepX = INITIAL_BALL_SPEED;
  private static int ballStepY = INITIAL_BALL_SPEED;
  private static int playerOneScore = 0;
  private static int playerTwoScore = 0;
  private static boolean showTitleScreen = true;

  private Rectangle topWall = new Rectangle(WALL_X, TOP_WALL_Y, WALL_WIDTH, WALL_THICKNESS);
  private Rectangle bottomWall = new Rectangle(WALL_X, BOTTOM_WALL_Y, WALL_WIDTH, WALL_THICKNESS);

  private Rectangle playerOnePaddle = new Rectangle(LEFT_PADDLE_X, playerOneY, PADDLE_COLLISION_WIDTH, PADDLE_HEIGHT);
  private Rectangle playerTwoPaddle = new Rectangle(RIGHT_PADDLE_X, playerTwoY, PADDLE_COLLISION_WIDTH, PADDLE_HEIGHT);
  private Rectangle ballRect = new Rectangle(ballX, ballY, BALL_SIZE, BALL_SIZE);

  ClientSession(Socket clientSocket) {
    this.clientSocket = clientSocket;
  }

  @Override
  public void run() {
    try {
      DataInputStream inputStream = new DataInputStream(clientSocket.getInputStream());
      int clientNumber = registerClientOutput(new DataOutputStream(clientSocket.getOutputStream()));

      new StateWriter(clientNumber).start();

      if (connectedClients == MAX_PLAYERS) {
        showTitleScreen = false;
        new BallLoop().start();
      }

      char playerMarker = inputStream.readChar();

      do {
        boolean moveUp = inputStream.readBoolean();
        boolean moveDown = inputStream.readBoolean();

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

  private static synchronized int registerClientOutput(DataOutputStream outputStream) {
    int clientNumber = connectedClients;
    clientOutputs[clientNumber] = outputStream;
    connectedClients++;
    return clientNumber;
  }

  class BallLoop extends Thread {
    private boolean canBounceFromPlayerOne = true;

    @Override
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
          sleep(NETWORK_TICK_SLEEP_MILLIS);
        } catch (InterruptedException e) {
        }

      } while (true);
    }
  }

  class StateWriter extends Thread {
    private final int clientNumber;

    StateWriter(int clientNumber) {
      this.clientNumber = clientNumber;
    }

    @Override
    public void run() {
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
          sleep(NETWORK_TICK_SLEEP_MILLIS);
        } catch (InterruptedException e) {
        }

      } while (true);

    }

  }

}
