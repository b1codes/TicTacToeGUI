# MVC Architecture Refactor — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Replace the static-field monolith in `TicTacToe.java` with a clean `GameModel` / `GameView` / `GameController` separation so each layer has a single, clear responsibility.

**Architecture:** `GameModel` owns all mutable game state (`char[][] board`, players, turn). `GameView` owns the `JFrame` and all UI, exposing targeted mutation methods. `GameController` owns the `ActionListener`, timer, config dialogs, and all coordination. `TicTacToe.java` becomes a 3-line entry point.

**Tech Stack:** Java 8+, Java Swing, no build tool — compile with `javac`, run with `java`.

---

## File Map

| File | Action | Notes |
|------|--------|-------|
| `TTTGUI/src/GameModel.java` | Create | Game state: board, players, turn, mode |
| `TTTGUI/src/GameView.java` | Create | JFrame + all UI; mutation methods |
| `TTTGUI/src/GameController.java` | Create | ActionListener, timer, config, coordination |
| `TTTGUI/src/JAButton.java` | Modify | Move `Action` enum here; remove auto-listener |
| `TTTGUI/src/GridSpace.java` | Modify | Reference `JAButton.Action` instead of `TicTacToe.Action` |
| `TTTGUI/src/GameAnalyzer.java` | Modify | `makeSmartMove` takes `char computerMark` param |
| `TTTGUI/src/TicTacToe.java` | Modify | Strip to `main()` only |
| `TTTGUI/src/Grid.java` | Unchanged | |
| `TTTGUI/src/Player.java` | Unchanged | |
| `TTTGUI/src/MiniMax.java` | Unchanged | |

**Compile command (run from project root after every task):**
```bash
javac TTTGUI/src/*.java -d TTTGUI/out/production/TTTGUI
```
**Run command:**
```bash
java -cp TTTGUI/out/production/TTTGUI TicTacToe
```

---

## Task 1: Create GameModel

**Files:**
- Create: `TTTGUI/src/GameModel.java`

- [ ] **Step 1: Create `GameModel.java`**

```java
public class GameModel {
    private Player player1;
    private Player player2;
    private boolean isPlayer1Turn;
    private boolean isPVComp;
    private char[][] board;

    public GameModel(Player player1, Player player2, boolean isPlayer1Turn, boolean isPVComp) {
        this.player1 = player1;
        this.player2 = player2;
        this.isPlayer1Turn = isPlayer1Turn;
        this.isPVComp = isPVComp;
        this.board = new char[3][3];
        resetBoard();
    }

    public Player getPlayer1() { return player1; }
    public Player getPlayer2() { return player2; }
    public boolean isPlayer1Turn() { return isPlayer1Turn; }
    public void setPlayer1Turn(boolean isPlayer1Turn) { this.isPlayer1Turn = isPlayer1Turn; }
    public boolean isPVComp() { return isPVComp; }
    public char[][] getBoard() { return board; }

    public Player getCurrentPlayer() {
        return isPlayer1Turn ? player1 : player2;
    }

    public void applyMove(int index, char mark) {
        board[index / 3][index % 3] = mark;
    }

    public void resetBoard() {
        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 3; c++)
                board[r][c] = ' ';
    }
}
```

- [ ] **Step 2: Compile to verify no errors**

```bash
javac TTTGUI/src/*.java -d TTTGUI/out/production/TTTGUI
```
Expected: no errors (GameModel compiles alongside unchanged existing files).

- [ ] **Step 3: Commit**

```bash
git add TTTGUI/src/GameModel.java
git commit -m "feat: add GameModel with board state and player management"
```

---

## Task 2: Move Action Enum to JAButton; Decouple Auto-Listener

**Why this must be one atomic task:** `JAButton` currently holds a reference to `TicTacToe.actionListener` in its constructor, and `TicTacToe` defines the `Action` enum. Moving the enum means updating every reference site before the code compiles again — JAButton, GridSpace, and TicTacToe's action listener all in one commit.

**Files:**
- Modify: `TTTGUI/src/JAButton.java`
- Modify: `TTTGUI/src/GridSpace.java`
- Modify: `TTTGUI/src/TicTacToe.java`

- [ ] **Step 1: Rewrite `JAButton.java`**

Add the `Action` enum here (moved from `TicTacToe`). Remove the `this.addActionListener(TicTacToe.actionListener)` line from the constructor — listeners are now wired externally by the controller.

```java
import javax.swing.*;

public class JAButton extends JButton {
    public enum Action {
        ChangeGridSpace, Restart, ChangeGameConfig, ChangeGameMode, Quit, ResetPlayerStats
    }

    private Action actionType;

    public JAButton(String text, Action action) {
        super(text);
        this.actionType = action;
    }

    public Action getActionType() {
        return this.actionType;
    }
}
```

- [ ] **Step 2: Update `GridSpace.java` to reference `JAButton.Action`**

Only the two `super(...)` calls and the return type of `getActionType()` change.

```java
import java.awt.*;

public class GridSpace extends JAButton {
    private final int identifier;
    private State currentState;
    public enum State { X, O, EMPTY }

    private static final Font buttonFont = new Font("Arial", Font.BOLD, 45);

    public GridSpace(String text, int identifier) {
        super(text, JAButton.Action.ChangeGridSpace);
        this.identifier = identifier;
        this.currentState = State.EMPTY;
        this.setFont(buttonFont);
        this.revalidate();
    }

    public GridSpace(String text, int identifier, State state) {
        super(text, JAButton.Action.ChangeGridSpace);
        this.identifier = identifier;
        this.currentState = state;
        this.setFont(buttonFont);
        this.revalidate();
    }

    public int getIdentifier() { return this.identifier; }
    public State getCurrentState() { return this.currentState; }
    public void setCurrentState(State state) { this.currentState = state; }

    @Override
    public JAButton.Action getActionType() {
        return super.getActionType();
    }
}
```

