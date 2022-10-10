package engine.bitBoard;

public class BitboardRows {
    public static final BitBoard[] ROWS = calcRows();

    private static BitBoard[] calcRows() {
        BitBoard[] bitBoards = new BitBoard[8];
        for (int i = 0; i < bitBoards.length; i++) {
            bitBoards[i] = new BitBoard();
            for (int j = 0; j < bitBoards.length; j++) {
                bitBoards[i].setBit(i * 8 + j);
            }
        }
        return bitBoards;
    }
}