package UI.screen;

import engine.board.Move;
import engine.board.Position;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.graphics.ImageRenderer;
import de.gurkenlabs.litiengine.graphics.ShapeRenderer;
import de.gurkenlabs.litiengine.gui.ImageComponent;
import de.gurkenlabs.litiengine.gui.screens.Screen;
import engine.moveGeneration.LegalMoveGen;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static UI.screen.PiecesImage.*;
import static engine.board.Pieces.*;

public class GameScreen extends Screen {
    public static final String NAME = "GAMESCREEN";
    private static List<Rectangle2D> board;
    private Position position;
    private Move move;
    private final Map<Integer, Image> pieces;
    private List<ImageComponent> imageComponents;
    private Integer selectedImage;
    private List<Move> legalMove;

    public GameScreen() {
        super(NAME);
        pieces = new HashMap<>();
        selectedImage = null;
    }

    @Override
    protected void initializeComponents() {
        board = new ArrayList<>(64);
        imageComponents = new ArrayList<>(64);

        double x = Game.window().getWidth() / 3.5;
        double y = Game.window().getHeight() - 250;
        for (int i = 0; i < 64; i++) {
            Rectangle2D rect = new Rectangle2D.Double(x, y, 150, 150);
            ImageComponent imageComponent = new ImageComponent(x, y, 150, 150);
            imageComponent.onClicked(callback -> {
                for (int j = 0; j < 64; j++) {
                    if (imageComponents.get(j) == imageComponent) {
                        Image image = pieces.get(j);
                        if (selectedImage == null && image != null) {
                            selectedImage = j;
                        } else if (selectedImage != null) {
                            for (Move move : legalMove) {
                                if (move.getFrom() == selectedImage && move.getTo() == j) {
                                    position.move(move);
                                    this.move = move;
                                    break;
                                }
                            }
                            selectedImage = null;
                            legalMove = null;
                        }
                    }
                }

            });
            imageComponents.add(imageComponent);
            getComponents().add(imageComponent);

            board.add(rect);
            if ((i + 1) % 8 == 0) {
                x -= 150 * 7;
                y -= 150;
            } else {
                x += 150;
            }
        }
    }

    @Override
    public void prepare() {
        super.prepare();
        Game.window().getRenderComponent().setBackground(Color.decode("#312e2b"));
    }


    @Override
    public void render(Graphics2D g) {
        int scale = 0;
        if (position.getSide() == GameManager.side && legalMove == null) {
            legalMove = LegalMoveGen.generate(position, position.getSide(), false, false);
        }

        for (int i = 0; i < 64; i++) {
            Rectangle2D rect = board.get(i);
            if ((i + scale) % 2 == 0) {
                g.setColor(Color.decode("#769656"));
            } else {
                g.setColor(Color.decode("#eeeed2"));
            }
            ShapeRenderer.render(g, rect);

            if (selectedImage != null) {
                for (Move move : legalMove) {
                    if (move.getFrom() == selectedImage && move.getTo() == i) {
                        RoundRectangle2D.Double round = new RoundRectangle2D.Double(rect.getCenterX(), rect.getCenterY(), 20, 20, 20, 20);
                        g.setColor(Color.decode("#0000ff"));
                        ShapeRenderer.render(g, round);
                        break;
                    }
                }
            }

            if ((i + 1) % 8 == 0) {
                scale++;
            }
        }

        Image image;
        for (int i = 7; i >= 0; i--) {
            for (int j = 0; j < 8; j++) {
                int pos = i * 8 + j;
                image = null;
                if (position.getPieces().getPieceBitBoard()[WHITE][PAWN].getBit(pos)) {
                    image = WHITE_PAWN;
                } else if (position.getPieces().getPieceBitBoard()[WHITE][KNIGHT].getBit(pos)) {
                    image = WHITE_KNIGHT;
                } else if (position.getPieces().getPieceBitBoard()[WHITE][BISHOP].getBit(pos)) {
                    image = WHITE_BISHOP;
                } else if (position.getPieces().getPieceBitBoard()[WHITE][ROOK].getBit(pos)) {
                    image = WHITE_ROOK;
                } else if (position.getPieces().getPieceBitBoard()[WHITE][QUEEN].getBit(pos)) {
                    image = WHITE_QUEEN;
                } else if (position.getPieces().getPieceBitBoard()[WHITE][KING].getBit(pos)) {
                    image = WHITE_KING;
                } else if (position.getPieces().getPieceBitBoard()[BLACK][PAWN].getBit(pos)) {
                    image = BLACK_PAWN;
                } else if (position.getPieces().getPieceBitBoard()[BLACK][KNIGHT].getBit(pos)) {
                    image = BLACK_KNIGHT;
                } else if (position.getPieces().getPieceBitBoard()[BLACK][BISHOP].getBit(pos)) {
                    image = BLACK_BISHOP;
                } else if (position.getPieces().getPieceBitBoard()[BLACK][ROOK].getBit(pos)) {
                    image = BLACK_ROOK;
                } else if (position.getPieces().getPieceBitBoard()[BLACK][QUEEN].getBit(pos)) {
                    image = BLACK_QUEEN;
                } else if (position.getPieces().getPieceBitBoard()[BLACK][KING].getBit(pos)) {
                    image = BLACK_KING;
                } else if (move != null && move.getFrom() == pos) {
                    setColor(g, j, i);
                }
                if (move != null && move.getTo() == pos) {
                    setColor(g, j, i);
                }
                if (selectedImage != null && selectedImage == pos) {
                    setColor(g, j, i);
                }

                if (image != null) {
                    pieces.put(pos, image);
                    ImageRenderer.render(g, image, board.get(pos).getBounds().getLocation());
                } else {
                    pieces.remove(pos);
                }
            }
        }

        super.render(g);
    }

    private void setColor(Graphics2D g, int j, int i) {
        Rectangle2D rectangle = board.get(i * 8 + j);
        if ((j + i) % 2 == 0) {
            g.setColor(Color.decode("#13d433"));
        } else {
            g.setColor(Color.decode("#c2ccc4"));
        }
        ShapeRenderer.render(g, rectangle);
    }

    public void updatePosition(Position position, Move move) {
        this.position = position;
        this.move = move;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }
}
