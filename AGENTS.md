# AGENTS.md

## Maintainer intent
- This project was originally written during graduation as a Java learning exercise.
- Current goal: refactor the repository so anyone on GitHub can run it easily and understand it quickly.
- Educational focus: learning Java socket connections and how to build a simple client/server game.
- Prefer refactors that improve readability, naming, structure, and onboarding docs while preserving behavior.

## Project shape
- Plain Java project (no Maven/Gradle); compile with `javac` and run with `java`.
- Source layout is package-based under `src/`:
  - `client` package: UI, input, and client socket thread.
  - `server` package: server bootstrap and client sessions.
  - `shared` package: constants that must stay aligned across client/server.
- Client entrypoint: `src/client/ClientMain.java` (`client.ClientMain`).
- Server entrypoint: `src/server/ServerMain.java` (`server.ServerMain`).

## Run commands (from repo root)
- Compile all sources: `javac -d out src/shared/PongConstants.java src/server/*.java src/client/*.java`
- Run server: `java -cp out server.ServerMain`
- Run clients: start `java -cp out client.ClientMain` in two separate terminals.
- Required order: start server first, then both clients.

## Critical coupling (easy to break)
- Default port is `5050` via `shared.PongConstants.DEFAULT_PORT`.
- `PONG_PORT` environment variable overrides the default on both sides; use the same value for server and clients.
- Wire protocol is positional `DataInputStream/DataOutputStream`; keep read/write order exactly aligned between server and client (`docs/protocol.md`).
- Match stays in waiting screen until 2 clients connect.
- Client 2 uses mirrored ball X; preserve this behavior unless intentionally changing gameplay.

## Verification
- No automated tests/lint/typecheck are configured.
- Manual smoke test:
  - server starts and accepts two clients
  - both paddles move
  - ball and score remain synchronized from both client views

## Repo gotchas
- `.gitignore` excludes `*.class` and `*.jar`; do not commit compiled artifacts.
- Build output should go to `out/`.
- Server code is effectively 2-player only (`MAX_PLAYERS = 2`), so do not assume extra clients are supported.
