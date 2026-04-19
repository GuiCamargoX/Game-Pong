# Architecture Overview 🏗️

This project uses a server-authoritative multiplayer architecture.

If you are new to networking, read this once, then open each referenced class while you follow the steps.

## Big idea

- The **server** is the source of truth.
- The **clients** send input and render server state.

That single decision keeps both players synchronized.

## Concept-to-code map 🗺️

- `src/server/ServerMain.java`
  - Opens the listening socket.
  - Accepts clients.
  - Starts one `ClientSession` per connection.

- `src/server/ClientSession.java`
  - Handles one client connection.
  - Reads player input from that client.
  - Updates shared game state and sends updates back.

- `src/client/ClientMain.java`
  - Creates the game window and starts network thread.

- `src/client/GameClient.java`
  - Connects to server.
  - Sends input booleans.
  - Reads synchronized game state.

- `src/client/GamePanel.java`
  - Draws score, paddles, and ball.
  - Captures keyboard input.

- `src/shared/PongConstants.java`
  - Shared values for both sides (ports, dimensions, markers, tick timing).

## Runtime flow

1. `server.ServerMain` starts and listens on a port.
2. Two clients connect.
3. Server assigns `clientNumber` and receives player marker.
4. Client loops: send `moveUp`/`moveDown`.
5. Server loops: process input, update game state, broadcast snapshot.
6. Clients render received snapshot.

Tip: when debugging, always follow this exact order. It quickly reveals where things go wrong.

## Sequence example (text diagram) 🔄

```text
Client A                Server                  Client B
   | -- connect --------> |                         |
   | <--- clientNumber=0- |                         |
   | -- marker '1' ------> |                         |
   |                       | <-------- connect ----- |
   |                       | ---- clientNumber=1 --> |
   |                       | <------ marker '2' ---- |
   | -- moveUp/down -----> |                         |
   | <--- full state ----- | ----- full state ----> |
```

## Why this architecture is good for learning

- Easy to reason about: one authority for game state.
- Easy to test: run one server and two clients locally.
- Easy to refactor safely: constants and protocol are centralized.

## Limits to keep in mind

- This design is intentionally simple, not high-scale production networking.
- It is effectively 2-player only (`MAX_PLAYERS = 2`).
- Per-client threads are great for clarity, but not the most scalable model.