- [ ] **Step 3: Update `TicTacToe.java` — remove the `Action` enum; update all references to `JAButton.Action`**

The `Action` enum is gone from `TicTacToe`. Every `Action.X` reference in the `actionListener` body becomes `JAButton.Action.X`. The `actionListener` field type and the `Action` import stay the same — only the enum qualifier changes.

Replace `TicTacToe.java` with the following. This is identical to the current file except: (1) the `Action` enum block is deleted, (2) every bare `Action.X` reference becomes `JAButton.Action.X`, and (3) `public enum Action { ... }` is removed.

```java
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

public class TicTacToe {

    public static JFrame game;
    public static JPanel header;
    public static JLabel message;

    public static JPanel player1Stats;
    public static JLabel player1Name;
    public static JLabel player1Wins;
    public static JLabel player1Losses;
    public static JLabel player1Draws;

    public static JPanel player2Stats;
    public static JLabel player2Name;
    public static JLabel player2Wins;
    public static JLabel player2Losses;
    public static JLabel player2Draws;

    public static Grid board;
    public static Player player1;
    public static Player player2;

    public static boolean isPlayer1Turn;
    public static boolean isPVComp;

    private static Timer compTimer;

    private static final Font typicalFont = new Font("Arial", Font.PLAIN, 20);
    private static final EmptyBorder padding = new EmptyBorder(10, 10, 10, 10);

    public static ActionListener actionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() instanceof GridSpace) {
                GridSpace gridButton = (GridSpace) e.getSource();
                int gridIdentifier = gridButton.getIdentifier();
                JAButton.Action gridButtonAction = gridButton.getActionType();
                if (!GameAnalyzer.gameOver(board) && gridButtonAction == JAButton.Action.ChangeGridSpace) {
                    String currMark = "";
                    if (isPlayer1Turn) {
                        if (player1.isX()) currMark = "X";
                        else currMark = "O";
                    } else {
                        if (player2.isX()) currMark = "X";
                        else currMark = "O";
                    }
                    GridSpace.State newState = GridSpace.State.EMPTY;
                    if (currMark.equals("X")) {
                        newState = GridSpace.State.X;
                    } else {
                        newState = GridSpace.State.O;
                    }
                    boolean tryAgain = false;
                    boolean gameOver = false;
                    if (gridButton.getCurrentState() == GridSpace.State.EMPTY) {
                        gridButton.setCurrentState(newState);
                        gridButton.setText(currMark);
                        board.updateGrid(gridIdentifier, gridButton);
                        gameOver = GameAnalyzer.gameOver(board);
                        isPlayer1Turn = !isPlayer1Turn;
                    } else {
                        tryAgain = true;
                    }

                    if (gameOver) {
                        applyGameOverResult();
                    } else if (!tryAgain) {
                        if (isPlayer1Turn) {
                            message.setText("It's " + player1.getName() + "'s turn.");
                        } else {
                            message.setText("It's " + player2.getName() + "'s turn.");
                        }
                    } else {
                        message.setText("That space is already taken! Try again.");
                    }

                    if (isPVComp && !GameAnalyzer.gameOver(board)) {
                        message.setText("Computer is thinking...");
                        setGridEnabled(false);
                        compTimer = new Timer(500, new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent evt) {
                                char computerMark = player2.isX() ? 'X' : 'O';
                                int identifier = player2.isSmartComputer()
                                        ? GameAnalyzer.makeSmartMove(board, computerMark)
                                        : GameAnalyzer.makeRandomMove(board);
                                GridSpace compButton = board.grid.get(identifier);
                                GridSpace.State compNewState = computerMark == 'X' ? GridSpace.State.X : GridSpace.State.O;
                                compButton.setCurrentState(compNewState);
                                compButton.setText(String.valueOf(computerMark));
                                board.updateGrid(identifier, compButton);
                                isPlayer1Turn = !isPlayer1Turn;
                                setGridEnabled(true);
                                if (GameAnalyzer.gameOver(board)) {
                                    applyGameOverResult();
                                } else {
                                    message.setText("It's " + player1.getName() + "'s turn.");
                                }
                                game.revalidate();
                                game.repaint();
                            }
                        });
                        compTimer.setRepeats(false);
                        compTimer.start();
                    }

                    game.update(game.getGraphics());
                }
                game.update(game.getGraphics());
            } else if (e.getSource() instanceof JAButton) {
                JAButton button = (JAButton) e.getSource();
                JAButton.Action buttonAction = button.getActionType();
                if (buttonAction == JAButton.Action.Restart) {
                    if (compTimer != null && compTimer.isRunning()) {
                        compTimer.stop();
                        setGridEnabled(true);
                    }
                    board.resetGrid();
                    if (isPlayer1Turn) {
                        message.setText("NEW GAME! It's " + player1.getName() + "'s turn.");
                    } else {
                        message.setText("NEW GAME! It's " + player2.getName() + "'s turn.");
                    }
                    if (isPVComp && !GameAnalyzer.gameOver(board) && !isPlayer1Turn) {
                        message.setText("Computer is thinking...");
                        setGridEnabled(false);
                        compTimer = new Timer(500, new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent evt) {
                                char computerMark = player2.isX() ? 'X' : 'O';
                                int identifier = player2.isSmartComputer()
                                        ? GameAnalyzer.makeSmartMove(board, computerMark)
                                        : GameAnalyzer.makeRandomMove(board);
                                GridSpace compButton = board.grid.get(identifier);
                                GridSpace.State compNewState = computerMark == 'X' ? GridSpace.State.X : GridSpace.State.O;
                                compButton.setCurrentState(compNewState);
                                compButton.setText(String.valueOf(computerMark));
                                board.updateGrid(identifier, compButton);
                                isPlayer1Turn = !isPlayer1Turn;
                                setGridEnabled(true);
                                message.setText("It's " + player1.getName() + "'s turn.");
                                game.revalidate();
                                game.repaint();
                            }
                        });
                        compTimer.setRepeats(false);
                        compTimer.start();
                    }
                    board.update(board.getGraphics());
                    game.update(game.getGraphics());
                } else if (buttonAction == JAButton.Action.Quit) {
                    game.dispatchEvent(new WindowEvent(game, WindowEvent.WINDOW_CLOSING));
                } else if (buttonAction == JAButton.Action.ResetPlayerStats) {
                    player1.resetStats();
                    player2.resetStats();
                    player1Wins.setText(player1.getNumWins() + " Wins");
                    player1Losses.setText(player1.getNumLosses() + " Losses");
                    player1Draws.setText(player1.getNumDraws() + " Draws");
                    player2Wins.setText(player2.getNumWins() + " Wins");
                    player2Losses.setText(player2.getNumLosses() + " Losses");
                    player2Draws.setText(player2.getNumDraws() + " Draws");
                } else if (buttonAction == JAButton.Action.ChangeGameConfig) {
                    if (compTimer != null && compTimer.isRunning()) compTimer.stop();
                    game.dispose();
                } else if (buttonAction == JAButton.Action.ChangeGameMode) {
                    if (compTimer != null && compTimer.isRunning()) compTimer.stop();
                    game.dispose();
                    startGame();
                }
                game.update(game.getGraphics());
            }
            game.update(game.getGraphics());
        }
    };

    public static void startGame() {
        int gameMode = -1;
        while (true) {
            int chooseGameMode = showStartMenu();
            if ((chooseGameMode == 2 && configPVP()) || (chooseGameMode == 1 && configPVComp()) ||
                    (chooseGameMode == 0 && configCompVComp())) {
                gameMode = chooseGameMode;
                break;
            } else if (chooseGameMode == -1) {
                return;
            }
        }
        switch (gameMode) {
            case 2: gamePVP(); break;
            case 1: gamePVComp(); break;
            case 0: gameCompVComp(); break;
        }
    }

    public static void main(String[] args) {
        startGame();
    }

    public static boolean configPVP() {
        isPVComp = false;
        String p1Name = JOptionPane.showInputDialog(null, "Enter Player 1's Name.", "",
                JOptionPane.QUESTION_MESSAGE, null, null, "Player 1").toString();
        if (p1Name == null) return false;

        String[] markOptions = {"O", "X"};
        int player1Mark = JOptionPane.showOptionDialog(null, "Choose Player 1's Mark.", "",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, markOptions, null);
        if (player1Mark == -1) return false;
        boolean player1IsX = (player1Mark == 1);

        String p2Name = JOptionPane.showInputDialog(null, "Enter Player 2's Name.", "",
                JOptionPane.QUESTION_MESSAGE, null, null, "Player 2").toString();
        if (p2Name == null) return false;

        String[] startOptions = {"Player 2", "Player 1"};
        int startingPlayer = JOptionPane.showOptionDialog(null, "Which Player Goes First?", "",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, startOptions, null);
        if (startingPlayer == -1) return false;
        boolean player1Starts = (startingPlayer == 1);

        isPlayer1Turn = player1Starts;
        player1 = new Player(p1Name, false, false, player1IsX, player1Starts);
        player2 = new Player(p2Name, false, false, !player1IsX, !player1Starts);
        return true;
    }

    public static boolean configPVComp() {
        isPVComp = true;
        String p1Name = JOptionPane.showInputDialog(null, "Enter Player 1's Name.", "",
                JOptionPane.QUESTION_MESSAGE, null, null, "Player 1").toString();
        if (p1Name == null) return false;

        String[] markOptions = {"O", "X"};
        int player1Mark = JOptionPane.showOptionDialog(null, "Choose Player's Mark.", "",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, markOptions, null);
        if (player1Mark == -1) return false;
        boolean player1IsX = (player1Mark == 1);

        String[] compOptions = {"Smart", "Normal"};
        int compLevel = JOptionPane.showOptionDialog(null, "Choose Computer's Level.", "",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, compOptions, null);
        if (compLevel == -1) return false;
        boolean isSmartComputer = (compLevel == 0);

        String[] startOptions = {"Computer", "Player"};
        int startingPlayer = JOptionPane.showOptionDialog(null, "Which Player Goes First?", "",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, startOptions, null);
        if (startingPlayer == -1) return false;
        boolean player1Starts = (startingPlayer == 1);

        isPlayer1Turn = player1Starts;
        player1 = new Player(p1Name, false, false, player1IsX, player1Starts);
        player2 = new Player("Computer", true, isSmartComputer, !player1IsX, !player1Starts);
        return true;
    }

    public static boolean configCompVComp() {
        boolean computer1IsX = Math.random() < 0.5;
        boolean computer1Starts = Math.random() < 0.5;
        player1 = new Player("Computer 1", true, false, computer1IsX, computer1Starts);
        player2 = new Player("Computer 2", true, false, !computer1IsX, !computer1Starts);
        return true;
    }

    public static void gamePVP() {
        game = new JFrame();
        Container content = game.getContentPane();
        content.setLayout(new BorderLayout());

        header = new JPanel();
        message = new JLabel(player1.starts()
                ? "It's " + player1.getName() + "'s turn."
                : "It's " + player2.getName() + "'s turn.");
        message.setFont(typicalFont);
        header.add(message);
        content.add(header, BorderLayout.NORTH);

        JAButton restart = new JAButton("Restart", JAButton.Action.Restart);
        JAButton gameConfigChange = new JAButton("Change Game Configuration", JAButton.Action.ChangeGameConfig);
        JAButton gameModeChange = new JAButton("Change Game Mode", JAButton.Action.ChangeGameMode);
        JAButton quitGame = new JAButton("Quit", JAButton.Action.Quit);
        JAButton resetPlayerStats = new JAButton("Reset Player Stats", JAButton.Action.ResetPlayerStats);
        restart.addActionListener(actionListener);
        gameConfigChange.addActionListener(actionListener);
        gameModeChange.addActionListener(actionListener);
        quitGame.addActionListener(actionListener);
        resetPlayerStats.addActionListener(actionListener);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(restart);
        buttonPanel.add(gameConfigChange);
        buttonPanel.add(gameModeChange);
        buttonPanel.add(resetPlayerStats);
        buttonPanel.add(quitGame);
        content.add(buttonPanel, BorderLayout.SOUTH);

        board = new Grid();
        board.setPreferredSize(new Dimension(300, 300));
        board.setMaximumSize(new Dimension(300, 300));
        for (GridSpace space : board.grid) space.addActionListener(actionListener);
        content.add(board, BorderLayout.CENTER);

        player1Stats = new JPanel(new GridLayout(0, 1));
        player1Name = new JLabel(player1.getName());
        player1Wins = new JLabel(player1.getNumWins() + " Wins");
        player1Losses = new JLabel(player1.getNumLosses() + " Losses");
        player1Draws = new JLabel(player1.getNumDraws() + " Draws");
        player1Name.setFont(typicalFont); player1Wins.setFont(typicalFont);
        player1Losses.setFont(typicalFont); player1Draws.setFont(typicalFont);
        player1Stats.add(player1Name); player1Stats.add(player1Wins);
        player1Stats.add(player1Losses); player1Stats.add(player1Draws);
        player1Stats.setAlignmentX(Component.LEFT_ALIGNMENT);
        player1Stats.setBorder(padding);
        player1Stats.setPreferredSize(new Dimension(150, 300));
        player1Stats.setMaximumSize(new Dimension(150, 300));

        player2Stats = new JPanel(new GridLayout(0, 1));
        player2Name = new JLabel(player2.getName());
        player2Wins = new JLabel(player2.getNumWins() + " Wins");
        player2Losses = new JLabel(player2.getNumLosses() + " Losses");
        player2Draws = new JLabel(player2.getNumDraws() + " Draws");
        player2Name.setFont(typicalFont); player2Wins.setFont(typicalFont);
        player2Losses.setFont(typicalFont); player2Draws.setFont(typicalFont);
        player2Stats.add(player2Name); player2Stats.add(player2Wins);
        player2Stats.add(player2Losses); player2Stats.add(player2Draws);
        player2Stats.setAlignmentX(Component.LEFT_ALIGNMENT);
        player2Stats.setBorder(padding);
        player2Stats.setPreferredSize(new Dimension(150, 300));
        player2Stats.setMaximumSize(new Dimension(150, 300));

        content.add(player1Stats, BorderLayout.WEST);
        content.add(player2Stats, BorderLayout.EAST);

        game.setTitle("PVP TicTacToe");
        game.setSize(900, 700);
        game.setResizable(false);
        game.setLocationRelativeTo(null);
        game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        game.setVisible(true);
        game.update(game.getGraphics());
    }

    public static void gamePVComp() {
        gamePVP(); // identical UI — reuse
    }

    public static void gameCompVComp() {
    }

    private static void applyGameOverResult() {
        char[][] charBoard = new char[3][3];
        for (int i = 0; i < 9; i++) {
            GridSpace gs = board.grid.get(i);
            char c = gs.getCurrentState() == GridSpace.State.X ? 'X'
                    : gs.getCurrentState() == GridSpace.State.O ? 'O' : ' ';
            charBoard[i / 3][i % 3] = c;
        }
        if (GameAnalyzer.isWinX(charBoard)) {
            Player winner = player1.isX() ? player1 : player2;
            Player loser = player1.isX() ? player2 : player1;
            winner.setNumWins(winner.getNumWins() + 1);
            loser.setNumLosses(loser.getNumLosses() + 1);
            refreshStatLabels();
            message.setText("Game over. " + winner.getName() + " won the game!");
        } else if (GameAnalyzer.isWinO(charBoard)) {
            Player winner = !player1.isX() ? player1 : player2;
            Player loser = !player1.isX() ? player2 : player1;
            winner.setNumWins(winner.getNumWins() + 1);
            loser.setNumLosses(loser.getNumLosses() + 1);
            refreshStatLabels();
            message.setText("Game over. " + winner.getName() + " won the game!");
        } else {
            player1.setNumDraws(player1.getNumDraws() + 1);
            player2.setNumDraws(player2.getNumDraws() + 1);
            refreshStatLabels();
            message.setText("Game over. It's a draw!");
        }
    }

    private static void refreshStatLabels() {
        player1Wins.setText(player1.getNumWins() + " Wins");
        player1Losses.setText(player1.getNumLosses() + " Losses");
        player1Draws.setText(player1.getNumDraws() + " Draws");
        player2Wins.setText(player2.getNumWins() + " Wins");
        player2Losses.setText(player2.getNumLosses() + " Losses");
        player2Draws.setText(player2.getNumDraws() + " Draws");
    }

    private static void setGridEnabled(boolean enabled) {
        for (GridSpace space : board.grid) space.setEnabled(enabled);
    }

    public static int showStartMenu() {
        String[] options = {"Computer vs. Computer Simulation", "Player vs. Computer", "Player vs. Player"};
        return JOptionPane.showOptionDialog(null,
                "Choose what game mode do you want to play in?", "TTT Start Menu",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, null);
    }
}
```

