# Pong Java Socket Tutorial 🏓

This repository is a learning project for Java sockets and basic client/server game architecture.

The goal is educational: understand how a server accepts multiple clients, sends synchronized state, and reads live input over TCP.

If you are learning sockets for the first time, you are in the right place 👋. Keep changes small, run often, and use this project as a playground.

## Learning goals 🎯

- Start a Java TCP server and connect two clients.
- Understand a simple positional wire protocol using `DataInputStream` and `DataOutputStream`.
- See how a server-authoritative game loop keeps both clients synchronized.

## Prerequisites

- JDK 8+ (`javac` and `java` in your `PATH`)
- Three terminals (1 for server, 2 for clients)

## Quickstart 🚀

From the repository root:

```bash
javac -d out src/shared/PongConstants.java src/server/*.java src/client/*.java
```

Start server first:

```bash
java -cp out server.ServerMain
```

Start two clients (one per terminal):

```bash
java -cp out client.ClientMain
```

The game stays on the waiting screen until both clients connect.

Nice, you are running a full Java client/server game 🎉.

## Port configuration

- Default port is `5050`.
- Set `PONG_PORT` on server and clients when you want a custom port.

```bash
PONG_PORT=6000 java -cp out server.ServerMain
PONG_PORT=6000 java -cp out client.ClientMain
```

## Package layout

- `src/client/ClientMain.java`: client entrypoint (UI + client thread bootstrap)
- `src/client/GameClient.java`: socket I/O (send inputs, receive state)
- `src/client/GamePanel.java`: rendering and keyboard input
- `src/server/ServerMain.java`: server entrypoint and accept loop
- `src/server/ClientSession.java`: per-client session + state broadcast loop
- `src/shared/PongConstants.java`: shared network and game constants

## Protocol and architecture docs

- `docs/protocol.md`: exact field order for client/server stream messages
- `docs/architecture.md`: execution flow and responsibilities

## Refactor roadmap (for beginners) 🧭

If this is your first client/server refactor, pick one track and test after every change.

### 1) I want to change the protocol (socket messages)

Start here:

- `docs/protocol.md`
- `src/client/GameClient.java`
- `src/server/ClientSession.java`

Rule of thumb:

- Keep field order exactly aligned on both sides.
- If you add/remove/reorder one field, update client and server together.

### 2) I want to change gameplay or physics

Start here:

- `src/shared/PongConstants.java`
- `src/server/ClientSession.java` (`BallLoop` and scoring logic)

Rule of thumb:

- Physics is server-authoritative.
- Clients should render server state, not calculate their own ball movement.

### 3) I want to change visuals or controls

Start here:

- `src/client/GamePanel.java`

Rule of thumb:

- UI changes are usually client-only.
- If you only change fonts/colors/text, protocol usually does not change.

### Safe workflow for every refactor ✅

1. Compile:
   `javac -d out src/shared/PongConstants.java src/server/*.java src/client/*.java`
2. Start server:
   `java -cp out server.ServerMain`
3. Start two clients:
   `java -cp out client.ClientMain`
4. Confirm both clients stay synchronized.

## Troubleshooting 🛠️

- `Could not listen on port: 5050`: port is busy; run with another `PONG_PORT` value on server and clients.
- Waiting screen does not exit: server must have exactly 2 clients connected.
- Desync after refactor: verify read/write order in `docs/protocol.md` is unchanged.

## Manual smoke test 🧪

- Server starts and logs two client connections.
- Both paddles move.
- Ball and score stay synchronized from both client windows.
