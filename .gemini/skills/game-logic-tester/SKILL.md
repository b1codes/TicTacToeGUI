---
name: game-logic-tester
description: Automated test generation for game engines, specifically for verifying TicTacToe win conditions, AI move optimality, and draw states. Use when the user wants to add unit tests or ensure logic integrity.
---

# Game Logic Tester

This skill provides specialized workflows for writing unit tests to verify the core logic of the TicTacToe game, including `GameAnalyzer` and `MiniMax`.

## Test Scenarios

- **Win Conditions**: Verify horizontal, vertical, and diagonal wins for both X and O.
- **Draw States**: Ensure a full board with no winner is correctly identified as a draw.
- **AI Move Optimality**: Test `MiniMax` to ensure it always picks the best move (or avoids losing).
- **Invalid Moves**: Confirm that the system prevents moves on occupied spaces.
- **State Integrity**: Verify the `Grid` state remains consistent after series of moves.

## JUnit Integration

Use JUnit 5 for writing tests.

### Example Test Case Pattern

```java
@Test
void testVerticalWinX() {
    Grid grid = new Grid();
    grid.setSpace(0, 0, State.X);
    grid.setSpace(1, 0, State.X);
    grid.setSpace(2, 0, State.X);
    assertTrue(GameAnalyzer.checkWinner(grid) == Player.X);
}
```

## AI Testing Strategy

- **Hard-coded Board States**: Initialize boards near completion and verify AI move selection.
- **Perfect Game Play-through**: Simulate an AI vs AI match; it should always result in a draw.
- **Depth Scoring**: Verify `MiniMax` prioritizes faster wins (lower depth) over slower ones.

## Recommended Tools
- JUnit 5
- Mockito (if needed for isolating components)
