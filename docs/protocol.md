# Wire Protocol (TCP Data Streams) 🔌

This project uses a positional binary protocol over TCP.

In practice, that means both sides must read and write fields in the exact same order.

## Why order matters

`DataInputStream` does not know message schemas. It only reads bytes in sequence.

If server writes:

1. `writeInt(score)`
2. `writeBoolean(showTitle)`

but client reads:

1. `readBoolean()`
2. `readInt()`

the client decodes invalid data and synchronization breaks.

## Transport and format

- Transport: TCP socket
- Serialization: `DataInputStream` / `DataOutputStream`
- Tick target: 60 updates/sec

## Handshake 🤝

1. Server sends `int clientNumber` (`0` or `1`).
2. Client sends `char playerMarker` (`'1'` or `'2'`).

## Client -> Server (repeated) 📤

Every tick:

1. `boolean moveUp`
2. `boolean moveDown`

## Server -> Client (repeated) 📥

Every tick:

1. `boolean showTitleScreen`
2. `int playerOneScore`
3. `int playerTwoScore`
4. `int playerOneY`
5. `int playerTwoY`
6. `int ballX` (mirrored for client 2)
7. `int ballY`

## Code locations

- Server writes in `src/server/ClientSession.java` (`StateWriter`).
- Client reads in `src/client/GameClient.java` (`ServerReader`).
- Client writes input in `src/client/GameClient.java` main loop.
- Server reads input in `src/server/ClientSession.java` run loop.

## Important constraints ⚠️

- Server is effectively 2-player only.
- Match stays in waiting screen until both clients are connected.
- Client 2 receives mirrored ball X to preserve gameplay perspective.

## Common mistakes 🚫

- Changing server write order without changing client read order.
- Adding a field on one side only.
- Testing with one client and assuming multiplayer sync is correct.

## Safe protocol evolution example ✅

Suppose you want to add `int matchTime` to server -> client updates.

Safe process:

1. Add `writeInt(matchTime)` to server in the chosen position.
2. Add `readInt()` in client at the exact same position.
3. Store and use the value in `GamePanel`.
4. Recompile and run with two clients.

Optional beginner-friendly versioning idea:

- First field is `int protocolVersion`.
- Client can fail fast with a clear message if versions differ.

## Quick desync debugging checklist 🧪

1. Confirm server and clients use the same port (`PONG_PORT` or `5050`).
2. Confirm handshake completes on both clients.
3. Compare write order and read order line by line.
4. Verify both clients receive updates continuously.