> **Note:** `gamePVComp()` now delegates to `gamePVP()` since the UI is identical. This intermediate state keeps the app working while the full MVC refactor is in progress.

- [ ] **Step 4: Compile to verify**

```bash
javac TTTGUI/src/*.java -d TTTGUI/out/production/TTTGUI
```
Expected: no errors.

- [ ] **Step 5: Run and verify basic gameplay still works**

```bash
java -cp TTTGUI/out/production/TTTGUI TicTacToe
```
Start a PVP game, make a few moves, confirm all buttons work (Restart, Quit, Reset Stats).

- [ ] **Step 6: Commit**

```bash
git add TTTGUI/src/JAButton.java TTTGUI/src/GridSpace.java TTTGUI/src/TicTacToe.java
git commit -m "refactor: move Action enum to JAButton; decouple auto-listener from constructor"
```

---

## Task 3: Fix GameAnalyzer — Remove TicTacToe.player2 Reference

**Files:**
- Modify: `TTTGUI/src/GameAnalyzer.java`

- [ ] **Step 1: Update `makeSmartMove` signature to accept `char computerMark`**

Change the method signature and remove the line `char computerMark = TicTacToe.player2.isX() ? 'X' : 'O';`. The caller now passes this value.

Replace the existing `makeSmartMove` method body in `GameAnalyzer.java`:

