# AGENTS.md

## Maintainer intent
- This project was originally written during graduation as a Java learning exercise.
- Current goal: refactor the repository so anyone on GitHub can run it easily and understand it quickly.
- Educational focus: learning Java socket connections and how to build a simple client/server game.
- Prefer refactors that improve readability, naming, structure, and onboarding docs while preserving behavior.

## Project shape
- Plain Java project (no Maven/Gradle); use `javac`/`java` directly.
- Client entrypoint: `Main.java` (`Main` creates `PongPanel` and starts `Cliente`).
- Server entrypoint: `Servidor/Servidor.java` (`Servidor`), with a prebuilt `Servidor/Servidor.jar`.
- Classes are in the default package (no `package` declarations).

## Run commands (from repo root)
- Compile server: `javac Servidor/Servidor.java`
- Compile client: `javac Main.java Cliente.java PongPanel.java`
- Run server: `java -cp Servidor Servidor` (or `java -jar Servidor/Servidor.jar`)
- Run clients: start `java Main` in two separate terminals.
- Required order: start server first, then both clients.

## Critical coupling (easy to break)
- Port is hardcoded to `80` in both `Cliente.java` and `Servidor/Servidor.java`; change both files together.
- Port `80` may require elevated privileges on Linux/macOS; if refactoring for easier local use, switch both sides to an unprivileged port.
- Wire protocol is positional `DataInputStream/DataOutputStream`; keep read/write order exactly aligned between server and client.
- Match stays in waiting screen until 2 clients connect.

## Verification
- No automated tests/lint/typecheck are configured.
- Manual smoke test:
  - server starts and accepts two clients
  - both paddles move
  - ball and score remain synchronized from both client views

## Repo gotchas
- `.gitignore` excludes `*.class` and `*.jar`, but `Servidor/*.class` and `Servidor/Servidor.jar` are currently tracked.
- Recompiling server can modify tracked binaries.
- Server code is effectively 2-player only (`DataOutputStream[2]`), so do not assume extra clients are supported.
