package ai;

import view.Grid;
import view.GridSpace;

import java.util.ArrayList;
import java.util.Random;

public class GameAnalyzer {

    private static char[][] getCharGrid(Grid board) {
        ArrayList<GridSpace> boardList = board.grid;
        GridSpace[][] currGrid = new GridSpace[][] { { boardList.get(0), boardList.get(1), boardList.get(2) },
                { boardList.get(3), boardList.get(4), boardList.get(5) },
                { boardList.get(6), boardList.get(7), boardList.get(8) } };
        char[][] currGridChar = new char[3][3];
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                switch (currGrid[r][c].getCurrentState()) {
                    case EMPTY -> {
                        currGridChar[r][c] = ' ';
                    }
                    case X -> currGridChar[r][c] = 'X';
                    case O -> currGridChar[r][c] = 'O';
                }
            }
        }

        return currGridChar;
    }

    public static boolean isWinX(char[][] board) {
        // Check Rows
        for (int r = 0; r < 3; r++) {
            if (board[r][0] == 'X' && board[r][1] == 'X' && board[r][2] == 'X')
                return true;
        }
        // Check Columns
        for (int c = 0; c < 3; c++) {
            if (board[0][c] == 'X' && board[1][c] == 'X' && board[2][c] == 'X')
                return true;
        }
        // Check Diagonals
        if (board[0][0] == 'X' && board[1][1] == 'X' && board[2][2] == 'X')
            return true;
        if (board[0][2] == 'X' && board[1][1] == 'X' && board[2][0] == 'X')
            return true;
        return false;
    }

    public static boolean isWinO(char[][] board) {
        // Check Rows
        for (int r = 0; r < 3; r++) {
            if (board[r][0] == 'O' && board[r][1] == 'O' && board[r][2] == 'O')
                return true;
        }
        // Check Columns
        for (int c = 0; c < 3; c++) {
            if (board[0][c] == 'O' && board[1][c] == 'O' && board[2][c] == 'O')
                return true;
        }
        // Check Diagonals
        if (board[0][0] == 'O' && board[1][1] == 'O' && board[2][2] == 'O')
            return true;
        if (board[0][2] == 'O' && board[1][1] == 'O' && board[2][0] == 'O')
            return true;
        return false;
    }

    public static boolean isDraw(char[][] board) {
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                if (board[r][c] == ' ')
                    return false; // Found an empty spot, not a draw
            }
        }
        return !isWinX(board) && !isWinO(board);
    }

    public static boolean gameOver(char[][] board) {
        return isWinX(board) || isWinO(board) || isDraw(board);
    }

    public static boolean isDraw(Grid board) {
        boolean allSpacesTaken = true;
        ArrayList<GridSpace> boardList = board.grid;
        GridSpace[][] currGrid = new GridSpace[][] { { boardList.get(0), boardList.get(1), boardList.get(2) },
                { boardList.get(3), boardList.get(4), boardList.get(5) },
                { boardList.get(6), boardList.get(7), boardList.get(8) } };
        char[][] currGridChar = new char[3][3];
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                switch (currGrid[r][c].getCurrentState()) {
                    case EMPTY -> {
                        currGridChar[r][c] = ' ';
                        allSpacesTaken = false;
                    }
                    case X -> currGridChar[r][c] = 'X';
                    case O -> currGridChar[r][c] = 'O';
                }
            }
        }

        return allSpacesTaken && !isWinO(board) && !isWinX(board);
    }

    public static boolean gameOver(Grid board) {
        return isWinO(board) || isWinX(board) || isDraw(board);
    }

    public static boolean isWinO(Grid board) {
        ArrayList<GridSpace> boardList = board.grid;
        GridSpace[][] currGrid = new GridSpace[][] { { boardList.get(0), boardList.get(1), boardList.get(2) },
                { boardList.get(3), boardList.get(4), boardList.get(5) },
                { boardList.get(6), boardList.get(7), boardList.get(8) } };
        char[][] currGridChar = new char[3][3];
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                switch (currGrid[r][c].getCurrentState()) {
                    case EMPTY -> currGridChar[r][c] = ' ';
                    case X -> currGridChar[r][c] = 'X';
                    case O -> currGridChar[r][c] = 'O';
                }
            }
        }

        if ((currGridChar[0][0] == 'O') && (currGridChar[0][0] == currGridChar[0][1])
                && (currGridChar[0][0] == currGridChar[0][2])) {
            return true;
        } else if ((currGridChar[1][0] == 'O') && (currGridChar[1][0] == currGridChar[1][1])
                && (currGridChar[1][0] == currGridChar[1][2])) {
            return true;
        } else if ((currGridChar[2][0] == 'O') && (currGridChar[2][0] == currGridChar[2][1])
                && (currGridChar[2][0] == currGridChar[2][2])) {
            return true;
        } else if ((currGridChar[0][0] == 'O') && (currGridChar[0][0] == currGridChar[1][0])
                && (currGridChar[0][0] == currGridChar[2][0])) {
            return true;
        } else if ((currGridChar[0][1] == 'O') && (currGridChar[0][1] == currGridChar[1][1])
                && (currGridChar[0][1] == currGridChar[2][1])) {
            return true;
        } else if ((currGridChar[0][2] == 'O') && (currGridChar[0][2] == currGridChar[1][2])
                && (currGridChar[0][2] == currGridChar[2][2])) {
            return true;
        } else if ((currGridChar[0][0] == 'O') && (currGridChar[0][0] == currGridChar[1][1])
                && (currGridChar[0][0] == currGridChar[2][2])) {
            return true;
        } else if ((currGridChar[2][0] == 'O') && (currGridChar[2][0] == currGridChar[1][1])
                && (currGridChar[2][0] == currGridChar[0][2])) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isWinX(Grid board) {
        ArrayList<GridSpace> boardList = board.grid;
        GridSpace[][] currGrid = new GridSpace[][] { { boardList.get(0), boardList.get(1), boardList.get(2) },
                { boardList.get(3), boardList.get(4), boardList.get(5) },
                { boardList.get(6), boardList.get(7), boardList.get(8) } };
        char[][] currGridChar = new char[3][3];
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                switch (currGrid[r][c].getCurrentState()) {
                    case EMPTY -> currGridChar[r][c] = ' ';
                    case X -> currGridChar[r][c] = 'X';
                    case O -> currGridChar[r][c] = 'O';
                }
            }
        }

        if ((currGridChar[0][0] == 'X') && (currGridChar[0][0] == currGridChar[0][1])
                && (currGridChar[0][0] == currGridChar[0][2])) {
            return true;
        } else if ((currGridChar[1][0] == 'X') && (currGridChar[1][0] == currGridChar[1][1])
                && (currGridChar[1][0] == currGridChar[1][2])) {
            return true;
        } else if ((currGridChar[2][0] == 'X') && (currGridChar[2][0] == currGridChar[2][1])
                && (currGridChar[2][0] == currGridChar[2][2])) {
            return true;
        } else if ((currGridChar[0][0] == 'X') && (currGridChar[0][0] == currGridChar[1][0])
                && (currGridChar[0][0] == currGridChar[2][0])) {
            return true;
        } else if ((currGridChar[0][1] == 'X') && (currGridChar[0][1] == currGridChar[1][1])
                && (currGridChar[0][1] == currGridChar[2][1])) {
            return true;
        } else if ((currGridChar[0][2] == 'X') && (currGridChar[0][2] == currGridChar[1][2])
                && (currGridChar[0][2] == currGridChar[2][2])) {
            return true;
        } else if ((currGridChar[0][0] == 'X') && (currGridChar[0][0] == currGridChar[1][1])
                && (currGridChar[0][0] == currGridChar[2][2])) {
            return true;
        } else if ((currGridChar[2][0] == 'X') && (currGridChar[2][0] == currGridChar[1][1])
                && (currGridChar[2][0] == currGridChar[0][2])) {
            return true;
        } else {
            return false;
        }
    }

    // Returns the 0-8 index of an immediate winning cell for `player`, or -1 if none.
    private static int findImmediateMove(char[][] board, char player) {
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                if (board[r][c] == ' ') {
                    board[r][c] = player;
                    boolean wins = (player == 'X') ? isWinX(board) : isWinO(board);
                    board[r][c] = ' ';
                    if (wins) return (r * 3) + c;
                }
            }
        }
        return -1;
    }

    public static int makeSmartMove(Grid board, char computerMark) {
        char[][] currCharGrid = getCharGrid(board);
        char opponentMark = (computerMark == 'X') ? 'O' : 'X';

        // Take an immediate win if available.
        int winMove = findImmediateMove(currCharGrid, computerMark);
        if (winMove != -1) return winMove;

        // Block an immediate opponent win.
        int blockMove = findImmediateMove(currCharGrid, opponentMark);
        if (blockMove != -1) return blockMove;

        // Fall back to full minimax search.
        return MiniMax.getBestMove(currCharGrid, computerMark == 'X');
    }

    public static int makeRandomMove(Grid board) {
        ArrayList<Integer> emptySpaces = getEmptySpaces(board);
        int rnd = new Random().nextInt(emptySpaces.size());
        return emptySpaces.get(rnd);
    }

    private static ArrayList<Integer> getEmptySpaces(Grid board) {
        ArrayList<Integer> emptySpaces = new ArrayList<Integer>();
        for (int i = 0; i < 9; i++) {
            if (board.grid.get(i).getCurrentState() == GridSpace.State.EMPTY) {
                emptySpaces.add(board.grid.get(i).getIdentifier());
            }
        }
        return emptySpaces;
    }
}
