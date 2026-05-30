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

    public enum Action {
        ChangeGridSpace, Restart, ChangeGameConfig, ChangeGameMode, Quit, ResetPlayerStats
    }

    public static ActionListener actionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() instanceof GridSpace) {
                GridSpace gridButton = (GridSpace) e.getSource();
                int gridIdentifier = gridButton.getIdentifier();
                Action gridButtonAction = gridButton.getActionType();
                if (!GameAnalyzer.gameOver(board) && gridButtonAction == Action.ChangeGridSpace) {
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
                                int identifier = player2.isSmartComputer()
                                        ? GameAnalyzer.makeSmartMove(board)
                                        : GameAnalyzer.makeRandomMove(board);
                                GridSpace compButton = board.grid.get(identifier);
                                String compMark = player2.isX() ? "X" : "O";
                                GridSpace.State compNewState = compMark.equals("X") ? GridSpace.State.X : GridSpace.State.O;
                                compButton.setCurrentState(compNewState);
                                compButton.setText(compMark);
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
                Action buttonAction = button.getActionType();
                if (buttonAction == Action.Restart) {
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
                                int identifier = player2.isSmartComputer()
                                        ? GameAnalyzer.makeSmartMove(board)
                                        : GameAnalyzer.makeRandomMove(board);
                                GridSpace compButton = board.grid.get(identifier);
                                String compMark = player2.isX() ? "X" : "O";
                                GridSpace.State compNewState = compMark.equals("X") ? GridSpace.State.X : GridSpace.State.O;
                                compButton.setCurrentState(compNewState);
                                compButton.setText(compMark);
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
                } else if (buttonAction == Action.Quit) {
                    game.dispatchEvent(new WindowEvent(game, WindowEvent.WINDOW_CLOSING));
                } else if (buttonAction == Action.ResetPlayerStats) {
                    player1.resetStats();
                    player2.resetStats();
                    player1Wins.setText(player1.getNumWins() + " Wins");
                    player1Losses.setText(player1.getNumLosses() + " Losses");
                    player1Draws.setText(player1.getNumDraws() + " Draws");
                    player2Wins.setText(player2.getNumWins() + " Wins");
                    player2Losses.setText(player2.getNumLosses() + " Losses");
                    player2Draws.setText(player2.getNumDraws() + " Draws");
                } else if (buttonAction == Action.ChangeGameConfig) {
                    if (compTimer != null && compTimer.isRunning()) compTimer.stop();
                    game.dispose();
                } else if (buttonAction == Action.ChangeGameMode) {
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
            case 2:
                gamePVP();
                break;
            case 1:
                gamePVComp();
                break;
            case 0:
                gameCompVComp();
                break;
        }
    }

    public static void main(String[] args) {
        startGame();
    }

    public static boolean configPVP() {
        isPVComp = false;
        String player1Name = JOptionPane.showInputDialog(null, "Enter Player 1's Name.", "",
                JOptionPane.QUESTION_MESSAGE, null,null,"Player 1").toString();
        if (player1Name == null) return false;

        String[] markOptions = { "O", "X" };
        boolean player1IsX;
        int player1Mark = JOptionPane.showOptionDialog(null, "Choose Player 1's Mark.", "",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, markOptions, null);
        if (player1Mark == 1) {
            player1IsX = true;
        } else if (player1Mark == 0) {
            player1IsX = false;
        } else {
            return false;
        }

        String player2Name = JOptionPane.showInputDialog(null, "Enter Player 2's Name.", "",
                JOptionPane.QUESTION_MESSAGE,null,null,"Player 2").toString();
        if (player1Name == null) return false;

        String[] startOptions = { "Player 2", "Player 1" };
        boolean player1Starts;
        int startingPlayer = JOptionPane.showOptionDialog(null, "Which Player Goes First?", "",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, startOptions, null);
        if (startingPlayer == 0) {
            player1Starts = false;
        } else if (startingPlayer == 1) {
            player1Starts = true;
        } else {
            return false;
        }
        isPlayer1Turn = player1Starts;
        player1 = new Player(player1Name, false, false, player1IsX, player1Starts);
        player2 = new Player(player2Name, false, false, !player1IsX, !player1Starts);

        return true;
    }

    public static boolean configPVComp() {
        isPVComp = true;
        String player1Name = JOptionPane.showInputDialog(null, "Enter Player 1's Name.", "",
                JOptionPane.QUESTION_MESSAGE, null,null,"Player 1").toString();
        if (player1Name == null) return false;

        String[] markOptions = { "O", "X" };
        boolean player1IsX;
        int player1Mark = JOptionPane.showOptionDialog(null, "Choose Player's Mark.", "",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, markOptions, null);
        if (player1Mark == 0) {
            player1IsX = false;
        } else if (player1Mark == 1) {
            player1IsX = true;
        } else {
            return false;
        }

        String[] compOptions = {"Smart", "Normal"};
        boolean isSmartComputer;
        int compLevel = JOptionPane.showOptionDialog(null, "Choose Computer's Level.", "",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, compOptions, null);
        System.out.println(compLevel);
        if (compLevel == 0) {
            isSmartComputer = true;
        } else if (compLevel == 1) {
            isSmartComputer = false;
        } else  {
            return false;
        }

        String[] startOptions = { "Computer", "Player" };
        boolean player1Starts;
        int startingPlayer = JOptionPane.showOptionDialog(null, "Which Player Goes First?", "",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, startOptions, null);
        if (startingPlayer == 0) {
            player1Starts = false;
        } else if (startingPlayer == 1) {
            player1Starts = true;
        } else {
            return false;
        }

        isPlayer1Turn = player1Starts;
        player1 = new Player(player1Name, false, false, player1IsX, player1Starts);
        player2 = new Player("Computer", true, isSmartComputer, !player1IsX, !player1Starts);

        return true;
    }

    public static boolean configCompVComp() {

        int coinFlip1 = (int)(Math.random() + 0.5);
        int coinFlip2 = (int)(Math.random() + 0.5);
        boolean computer1IsX;
        boolean computer1Starts;

        if (coinFlip1 == 0) {
            computer1IsX = true;
        } else {
            computer1IsX = false;
        }

        if (coinFlip2 == 0) {
            computer1Starts = true;
        } else {
            computer1Starts = false;
        }

        if (coinFlip1 == 0) {

        }

        player1 = new Player("Computer 1", true, false, computer1IsX, computer1Starts);
        player2 = new Player("Computer 2", true, false, !computer1IsX, !computer1Starts);

        return true;
    }

    public static void gamePVP() {
        game = new JFrame();
        Container content = game.getContentPane();
        content.setLayout(new BorderLayout());

        header = new JPanel();
        if (player1.starts()) {
            message = new JLabel("It's " + player1.getName() + "'s turn.");
        } else {
            message = new JLabel("It's " + player2.getName() + "'s turn.");
        }
        message.setFont(typicalFont);
        header.add(message);
        content.add(header, BorderLayout.NORTH);

        JAButton restart = new JAButton("Restart", Action.Restart);
        JAButton gameConfigChange = new JAButton("Change Game Configuration", Action.ChangeGameConfig);
        JAButton gameModeChange = new JAButton("Change Game Mode", Action.ChangeGameMode);
        JAButton quitGame = new JAButton("Quit", Action.Quit);
        JAButton resetPlayerStats = new JAButton("Reset Player Stats", Action.ResetPlayerStats);
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
        board.setPreferredSize(new Dimension(300,300));
        board.setMaximumSize(new Dimension(300,300));
        content.add(board, BorderLayout.CENTER);

        player1Stats = new JPanel(new GridLayout(0,1));
        player1Name = new JLabel(player1.getName());
        player1Wins = new JLabel(player1.getNumWins() + " Wins");
        player1Losses = new JLabel(player1.getNumLosses() + " Losses");
        player1Draws = new JLabel(player1.getNumDraws() + " Draws");
        player1Name.setFont(typicalFont);
        player1Wins.setFont(typicalFont);
        player1Losses.setFont(typicalFont);
        player1Draws.setFont(typicalFont);
        player1Stats.add(player1Name);
        player1Stats.add(player1Wins);
        player1Stats.add(player1Losses);
        player1Stats.add(player1Draws);
        player1Stats.setAlignmentX(Component.LEFT_ALIGNMENT);
        player1Stats.setBorder(padding);
        player1Stats.setPreferredSize(new Dimension(150, 300));
        player1Stats.setMaximumSize(new Dimension(150, 300));

        player2Stats = new JPanel(new GridLayout(0,1));
        player2Name = new JLabel(player2.getName());
        player2Wins = new JLabel(player2.getNumWins() + " Wins");
        player2Losses = new JLabel(player2.getNumLosses() + " Losses");
        player2Draws = new JLabel(player2.getNumDraws() + " Draws");
        player2Name.setFont(typicalFont);
        player2Wins.setFont(typicalFont);
        player2Losses.setFont(typicalFont);
        player2Draws.setFont(typicalFont);
        player2Stats.add(player2Name);
        player2Stats.add(player2Wins);
        player2Stats.add(player2Losses);
        player2Stats.add(player2Draws);
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
        game = new JFrame();
        Container content = game.getContentPane();
        content.setLayout(new BorderLayout());

        header = new JPanel();
        if (player1.starts()) {
            message = new JLabel("It's " + player1.getName() + "'s turn.");
        } else {
            message = new JLabel("It's " + player2.getName() + "'s turn.");
        }
        message.setFont(typicalFont);
        header.add(message);
        content.add(header, BorderLayout.NORTH);

        JAButton restart = new JAButton("Restart", Action.Restart);
        JAButton gameConfigChange = new JAButton("Change Game Configuration", Action.ChangeGameConfig);
        JAButton gameModeChange = new JAButton("Change Game Mode", Action.ChangeGameMode);
        JAButton quitGame = new JAButton("Quit", Action.Quit);
        JAButton resetPlayerStats = new JAButton("Reset Player Stats", Action.ResetPlayerStats);
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
        board.setPreferredSize(new Dimension(300,300));
        board.setMaximumSize(new Dimension(300,300));
        content.add(board, BorderLayout.CENTER);

        player1Stats = new JPanel(new GridLayout(0,1));
        player1Name = new JLabel(player1.getName());
        player1Wins = new JLabel(player1.getNumWins() + " Wins");
        player1Losses = new JLabel(player1.getNumLosses() + " Losses");
        player1Draws = new JLabel(player1.getNumDraws() + " Draws");
        player1Name.setFont(typicalFont);
        player1Wins.setFont(typicalFont);
        player1Losses.setFont(typicalFont);
        player1Draws.setFont(typicalFont);
        player1Stats.add(player1Name);
        player1Stats.add(player1Wins);
        player1Stats.add(player1Losses);
        player1Stats.add(player1Draws);
        player1Stats.setAlignmentX(Component.LEFT_ALIGNMENT);
        player1Stats.setBorder(padding);
        player1Stats.setPreferredSize(new Dimension(150, 300));
        player1Stats.setMaximumSize(new Dimension(150, 300));

        player2Stats = new JPanel(new GridLayout(0,1));
        player2Name = new JLabel(player2.getName());
        player2Wins = new JLabel(player2.getNumWins() + " Wins");
        player2Losses = new JLabel(player2.getNumLosses() + " Losses");
        player2Draws = new JLabel(player2.getNumDraws() + " Draws");
        player2Name.setFont(typicalFont);
        player2Wins.setFont(typicalFont);
        player2Losses.setFont(typicalFont);
        player2Draws.setFont(typicalFont);
        player2Stats.add(player2Name);
        player2Stats.add(player2Wins);
        player2Stats.add(player2Losses);
        player2Stats.add(player2Draws);
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

    public static void gameCompVComp() {
    }

    private static void applyGameOverResult() {
        if (GameAnalyzer.isWinX(board)) {
            String playerName;
            if (player1.isX()) {
                playerName = player1.getName();
                player1.setNumWins(player1.getNumWins() + 1);
                player2.setNumLosses(player2.getNumLosses() + 1);
                player1Wins.setText(player1.getNumWins() + " Wins");
                player2Losses.setText(player2.getNumLosses() + " Losses");
            } else {
                playerName = player2.getName();
                player2.setNumWins(player2.getNumWins() + 1);
                player1.setNumLosses(player1.getNumLosses() + 1);
                player2Wins.setText(player2.getNumWins() + " Wins");
                player1Losses.setText(player1.getNumLosses() + " Losses");
            }
            message.setText("Game over. " + playerName + " won the game!");
        } else if (GameAnalyzer.isWinO(board)) {
            String playerName;
            if (!player1.isX()) {
                playerName = player1.getName();
                player1.setNumWins(player1.getNumWins() + 1);
                player2.setNumLosses(player2.getNumLosses() + 1);
                player1Wins.setText(player1.getNumWins() + " Wins");
                player2Losses.setText(player2.getNumLosses() + " Losses");
            } else {
                playerName = player2.getName();
                player2.setNumWins(player2.getNumWins() + 1);
                player1.setNumLosses(player1.getNumLosses() + 1);
                player2Wins.setText(player2.getNumWins() + " Wins");
                player1Losses.setText(player1.getNumLosses() + " Losses");
            }
            message.setText("Game over. " + playerName + " won the game!");
        } else {
            message.setText("Game over. It's a draw!");
            player1.setNumDraws(player1.getNumDraws() + 1);
            player2.setNumDraws(player2.getNumDraws() + 1);
            player1Draws.setText(player1.getNumDraws() + " Draws");
            player2Draws.setText(player2.getNumDraws() + " Draws");
        }
    }

    private static void setGridEnabled(boolean enabled) {
        for (GridSpace space : board.grid) {
            space.setEnabled(enabled);
        }
    }

    public static int showStartMenu() {
        String[] options = {"Computer vs. Computer Simulation", "Player vs. Computer", "Player vs. Player"};
        int result = JOptionPane.showOptionDialog(null, "Choose what game mode do you want " +
                        "to play in?", "TTT Start Menu", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, options, null);
        return result;
    }

}
