import javafx.scene.image.Image;

public abstract class Piece {
    protected boolean isWhite;
    protected Image image;

    public Piece(boolean isWhite, Image image) {
        this.isWhite = isWhite;
        this.image = image;
    }

    public boolean isWhite() {
        return isWhite;
    }

    public Image getImage() {
        return image;
    }

    public abstract boolean isLegalMove(
        PieceInstance[][] board,
        int fromRow, int fromCol,
        int toRow, int toCol
    );
}

// Pawn
class Pawn extends Piece {
    public Pawn(boolean isWhite, Image image) {
        super(isWhite, image);
    }

    @Override
    public boolean isLegalMove(PieceInstance[][] board, int fromRow, int fromCol, int toRow, int toCol) {
        int direction = isWhite ? -1 : 1;

        // Forward move
        if (fromCol == toCol && toRow - fromRow == direction && board[toRow][toCol] == null)
            return true;

        if (fromCol == toCol
            && ((isWhite && fromRow == 6) || (!isWhite && fromRow == 1))
            && toRow - fromRow == 2 * direction
            && board[fromRow + direction][fromCol] == null
            && board[toRow][toCol] == null)
            return true;

        if (Math.abs(fromCol - toCol) == 1
            && toRow - fromRow == direction
            && board[toRow][toCol] != null
            && board[toRow][toCol].getPiece().isWhite() != isWhite)
            return true;

        return false;
    }
} 


class Rook extends Piece {
    public Rook(boolean isWhite, Image image) {
        super(isWhite, image);
    }

    @Override
    public boolean isLegalMove(PieceInstance[][] board, int fromRow, int fromCol, int toRow, int toCol) {
        if (fromRow != toRow && fromCol != toCol) return false;

        int rowStep = Integer.compare(toRow, fromRow);
        int colStep = Integer.compare(toCol, fromCol);

        int r = fromRow + rowStep;
        int c = fromCol + colStep;

        while (r != toRow || c != toCol) {
            if (board[r][c] != null) return false; 
            r += rowStep;
            c += colStep;
        }

        return board[toRow][toCol] == null
            || board[toRow][toCol].getPiece().isWhite() != this.isWhite;
    }
}

class Bishop extends Piece {
    public Bishop(boolean isWhite, Image image) {
        super(isWhite, image);
    }

    @Override
    public boolean isLegalMove(PieceInstance[][] board, int fromRow, int fromCol, int toRow, int toCol) {
        if (Math.abs(toRow - fromRow) != Math.abs(toCol - fromCol)) {
            return false;
        }

        
        int rowStep = Integer.compare(toRow, fromRow); // -1 or +1
        int colStep = Integer.compare(toCol, fromCol); // -1 or +1

        
        int r = fromRow + rowStep;
        int c = fromCol + colStep;
        while (r != toRow && c != toCol) {
            if (board[r][c] != null) {
                return false; 
            }
            r += rowStep;
            c += colStep;
        }

        return board[toRow][toCol] == null
            || board[toRow][toCol].getPiece().isWhite() != this.isWhite();
    }


}

class Queen extends Piece {
    public Queen(boolean isWhite, Image image) {
        super(isWhite, image);
    }

    @Override
    public boolean isLegalMove(PieceInstance[][] board, int fromRow, int fromCol, int toRow, int toCol) {
        
        boolean straight = (fromRow == toRow || fromCol == toCol);
        boolean diagonal = Math.abs((toRow - fromRow)) == Math.abs((toCol - fromCol));

        if (!straight && !diagonal) {
            return false;
        }

        int rowStep = Integer.compare(toRow, fromRow);
        int colStep = Integer.compare(toCol, fromCol);

        int r = fromRow + rowStep;
        int c = fromCol + colStep;

        while (r != toRow || c != toCol) {
            if(board[r][c] != null) {
                return false;
            }
            r += rowStep;
            c += colStep;
        }
        return board[toRow][toCol] == null || board[toRow][toCol].getPiece().isWhite() != this.isWhite();

    }
}

class King extends Piece {
    public King(boolean isWhite, Image image) {
        super(isWhite, image);
    }

    @Override
    public boolean isLegalMove(PieceInstance[][] board, int fromRow, int fromCol, int toRow, int toCol) {
        int rowDiff = Math.abs(toRow - fromRow);
        int colDiff = Math.abs(toCol - fromCol);

        // Must move exactly 1 square in any direction
        if (rowDiff <= 1 && colDiff <= 1 && !(rowDiff == 0 && colDiff == 0)) {
            
            return board[toRow][toCol] == null 
                || board[toRow][toCol].getPiece().isWhite() != this.isWhite();
        }

        return false;
    }
}

class Knight extends Piece {

    public Knight(boolean isWhite, Image image) {
        super(isWhite, image);
    }

    @Override
    public boolean isLegalMove(PieceInstance[][] board, int fromRow, int fromCol, int toRow, int toCol) {
        boolean gallop = Math.abs(toRow - fromRow) == 2 && Math.abs(toCol - fromCol) == 1 || Math.abs(toCol - fromCol) == 2 && Math.abs(toRow - fromRow) == 1;
        
        if (!gallop) {
            return false;
        }
        
        return board[toRow][toCol] == null || board[toRow][toCol].getPiece().isWhite() != this.isWhite();
    }
}