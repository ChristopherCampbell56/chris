public class PieceInstance {
    private Piece piece;
    private int row, col;

    public PieceInstance(Piece piece, int row, int col) {
        this.piece = piece;
        this.row = row;
        this.col = col;
    }

    public Piece getPiece() {
        return this.piece;
    }

    public int getRow() {
        return this.row;
    }

    public int getCol() {
        return this.col;
    }

    public void setPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public boolean isLegalMove(PieceInstance[][] board, int toRow, int toCol) {
        return this.piece.isLegalMove(board, row, col, toRow, toCol);
    }
}
