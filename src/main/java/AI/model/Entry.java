package AI.model;

import engine.zobrist.ZobristHash;

public class Entry {
    private ZobristHash hash;
    private long depth;
    private int bestMoveIndex;

    public Entry(ZobristHash hash, long depth, int bestMoveIndex) {
        this.hash = hash;
        this.depth = depth;
        this.bestMoveIndex = bestMoveIndex;
    }

    public ZobristHash getHash() {
        return hash;
    }

    public long getDepth() {
        return depth;
    }

    public int getBestMoveIndex() {
        return bestMoveIndex;
    }
}
