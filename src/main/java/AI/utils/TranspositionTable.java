package AI.utils;

import AI.model.Entry;
import engine.zobrist.ZobristHash;

import java.util.TreeMap;

public class TranspositionTable {
    private final TreeMap<ZobristHash, Entry> entries;

    public TranspositionTable() {
        this.entries = new TreeMap<>();
    }

    public void addEntry(Entry entry) {
        Entry existsEntry = entries.get(entry.getHash());
        if (existsEntry == null || (entries.lastEntry() != null && existsEntry == entries.lastEntry().getValue()) || existsEntry.getDepth() < entry.getDepth()) {
            entries.put(entry.getHash(), entry);
        }
    }

    public int tryToFindBestMoveIndex(ZobristHash hash) {
        Entry existsEntry = entries.get(hash);
        if (existsEntry == null || entries.lastEntry() == null || hash == entries.lastEntry().getKey()) {
            return 255;
        }
        return existsEntry.getBestMoveIndex();
    }
}
