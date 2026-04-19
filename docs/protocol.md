# Wire Protocol (TCP Data Streams)

The game protocol is positional and order-sensitive.

- Transport: TCP socket
- Serialization: `DataInputStream` / `DataOutputStream`
- Tick: 60 updates/sec (target)

If you change field order on one side, you must change the other side in the exact same order.

## Handshake

1. Server assigns `clientNumber` (`0` or `1`) and sends it as `int`.
2. Client sends its player marker as `char` (`'1'` or `'2'`).

## Client -> Server (repeated)

Every tick, each client sends:

1. `boolean moveUp`
2. `boolean moveDown`

## Server -> Client (repeated)

Every tick, server sends to each client:

1. `boolean showTitleScreen`
2. `int playerOneScore`
3. `int playerTwoScore`
4. `int playerOneY`
5. `int playerTwoY`
6. `int ballX` (or mirrored X for client 2)
7. `int ballY`

## Important constraints

- Server is effectively 2-player only.
- Match stays in waiting screen until both clients are connected.
- Client 2 receives mirrored ball X to preserve the original view behavior.
