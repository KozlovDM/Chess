package engine.bitBoard;

public class BitBoard {
    private static final int[] BIT_SCAN_TABLE = {
            0, 47, 1, 56, 48, 27, 2, 60,
            57, 49, 41, 37, 28, 16, 3, 61,
            54, 58, 35, 52, 50, 42, 21, 44,
            38, 32, 29, 23, 17, 11, 4, 62,
            46, 55, 26, 59, 40, 36, 15, 53,
            34, 51, 20, 43, 31, 22, 10, 45,
            25, 39, 14, 33, 19, 30, 9, 24,
            13, 18, 8, 12, 7, 6, 5, 63
    };

    private long board;

    public BitBoard(long board) {
        this.board = board;
    }

    public BitBoard(int square) {
        this.board = 1L << square;
    }

    public BitBoard() {
    }

    public void setBit(int square) {
        board |= 1L << square;
    }

    public void clearBit(int square) {
        board &= ~(1L << square);
    }

    public boolean getBit(int square) {
        return (board & (1L << square)) != 0;
    }

    public long countBit() {
        return Long.bitCount(board);
    }

    public int bitScanForward() {
        return BIT_SCAN_TABLE[(int) (((board ^ (board - 1)) * 0x03f79d71b4cb0a89L) >>> 58)];
    }

    public int bitScanReverse() {
        long tmp = board;
        tmp = tmp | (tmp >>> 1);
        tmp = tmp | (tmp >>> 2);
        tmp = tmp | (tmp >>> 4);
        tmp = tmp | (tmp >>> 8);
        tmp = tmp | (tmp >>> 16);
        tmp = tmp | (tmp >>> 32);
        return BIT_SCAN_TABLE[(int) ((tmp * 0x03f79d71b4cb0a89L) >>> 58)];
    }

    public long getBoard() {
        return board;
    }

    public void setBoard(long board) {
        this.board = board;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BitBoard bitBoard = (BitBoard) o;

        return board == bitBoard.board;
    }

    @Override
    public int hashCode() {
        return (int) (board ^ (board >>> 32));
    }
}
