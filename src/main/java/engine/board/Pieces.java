package engine.board;

import engine.bitBoard.BitBoard;

import java.util.Arrays;

public class Pieces {
    public static final int PAWN = 0;
    public static final int KNIGHT = 1;
    public static final int BISHOP = 2;
    public static final int ROOK = 3;
    public static final int QUEEN = 4;
    public static final int KING = 5;
    public static final int WHITE = 0;
    public static final int BLACK = 1;

    private final BitBoard[][] pieceBitBoard;
    private BitBoard[] sideBitBoard;
    private BitBoard[] inversionSideBitBoard;
    private BitBoard all;
    private BitBoard empty;

    public Pieces(String startingLineup) {
        pieceBitBoard = new BitBoard[2][6];

        for (int i = 0; i < pieceBitBoard.length; i++) {
            for (int j = 0; j < pieceBitBoard[i].length; j++) {
                pieceBitBoard[i][j] = new BitBoard();
            }
        }

        int i = 0;
        int j = 7;
        int side;

        for (char symbol : startingLineup.toCharArray()) {
            if (symbol == '/') {
                i = 0;
                j--;
            } else if (Character.isDigit(symbol)) {
                i = i + symbol - '0';
            } else {
                if (Character.isUpperCase(symbol)) {
                    symbol = Character.toLowerCase(symbol);
                    side = WHITE;
                } else {
                    side = BLACK;
                }
                int square = j * 8 + i;
                switch (symbol) {
                    case 'p' -> pieceBitBoard[side][PAWN].setBit(square);
                    case 'n' -> pieceBitBoard[side][KNIGHT].setBit(square);
                    case 'b' -> pieceBitBoard[side][BISHOP].setBit(square);
                    case 'r' -> pieceBitBoard[side][ROOK].setBit(square);
                    case 'q' -> pieceBitBoard[side][QUEEN].setBit(square);
                    case 'k' -> pieceBitBoard[side][KING].setBit(square);
                }
                i++;
            }
        }
        updateBitBoards();
    }

    public Pieces(Pieces pieces) {
        pieceBitBoard = new BitBoard[2][6];
        sideBitBoard = new BitBoard[2];
        inversionSideBitBoard = new BitBoard[2];
        for (int i = 0; i < pieces.getPieceBitBoard().length; i++) {
            sideBitBoard[i] = new BitBoard(pieces.getSideBitBoard()[i].getBoard());
            inversionSideBitBoard[i] = new BitBoard(pieces.getInversionSideBitBoard()[i].getBoard());
            for (int j = 0; j < pieces.getPieceBitBoard()[i].length; j++) {
                pieceBitBoard[i][j] = new BitBoard(pieces.getPieceBitBoard()[i][j].getBoard());
            }
        }
        all = new BitBoard(pieces.getAll().getBoard());
        empty = new BitBoard(pieces.getEmpty().getBoard());
    }

    public static int inverse(int side) {
        return side == BLACK ? WHITE : BLACK;
    }

    public void updateBitBoards() {
        sideBitBoard = new BitBoard[2];
        inversionSideBitBoard = new BitBoard[2];

        sideBitBoard[WHITE] = new BitBoard(pieceBitBoard[WHITE][PAWN].getBoard() |
                pieceBitBoard[WHITE][KNIGHT].getBoard() |
                pieceBitBoard[WHITE][BISHOP].getBoard() |
                pieceBitBoard[WHITE][ROOK].getBoard() |
                pieceBitBoard[WHITE][QUEEN].getBoard() |
                pieceBitBoard[WHITE][KING].getBoard());

        sideBitBoard[BLACK] = new BitBoard(pieceBitBoard[BLACK][PAWN].getBoard() |
                pieceBitBoard[BLACK][KNIGHT].getBoard() |
                pieceBitBoard[BLACK][BISHOP].getBoard() |
                pieceBitBoard[BLACK][ROOK].getBoard() |
                pieceBitBoard[BLACK][QUEEN].getBoard() |
                pieceBitBoard[BLACK][KING].getBoard());

        inversionSideBitBoard[WHITE] = new BitBoard(((-sideBitBoard[WHITE].getBoard()) - 1));

        inversionSideBitBoard[BLACK] = new BitBoard(((-sideBitBoard[BLACK].getBoard()) - 1));

        all = new BitBoard(sideBitBoard[WHITE].getBoard() | sideBitBoard[BLACK].getBoard());
        empty = new BitBoard(((-all.getBoard()) - 1));
    }

