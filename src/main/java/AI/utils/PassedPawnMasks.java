package AI.utils;

import engine.bitBoard.BitBoard;

public class PassedPawnMasks {

    public static final BitBoard[] WHITE_PASSED_PAWN_MASKS;
    public static final BitBoard[] BLACK_PASSED_PAWN_MASKS;

    static {
        WHITE_PASSED_PAWN_MASKS = calc_white_passed_pawn_masks();
        BLACK_PASSED_PAWN_MASKS = calc_black_passed_pawn_masks();
    }

    private static BitBoard[] calc_white_passed_pawn_masks() {
        BitBoard[] masks = new BitBoard[64];
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                for (int y1 = y + 1; y1 < 8; y1++) {
                    setBit(masks, x, y, y1);
                }
            }
        }

        return masks;
    }


    private static BitBoard[] calc_black_passed_pawn_masks() {
        BitBoard[] masks = new BitBoard[64];
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                for (int y1 = y - 1; y1 >= 0; y1--) {
                    setBit(masks, x, y, y1);
                }
            }
        }

        return masks;
    }

    private static void setBit(BitBoard[] masks, int x, int y, int y1) {
        if (x != 0) {
            if (masks[y * 8 + x] == null) {
                masks[y * 8 + x] = new BitBoard(y1 * 8 + x - 1);
            } else {
                masks[y * 8 + x].setBit(y1 * 8 + x - 1);
            }
        }
        if (x != 7) {
            if (masks[y * 8 + x] == null) {
                masks[y * 8 + x] = new BitBoard(y1 * 8 + x + 1);
            } else {
                masks[y * 8 + x].setBit(y1 * 8 + x + 1);
            }
        }
        if (masks[y * 8 + x] == null) {
            masks[y * 8 + x] = new BitBoard(y1 * 8 + x);
        } else {
            masks[y * 8 + x].setBit(y1 * 8 + x);
        }
    }
}
