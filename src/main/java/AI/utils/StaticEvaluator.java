package AI.utils;

import AI.model.*;
import engine.bitBoard.BitBoard;
import engine.bitBoard.BitboardColumns;
import engine.board.Pieces;
import engine.moveGeneration.PsLegalMoveMaskGen;

public class StaticEvaluator {

    private static final int TWO_BISHOPS = 50;

    public static long evaluate(Pieces pieces, boolean wLCastling, boolean wSCastling, boolean bLCastling, boolean bSCastling, boolean whiteCastlingHappened, boolean blackCastlingHappened) {
        long evaluation = 0;

        long material = material(pieces);
        long mobility = mobility(pieces);
        long double_pawn = pawnStructureDoublePawn(pieces);
        long connected_pawn = pawnStructureConnectedPawn(pieces);
        long pawn_promotion =pawnStructurePawnPromotion(pieces);
        long crashed_castling = kingSafetyCrashedCastling(wLCastling, wSCastling, bLCastling, bSCastling, whiteCastlingHappened, blackCastlingHappened);
        long pawnShield = kingSafetyPawnShield(pieces, whiteCastlingHappened, blackCastlingHappened);
        long twoBishops = twoBishops(pieces);
        long endgame = endgame(pieces, material >= 0);

        evaluation += material;
        evaluation += mobility;
        evaluation += double_pawn;
        evaluation += connected_pawn;
        evaluation += pawn_promotion;
        evaluation += crashed_castling;
        evaluation += pawnShield;
        evaluation += twoBishops;
        evaluation += endgame;

        return evaluation;
    }

    private static long material(Pieces pieces) {
        long material = 0;

        material += Material.PAWN * (pieces.getPieceBitBoard()[Pieces.WHITE][Pieces.PAWN].countBit() - pieces.getPieceBitBoard()[Pieces.BLACK][Pieces.PAWN].countBit());
        material += Material.KNIGHT * (pieces.getPieceBitBoard()[Pieces.WHITE][Pieces.KNIGHT].countBit() - pieces.getPieceBitBoard()[Pieces.BLACK][Pieces.KNIGHT].countBit());
        material += Material.BISHOP * (pieces.getPieceBitBoard()[Pieces.WHITE][Pieces.BISHOP].countBit() - pieces.getPieceBitBoard()[Pieces.BLACK][Pieces.BISHOP].countBit());
        material += Material.ROOK * (pieces.getPieceBitBoard()[Pieces.WHITE][Pieces.ROOK].countBit() - pieces.getPieceBitBoard()[Pieces.BLACK][Pieces.ROOK].countBit());
        material += Material.QUEEN * (pieces.getPieceBitBoard()[Pieces.WHITE][Pieces.QUEEN].countBit() - pieces.getPieceBitBoard()[Pieces.BLACK][Pieces.QUEEN].countBit());

        return material;
    }

