package engine.board;

import engine.zobrist.ZobristHash;

import java.util.HashMap;
import java.util.Map;

public class RepetitionHistory {
    private final Map<ZobristHash, Integer> hashes;

    public RepetitionHistory() {
        this.hashes = new HashMap<>();
    }

    public RepetitionHistory(RepetitionHistory repetitionHistory) {
        this.hashes = new HashMap<>(repetitionHistory.hashes);
    }

    public void addPosition(ZobristHash hash) {
        Integer count = hashes.get(hash);
        if (count == null) {
            hashes.put(hash, 1);
        } else {
            hashes.put(hash, ++count);
        }
    }

    public void clear() {
        hashes.clear();
    }

    public Integer getRepetitionNumber(ZobristHash hash) {
        return hashes.get(hash);
    }
}
