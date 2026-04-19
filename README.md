# Pong Java Socket Tutorial 🏓

Welcome! This repository is a beginner-friendly Java socket project.

You will learn how a server accepts clients, how sockets exchange data, and how a server-authoritative game stays synchronized between players.

I hope this repo helps you learn how servers work in Java. I created this project during my Computer Science course while learning Java, sockets, and object-oriented programming, and later refactored it with Codex. ;]

## What you will learn 🎯

- How to create a TCP server in Java with `ServerSocket`.
- How clients connect with `Socket` and exchange binary messages.
- How to structure a simple multiplayer game with client and server responsibilities.

## Server concepts before coding 🧠

In simple terms, a server is a program that listens for connections and responds to clients.

Common server models:

- **Iterative server**: handles one client at a time (simple, but limited).
- **Thread-per-client**: each client gets its own thread (easy to understand, great for tutorials).
- **Thread pool**: reuses worker threads to scale better.
- **Event-driven (non-blocking)**: advanced model for high concurrency.

This project implements **thread-per-client** plus a **server-authoritative game loop**.

- The server is the referee: it owns score, ball, and paddle positions.
- Clients send input and render what the server says is true.

If you want a deeper conceptual explanation with code examples, see `docs/server-concepts.md`.

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

## How socket connection works in this project 🔌

1. `server.ServerMain` opens a listening socket.
2. Each `client.ClientMain` starts `client.GameClient` and connects.
3. Server assigns a `clientNumber` (`0` or `1`).
4. Client sends a player marker (`'1'` or `'2'`).
5. Client repeatedly sends input booleans (`moveUp`, `moveDown`).
6. Server updates game state and repeatedly broadcasts full state to both clients.

Mini timeline example:

```text
Server: listen(5050)
Client A: connect -> receives clientNumber=0 -> sends '1'
Client B: connect -> receives clientNumber=1 -> sends '2'
Loop: clients send input booleans
Loop: server sends scores + paddles + ball position
```

## Port configuration ⚙️

- Default port is `5050`.
- Set `PONG_PORT` on server and clients when you want a custom port.

```bash
PONG_PORT=6000 java -cp out server.ServerMain
PONG_PORT=6000 java -cp out client.ClientMain
```

## Package layout 🗂️

- `src/client/ClientMain.java`: client entrypoint (UI + client thread bootstrap)
- `src/client/GameClient.java`: socket I/O (send inputs, receive state)
- `src/client/GamePanel.java`: rendering and keyboard input
- `src/server/ServerMain.java`: server entrypoint and accept loop
- `src/server/ClientSession.java`: per-client session + state broadcast loop
- `src/shared/PongConstants.java`: shared network and game constants

## Learning docs 📚

- `docs/server-concepts.md`: server models, socket fundamentals, TCP vs UDP, examples
- `docs/architecture.md`: execution flow and responsibility split
- `docs/protocol.md`: exact stream field order and safe protocol changes

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