    private static long mobility(Pieces pieces) {
        long mobility = 0;

        BitBoard[][] iterationMasks = pieces.getPieceBitBoard();
        int index;

        long knightMoves = 0;
        long bishopMoves = 0;
        long rookMoves = 0;
        long queenMoves = 0;

        while (iterationMasks[Pieces.WHITE][Pieces.KNIGHT].getBoard() != 0) {
            index = iterationMasks[Pieces.WHITE][Pieces.KNIGHT].bitScanForward();
            iterationMasks[Pieces.WHITE][Pieces.KNIGHT].clearBit(index);
            knightMoves += PsLegalMoveMaskGen.generateKnightMask(pieces, index, Pieces.WHITE, false).countBit();
        }
        while (iterationMasks[Pieces.WHITE][Pieces.BISHOP].getBoard() != 0) {
            index = iterationMasks[Pieces.WHITE][Pieces.BISHOP].bitScanForward();
            iterationMasks[Pieces.WHITE][Pieces.BISHOP].clearBit(index);
            bishopMoves += PsLegalMoveMaskGen.generateBishopMask(pieces, index, Pieces.WHITE, false).countBit();
        }
        while (iterationMasks[Pieces.WHITE][Pieces.ROOK].getBoard() != 0) {
            index = iterationMasks[Pieces.WHITE][Pieces.ROOK].bitScanForward();
            iterationMasks[Pieces.WHITE][Pieces.ROOK].clearBit(index);
            rookMoves += PsLegalMoveMaskGen.generateRookMask(pieces, index, Pieces.WHITE, false).countBit();
        }
        while (iterationMasks[Pieces.WHITE][Pieces.QUEEN].getBoard() != 0) {
            index = iterationMasks[Pieces.WHITE][Pieces.QUEEN].bitScanForward();
            iterationMasks[Pieces.WHITE][Pieces.QUEEN].clearBit(index);
            queenMoves += PsLegalMoveMaskGen.generateQueenMask(pieces, index, Pieces.WHITE, false).countBit();
        }

        while (iterationMasks[Pieces.BLACK][Pieces.KNIGHT].getBoard() != 0) {
            index = iterationMasks[Pieces.BLACK][Pieces.KNIGHT].bitScanForward();
            iterationMasks[Pieces.BLACK][Pieces.KNIGHT].clearBit(index);
            knightMoves -= PsLegalMoveMaskGen.generateKnightMask(pieces, index, Pieces.BLACK, false).countBit();
        }
        while (iterationMasks[Pieces.BLACK][Pieces.BISHOP].getBoard() != 0) {
            index = iterationMasks[Pieces.BLACK][Pieces.BISHOP].bitScanForward();
            iterationMasks[Pieces.BLACK][Pieces.BISHOP].clearBit(index);
            bishopMoves -= PsLegalMoveMaskGen.generateBishopMask(pieces, index, Pieces.BLACK, false).countBit();
        }
        while (iterationMasks[Pieces.BLACK][Pieces.ROOK].getBoard() != 0) {
            index = iterationMasks[Pieces.BLACK][Pieces.ROOK].bitScanForward();
            iterationMasks[Pieces.BLACK][Pieces.ROOK].clearBit(index);
            rookMoves -= PsLegalMoveMaskGen.generateRookMask(pieces, index, Pieces.BLACK, false).countBit();
        }
        while (iterationMasks[Pieces.BLACK][Pieces.QUEEN].getBoard() != 0) {
            index = iterationMasks[Pieces.BLACK][Pieces.QUEEN].bitScanForward();
            iterationMasks[Pieces.BLACK][Pieces.QUEEN].clearBit(index);
            queenMoves -= PsLegalMoveMaskGen.generateQueenMask(pieces, index, Pieces.BLACK, false).countBit();
        }

        mobility += Mobility.KNIGHT * knightMoves;
        mobility += Mobility.BISHOP * bishopMoves;
        mobility += Mobility.ROOK * rookMoves;
        mobility += Mobility.QUEEN * queenMoves;

        return mobility;
    }

    private static long pawnStructureDoublePawn(Pieces pieces) {
        long double_pawn = 0;

        long doublePawnCtr = 0;

        long whitePawns;
        long blackPawns;

        for (int x = 0; x < 8; x = x + 1) {
            whitePawns = pieces.getPieceBitBoard()[Pieces.WHITE][Pieces.PAWN].getBoard() & BitboardColumns.COLUMNS[x].countBit();
            blackPawns = pieces.getPieceBitBoard()[Pieces.BLACK][Pieces.PAWN].getBoard() & BitboardColumns.COLUMNS[x].countBit();

            doublePawnCtr += Math.max(0, whitePawns - 1);
            doublePawnCtr -= Math.max(0, blackPawns - 1);
        }

        double_pawn += PawnStructure.DOUBLE_PAWN * doublePawnCtr;

        return double_pawn;
    }

    private static long pawnStructureConnectedPawn(Pieces pieces) {
        long connectedPawn = 0;
        long connectedPawnCtr = 0;

        long whiteCaptures = PsLegalMoveMaskGen.generatePawnLeftCapturesMask(pieces, Pieces.WHITE, true) | PsLegalMoveMaskGen.generatePawnRightCapturesMask(pieces, Pieces.WHITE, true);
        long blackCaptures = PsLegalMoveMaskGen.generatePawnLeftCapturesMask(pieces, Pieces.BLACK, true) | PsLegalMoveMaskGen.generatePawnRightCapturesMask(pieces, Pieces.BLACK, true);

        connectedPawnCtr += new BitBoard(whiteCaptures & pieces.getPieceBitBoard()[Pieces.WHITE][Pieces.PAWN].getBoard()).countBit();
        connectedPawnCtr -= new BitBoard(blackCaptures & pieces.getPieceBitBoard()[Pieces.BLACK][Pieces.PAWN].getBoard()).countBit();

        connectedPawn += PawnStructure.CONNECTED_PAWN * connectedPawnCtr;

        return connectedPawn;
    }

    private static long pawnStructurePawnPromotion(Pieces pieces) {
        long pawn_promotion = 0;

        BitBoard white_pawns = pieces.getPieceBitBoard()[Pieces.WHITE][Pieces.PAWN];
        BitBoard black_pawns = pieces.getPieceBitBoard()[Pieces.BLACK][Pieces.PAWN];

        int index;

        while (white_pawns.getBoard() != 0) {
            index = white_pawns.bitScanForward();
            white_pawns.clearBit(index);

            if ((PassedPawnMasks.WHITE_PASSED_PAWN_MASKS[index].getBoard() & pieces.getPieceBitBoard()[Pieces.BLACK][Pieces.PAWN].getBoard()) != 0) {
                pawn_promotion += PawnStructure.DEFAULT_PAWN_PROMOTION[index / 8];
            } else {
                pawn_promotion += PawnStructure.PASSED_PAWN_PROMOTION[index / 8];
            }
        }

        while (black_pawns.getBoard() != 0) {
            index = black_pawns.bitScanForward();
            black_pawns.clearBit(index);

            if ((PassedPawnMasks.BLACK_PASSED_PAWN_MASKS[index].getBoard() & pieces.getPieceBitBoard()[Pieces.WHITE][Pieces.PAWN].getBoard()) != 0) {
                pawn_promotion = pawn_promotion - PawnStructure.DEFAULT_PAWN_PROMOTION[7 - index / 8];
            } else {
                pawn_promotion = pawn_promotion - PawnStructure.PASSED_PAWN_PROMOTION[7 - index / 8];
            }
        }

        return pawn_promotion;
    }

