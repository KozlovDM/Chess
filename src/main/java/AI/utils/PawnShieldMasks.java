package AI.utils;

import engine.bitBoard.BitBoard;

public class PawnShieldMasks {

    public static final BitBoard[] WHITE_PAWN_SHIELD_MASKS;
    public static final BitBoard[] BLACK_PAWN_SHIELD_MASKS;

    static {
        WHITE_PAWN_SHIELD_MASKS = calcWhitePawnShieldMasks();
        BLACK_PAWN_SHIELD_MASKS = calcBlackPawnShieldMasks();
    }

    private static BitBoard[] calcWhitePawnShieldMasks() {
        BitBoard[] masks = new BitBoard[64];

        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                if (y == 7) {
                    masks[y * 8 + x] = new BitBoard(0L);
                    continue;
                }
                if (x != 0) {
                    setValue(masks, x, y, (y + 1) * 8 + x - 1);
                }
                if (x != 7) {
                    setValue(masks, x, y, (y + 1) * 8 + x + 1);
                }
                setValue(masks, x, y, (y + 1) * 8 + x);

                if (y != 6) {
                    if (x != 0) {
                        setValue(masks, x, y, (y + 2) * 8 + x - 1);
                    }
                    if (x != 7) {
                        setValue(masks, x, y, (y + 2) * 8 + x + 1);
                    }
                    setValue(masks, x, y, (y + 2) * 8 + x);
                }
            }
        }

        return masks;
    }


    private static BitBoard[] calcBlackPawnShieldMasks() {
        BitBoard[] masks = new BitBoard[64];

        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                if (y == 0) {
                    masks[x] = new BitBoard(0L);
                    continue;
                }
                if (x != 0) {
                    setValue(masks, x, y, (y - 1) * 8 + x - 1);
                }
                if (x != 7) {
                    setValue(masks, x, y, (y - 1) * 8 + x + 1);
                }
                setValue(masks, x, y, (y - 1) * 8 + x);

                if (y != 1) {
                    if (x != 0) {
                        setValue(masks, x, y, (y - 2) * 8 + x - 1);
                    }
                    if (x != 7) {
                        setValue(masks, x, y, (y - 2) * 8 + x + 1);
                    }
                    setValue(masks, x, y, (y - 2) * 8 + x);
                }
            }
        }

        return masks;
    }

    private static void setValue(BitBoard[] masks, int x, int y, int square) {
        if (masks[y * 8 + x] == null) {
            masks[y * 8 + x] = new BitBoard(square);
        } else {
            masks[y * 8 + x].setBit(square);
        }
    }
}
