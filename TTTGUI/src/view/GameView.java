package view;

import model.Player;

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
