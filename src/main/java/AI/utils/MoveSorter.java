package AI.utils;

import AI.model.Material;
import engine.bitBoard.BitBoard;
import engine.board.Move;
import engine.board.Pieces;
import engine.moveGeneration.PsLegalMoveMaskGen;

import java.util.List;

public class MoveSorter {

    public static void sort(Pieces pieces, List<Move> moves) {
        for (int i = 0; i < moves.size() - 1; i++) {
            for (int j = 0; j < moves.size() - i - 1; j++) {
                if (evaluateMove(pieces, moves.get(j)) < evaluateMove(pieces, moves.get(j + 1))) {
                    Move tmp = moves.get(j);
                    moves.set(j, moves.get(j + 1));
                    moves.set(j + 1, tmp);
                }
            }
        }
    }

    private static long evaluateMove(Pieces pieces, Move move) {
        long evaluation = 0;

        if (move.getAttackerType() != Pieces.PAWN) {
            BitBoard opponentPawnAttacks = new BitBoard(PsLegalMoveMaskGen.generatePawnLeftCapturesMask(pieces, Pieces.inverse(move.getAttackerSide()), true)
                    | PsLegalMoveMaskGen.generatePawnRightCapturesMask(pieces, Pieces.inverse(move.getAttackerSide()), true));
            if (opponentPawnAttacks.getBit(move.getTo())) {
                evaluation -= switch (move.getAttackerType()) {
                    case Pieces.KNIGHT -> Material.KNIGHT;
                    case Pieces.BISHOP -> Material.BISHOP;
                    case Pieces.ROOK -> Material.ROOK;
                    case Pieces.QUEEN -> Material.QUEEN;
                    default -> 0;
                };
            }
        }

        if (move.getDefenderType() != -1) {
            evaluation += switch (move.getDefenderType()) {
                case Pieces.PAWN -> 1000 * Material.PAWN;
                case Pieces.KNIGHT -> 1000 * Material.KNIGHT;
                case Pieces.BISHOP -> 1000 * Material.BISHOP;
                case Pieces.ROOK -> 1000 * Material.ROOK;
                case Pieces.QUEEN -> 1000 * Material.QUEEN;
                default -> 0;
            };
            evaluation -= switch (move.getAttackerType()) {
                case Pieces.PAWN -> Material.PAWN;
                case Pieces.KNIGHT -> Material.KNIGHT;
                case Pieces.BISHOP -> Material.BISHOP;
                case Pieces.ROOK -> Material.ROOK;
                case Pieces.QUEEN -> Material.QUEEN;
                default -> 0;
            };
        }

        return evaluation;
    }
}
