package controller;

import ai.GameAnalyzer;
import model.GameModel;
import model.Player;
import view.GameView;
import view.GridSpace;
import view.JAButton;

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

            default:
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
                    view.setMessage("It's " + model.getCurrentPlayer().getName() + "'s turn.");
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
        Object raw1 = JOptionPane.showInputDialog(null, "Enter Player 1's Name.", "",
                JOptionPane.QUESTION_MESSAGE, null, null, "Player 1");
        if (raw1 == null) return false;
        String p1Name = raw1.toString();

        String[] markOptions = {"O", "X"};
        int player1Mark = JOptionPane.showOptionDialog(null, "Choose Player 1's Mark.", "",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, markOptions, null);
        if (player1Mark == -1) return false;
        boolean player1IsX = (player1Mark == 1);

        Object raw2 = JOptionPane.showInputDialog(null, "Enter Player 2's Name.", "",
                JOptionPane.QUESTION_MESSAGE, null, null, "Player 2");
        if (raw2 == null) return false;
        String p2Name = raw2.toString();

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
        Object raw1 = JOptionPane.showInputDialog(null, "Enter Player 1's Name.", "",
                JOptionPane.QUESTION_MESSAGE, null, null, "Player 1");
        if (raw1 == null) return false;
        String p1Name = raw1.toString();

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
        JOptionPane.showMessageDialog(null, "Computer vs. Computer mode is not yet implemented.",
                "Not Yet Implemented", JOptionPane.INFORMATION_MESSAGE);
        return false;
    }
}
