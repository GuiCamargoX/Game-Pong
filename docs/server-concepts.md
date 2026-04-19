# Server Concepts for Beginners 👨‍🏫

This guide explains the core ideas behind the project before you dive into the code.

## What is a server?

A server is a program that waits for requests from clients and sends responses back.

In Java socket applications, a typical server lifecycle is:

1. Open a listening socket (`ServerSocket`).
2. Wait for client connections (`accept()`).
3. Exchange data using streams.
4. Keep running until stopped.

Think of this Pong server like a referee:

- clients ask to move paddles,
- server decides the official game state,
- server broadcasts that state to everyone.

## Common server models

### 1) Iterative server (one client at a time)

Good first step to learn sockets, but not good for multiplayer.

```java
ServerSocket server = new ServerSocket(5050);
while (true) {
    Socket client = server.accept();
    handleClient(client); // blocks until done
}
```

### 2) Thread-per-client server (what this repo uses)

Each connected client gets its own worker thread.

```java
ServerSocket server = new ServerSocket(5050);
while (true) {
    Socket client = server.accept();
    new ClientWorker(client).start();
}
```

Why it is good for learning:

- clear mental model,
- easy to debug,
- code maps nicely to connection flow.

### 3) Thread pool server

Instead of creating unlimited threads, reuse a fixed set of workers.

Great next step after thread-per-client when you want better scalability.

### 4) Event-driven / non-blocking server

Uses selectors and a single event loop (`java.nio`), typically for high concurrency.

Powerful, but conceptually harder for beginners.

## What we implemented in this project

This project uses:

- **TCP sockets** for reliable ordered delivery,
- **thread-per-client** sessions,
- **server-authoritative game logic**.

Concretely:

- `server.ServerMain` listens and accepts connections.
- `server.ClientSession` handles each client and runs state loops.
- `client.GameClient` sends input and reads synchronized state.
- `client.GamePanel` only renders what the server sends.

## How Java socket connections work

### Server side

```java
ServerSocket serverSocket = new ServerSocket(5050);
Socket clientSocket = serverSocket.accept(); // blocks until a client connects
DataInputStream in = new DataInputStream(clientSocket.getInputStream());
DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
```

### Client side

```java
Socket socket = new Socket("localhost", 5050);
DataInputStream in = new DataInputStream(socket.getInputStream());
DataOutputStream out = new DataOutputStream(socket.getOutputStream());
```

### Important blocking behavior

- `accept()` blocks until someone connects.
- `readInt()`, `readBoolean()`, etc. block until bytes arrive.
- If sender and receiver disagree on field order, reads can block or decode garbage.

## TCP vs UDP (in simple words)

- **TCP**: reliable, ordered, connection-oriented. Easier for tutorials and deterministic synchronization.
- **UDP**: faster and lighter, but packets can drop or arrive out of order.

Why this tutorial uses TCP:

- easier debugging,
- fewer moving parts,
- better for learning protocol fundamentals first.

## Practical examples

### Example A: safe protocol change

Goal: add a new `int matchTime` field from server to client.

Safe sequence:

1. Update server write order to include `matchTime`.
2. Update client read order at the exact same point.
3. Recompile and run server + two clients.

Never change only one side.

### Example B: tuning gameplay speed

Goal: make the ball move faster.

1. Open `src/shared/PongConstants.java`.
2. Increase `INITIAL_BALL_SPEED`.
3. Recompile and run smoke test.

This is safer than editing loop math directly.

### Example C: changing only UI text

Goal: change waiting message language.

1. Edit text in `src/client/GamePanel.java`.
2. Recompile and run.

No protocol changes required.

## Suggested learning path

1. Run the project unchanged.
2. Read `docs/architecture.md`.
3. Read `docs/protocol.md` and map each field to code.
4. Make one tiny change and verify with two clients.

Small steps are the fastest way to learn networking confidently.
