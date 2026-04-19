import java.io.*;
import java.net.*;
import java.util.*;

public class Cliente extends Thread {
  private static final String HOST = "localhost";
  private static final int DEFAULT_PORT = 5050;
  private static final String PORT_ENV = "PONG_PORT";
  private static final int NETWORK_TICK_RATE = 60;
  private static final long NETWORK_TICK_SLEEP_MILLIS = 1000L / NETWORK_TICK_RATE;
  private static final char PLAYER_ONE_MARKER = '1';
  private static final char PLAYER_TWO_MARKER = '2';

  static DataOutputStream outputStream = null;
  static DataInputStream inputStream = null;
  static boolean stopReaderThread = false;

  int playerOneY = 0;
  int playerTwoY = 0;
  int ballX = 0;
  int ballY = 0;
  int playerOneScore = 0;
  int playerTwoScore = 0;

  int clientNumber;
  boolean moveUp = false;
  boolean moveDown = false;
  boolean showTitleScreen = true;

  Socket clientSocket = null;
  PongPanel gamePanel = null;

  Cliente(PongPanel gamePanel) {
    this.gamePanel = gamePanel;
  }

  public void run() {
    try {
      int port = resolvePort();
      clientSocket = new Socket(HOST, port);
      outputStream = new DataOutputStream(clientSocket.getOutputStream());
      inputStream = new DataInputStream(clientSocket.getInputStream());

      clientNumber = inputStream.readInt();
      new ServerReader().start();

    } catch (UnknownHostException e) {
      System.err.println("Don't know about host.");
      return;
    } catch (IOException e) {
      System.err.println("Couldn't get I/O for the connection to host");
      return;
    }

    try {
      switch (clientNumber) {
      case 0:
        outputStream.writeChar(PLAYER_ONE_MARKER);
        break;
      case 1:
        outputStream.writeChar(PLAYER_TWO_MARKER);
        break;
      }

      do {
        moveUp = gamePanel.moveUp;
        moveDown = gamePanel.moveDown;

        outputStream.writeBoolean(moveUp);
        outputStream.writeBoolean(moveDown);
        outputStream.flush();

        try {
          sleep(NETWORK_TICK_SLEEP_MILLIS);
        } catch (InterruptedException e) {
        }

      } while (true);

    } catch (UnknownHostException e) {
      System.err.println("Trying to connect to unknown host: " + e);
    } catch (IOException e) {
      System.err.println("IOException:  " + e);
    }
  }

  private int resolvePort() {
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

  class ServerReader extends Thread {

    public void run() {

      try {

        do {
          showTitleScreen = inputStream.readBoolean();

          playerOneScore = inputStream.readInt();
          playerTwoScore = inputStream.readInt();

          playerOneY = inputStream.readInt();
          playerTwoY = inputStream.readInt();

          ballX = inputStream.readInt();
          ballY = inputStream.readInt();

          if (clientNumber == 0) {
            gamePanel.showTitleScreen = showTitleScreen;
            gamePanel.playerOneY = playerOneY;
            gamePanel.playerTwoY = playerTwoY;
            gamePanel.ballX = ballX;
            gamePanel.ballY = ballY;

            gamePanel.playerOneScore = playerOneScore;
            gamePanel.playerTwoScore = playerTwoScore;
          }

          if (clientNumber == 1) {
            gamePanel.showTitleScreen = showTitleScreen;
            gamePanel.playerOneY = playerTwoY;
            gamePanel.playerTwoY = playerOneY;
            gamePanel.ballX = ballX;
            gamePanel.ballY = ballY;

            gamePanel.playerOneScore = playerTwoScore;
            gamePanel.playerTwoScore = playerOneScore;
          }

        } while (!stopReaderThread);

      } catch (IOException e) {
        System.err.println("IOException:  " + e);
      }

    }
  }

}
