package engine.bitBoard;

public class BitboardColumns {
    public static final BitBoard[] COLUMNS = calculatedColumns();
    public static final BitBoard[] INVERSION_COLUMNS = calculatedInversionColumns();

    private static BitBoard[] calculatedColumns() {
        BitBoard[] bitBoards =  new BitBoard[8];
        for (int i = 0; i < bitBoards.length; i++) {
            bitBoards[i] = new BitBoard();
            for (int j = 0; j < bitBoards.length; j++) {
                bitBoards[i].setBit(j * 8 + i);
            }
        }
        return bitBoards;
    }

    private static BitBoard[] calculatedInversionColumns() {
        BitBoard[] bitBoards =  new BitBoard[8];
        for (int i = 0; i < COLUMNS.length; i++) {
            bitBoards[i] = new BitBoard((-COLUMNS[i].getBoard()) - 1);
        }
        return bitBoards;
    }
}