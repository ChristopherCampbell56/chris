public class Move {
    public int fromRow;
    public int fromCol;
    public int toRow;
    public int toCol;

    public PieceInstance movedPiece;
    public PieceInstance capturedPiece = null;

    public Move(int fromRow, int fromCol, int toRow, int toCol, PieceInstance movedPiece) {
        this.fromRow = fromRow;
        this.fromCol = fromCol;
        this.toRow = toRow;
        this.toCol = toCol;
        this.movedPiece = movedPiece;
    }

    public void setCapturedPiece(PieceInstance capturedPiece) {
        this.capturedPiece = capturedPiece;
    }
}
