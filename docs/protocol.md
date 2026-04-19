# Wire Protocol (TCP Data Streams) 🔌

The game protocol is positional and order-sensitive.

This can feel strict at first, but once you follow the order carefully, it becomes very predictable.

- Transport: TCP socket
- Serialization: `DataInputStream` / `DataOutputStream`
- Tick: 60 updates/sec (target)

If you change field order on one side, you must change the other side in the exact same order.

## Handshake 🤝

1. Server assigns `clientNumber` (`0` or `1`) and sends it as `int`.
2. Client sends its player marker as `char` (`'1'` or `'2'`).

## Client -> Server (repeated) 📤

Every tick, each client sends:

1. `boolean moveUp`
2. `boolean moveDown`

## Server -> Client (repeated) 📥

Every tick, server sends to each client:

1. `boolean showTitleScreen`
2. `int playerOneScore`
3. `int playerTwoScore`
4. `int playerOneY`
5. `int playerTwoY`
6. `int ballX` (or mirrored X for client 2)
7. `int ballY`

## Important constraints ⚠️

- Server is effectively 2-player only.
- Match stays in waiting screen until both clients are connected.
- Client 2 receives mirrored ball X to preserve the original view behavior.

## Common mistakes 🚫

- Changing read order on client but not write order on server (or the opposite).
- Forgetting that client 2 receives mirrored ball X.
- Testing only one client and assuming synchronization is correct.

## Quick desync debugging checklist 🧪

1. Confirm server and clients use the same port (`PONG_PORT` or default `5050`).
2. Confirm both clients complete handshake (`clientNumber` then marker char).
3. Compare server->client field order in code with the list in this file.
4. Re-run with two clients and verify both views update together.
