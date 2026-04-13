import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.media.AudioClip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.scene.shape.StrokeType;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Pane;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;

public class ChessBoard extends Application {


    /* Instance of ChessBoard called in launch(args); in main method (instance variables: board[][], gridPane, selectedRow, selectedCol 
       void start method is called on ChessBoard instance (ChessBoard.start()) by JavaFX, which calls this.gridPane = new gridPane(), then heap objects
       whitePawnImage, blackPawnImage etc. are created locally in the function for assignment to Piece.image, for loop initialises the PieceInstance(Piece piece, int row, int col)
       positions for each PieceInstance on the board.
       Each time the board is rendered, the JavaFX gridPane is cleared, the gridPane is re-rendered, squares filled, lambda functions to setOnMouseClicked(),
       if the row variable in for loop == selectedRow and  col == selectedCol: tile.SetStroke(Color.YELLOW) , tile.setStrokeWidth(3), 
       tile.setStrokeType(StrokeType.INSIDE)  
    */
    public static final int BOARD_SIZE = 8;
    private static final int TILE_SIZE = 80;

    private GridPane gridPane;
    public PieceInstance[][] board = new PieceInstance[BOARD_SIZE][BOARD_SIZE]; 
    private int selectedRow = -1;
    private int selectedCol = -1;
    public Game game;
    
    public enum CastlingRight {
        WHITE_KINGSIDE(0),
        WHITE_QUEENSIDE(1),
        BLACK_KINGSIDE(3),
        BLACK_QUEENSIDE(4);

        public final int bit;

        private CastlingRight(int bit) { // 0001, 0010, 0100, 1000
            this.bit = bit;
        }

        public int mask() {
            return 1 << this.bit;
        }

        public int enable(int rights) { // 0111 OR 1000 1111
            return rights | mask();
        }

        public int disable(int rights) {
            return rights & ~mask();
        }

        public int toggle(int rights) {
            return rights ^ mask();
        }
        public boolean isSet(int rights) {
            return (rights & mask()) != 0;
        }
    }
    @Override
    public void start(Stage primaryStage) {
        game = new Game();
        game.setChessBoard(this);
        StackPane root = new StackPane();
        BorderPane layout = new BorderPane();
        Pane background = new Pane();
        background.setStyle("-fx-background-color: lightblue;");
        gridPane = new GridPane();
        StackPane gridWrapper = new StackPane(gridPane);
        Button undoButton = new Button();
        Button redoButton = new Button();
        VBox controls = new VBox(undoButton, redoButton);
        
        gridPane.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        gridPane.setPrefSize(BOARD_SIZE * TILE_SIZE, BOARD_SIZE * TILE_SIZE);
        layout.setCenter(gridWrapper);
        controls.setAlignment(Pos.CENTER);
        layout.setBottom(controls);

        initializeBoard();
        renderBoard();
        root.getChildren().addAll(background, layout);
        Scene scene = new Scene(root, BOARD_SIZE * TILE_SIZE, BOARD_SIZE * TILE_SIZE);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Chess");
        primaryStage.show();
    }

    private void initializeBoard() {
        Image whitePawnImage  = loadImage("/images/white-pawn.png");
        Image blackPawnImage  = loadImage("/images/black-pawn.png");

        Image whiteRookImage  = loadImage("/images/white-rook.png");
        Image blackRookImage  = loadImage("/images/black-rook.png");

        Image whiteBishopImage = loadImage("/images/white-bishop.png");
        Image blackBishopImage = loadImage("/images/black-bishop.png");

        Image whiteQueenImage = loadImage("/images/white-queen.png");
        Image blackQueenImage = loadImage("/images/black-queen.png");

        Image whiteKingImage  = loadImage("/images/white-king.png");
        Image blackKingImage  = loadImage("/images/black-king.png");

        Image whiteKnightImage = loadImage("/images/white-knight.png");
        Image blackKnightImage = loadImage("/images/black-knight.png");

        // Pawns
        for (int col = 0; col < BOARD_SIZE; col++) {
            board[6][col] = new PieceInstance(new Pawn(true, whitePawnImage), 6, col);
            board[1][col] = new PieceInstance(new Pawn(false, blackPawnImage), 1, col);
        }

        // Rooks
        board[7][0] = new PieceInstance(new Rook(true, whiteRookImage), 7, 0);
        board[7][7] = new PieceInstance(new Rook(true, whiteRookImage), 7, 7);
        board[0][0] = new PieceInstance(new Rook(false, blackRookImage), 0, 0);
        board[0][7] = new PieceInstance(new Rook(false, blackRookImage), 0, 7);


        board[0][2] = new PieceInstance(new Bishop(false, blackBishopImage), 0, 2);
        board[0][5] = new PieceInstance(new Bishop(false, blackBishopImage), 0, 5);
        board[7][2] = new PieceInstance(new Bishop(true, whiteBishopImage), 7, 2);
        board[7][5] = new PieceInstance(new Bishop(true, whiteBishopImage), 7, 5);

        board[0][1] = new PieceInstance(new Knight(false, blackKnightImage), 0, 1);
        board[0][6] = new PieceInstance(new Knight(false, blackKnightImage), 0, 6);
        board[7][1] = new PieceInstance(new Knight(true, whiteKnightImage), 7, 1);
        board[7][6] = new PieceInstance(new Knight(true, whiteKnightImage), 7, 6);

        board[0][3] = new PieceInstance(new Queen(false, blackQueenImage), 0, 3);
        board[7][3] = new PieceInstance(new Queen(true, whiteQueenImage), 7, 3);

        board[0][4] = new PieceInstance(new King(false, blackKingImage), 0, 4);
        board[7][4] = new PieceInstance(new King(true, whiteKingImage), 7, 4);
    }