```java
public static int makeSmartMove(Grid board, char computerMark) {
    char[][] currCharGrid = getCharGrid(board);
    char opponentMark = (computerMark == 'X') ? 'O' : 'X';

    int winMove = findImmediateMove(currCharGrid, computerMark);
    if (winMove != -1) return winMove;

    int blockMove = findImmediateMove(currCharGrid, opponentMark);
    if (blockMove != -1) return blockMove;

    return MiniMax.getBestMove(currCharGrid, computerMark == 'X');
}
```

- [ ] **Step 2: Compile to verify**

```bash
javac TTTGUI/src/*.java -d TTTGUI/out/production/TTTGUI
```
Expected: no errors. (TicTacToe.java was already updated to call `makeSmartMove(board, computerMark)` with a local `computerMark` variable in Task 2.)

- [ ] **Step 3: Run and verify smart computer still plays correctly**

```bash
java -cp TTTGUI/out/production/TTTGUI TicTacToe
```
Start a Player vs. Computer (Smart) game. Verify the computer blocks winning moves and takes winning moves when available.

- [ ] **Step 4: Commit**

```bash
git add TTTGUI/src/GameAnalyzer.java
git commit -m "refactor: remove TicTacToe.player2 dependency from GameAnalyzer.makeSmartMove"
```

