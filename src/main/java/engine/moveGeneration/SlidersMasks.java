package engine.moveGeneration;

import engine.bitBoard.BitBoard;

public class SlidersMasks {

    public static final BitBoard[][] MASKS;

    public static final int NORTH = 0;
    public static final int SOUTH = 1;
    public static final int WEST = 2;
    public static final int EAST = 3;
    public static final int NORTH_WEST = 4;
    public static final int NORTH_EAST = 5;
    public static final int SOUTH_WEST = 6;
    public static final int SOUTH_EAST = 7;

    static {
        MASKS = calculateMask();
    }

    private static BitBoard calculateMask(int position, int direction) {
        BitBoard mask = new BitBoard();

        int x = position % 8;
        int y = position / 8;

        while (true) {
            switch (direction) {
                case NORTH -> y++;
                case SOUTH -> y--;
                case WEST -> x--;
                case EAST -> x++;
                case NORTH_WEST -> {
                    y++;
                    x--;
                }
                case NORTH_EAST -> {
                    y++;
                    x++;
                }
                case SOUTH_WEST -> {
                    y--;
                    x--;
                }
                case SOUTH_EAST -> {
                    y--;
                    x++;
                }
            }
            if (x > 7 || x < 0 || y > 7 || y < 0) break;
            mask.setBit(y * 8 + x);
        }
        return mask;
    }

    private static BitBoard[][] calculateMask() {
        BitBoard[][] masks = new BitBoard[64][8];

        for (int i = 0; i < 64; i++) {
            for (int j = 0; j < 8; j++) {
                masks[i][j] = calculateMask(i, j);
            }
        }
        return masks;
    }
}
