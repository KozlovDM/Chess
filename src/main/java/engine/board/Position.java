package engine.board;

import engine.zobrist.ZobristHash;

public class Position {
    private Pieces pieces;
    private int enPassant;
    private boolean wLCastling;
    private boolean wSCastling;
    private boolean bLCastling;
    private boolean bSCastling;
    private boolean whiteCastlingHappened;
    private boolean blackCastlingHappened;
    private float moveCount;
    private float fiftyMoveCounter;
    private ZobristHash hash;
    private RepetitionHistory repetitionHistory;
    private int side;

    public Position(String startingLineup) {
        String[] lineUp = startingLineup.split(" ");
        pieces = new Pieces(lineUp[0]);
        this.enPassant = "-".equals(lineUp[3]) ? -1 : (lineUp[3].charAt(1) - '1') * 8 + lineUp[3].charAt(0) - 'a';

        side = "w".equals(lineUp[1]) ? Pieces.WHITE : Pieces.BLACK;
        for (char castling : lineUp[2].toCharArray()) {
            switch (castling) {
                case 'k' -> bSCastling = true;
                case 'q' -> bLCastling = true;
                case 'K' -> wSCastling = true;
                case 'Q' -> wLCastling = true;
            }
        }
        whiteCastlingHappened = false;
        blackCastlingHappened = false;
        this.moveCount = Integer.parseInt(lineUp[5]);
        fiftyMoveCounter = Integer.parseInt(lineUp[4]);;
        hash = new ZobristHash(pieces, (moveCount - (float) Math.floor(moveCount) > 1e-4), wLCastling, wSCastling, bLCastling, bSCastling);
        repetitionHistory = new RepetitionHistory();
        repetitionHistory.addPosition(hash);
    }

    public Position(Position position) {
        pieces = new Pieces(position.pieces);
        enPassant = position.enPassant;
        wLCastling = position.wLCastling;
        wSCastling = position.wSCastling;
        bLCastling = position.bLCastling;
        bSCastling = position.bSCastling;
        whiteCastlingHappened = position.whiteCastlingHappened;
        blackCastlingHappened = position.blackCastlingHappened;
        moveCount = position.moveCount;
        fiftyMoveCounter = position.fiftyMoveCounter;
        hash = new ZobristHash(position.getHash());
        repetitionHistory = new RepetitionHistory(position.repetitionHistory);
        side = position.side;

    }

