# MVC Architecture Refactor — Design Spec

**Date:** 2026-05-31
**Task:** ClickUp 86b9gd16n — Refactor to MVC Architecture

## Goal

Decouple game logic from the UI and eliminate the current static-heavy architecture in `TicTacToe.java`. Introduce `GameModel`, `GameView`, and `GameController` so that each layer has a single, clear responsibility.

---

## Architecture

### File Map

| File | Status | Responsibility |
|------|--------|----------------|
| `TicTacToe.java` | Modified | Entry point only — calls `new GameController().start()` |
| `GameModel.java` | New | All mutable game state (players, board, turn, mode) |
| `GameView.java` | New | JFrame + all UI panels/labels; exposes targeted mutation methods |
| `GameController.java` | New | ActionListener, timer, config dialogs, M↔V coordination |
| `JAButton.java` | Modified | `Action` enum moves here; constructor no longer auto-adds listener |
| `GridSpace.java` | Modified | References `JAButton.Action` instead of `TicTacToe.Action` |
| `GameAnalyzer.java` | Modified | `makeSmartMove` takes `char computerMark` param; no TicTacToe ref |
| `Grid.java` | Unchanged | |
| `Player.java` | Unchanged | |
| `MiniMax.java` | Unchanged | |

### Dependency Graph (strictly layered)

```
TicTacToe → GameController
GameController → GameModel, GameView, GameAnalyzer, Player
GameView → Grid, GridSpace, JAButton
GameAnalyzer → MiniMax
```

No class below `GameController` references `GameController`, `GameModel`, or `GameView`.

---

## Components

### `GameModel`

Plain Java object, no Swing imports.

**Fields:**
- `Player player1`
- `Player player2`
- `boolean isPlayer1Turn`
- `boolean isPVComp`
- `char[][] board` — source of truth for board state; `' '` = empty, `'X'`/`'O'` = marked

**Methods:**
- Getters/setters for all fields
- `resetBoard()` — fills `board` with `' '`
- `getCurrentPlayer()` — returns `player1` or `player2` based on `isPlayer1Turn`
- `applyMove(int index, char mark)` — writes `mark` into `board[index/3][index%3]`

### `GameView`

Owns the `JFrame` and all UI components. Builds a single unified layout used for all game modes (PVP, PVComp, CompVComp).

**Constructor:** builds the full window — header with message label, center `Grid`, side player stat panels, bottom button row. Does not show the window; controller calls `setVisible(true)`.

**Mutation methods (called by controller):**
- `setMessage(String text)`
- `updateCell(int index, char mark)` — sets the `GridSpace` text and state at the given 0–8 index
- `resetBoard()` — clears all `GridSpace` cells
- `setGridEnabled(boolean enabled)` — enables/disables all 9 `GridSpace` buttons
- `refreshStats(Player p1, Player p2)` — updates all win/loss/draw labels

**Listener wiring (called by controller after construction):**
- `addGridListener(ActionListener l)` — stores `l` internally and adds it to all 9 `GridSpace` buttons. `resetBoard()` automatically re-applies the stored listener to the new `GridSpace` instances created by `Grid.resetGrid()`, so the controller never needs to re-wire after a restart.
- `getActionButtons()` — returns the list of `JAButton`s (restart, quit, etc.) so controller can add listeners

**Accessors:**
- `getGrid()` — returns the `Grid` (needed by `GameAnalyzer` and for computer move target lookup)
- `dispose()` — delegates to `JFrame.dispose()`

Config dialogs (`showStartMenu`, `configPVP`, `configPVComp`, `configCompVComp`) remain in `GameController` — they gather user input via `JOptionPane`, not display.

### `GameController`

Owns all coordination logic.

**Fields:**
- `GameModel model`
- `GameView view`
- `Timer compTimer`

**Public methods:**
- `start()` — runs the game loop: show start menu, run config dialogs, build model + view, wire listeners, show window, trigger first computer move if applicable

**Private methods:**
- `buildActionListener()` — returns the `ActionListener` wired to all buttons and grid spaces
- `handleGridClick(GridSpace gs)` — validates move, updates model + view, checks game over, triggers AI
- `handleButtonClick(JAButton btn)` — dispatches on `JAButton.Action`
- `triggerComputerMove()` — disables grid, starts timer, on tick: calls `GameAnalyzer`, updates model + view, re-enables grid
- `applyGameOverResult()` — updates player stats in model, calls `view.refreshStats()` and `view.setMessage()`
- `configPVP()`, `configPVComp()`, `configCompVComp()` — JOptionPane dialogs; return `false` if user cancels
- `showStartMenu()` — returns 0/1/2 or -1 if cancelled

### Changes to Existing Classes

**`JAButton`:**
- `TicTacToe.Action` enum moves here, renamed to `JAButton.Action` (same values: `ChangeGridSpace`, `Restart`, `ChangeGameConfig`, `ChangeGameMode`, `Quit`, `ResetPlayerStats`)
- Constructor removes `this.addActionListener(TicTacToe.actionListener)` — controller wires listeners externally

**`GridSpace`:**
- `super(text, TicTacToe.Action.ChangeGridSpace)` → `super(text, JAButton.Action.ChangeGridSpace)`
- No other changes

**`GameAnalyzer`:**
- `makeSmartMove(Grid board)` → `makeSmartMove(Grid board, char computerMark)`
- Removes `TicTacToe.player2` reference; caller (controller) passes the computer's mark
- `MiniMax.getBestMove` call updates accordingly: `MiniMax.getBestMove(currCharGrid, computerMark == 'X')`

**`TicTacToe`:**
```java
public class TicTacToe {
    public static void main(String[] args) {
        new GameController().start();
    }
}
```

---

## Data Flow

### Human Move (PVP or PVComp)

1. Player clicks `GridSpace` → controller's `ActionListener` fires
2. `handleGridClick`: checks cell is empty, gets current mark from `model.getCurrentPlayer()`
3. `model.applyMove(index, mark)` → `view.updateCell(index, mark)`
4. `GameAnalyzer.gameOver(model.getBoard())` check
5. Game over → `applyGameOverResult()` (updates model stats, calls `view.refreshStats` + `view.setMessage`)
6. Not game over + PVComp → `triggerComputerMove()`

### Computer Move

1. `view.setGridEnabled(false)`, `view.setMessage("Computer is thinking...")`
2. `compTimer` fires after 500 ms
3. `GameAnalyzer.makeSmartMove(view.getGrid(), computerMark)` or `makeRandomMove` returns index
4. `model.applyMove(index, mark)` → `view.updateCell(index, mark)`
5. Game over check → update message/stats; `view.setGridEnabled(true)`

### Restart

1. `compTimer` stopped if running
2. `model.resetBoard()` → `view.resetBoard()`
3. Turn stays with whichever player's turn it currently is (no turn flip on restart, matching current behavior)
4. If PVComp and computer's turn → `triggerComputerMove()`

### Start / Change Game Mode

1. Controller runs config dialogs → builds `Player` objects → creates `GameModel`
2. Creates `GameView`, wires all listeners
3. Shows window; if PVComp and computer goes first → `triggerComputerMove()`
4. On "Change Game Mode": `view.dispose()`, loop back to `start()`
5. On "Change Game Config": `view.dispose()`, re-run config for same mode

---

## Out of Scope

- `gameCompVComp()` remains a stub — not part of this task
- No new visual changes — the layout is identical to the current one
- No changes to `Grid`, `Player`, or `MiniMax`
