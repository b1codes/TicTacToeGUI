package ai;

// MiniMax algorithm — inspired by github.com/DavidHurst/MiniMax-TicTacToe-Java
public class MiniMax {

    // Scores for Minimax
    private static final int WIN_SCORE = 10;
    private static final int LOSE_SCORE = -10;
    private static final int DRAW_SCORE = 0;

    /**
     * Calculates the best move for the given player.
     *
     * @param board   The current game state as characters
     * @param isXTurn True if the computer is playing as 'X'
     * @return The 0-8 index of the best move
     */
    public static int getBestMove(char[][] board, boolean isXTurn) {
        int bestScore = isXTurn ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        int bestMove = -1;

        // Loop through all cells
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                // If cell is empty
                if (board[r][c] == ' ') {
                    // Make hypothetical move
                    board[r][c] = isXTurn ? 'X' : 'O';

                    // Evaluate this move
                    int score = minimax(board, 0, !isXTurn);

                    // Undo move (backtrack)
                    board[r][c] = ' ';

                    // Update best score based on maximizing or minimizing player
                    if (isXTurn) { // Maximize for X
                        if (score > bestScore) {
                            bestScore = score;
                            bestMove = (r * 3) + c;
                        }
                    } else { // Minimize for O
                        if (score < bestScore) {
                            bestScore = score;
                            bestMove = (r * 3) + c;
                        }
                    }
                }
            }
        }
        return bestMove;
    }

    private static int minimax(char[][] board, int depth, boolean isMax) {
        // 1. Check for terminal states (Win/Loss/Draw)
        if (GameAnalyzer.isWinX(board))
            return WIN_SCORE - depth; // Prefer fast wins
        if (GameAnalyzer.isWinO(board))
            return LOSE_SCORE + depth; // Prefer slow losses
        if (GameAnalyzer.isDraw(board))
            return DRAW_SCORE;

        // 2. Recursive step
        if (isMax) { // Maximizing Player (X)
            int bestScore = Integer.MIN_VALUE;
            for (int r = 0; r < 3; r++) {
                for (int c = 0; c < 3; c++) {
                    if (board[r][c] == ' ') {
                        board[r][c] = 'X';
                        bestScore = Math.max(bestScore, minimax(board, depth + 1, false));
                        board[r][c] = ' ';
                    }
                }
            }
            return bestScore;
        } else { // Minimizing Player (O)
            int bestScore = Integer.MAX_VALUE;
            for (int r = 0; r < 3; r++) {
                for (int c = 0; c < 3; c++) {
                    if (board[r][c] == ' ') {
                        board[r][c] = 'O';
                        bestScore = Math.min(bestScore, minimax(board, depth + 1, true));
                        board[r][c] = ' ';
                    }
                }
            }
            return bestScore;
        }
    }
}