    public void move(Move move) {
        side = Pieces.inverse(side);
        removePiece(move.getFrom(), move.getAttackerType(), move.getAttackerSide());
        addPiece(move.getTo(), move.getAttackerType(), move.getAttackerSide());
        if (move.getDefenderType() != -1) {
            removePiece(move.getTo(), move.getDefenderType(), move.getDefenderSide());
        }

        switch (move.getFlag()) {
            case Move.PAWN_LONG_MOVE -> setEnPassant((move.getFrom() + move.getTo()) / 2);
            case Move.EN_PASSANT_CAPTURE -> {
                if (move.getAttackerSide() == Pieces.WHITE) {
                    removePiece(move.getTo() - 8, Pieces.PAWN, Pieces.BLACK);
                } else {
                    removePiece(move.getTo() + 8, Pieces.PAWN, Pieces.WHITE);
                }
            }
            case Move.WHITE_LONG_CASTLING -> {
                removePiece(0, Pieces.ROOK, Pieces.WHITE);
                addPiece(3, Pieces.ROOK, Pieces.WHITE);
                whiteCastlingHappened = true;
            }
            case Move.WHITE_SHORT_CASTLING -> {
                removePiece(7, Pieces.ROOK, Pieces.WHITE);
                addPiece(5, Pieces.ROOK, Pieces.WHITE);
                whiteCastlingHappened = true;
            }
            case Move.BLACK_LONG_CASTLING -> {
                removePiece(56, Pieces.ROOK, Pieces.BLACK);
                addPiece(59, Pieces.ROOK, Pieces.BLACK);
                blackCastlingHappened = true;
            }
            case Move.BLACK_SHORT_CASTLING -> {
                removePiece(63, Pieces.ROOK, Pieces.BLACK);
                addPiece(61, Pieces.ROOK, Pieces.BLACK);
                blackCastlingHappened = true;
            }
            case Move.PROMOTE_TO_KNIGHT -> {
                removePiece(move.getTo(), Pieces.PAWN, move.getAttackerSide());
                addPiece(move.getTo(), Pieces.KNIGHT, move.getAttackerSide());
            }
            case Move.PROMOTE_TO_BISHOP -> {
                removePiece(move.getTo(), Pieces.PAWN, move.getAttackerSide());
                addPiece(move.getTo(), Pieces.BISHOP, move.getAttackerSide());
            }
            case Move.PROMOTE_TO_ROOK -> {
                removePiece(move.getTo(), Pieces.PAWN, move.getAttackerSide());
                addPiece(move.getTo(), Pieces.ROOK, move.getAttackerSide());
            }
            case Move.PROMOTE_TO_QUEEN -> {
                removePiece(move.getTo(), Pieces.PAWN, move.getAttackerSide());
                addPiece(move.getTo(), Pieces.QUEEN, move.getAttackerSide());
            }
        }

        pieces.updateBitBoards();
        if (move.getFlag() != Move.PAWN_LONG_MOVE) {
            setEnPassant(-1);
        }

        switch (move.getFrom()) {
            case 0 -> removeWLCastling();
            case 4 -> {
                removeWLCastling();
                removeWSCastling();
            }
            case 7 -> removeWSCastling();
            case 56 -> removeBLCastling();
            case 60 -> {
                removeBLCastling();
                removeBSCastling();
            }
            case 63 -> removeBSCastling();
        }

        updateMoveCounter();
        boolean breakEvent = move.getAttackerType() == Pieces.PAWN || move.getDefenderType() != -1;
        updateFiftyMovesCounter(breakEvent);
        if (breakEvent) {
            repetitionHistory.clear();
        }
        repetitionHistory.addPosition(hash);
    }

    public void addPiece(int square, int type, int side) {
        pieces.getPieceBitBoard()[side][type].setBit(square);
        hash.invertPiece(square, type, side);
    }

    public void removePiece(int square, int type, int side) {
        pieces.getPieceBitBoard()[side][type].clearBit(square);
        hash.invertPiece(square, type, side);
    }

    private void setEnPassant(int enPassant) {
        this.enPassant = enPassant;
    }

    private void removeWLCastling() {
        wLCastling = false;
        hash.invertWLCastling();

    }

    private void removeWSCastling() {
        wSCastling = false;
        hash.invertWSCastling();

    }

    private void removeBLCastling() {
        bLCastling = false;
        hash.invertBLCastling();

    }

    private void removeBSCastling() {
        bSCastling = false;
        hash.invertBSCastling();

    }

    private void updateMoveCounter() {
        moveCount += 0.5f;
        hash.invertMove();
    }

    private void updateFiftyMovesCounter(boolean breakEvent) {
        if (breakEvent) {
            fiftyMoveCounter = 0;
        }
        fiftyMoveCounter += 0.5f;
    }

    public Pieces getPieces() {
        return pieces;
    }

    public int getEnPassant() {
        return enPassant;
    }

    public boolean iswLCastling() {
        return wLCastling;
    }

    public boolean iswSCastling() {
        return wSCastling;
    }

    public boolean isbLCastling() {
        return bLCastling;
    }

    public boolean isbSCastling() {
        return bSCastling;
    }

    public float getMoveCount() {
        return moveCount;
    }

    public boolean isWhiteCastlingHappened() {
        return whiteCastlingHappened;
    }

    public boolean isBlackCastlingHappened() {
        return blackCastlingHappened;
    }

    public float getFiftyMoveCounter() {
        return fiftyMoveCounter;
    }

    public ZobristHash getHash() {
        return hash;
    }

    public RepetitionHistory getRepetitionHistory() {
        return repetitionHistory;
    }

    public int getSide() {
        return side;
    }

    public void setSide() {
        side = side == Pieces.BLACK ? Pieces.WHITE : Pieces.BLACK;
    }
}