---

## Task 4: Create GameView

**Files:**
- Create: `TTTGUI/src/GameView.java`

- [ ] **Step 1: Create `GameView.java`**

```java
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class GameView {
    private JFrame frame;
    private JLabel message;
    private JLabel player1NameLabel, player1WinsLabel, player1LossesLabel, player1DrawsLabel;
    private JLabel player2NameLabel, player2WinsLabel, player2LossesLabel, player2DrawsLabel;
    private Grid board;
    private List<JAButton> actionButtons;
    private ActionListener gridListener;

    private static final Font typicalFont = new Font("Arial", Font.PLAIN, 20);
    private static final EmptyBorder padding = new EmptyBorder(10, 10, 10, 10);

    public GameView(Player p1, Player p2, String title) {
        frame = new JFrame(title);
        Container content = frame.getContentPane();
        content.setLayout(new BorderLayout());

        JPanel header = new JPanel();
        message = new JLabel("");
        message.setFont(typicalFont);
        header.add(message);
        content.add(header, BorderLayout.NORTH);

        actionButtons = new ArrayList<>();
        actionButtons.add(new JAButton("Restart", JAButton.Action.Restart));
        actionButtons.add(new JAButton("Change Game Configuration", JAButton.Action.ChangeGameConfig));
        actionButtons.add(new JAButton("Change Game Mode", JAButton.Action.ChangeGameMode));
        actionButtons.add(new JAButton("Reset Player Stats", JAButton.Action.ResetPlayerStats));
        actionButtons.add(new JAButton("Quit", JAButton.Action.Quit));

        JPanel buttonPanel = new JPanel();
        for (JAButton btn : actionButtons) buttonPanel.add(btn);
        content.add(buttonPanel, BorderLayout.SOUTH);

        board = new Grid();
        board.setPreferredSize(new Dimension(300, 300));
        board.setMaximumSize(new Dimension(300, 300));
        content.add(board, BorderLayout.CENTER);

        player1NameLabel = new JLabel(p1.getName());
        player1WinsLabel = new JLabel(p1.getNumWins() + " Wins");
        player1LossesLabel = new JLabel(p1.getNumLosses() + " Losses");
        player1DrawsLabel = new JLabel(p1.getNumDraws() + " Draws");
        JPanel p1Stats = buildStatsPanel(player1NameLabel, player1WinsLabel, player1LossesLabel, player1DrawsLabel);
        content.add(p1Stats, BorderLayout.WEST);

        player2NameLabel = new JLabel(p2.getName());
        player2WinsLabel = new JLabel(p2.getNumWins() + " Wins");
        player2LossesLabel = new JLabel(p2.getNumLosses() + " Losses");
        player2DrawsLabel = new JLabel(p2.getNumDraws() + " Draws");
        JPanel p2Stats = buildStatsPanel(player2NameLabel, player2WinsLabel, player2LossesLabel, player2DrawsLabel);
        content.add(p2Stats, BorderLayout.EAST);

        frame.setSize(900, 700);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private JPanel buildStatsPanel(JLabel name, JLabel wins, JLabel losses, JLabel draws) {
        JPanel panel = new JPanel(new GridLayout(0, 1));
        name.setFont(typicalFont);
        wins.setFont(typicalFont);
        losses.setFont(typicalFont);
        draws.setFont(typicalFont);
        panel.add(name); panel.add(wins); panel.add(losses); panel.add(draws);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setBorder(padding);
        panel.setPreferredSize(new Dimension(150, 300));
        panel.setMaximumSize(new Dimension(150, 300));
        return panel;
    }

    public void setMessage(String text) {
        message.setText(text);
    }

    public void updateCell(int index, char mark) {
        GridSpace space = board.grid.get(index);
        space.setText(String.valueOf(mark));
        space.setCurrentState(mark == 'X' ? GridSpace.State.X : GridSpace.State.O);
    }

    public void resetBoard() {
        board.resetGrid();
        if (gridListener != null) {
            for (GridSpace space : board.grid) {
                space.addActionListener(gridListener);
            }
        }
        frame.revalidate();
        frame.repaint();
    }

    public void setGridEnabled(boolean enabled) {
        for (GridSpace space : board.grid) space.setEnabled(enabled);
    }

    public void refreshStats(Player p1, Player p2) {
        player1NameLabel.setText(p1.getName());
        player1WinsLabel.setText(p1.getNumWins() + " Wins");
        player1LossesLabel.setText(p1.getNumLosses() + " Losses");
        player1DrawsLabel.setText(p1.getNumDraws() + " Draws");
        player2NameLabel.setText(p2.getName());
        player2WinsLabel.setText(p2.getNumWins() + " Wins");
        player2LossesLabel.setText(p2.getNumLosses() + " Losses");
        player2DrawsLabel.setText(p2.getNumDraws() + " Draws");
    }

    public void addGridListener(ActionListener l) {
        this.gridListener = l;
        for (GridSpace space : board.grid) space.addActionListener(l);
    }

    public List<JAButton> getActionButtons() {
        return actionButtons;
    }

    public Grid getGrid() {
        return board;
    }

    public void setVisible(boolean visible) {
        frame.setVisible(visible);
    }

    public void dispose() {
        frame.dispose();
    }

    public void refresh() {
        frame.revalidate();
        frame.repaint();
    }
}
```

