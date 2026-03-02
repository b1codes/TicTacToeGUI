# TicTacToeGUI Project Overview

This is a functional Tic-Tac-Toe application built using Java and the Swing GUI toolkit. It supports various game modes and features an AI opponent with a Minimax-based "Smart" mode.

## Project Structure and Architecture

The project follows a modular structure where GUI management and game logic are separated into several key classes:

### Core Components
- **`TicTacToe.java`**: The main entry point. It initializes the `JFrame`, manages the main game loop, handles UI events (via `ActionListener`), and maintains global game state.
- **`GameAnalyzer.java`**: The logic engine. It provides methods for checking win/loss/draw conditions and serves as a bridge between the GUI `Grid` and the AI algorithms. It also handles move selection for "Normal" (random) and "Smart" (Minimax) difficulty.
- **`MiniMax.java`**: Implements the Minimax algorithm to provide optimal moves for the AI. It uses a recursive approach with depth-based scoring to prioritize faster wins and slower losses.
- **`Grid.java` & `GridSpace.java`**: Represent the 3x3 game board. `Grid` is a collection of `GridSpace` objects, each representing an individual cell that can be in an `EMPTY`, `X`, or `O` state.
- **`Player.java`**: A data class that stores player information such as name, marker (X/O), and performance statistics (wins, losses, draws).
- **`JAButton.java`**: A custom extension of `JButton` used for UI elements.

### Architecture Highlights
- **Event-Driven UI**: User interactions are handled through a central `ActionListener` in the `TicTacToe` class.
- **State Management**: Game state (current turn, scores, board state) is largely managed through static fields in the `TicTacToe` class.
- **AI Logic**: The AI logic is decoupled from the UI, with `GameAnalyzer` translating the board state into a simple `char[][]` for processing by the `MiniMax` class.

## Building and Running

### Prerequisites
- Java Development Kit (JDK) 8 or higher.

### Compilation
To compile the project from the root directory:
```bash
javac TTTGUI/src/*.java -d TTTGUI/out/production/TTTGUI
```

### Execution
To run the application:
```bash
java -cp TTTGUI/out/production/TTTGUI TicTacToe
```

## Development Conventions

- **UI Framework**: Exclusively uses Java Swing for all graphical elements.
- **Coding Style**: Follows standard Java camelCase naming conventions.
- **Logic Separation**: Keep game rules and AI calculations within `GameAnalyzer` and `MiniMax`, while keeping `TicTacToe.java` focused on UI orchestration.
- **Dependencies**: The project is designed to be lightweight and does not require external libraries or build tools like Maven or Gradle. It is configured for use with IntelliJ IDEA (as seen in `.idea` and `.iml` files).

## Future Improvements (Potential)
- Transitioning from static state management to a more robust instance-based or MVC architecture.
- Adding unit tests for `GameAnalyzer` and `MiniMax` logic.
- Modernizing the UI with JavaFX or a more modern Swing Look and Feel.
