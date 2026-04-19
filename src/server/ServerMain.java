package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain {
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
