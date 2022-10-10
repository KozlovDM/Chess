package engine.zobrist;

public class ZobristHashConsteval {
    private static final long SEED = 0x98f107;
    private static final long MULTIPLIER = 0x71abc9;
    private static final long SUMMAND = 0xff1b3f;

    public static final long[][][] CONSTANTS = CalculatedConstants();
    public static final long BLACK_MOVE = nextRandom(CONSTANTS[63][1][5]);
    public static final long WHITE_LONG_CASTLING = nextRandom(BLACK_MOVE);
    public static final long WHITE_SHORT_CASTLING = nextRandom(WHITE_LONG_CASTLING);
    public static final long BLACK_LONG_CASTLING = nextRandom(WHITE_SHORT_CASTLING);
    public static final long BLACK_SHORT_CASTLING = nextRandom(BLACK_LONG_CASTLING);

    private static long nextRandom(long previous) {
        return MULTIPLIER * previous + SUMMAND;
    }

    private static long[][][] CalculatedConstants() {
        long[][][] result = new long[64][2][6];
        long previous = SEED;
        for (int i = 0; i < 64; i++) {
            for (int j = 0; j < 2; j++) {
                for (int k = 0; k < 6; k = k + 1) {
                    previous = nextRandom(previous);
                    result[i][j][k] = previous;
                }
            }
        }
        return result;
    }
}
