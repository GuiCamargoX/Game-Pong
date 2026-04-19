# Pong (Java Socket Client/Server Learning Project)

This repository is a small educational project built to learn Java socket connections and a basic 2-player client/server game loop.

## Prerequisites

- JDK 8+ installed (`javac` and `java` in `PATH`)
- Three terminals (1 server + 2 clients)

## Quickstart

From the repository root:

```bash
javac Servidor/Servidor.java
javac Main.java Cliente.java PongPanel.java
```

Start the server first:

```bash
java -cp Servidor Servidor
```

Start two clients (one in each terminal):

```bash
java Main
```

The match leaves the waiting screen only after both clients are connected.

Default port is `5050`.

To use a custom port, set `PONG_PORT` for both server and clients:

```bash
PONG_PORT=6000 java -cp Servidor Servidor
PONG_PORT=6000 java Main
```

## Project layout

- `Main.java`: client entrypoint, creates UI and starts `Cliente`
- `Cliente.java`: socket client, sends input and receives state from server
- `PongPanel.java`: game rendering and keyboard input
- `Servidor/Servidor.java`: server entrypoint and game state broadcast loop

## Troubleshooting

- `Could not listen on port: 5050`: the port is busy; choose another one with `PONG_PORT` for both server and clients.
- Waiting screen never leaves: verify server is running and two clients are connected.
- One side is desynced: keep client/server protocol read/write order aligned when refactoring.

## Manual smoke test

- Server starts and accepts two clients.
- Both paddles move.
- Ball and score stay synchronized from both client views.
