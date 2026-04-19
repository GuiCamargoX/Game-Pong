package client;

import static shared.PongConstants.DEFAULT_PORT;
import static shared.PongConstants.HOST;
import static shared.PongConstants.NETWORK_TICK_SLEEP_MILLIS;
import static shared.PongConstants.PLAYER_ONE_MARKER;
import static shared.PongConstants.PLAYER_TWO_MARKER;
import static shared.PongConstants.PORT_ENV;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class GameClient extends Thread {
  private DataOutputStream outputStream;
  private DataInputStream inputStream;
  private boolean stopReaderThread = false;

  private int playerOneY = 0;
  private int playerTwoY = 0;
  private int ballX = 0;
  private int ballY = 0;
  private int playerOneScore = 0;
  private int playerTwoScore = 0;

  private int clientNumber;
  private boolean showTitleScreen = true;

  private Socket clientSocket;
  private final GamePanel gamePanel;

  public GameClient(GamePanel gamePanel) {
    this.gamePanel = gamePanel;
  }

  @Override
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
      default:
        break;
      }

      do {
        outputStream.writeBoolean(gamePanel.moveUp);
        outputStream.writeBoolean(gamePanel.moveDown);
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

    @Override
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
