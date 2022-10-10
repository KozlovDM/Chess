import engine.board.Move;
import engine.board.Position;
import engine.moveGeneration.LegalMoveGen;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

public class TestChess {
    private static final String FEN = "rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8";

    private static final long[] NODES = {1, 44, 1486, 62379, 2103487, 89941194};

    @Test
    public void LegalMoveGenTester() {
        Position position = new Position(FEN);
        for (int i = 1; i < NODES.length; i++) {
            Date date = new Date();
            int actual = getNodesNumber(position, i);
            System.out.println("значение " + actual);
            System.out.println("время " + (new Date().getTime() - date.getTime()));
            Assertions.assertEquals(NODES[i], actual);
        }
    }

    private static int getNodesNumber(Position position, long depth) {
        if (depth == 0) {
            return 1;
        }
        int ctr = 0;

        List<Move> moves = LegalMoveGen.generate(position, position.getSide(), false, false);
        for (Move move : moves) {
            Position copy = new Position(position);
            copy.move(move);
            copy.setSide();
            ctr = ctr + getNodesNumber(copy, depth - 1);
        }
        return ctr;
    }
}