    private Image loadImage(String path) {
        var url = getClass().getResource(path);
        return new Image(url.toExternalForm());
    }

    private AudioClip loadAudioClip(String path) {
        var audioClip = getClass().getResource(path);
        return new AudioClip(audioClip.toExternalForm());
    }

    private void renderBoard() {
        gridPane.getChildren().clear();
        boolean replay = this.game.isReplay();
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                Rectangle tile = new Rectangle(TILE_SIZE, TILE_SIZE);
                tile.setFill((row + col) % 2 == 0 ? Color.BEIGE : Color.SADDLEBROWN);

                // Highlight selected piece
                if (row == selectedRow && col == selectedCol) {
                    tile.setStroke(Color.YELLOW);
                    tile.setStrokeWidth(3);
                    tile.setStrokeType(StrokeType.INSIDE);
                } else {
                    tile.setStroke(null);
                }
                /* Save snapshot of row and col then apply that to lamdba function, otherwise it uses last values for loop (7,7) */
                /* Lambda captures only values that are effectively final */

                int r = row, c = col;
                if (!replay) {
                    tile.setOnMouseClicked(_ -> handleClick(r, c));
                }
                gridPane.add(tile, col, row);
                PieceInstance pieceInstance = board[row][col];
                if (pieceInstance != null) {
                    ImageView imageView = new ImageView(pieceInstance.getPiece().getImage());
                    imageView.setFitWidth(TILE_SIZE);
                    imageView.setFitHeight(TILE_SIZE);
                    imageView.setMouseTransparent(true);
                    gridPane.add(imageView, col, row);
                }
            }
        
        } 
    }

    private void handleClick(int row, int col) {
        PieceInstance clickedPiece = board[row][col];

        if (selectedRow == -1) {
            if (clickedPiece != null && clickedPiece.getPiece().isWhite() == game.isWhiteMove()) {
                selectedRow = row;
                selectedCol = col;

                game.kingSelected = clickedPiece.getPiece() instanceof King;
            }
        }

        else {
            PieceInstance selectedPiece = board[selectedRow][selectedCol];
            boolean isWhiteTurn = game.isWhiteMove();

            if (selectedPiece == null) {
                resetSelection();
                renderBoard();
                return;
            }

            if (game.kingSelected && clickedPiece != null 
                    && clickedPiece.getPiece() instanceof Rook
                    && clickedPiece.getPiece().isWhite() == isWhiteTurn) {

                ChessBoard.CastlingRight side = null;

                if (isWhiteTurn) {
                    if (row == 7 && col == 7) side = ChessBoard.CastlingRight.WHITE_KINGSIDE;
                    else if (row == 7 && col == 0) side = ChessBoard.CastlingRight.WHITE_QUEENSIDE;
                } else {
                    if (row == 0 && col == 7) side = ChessBoard.CastlingRight.BLACK_KINGSIDE;
                    else if (row == 0 && col == 0) side = ChessBoard.CastlingRight.BLACK_QUEENSIDE;
                }

                if (side != null && side.isSet(game.getCastlingRights()) && game.tryCastling(side, isWhiteTurn)) {
                    playSound(loadAudioClip("/sounds/Pawn_Move.mp3"));
                    performCastlingMove(side, isWhiteTurn);
                    game.toggleToMove();
                    game.maintainRights();
                    resetSelection();
                    renderBoard();
                    return;
                }
            }

            if (selectedPiece.isLegalMove(board, row, col)
                    && selectedPiece.getPiece().isWhite() == isWhiteTurn
                    && game.simulateMove(selectedRow, selectedCol, row, col)) {
                playSound(loadAudioClip("/sounds/Pawn_Move.mp3"));
                movePiece(selectedRow, selectedCol, row, col);
                game.toggleToMove();
                game.maintainRights();
            }

            resetSelection();
            renderBoard();
        }
    }

    private static void playSound(AudioClip audioClip) {
        audioClip.setVolume(1.0);
        audioClip.play();
    }
    private void performCastlingMove(ChessBoard.CastlingRight side, boolean whiteKing) {
        if (whiteKing) {
            switch (side) {
                case WHITE_KINGSIDE:
                    movePiece(7, 4, 7, 6); // King e1 → g1
                    movePiece(7, 7, 7, 5); // Rook h1 → f1
                    break;
                case WHITE_QUEENSIDE:
                    movePiece(7, 4, 7, 2); // King e1 → c1
                    movePiece(7, 0, 7, 3); // Rook a1 → d1
                    break;
                default:
                break;
            }
        } else {
            switch (side) {
                case BLACK_KINGSIDE:
                    movePiece(0, 4, 0, 6); // King e8 → g8
                    movePiece(0, 7, 0, 5); // Rook h8 → f8
                    break;
                case BLACK_QUEENSIDE:
                    movePiece(0, 4, 0, 2); // King e8 → c8
                    movePiece(0, 0, 0, 3); // Rook a8 → d8
                    break;
                default:
                break;
            }
        }
    }
    private void resetSelection() {
        selectedRow = -1;
        selectedCol = -1;
        game.kingSelected = false;
        game.rookSelected = false;
    }

    public void movePiece(int fromRow, int fromCol, int toRow, int toCol) {
        PieceInstance movingPiece = board[fromRow][fromCol];
        board[toRow][toCol] = movingPiece;
        board[fromRow][fromCol] = null;
        movingPiece.setPosition(toRow, toCol);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
