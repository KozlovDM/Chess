package engine.zobrist;

import engine.board.Pieces;

public class ZobristHash implements Comparable<ZobristHash> {
    private long hash;

    public ZobristHash(Pieces pieces, boolean blackMove, boolean WLCastling, boolean WSCastling, boolean BLCastling, boolean BSCastling) {
        if (blackMove) invertMove();
        if (WLCastling) invertWLCastling();
        if (WSCastling) invertWSCastling();
        if (BLCastling) invertBLCastling();
        if (BSCastling) invertBSCastling();

        int side;
        for (int i = 0; i < 64; i++) {
            if (pieces.getSideBitBoard()[Pieces.WHITE].getBit(i)) side = Pieces.WHITE;
            else if (pieces.getSideBitBoard()[Pieces.BLACK].getBit(i)) side = Pieces.BLACK;
            else continue;

            for (int j = 0; j < 6; j++) {
                if (pieces.getPieceBitBoard()[side][j].getBit(i)) {
                    invertPiece(i, j, side);
                    break;
                }
            }
        }
    }

    public ZobristHash(ZobristHash zobristHash) {
        hash = zobristHash.hash;
    }

    public void invertPiece(int square, int type, int side) {
        hash = hash ^ ZobristHashConsteval.CONSTANTS[square][side][type];
    }

    public void invertMove() {
        hash = hash ^ ZobristHashConsteval.BLACK_MOVE;
    }

    public void invertWLCastling() {
        hash = hash ^ ZobristHashConsteval.WHITE_LONG_CASTLING;
    }

    public void invertWSCastling() {
        hash = hash ^ ZobristHashConsteval.WHITE_SHORT_CASTLING;
    }

    public void invertBLCastling() {
        hash = hash ^ ZobristHashConsteval.BLACK_LONG_CASTLING;
    }

    public void invertBSCastling() {
        hash = hash ^ ZobristHashConsteval.BLACK_SHORT_CASTLING;
    }

    public long getHash() {
        return hash;
    }

    @Override
    public int compareTo(ZobristHash o) {
        return Long.compare(this.getHash(), o.getHash());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ZobristHash that = (ZobristHash) o;

        return hash == that.hash;
    }

    @Override
    public int hashCode() {
        return (int) (hash ^ (hash >>> 32));
    }
}