    private static long kingSafetyCrashedCastling(boolean wLCastling, boolean wSCastling, boolean bLCastling, boolean bSCastling, boolean whiteCastlingHappened, boolean blackCastlingHappened) {
        long crashed_castling = 0;

        if (!whiteCastlingHappened) {
            if (!wLCastling) {
                crashed_castling = crashed_castling + KingSafety.CRASHED_CASTLING;
            }
            if (!wSCastling) {
                crashed_castling = crashed_castling + KingSafety.CRASHED_CASTLING;
            }
        }

        if (!blackCastlingHappened) {
            if (!bLCastling) {
                crashed_castling = crashed_castling - KingSafety.CRASHED_CASTLING;
            }
            if (!bSCastling) {
                crashed_castling = crashed_castling - KingSafety.CRASHED_CASTLING;
            }
        }

        return crashed_castling;
    }

    private static long kingSafetyPawnShield(Pieces pieces, boolean white_castling_happened, boolean black_castling_happened) {
        long pawnShield = 0;
        long pawnShieldCtr = 0;

        if (white_castling_happened) {
            long whitePawns = pieces.getPieceBitBoard()[Pieces.WHITE][Pieces.PAWN].getBoard();
            long whitePawnShield = PawnShieldMasks.WHITE_PAWN_SHIELD_MASKS[pieces.getPieceBitBoard()[Pieces.WHITE][Pieces.KING].bitScanForward()].getBoard();
            pawnShieldCtr += new BitBoard(whitePawns & whitePawnShield).countBit();
        }

        if (black_castling_happened) {
            long blackPawns = pieces.getPieceBitBoard()[Pieces.BLACK][Pieces.PAWN].getBoard();
            long blackPawnShield = PawnShieldMasks.BLACK_PAWN_SHIELD_MASKS[pieces.getPieceBitBoard()[Pieces.BLACK][Pieces.KING].bitScanForward()].getBoard();
            pawnShieldCtr -= new BitBoard(blackPawns & blackPawnShield).countBit();
        }

        pawnShield += KingSafety.PAWN_SHIELD * pawnShieldCtr;

        return pawnShield;
    }

    private static long twoBishops(Pieces pieces) {
        long twoBishops = 0;

        if (pieces.getPieceBitBoard()[Pieces.WHITE][Pieces.BISHOP].countBit() >= 2){
            twoBishops += TWO_BISHOPS;
        }
        if (pieces.getPieceBitBoard()[Pieces.BLACK][Pieces.BISHOP].countBit() >= 2){
            twoBishops -= TWO_BISHOPS;
        }

        return twoBishops;
    }

    private static long endgame(Pieces pieces, boolean whiteLeading) {
        int endgame = 0;

        if (pieces.getAll().countBit() > Endgame.MAXIMUM_PIECES_FOR_ENDGAME){
            return endgame;
        }

        int attackerSide;
        int defenderSide;

        if (whiteLeading) {
            attackerSide = Pieces.WHITE;
            defenderSide = Pieces.BLACK;
        } else {
            attackerSide = Pieces.BLACK;
            defenderSide = Pieces.WHITE;
        }

        int attackerKingP = pieces.getPieceBitBoard()[attackerSide][Pieces.KING].bitScanForward();
        int attackerKingX = attackerKingP % 8;
        int attackerKingY = attackerKingP / 8;

        int defenderKingP = pieces.getPieceBitBoard()[defenderSide][Pieces.KING].bitScanForward();
        int defenderKingX = defenderKingP % 8;
        int defenderKingY = defenderKingP / 8;

        endgame += Endgame.ATTACKER_KING_PROXIMITY_TO_DEFENDER_KING * (16 - Math.abs(attackerKingX - defenderKingX) - Math.abs(attackerKingY - defenderKingY));
        endgame += Endgame.DISTANCE_BETWEEN_DEFENDER_KING_AND_MIDDLE * (Math.abs(defenderKingX - 3) + Math.abs(defenderKingY - 4));

        if (!whiteLeading) {
            endgame = -endgame;
        }

        return endgame;
    }
}
