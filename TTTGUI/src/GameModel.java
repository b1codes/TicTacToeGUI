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