- [ ] **Step 2: Compile to verify**

```bash
javac TTTGUI/src/*.java -d TTTGUI/out/production/TTTGUI
```
Expected: no errors. (GameView compiles alongside the existing TicTacToe — it is not yet wired into the app.)

- [ ] **Step 3: Commit**

```bash
git add TTTGUI/src/GameView.java
git commit -m "feat: add GameView with unified UI layout and targeted mutation methods"
```

---

## Task 5: Create GameController

**Files:**
- Create: `TTTGUI/src/GameController.java`

- [ ] **Step 1: Create `GameController.java`**

```java
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameController {
    private GameModel model;
    private GameView view;
    private Timer compTimer;
    private int gameMode;

    public void start() {
        while (true) {
            gameMode = showStartMenu();
            if (gameMode == -1) return;

            boolean configured = false;
            if (gameMode == 2) configured = configPVP();
            else if (gameMode == 1) configured = configPVComp();
            else if (gameMode == 0) configured = configCompVComp();

            if (configured) {
                launchGame();
                return;
            }
        }
    }

    private void launchGame() {
        String title = gameMode == 2 ? "PVP TicTacToe"
                : gameMode == 1 ? "Player vs Computer TicTacToe"
                : "Computer vs Computer TicTacToe";

        view = new GameView(model.getPlayer1(), model.getPlayer2(), title);
        ActionListener listener = buildActionListener();
        view.addGridListener(listener);
        for (JAButton btn : view.getActionButtons()) btn.addActionListener(listener);

        Player startPlayer = model.getCurrentPlayer();
        view.setMessage("It's " + startPlayer.getName() + "'s turn.");
        view.setVisible(true);

        if (model.isPVComp() && !model.isPlayer1Turn()) {
            triggerComputerMove();
        }
    }

    private ActionListener buildActionListener() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() instanceof GridSpace) {
                    handleGridClick((GridSpace) e.getSource());
                } else if (e.getSource() instanceof JAButton) {
                    handleButtonClick((JAButton) e.getSource());
                }
            }
        };
    }

    private void handleGridClick(GridSpace gs) {
        if (GameAnalyzer.gameOver(model.getBoard())) return;
        if (gs.getCurrentState() != GridSpace.State.EMPTY) {
            view.setMessage("That space is already taken! Try again.");
            return;
        }

        Player current = model.getCurrentPlayer();
        char mark = current.isX() ? 'X' : 'O';

        model.applyMove(gs.getIdentifier(), mark);
        view.updateCell(gs.getIdentifier(), mark);
        model.setPlayer1Turn(!model.isPlayer1Turn());

        if (GameAnalyzer.gameOver(model.getBoard())) {
            applyGameOverResult();
            return;
        }

        view.setMessage("It's " + model.getCurrentPlayer().getName() + "'s turn.");

        if (model.isPVComp()) {
            triggerComputerMove();
        }
    }

    private void handleButtonClick(JAButton btn) {
        switch (btn.getActionType()) {
            case Restart:
                if (compTimer != null && compTimer.isRunning()) {
                    compTimer.stop();
                    view.setGridEnabled(true);
                }
                model.resetBoard();
                view.resetBoard();
                view.setMessage("NEW GAME! It's " + model.getCurrentPlayer().getName() + "'s turn.");
                if (model.isPVComp() && !model.isPlayer1Turn()) {
                    triggerComputerMove();
                }
                break;

            case Quit:
                System.exit(0);
                break;

            case ResetPlayerStats:
                model.getPlayer1().resetStats();
                model.getPlayer2().resetStats();
                view.refreshStats(model.getPlayer1(), model.getPlayer2());
                break;

            case ChangeGameConfig:
                if (compTimer != null && compTimer.isRunning()) compTimer.stop();
                view.dispose();
                boolean configured = false;
                if (gameMode == 2) configured = configPVP();
                else if (gameMode == 1) configured = configPVComp();
                else if (gameMode == 0) configured = configCompVComp();
                if (configured) launchGame();
                else start();
                break;

            case ChangeGameMode:
                if (compTimer != null && compTimer.isRunning()) compTimer.stop();
                view.dispose();
                start();
                break;
        }
    }

    private void triggerComputerMove() {
        view.setMessage("Computer is thinking...");
        view.setGridEnabled(false);
        char computerMark = model.getPlayer2().isX() ? 'X' : 'O';
        compTimer = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int index = model.getPlayer2().isSmartComputer()
                        ? GameAnalyzer.makeSmartMove(view.getGrid(), computerMark)
                        : GameAnalyzer.makeRandomMove(view.getGrid());
                model.applyMove(index, computerMark);
                view.updateCell(index, computerMark);
                model.setPlayer1Turn(!model.isPlayer1Turn());
                view.setGridEnabled(true);
                if (GameAnalyzer.gameOver(model.getBoard())) {
                    applyGameOverResult();
                } else {
                    view.setMessage("It's " + model.getPlayer1().getName() + "'s turn.");
                }
                view.refresh();
            }
        });
        compTimer.setRepeats(false);
        compTimer.start();
    }

    private void applyGameOverResult() {
        char[][] board = model.getBoard();
        Player p1 = model.getPlayer1();
        Player p2 = model.getPlayer2();

        if (GameAnalyzer.isWinX(board)) {
            Player winner = p1.isX() ? p1 : p2;
            Player loser = p1.isX() ? p2 : p1;
            winner.setNumWins(winner.getNumWins() + 1);
            loser.setNumLosses(loser.getNumLosses() + 1);
            view.setMessage("Game over. " + winner.getName() + " won the game!");
        } else if (GameAnalyzer.isWinO(board)) {
            Player winner = !p1.isX() ? p1 : p2;
            Player loser = !p1.isX() ? p2 : p1;
            winner.setNumWins(winner.getNumWins() + 1);
            loser.setNumLosses(loser.getNumLosses() + 1);
            view.setMessage("Game over. " + winner.getName() + " won the game!");
        } else {
            p1.setNumDraws(p1.getNumDraws() + 1);
            p2.setNumDraws(p2.getNumDraws() + 1);
            view.setMessage("Game over. It's a draw!");
        }
        view.refreshStats(p1, p2);
    }

    private int showStartMenu() {
        String[] options = {"Computer vs. Computer Simulation", "Player vs. Computer", "Player vs. Player"};
        return JOptionPane.showOptionDialog(null,
                "Choose what game mode do you want to play in?", "TTT Start Menu",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, null);
    }

    private boolean configPVP() {
        String p1Name = JOptionPane.showInputDialog(null, "Enter Player 1's Name.", "",
                JOptionPane.QUESTION_MESSAGE, null, null, "Player 1").toString();
        if (p1Name == null) return false;

        String[] markOptions = {"O", "X"};
        int player1Mark = JOptionPane.showOptionDialog(null, "Choose Player 1's Mark.", "",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, markOptions, null);
        if (player1Mark == -1) return false;
        boolean player1IsX = (player1Mark == 1);

        String p2Name = JOptionPane.showInputDialog(null, "Enter Player 2's Name.", "",
                JOptionPane.QUESTION_MESSAGE, null, null, "Player 2").toString();
        if (p2Name == null) return false;

        String[] startOptions = {"Player 2", "Player 1"};
        int startingPlayer = JOptionPane.showOptionDialog(null, "Which Player Goes First?", "",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, startOptions, null);
        if (startingPlayer == -1) return false;
        boolean player1Starts = (startingPlayer == 1);

        Player p1 = new Player(p1Name, false, false, player1IsX, player1Starts);
        Player p2 = new Player(p2Name, false, false, !player1IsX, !player1Starts);
        model = new GameModel(p1, p2, player1Starts, false);
        return true;
    }

    private boolean configPVComp() {
        String p1Name = JOptionPane.showInputDialog(null, "Enter Player 1's Name.", "",
                JOptionPane.QUESTION_MESSAGE, null, null, "Player 1").toString();
        if (p1Name == null) return false;

        String[] markOptions = {"O", "X"};
        int player1Mark = JOptionPane.showOptionDialog(null, "Choose Player's Mark.", "",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, markOptions, null);
        if (player1Mark == -1) return false;
        boolean player1IsX = (player1Mark == 1);

        String[] compOptions = {"Smart", "Normal"};
        int compLevel = JOptionPane.showOptionDialog(null, "Choose Computer's Level.", "",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, compOptions, null);
        if (compLevel == -1) return false;
        boolean isSmartComputer = (compLevel == 0);

        String[] startOptions = {"Computer", "Player"};
        int startingPlayer = JOptionPane.showOptionDialog(null, "Which Player Goes First?", "",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, startOptions, null);
        if (startingPlayer == -1) return false;
        boolean player1Starts = (startingPlayer == 1);

        Player p1 = new Player(p1Name, false, false, player1IsX, player1Starts);
        Player p2 = new Player("Computer", true, isSmartComputer, !player1IsX, !player1Starts);
        model = new GameModel(p1, p2, player1Starts, true);
        return true;
    }

    private boolean configCompVComp() {
        boolean computer1IsX = Math.random() < 0.5;
        boolean computer1Starts = Math.random() < 0.5;
        Player p1 = new Player("Computer 1", true, false, computer1IsX, computer1Starts);
        Player p2 = new Player("Computer 2", true, false, !computer1IsX, !computer1Starts);
        model = new GameModel(p1, p2, computer1Starts, false);
        return true;
    }
}
```

