import java.util.ArrayDeque;
import java.util.Deque;

public class Game {
    public ChessBoard chessBoard;
    public boolean isWhiteMove = true;
    private int rights = 0b1111;
    public boolean kingSelected = false;
    public boolean rookSelected = false;
    public Deque<Move> undoStack = new ArrayDeque<Move>();
    public Deque<Move> redoStack = new ArrayDeque<Move>();

    public int getCastlingRights() {
        return this.rights;
    }

    public void setCastlingRights(int rights) {
        this.rights = rights;
    }

    public void setChessBoard(ChessBoard chessBoard) {
        this.chessBoard = chessBoard;
    }

    public boolean isWhiteMove() {
        return this.isWhiteMove;
    }

    public void toggleToMove() {
        this.isWhiteMove = !this.isWhiteMove();
    }
    public boolean isReplay() {
        return !this.redoStack.isEmpty();
    }

    public void undoMove() {
        if (!this.undoStack.isEmpty()) {
            Move undoneMove = this.undoStack.pop();
            this.chessBoard.movePiece(undoneMove.toRow, undoneMove.toCol, undoneMove.fromRow, undoneMove.fromCol);
            if (this.undoStack.peek().capturedPiece != null) {
                this.replaceCapture(undoneMove);
            }
            this.redoStack.push(undoneMove);
        }
        
    }
    public void redoMove() {
        Move redoneMove = this.redoStack.pop();
        this.chessBoard.movePiece(redoneMove.fromRow, redoneMove.fromCol, redoneMove.toRow, redoneMove.toCol);
        this.undoStack.push(redoneMove);
    }
    public void replaceCapture(Move move) {
        PieceInstance capturedPiece = move.capturedPiece;
        this.chessBoard.board[move.toRow][move.toCol] = capturedPiece;
    }
    public boolean simulateMove(int fromRow, int fromCol, int toRow, int toCol) {
        PieceInstance[][] board = this.chessBoard.board;

        PieceInstance movingPiece = board[fromRow][fromCol];
        if (movingPiece == null) {
            return false; 
        }

        PieceInstance capturedPiece = board[toRow][toCol];

        board[toRow][toCol] = movingPiece;
        board[fromRow][fromCol] = null;
        movingPiece.setPosition(toRow, toCol);

        boolean inCheck = isKingInCheck(movingPiece.getPiece().isWhite());

        board[fromRow][fromCol] = movingPiece;
        board[toRow][toCol] = capturedPiece;
        movingPiece.setPosition(fromRow, fromCol);

        return !inCheck;
    }
    public void maintainRights() {
        if (this.chessBoard.board[0][0] == null || !(this.chessBoard.board[0][0].getPiece() instanceof Rook && !this.chessBoard.board[0][0].getPiece().isWhite())) {
            setCastlingRights(ChessBoard.CastlingRight.BLACK_QUEENSIDE.disable(getCastlingRights()));
        }
        if (this.chessBoard.board[0][7] == null || !(this.chessBoard.board[0][7].getPiece() instanceof Rook && !this.chessBoard.board[0][7].getPiece().isWhite())) {
            setCastlingRights(ChessBoard.CastlingRight.BLACK_KINGSIDE.disable(getCastlingRights()));
        }
        if (this.chessBoard.board[0][4] == null || !(this.chessBoard.board[0][4].getPiece() instanceof King && !this.chessBoard.board[0][4].getPiece().isWhite())) {
            setCastlingRights(ChessBoard.CastlingRight.BLACK_QUEENSIDE.disable(getCastlingRights()));
            setCastlingRights(ChessBoard.CastlingRight.BLACK_KINGSIDE.disable(getCastlingRights()));
        }
        if (this.chessBoard.board[7][0] == null || !(this.chessBoard.board[7][0].getPiece() instanceof Rook && this.chessBoard.board[7][0].getPiece().isWhite())) {
            setCastlingRights(ChessBoard.CastlingRight.WHITE_QUEENSIDE.disable(getCastlingRights()));
        }
        if (this.chessBoard.board[7][7] == null || !(this.chessBoard.board[7][7].getPiece() instanceof Rook && this.chessBoard.board[7][7].getPiece().isWhite())) {
            setCastlingRights(ChessBoard.CastlingRight.WHITE_KINGSIDE.disable(getCastlingRights()));
        }
        if (this.chessBoard.board[7][4] == null || !(this.chessBoard.board[7][4].getPiece() instanceof King && this.chessBoard.board[7][4].getPiece().isWhite())) {
            setCastlingRights(ChessBoard.CastlingRight.WHITE_QUEENSIDE.disable(getCastlingRights()));
            setCastlingRights(ChessBoard.CastlingRight.WHITE_KINGSIDE.disable(getCastlingRights()));
        }

        
    }
    public boolean tryCastling(ChessBoard.CastlingRight castlingRight, boolean whiteKing) {
        int[] kingCoordinates = findKing(whiteKing);
        int kingRow = kingCoordinates[0];
        int kingCol = kingCoordinates[1];
        switch(castlingRight) {
            // Test castling in each instance of CastlingRight by simulating moving the king moving to the position
            // Test for blockages in piece structure preventing movement, or if an enemy piece can take the king
            // If an enemy piece can take the king (isKingInCheck(boolean whiteKing)) in any iteration of while loop, return false
            case WHITE_KINGSIDE:
                int direction = 1;
                if (kingRow == 7 && kingCol == 4) {
                    if (isPathSafeForCastling(kingRow, kingCol, direction)) {
                        return true;
                    }
                } else {
                    return false;
                }
            case WHITE_QUEENSIDE:
                direction = -1;
                if (kingRow == 7 & kingCol == 4) {
                    if (isPathSafeForCastling(kingRow, kingCol, direction)) {
                        return true;
                    }
                } else {
                    return false;
                }
            case BLACK_KINGSIDE:
                direction = 1;
                if (kingRow == 0 && kingCol == 4) {
                    if (isPathSafeForCastling(kingRow, kingCol, direction)) {
                        return true;
                    } else {
                        return false;
                    }
                }
            case BLACK_QUEENSIDE:
                direction = -1;
                if (kingRow == 0 && kingCol == 4) {
                    if (isPathSafeForCastling(kingRow, kingCol, direction)) {
                        return true;
                    } else {
                        return false;
                    }
                }
            default:
                return false;
        } 
    }

    
    private boolean isPathSafeForCastling(int kingRow, int kingCol, int direction) {
        int r = kingRow;
        int c = kingCol;

        // Check only the two squares the king will cross (1 and 2 steps away)
        for (int step = 1; step <= 2; step++) {
            int newCol = c + (direction * step);

            if (this.chessBoard.board[r][newCol] != null || !simulateMove(kingRow, kingCol, r, newCol)) {
                return false; 
            }
        }

        return true;
    }
    public int[] findKing(boolean whiteKing) {
        for (int row = 0; row < ChessBoard.BOARD_SIZE; row++) {
            for (int col = 0; col < ChessBoard.BOARD_SIZE; col++) {
                PieceInstance p = this.chessBoard.board[row][col];
                if (p != null && p.getPiece() instanceof King &&
                    p.getPiece().isWhite() == whiteKing) {
                    return new int[]{row, col};
                }
            }
        }
        return null; 
    }

    private boolean isKingInCheck(boolean whiteKing) {
        int kingRow = -1, kingCol = -1;
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                PieceInstance piece = this.chessBoard.board[r][c];
                if (piece != null && piece.getPiece() instanceof King && piece.getPiece().isWhite() == whiteKing) {
                    kingRow = r;
                    kingCol = c;
                    break;
                }
            }
        }


        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                PieceInstance piece = this.chessBoard.board[r][c];
                if (piece != null && !piece.getPiece().isWhite() == whiteKing && piece.isLegalMove(this.chessBoard.board, kingRow, kingCol)) {
                    return true;
                }
            }
        }
        return false;
        
    }

    

    
}