    public BitBoard[][] getPieceBitBoard() {
        return pieceBitBoard;
    }

    public BitBoard[] getSideBitBoard() {
        return sideBitBoard;
    }

    public BitBoard[] getInversionSideBitBoard() {
        return inversionSideBitBoard;
    }

    public BitBoard getAll() {
        return all;
    }

    public BitBoard getEmpty() {
        return empty;
    }

    public String toFEN() {
        StringBuilder result = new StringBuilder();
        for (int i = 7; i >= 0; i--) {
            for (int j = 0; j < 8; j++) {
                int pos = i * 8 + j;
                if (pieceBitBoard[WHITE][PAWN].getBit(pos)) result.append("P");
                else if (pieceBitBoard[WHITE][KNIGHT].getBit(pos)) result.append("N");
                else if (pieceBitBoard[WHITE][BISHOP].getBit(pos)) result.append("B");
                else if (pieceBitBoard[WHITE][ROOK].getBit(pos)) result.append("R");
                else if (pieceBitBoard[WHITE][QUEEN].getBit(pos)) result.append("Q");
                else if (pieceBitBoard[WHITE][KING].getBit(pos)) result.append("K");
                else if (pieceBitBoard[BLACK][PAWN].getBit(pos)) result.append("p");
                else if (pieceBitBoard[BLACK][KNIGHT].getBit(pos)) result.append("n");
                else if (pieceBitBoard[BLACK][BISHOP].getBit(pos)) result.append("b");
                else if (pieceBitBoard[BLACK][ROOK].getBit(pos)) result.append("r");
                else if (pieceBitBoard[BLACK][QUEEN].getBit(pos)) result.append("q");
                else if (pieceBitBoard[BLACK][KING].getBit(pos)) result.append("k");
                else result.append("1");
            }
            result.append("/");
        }
        return result.toString();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (int i = 7; i >= 0; i--) {
            for (int j = 0; j < 8; j++) {
                int pos = i * 8 + j;
                result.append("| ");
                if (pieceBitBoard[WHITE][PAWN].getBit(pos)) result.append("♙");
                else if (pieceBitBoard[WHITE][KNIGHT].getBit(pos)) result.append("♘");
                else if (pieceBitBoard[WHITE][BISHOP].getBit(pos)) result.append("♗");
                else if (pieceBitBoard[WHITE][ROOK].getBit(pos)) result.append("♖");
                else if (pieceBitBoard[WHITE][QUEEN].getBit(pos)) result.append("♕");
                else if (pieceBitBoard[WHITE][KING].getBit(pos)) result.append("♔");
                else if (pieceBitBoard[BLACK][PAWN].getBit(pos)) result.append("♟");
                else if (pieceBitBoard[BLACK][KNIGHT].getBit(pos)) result.append("♞");
                else if (pieceBitBoard[BLACK][BISHOP].getBit(pos)) result.append("♝");
                else if (pieceBitBoard[BLACK][ROOK].getBit(pos)) result.append("♜");
                else if (pieceBitBoard[BLACK][QUEEN].getBit(pos)) result.append("♛");
                else if (pieceBitBoard[BLACK][KING].getBit(pos)) result.append("♚");
                else result.append(" ");

                result.append(" ");
            }
            result.append("|\n");
        }
        return result.toString();
    }

    // 1000000000000000000000000000000000000000000000000000000010000001

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Pieces pieces = (Pieces) o;

        for (int i = 0; i < pieceBitBoard.length; i++) {
            for (int j = 0; j < pieceBitBoard[i].length; j++) {
                if (!this.pieceBitBoard[i][j].equals(pieces.pieceBitBoard[i][j])) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = Arrays.deepHashCode(pieceBitBoard);
        result = 31 * result + Arrays.hashCode(sideBitBoard);
        result = 31 * result + Arrays.hashCode(inversionSideBitBoard);
        result = 31 * result + (all != null ? all.hashCode() : 0);
        result = 31 * result + (empty != null ? empty.hashCode() : 0);
        return result;
    }
}
