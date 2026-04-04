# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build and Run

Compile from the project root (requires JDK 8+):
```bash
javac TTTGUI/src/*.java -d TTTGUI/out/production/TTTGUI
```

Run the application:
```bash
java -cp TTTGUI/out/production/TTTGUI TicTacToe
```

There is no build tool (no Maven/Gradle). The project is configured for IntelliJ IDEA (`.idea/`, `.iml` files), which handles compilation automatically.

## Architecture

All source is in `TTTGUI/src/`. There are no packages — all classes are in the default package.

**Flow:** `TicTacToe.main()` → `startGame()` → config dialogs → `gamePVP()` / `gamePVComp()` / `gameCompVComp()`

**Key design decisions:**
- `TicTacToe` uses entirely static fields for all game state (`board`, `player1`, `player2`, `isPlayer1Turn`, `isPVComp`). There is no instance-based state management.
- A single static `ActionListener` in `TicTacToe` handles all UI events by checking `e.getSource()` type (`GridSpace` vs `JAButton`) and the source's `Action` enum type.
- `GameAnalyzer` bridges GUI state and AI: it converts `Grid` (a list of `GridSpace` Swing components) to a `char[][]` for use by `MiniMax`. Both `Grid`-based and `char[][]`-based overloads exist for win/draw detection.
- `MiniMax.getBestMove()` always assumes X maximizes and O minimizes. The computer is always `player2`.
- `GridSpace` extends `JButton` and stores its own board state (`State.EMPTY/X/O`) and a 0–8 identifier for its position. `JAButton` similarly extends `JButton` with an `Action` enum field.
- The `gameCompVComp()` method is currently unimplemented (empty body).

## Conventions

- Java Swing exclusively for GUI — no JavaFX or other frameworks.
- Game logic and AI belong in `GameAnalyzer` and `MiniMax`; UI orchestration belongs in `TicTacToe`.
- Standard Java camelCase naming.