- [ ] **Step 2: Compile to verify**

```bash
javac TTTGUI/src/*.java -d TTTGUI/out/production/TTTGUI
```
Expected: no errors. (GameController compiles alongside the existing TicTacToe — not yet wired in.)

- [ ] **Step 3: Commit**

```bash
git add TTTGUI/src/GameController.java
git commit -m "feat: add GameController with ActionListener, config dialogs, and AI coordination"
```

---

## Task 6: Strip TicTacToe to Entry Point; Wire GameController

**Files:**
- Modify: `TTTGUI/src/TicTacToe.java`

- [ ] **Step 1: Replace `TicTacToe.java` with the minimal entry point**

```java
public class TicTacToe {
    public static void main(String[] args) {
        new GameController().start();
    }
}
```

- [ ] **Step 2: Compile to verify**

```bash
javac TTTGUI/src/*.java -d TTTGUI/out/production/TTTGUI
```
Expected: no errors.

- [ ] **Step 3: Run the full app and verify all game modes**

```bash
java -cp TTTGUI/out/production/TTTGUI TicTacToe
```

Verify each scenario:
- **PVP:** Two players take turns, win/draw detection shows correct message, stats update, Restart clears the board, Reset Player Stats zeroes all counters, Quit exits, Change Game Mode returns to start menu, Change Game Configuration re-runs PVP config.
- **Player vs Computer (Smart):** Computer takes winning move when available; blocks player winning move; after restart where computer goes first, computer moves immediately.
- **Player vs Computer (Normal):** Computer makes random moves.
- **Start menu cancel:** Closing any dialog during setup loops back gracefully without crashing.

