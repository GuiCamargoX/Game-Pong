# AGENTS.md

## Maintainer intent
- This project was originally written during graduation as a Java learning exercise.
- Current goal: refactor the repository so anyone on GitHub can run it easily and understand it quickly.
- Educational focus: learning Java socket connections and how to build a simple client/server game.
- Prefer refactors that improve readability, naming, structure, and onboarding docs while preserving behavior.

## Project shape
- Plain Java project (no Maven/Gradle); use `javac`/`java` directly.
- Client entrypoint: `Main.java` (`Main` creates `PongPanel` and starts `Cliente`).
- Server entrypoint: `Servidor/Servidor.java` (`Servidor`).
- Classes are in the default package (no `package` declarations).

## Run commands (from repo root)
- Compile server: `javac Servidor/Servidor.java`
- Compile client: `javac Main.java Cliente.java PongPanel.java`
- Run server: `java -cp Servidor Servidor`
- Run clients: start `java Main` in two separate terminals.
- Required order: start server first, then both clients.

## Critical coupling (easy to break)
- Default port is `5050` in both `Cliente.java` and `Servidor/Servidor.java`.
- `PONG_PORT` environment variable overrides the default on both sides; use the same value for server and clients.
- Wire protocol is positional `DataInputStream/DataOutputStream`; keep read/write order exactly aligned between server and client.
- Match stays in waiting screen until 2 clients connect.

## Verification
- No automated tests/lint/typecheck are configured.
- Manual smoke test:
  - server starts and accepts two clients
  - both paddles move
  - ball and score remain synchronized from both client views

## Repo gotchas
- `.gitignore` excludes `*.class` and `*.jar`; do not commit compiled artifacts.
- Server code is effectively 2-player only (`DataOutputStream[2]`), so do not assume extra clients are supported.
