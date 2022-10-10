package UI.screen;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;

public class PiecesImage {
    public static Image WHITE_PAWN;
    public static Image WHITE_KNIGHT;
    public static Image WHITE_BISHOP;
    public static Image WHITE_ROOK;
    public static Image WHITE_QUEEN;
    public static Image WHITE_KING;

    public static Image BLACK_PAWN;
    public static Image BLACK_KNIGHT;
    public static Image BLACK_BISHOP;
    public static Image BLACK_ROOK;
    public static Image BLACK_QUEEN;
    public static Image BLACK_KING;

    static {
        try {
            WHITE_PAWN = ImageIO.read(PiecesImage.class.getResource("/pieces/white/pawn.png"));
            WHITE_KNIGHT = ImageIO.read(PiecesImage.class.getResource("/pieces/white/knight.png"));
            WHITE_BISHOP = ImageIO.read(PiecesImage.class.getResource("/pieces/white/bishop.png"));
            WHITE_ROOK = ImageIO.read(PiecesImage.class.getResource("/pieces/white/rook.png"));
            WHITE_QUEEN = ImageIO.read(PiecesImage.class.getResource("/pieces/white/queen.png"));
            WHITE_KING = ImageIO.read(PiecesImage.class.getResource("/pieces/white/king.png"));

            BLACK_PAWN = ImageIO.read(PiecesImage.class.getResource("/pieces/black/pawn.png"));
            BLACK_KNIGHT = ImageIO.read(PiecesImage.class.getResource("/pieces/black/knight.png"));
            BLACK_BISHOP = ImageIO.read(PiecesImage.class.getResource("/pieces/black/bishop.png"));
            BLACK_ROOK = ImageIO.read(PiecesImage.class.getResource("/pieces/black/rook.png"));
            BLACK_QUEEN = ImageIO.read(PiecesImage.class.getResource("/pieces/black/queen.png"));
            BLACK_KING = ImageIO.read(PiecesImage.class.getResource("/pieces/black/king.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