- [ ] **Step 4: Commit**

```bash
git add TTTGUI/src/TicTacToe.java
git commit -m "refactor: strip TicTacToe to entry point; MVC wired through GameController"
```

---

## Self-Review

**Spec coverage:**
- ✅ GameModel owns board, players, turn, isPVComp — Task 1
- ✅ GameView owns all UI with targeted mutation methods — Task 4
- ✅ GameController owns ActionListener and coordinates M↔V — Task 5
- ✅ AI triggers through controller (`triggerComputerMove`) — Task 5
- ✅ Action enum moved to JAButton — Task 2
- ✅ JAButton auto-listener removed — Task 2
- ✅ GridSpace references JAButton.Action — Task 2
- ✅ GameAnalyzer.makeSmartMove takes computerMark param — Task 3
- ✅ TicTacToe becomes entry point only — Task 6
- ✅ Single unified GameView for all modes — Tasks 4/5
- ✅ resetBoard re-wires gridListener to new GridSpace instances — Task 4 (GameView.resetBoard)
- ✅ gameCompVComp remains a stub — configCompVComp creates model but launchGame shows empty UI (no game logic)

**Type consistency check:**
- `model.getBoard()` returns `char[][]` — used correctly in `GameAnalyzer.isWinX(char[][])`, `isWinO(char[][])`, `gameOver(char[][])`
- `GameAnalyzer.makeSmartMove(Grid, char)` — called with `view.getGrid()` and `computerMark` — consistent
- `view.updateCell(int, char)` — called with index (0–8) and mark ('X'/'O') — consistent with `model.applyMove(int, char)`
- `JAButton.Action` enum values match all switch/comparison sites across GameController, GameView, GridSpace
