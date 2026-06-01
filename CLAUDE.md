# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build and Run

Compile from the project root (requires JDK 8+):
```bash
find TTTGUI/src -name "*.java" | xargs javac -d TTTGUI/out/production/TTTGUI
```

Run the application:
```bash
java -cp TTTGUI/out/production/TTTGUI TicTacToe
```

There is no build tool (no Maven/Gradle). The project is configured for IntelliJ IDEA (`.idea/`, `.iml` files), which handles compilation automatically.

## Architecture

Source is in `TTTGUI/src/` organised into four packages plus a root entry point:

```
TTTGUI/src/
  TicTacToe.java        ← entry point (default package); calls GameController.start()
  model/
    GameModel.java      ← board state (char[][]), players, turn, isPVComp
    Player.java         ← player data and win/loss/draw stats
  view/
    GameView.java       ← JFrame + all UI panels; exposes mutation methods
    Grid.java           ← JPanel containing 9 GridSpace buttons
    GridSpace.java      ← individual cell button; holds State (EMPTY/X/O) and 0–8 index
    JAButton.java       ← JButton subclass with Action enum; no auto-listener wiring
  controller/
    GameController.java ← ActionListener, comp timer, config dialogs, M↔V coordination
  ai/
    GameAnalyzer.java   ← win/draw detection and move selection (smart + random)
    MiniMax.java        ← recursive minimax algorithm
```

**Flow:** `TicTacToe.main()` → `GameController.start()` → config dialogs → `launchGame()` → Swing event loop

**Key design decisions:**
- `GameModel` is the source of truth for board state (`char[][]`). `GameController` updates model and view in lockstep on every move.
- `GameController` owns the single `ActionListener` and wires it to all buttons and grid spaces after constructing `GameView`. `JAButton` no longer auto-registers a listener in its constructor.
- `GameAnalyzer.makeSmartMove(Grid, char)` takes the computer's mark as a parameter — no references to `GameController` or `GameModel`.
- `MiniMax.getBestMove()` always assumes X maximises and O minimises.
- `GameView.resetBoard()` stores the registered `ActionListener` and re-applies it to the new `GridSpace` instances created by `Grid.resetGrid()`.
- Computer vs. Computer mode is not yet implemented — selecting it shows a dialog and returns to the start menu.

## Conventions

- Java Swing exclusively for GUI — no JavaFX or other frameworks.
- Game logic and AI belong in `ai/`; UI components in `view/`; state in `model/`; coordination in `controller/`.
- Standard Java camelCase naming.
