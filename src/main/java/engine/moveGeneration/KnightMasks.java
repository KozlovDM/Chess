package engine.moveGeneration;

import engine.bitBoard.BitBoard;

public class KnightMasks {
    public static BitBoard[] MASKS;

    static {
        MASKS = calculateMask();
    }

    private static BitBoard[] calculateMask() {
        BitBoard[] masks = new BitBoard[64];
        int dx;
        int dy;

        for (int x0 = 0; x0 < 8; x0++) {
            for (int y0 = 0; y0 < 8; y0++) {

                for (int x1 = 0; x1 < 8; x1++) {
                    for (int y1 = 0; y1 < 8; y1++) {

                        dx = Math.abs(x0 - x1);
                        dy = Math.abs(y0 - y1);

                        if ((dx == 2 && dy == 1) || (dx == 1 && dy == 2)) {
                            if (masks[y0 * 8 + x0] == null) {
                                masks[y0 * 8 + x0] = new BitBoard(y1 * 8 + x1);
                            } else {
                                masks[y0 * 8 + x0].setBit(y1 * 8 + x1);
                            }
                        }
                    }
                }
            }
        }

        return masks;
    }
}
