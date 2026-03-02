---
name: mvc-refactor-pro
description: Specialized workflows for refactoring monolithic Java Swing applications toward a Model-View-Controller (MVC) architecture. Use when the user wants to decouple game logic from the UI and move away from static state management in TicTacToe.java.
---

# MVC Refactor Pro

This skill provides a systematic approach for decoupling UI and business logic in Java Swing applications, enabling better testability and maintenance.

## Refactoring Process

### 1. Identify the Model
- Extract data fields (board state, scores, turn indicator) from the UI class.
- Create a dedicated `GameModel` class to encapsulate this state.

### 2. Isolate the View
- Move UI construction and rendering logic to a `GameView` or `BoardView` class.
- The View should observe the Model (using `PropertyChangeListener` or similar) to update its visual state.

### 3. Implement the Controller
- Move `ActionListener` implementations and logic that bridges the UI to the Model into a `GameController`.
- The Controller handles user input, updates the Model, and potentially notifies the View.

### 4. Remove Static State
- Replace static fields with instance variables.
- Use dependency injection to pass models and controllers to the components that need them.

## Best Practices
- Keep the View as passive as possible.
- The Model should have zero dependencies on Swing or any UI components.
- Use events or callbacks to notify components of state changes.
