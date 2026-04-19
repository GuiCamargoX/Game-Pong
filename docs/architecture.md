# Architecture Overview

This project uses a simple server-authoritative architecture.

## Runtime flow

1. `server.ServerMain` opens a `ServerSocket`.
2. Two TCP clients connect (`client.ClientMain` -> `client.GameClient`).
3. Each connection creates one `server.ClientSession` thread.
4. After both clients connect, the server enables gameplay and starts the ball loop.
5. Each client sends only input (`moveUp`, `moveDown`) at a fixed tick.
6. The server updates game state and broadcasts the full state to both clients.
7. Each client renders the received state in `client.GamePanel`.

## Responsibility split

- **Server authority**
  - Owns score, ball physics, and paddle positions.
  - Resolves collisions and scoring.
  - Broadcasts canonical state to all clients.

- **Client responsibility**
  - Captures local keyboard input.
  - Sends input booleans to server.
  - Draws game state received from server.

## Shared constants

`shared.PongConstants` is the source of truth for values that must stay aligned:

- networking (`DEFAULT_PORT`, `PORT_ENV`, `MAX_PLAYERS`)
- protocol markers (`PLAYER_ONE_MARKER`, `PLAYER_TWO_MARKER`)
- board and physics dimensions (`BOARD_WIDTH`, `PADDLE_HEIGHT`, `BALL_SIZE`, etc.)

Keeping those values centralized reduces accidental client/server mismatch.
