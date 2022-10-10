package engine.moveGeneration;

import engine.bitBoard.BitBoard;
import engine.board.Move;
import engine.board.Pieces;
import engine.board.Position;

import java.util.ArrayList;
import java.util.List;

public class LegalMoveGen {

    public static List<Move> generate(Position position, int side, boolean onlyCaptures, boolean isNotLegalCheck) {
        List<Move> moves = new ArrayList<>(218);

        BitBoard pawnLeftCapturesMask = new BitBoard(PsLegalMoveMaskGen.generatePawnLeftCapturesMask(position.getPieces(), side, false));
        BitBoard pawnRightCapturesMask = new BitBoard(PsLegalMoveMaskGen.generatePawnRightCapturesMask(position.getPieces(), side, false));

        int pawnLeftCapture;
        int pawnRightCapture;

        if (side == Pieces.WHITE) {
            pawnLeftCapture = -7;
            pawnRightCapture = -9;
        } else {
            pawnLeftCapture = 9;
            pawnRightCapture = 7;
        }

        pawnMaskToMoves(position.getPieces(), pawnLeftCapturesMask, side, pawnLeftCapture, true, Move.DEFAULT, moves, isNotLegalCheck);
        pawnMaskToMoves(position.getPieces(), pawnRightCapturesMask, side, pawnRightCapture, true, Move.DEFAULT, moves, isNotLegalCheck);

        if (!onlyCaptures) {
            BitBoard pawnDefaultMask = PsLegalMoveMaskGen.generatePawnDefaultMask(position.getPieces(), side);
            BitBoard pawnLongMask = PsLegalMoveMaskGen.generatePawnLongMask(position.getPieces(), side, pawnDefaultMask);

            int pawnDefaultMove;
            int pawnLongMove;

            if (side == Pieces.WHITE) {
                pawnDefaultMove = -8;
                pawnLongMove = -16;
            } else {
                pawnDefaultMove = 8;
                pawnLongMove = 16;
            }

            pawnMaskToMoves(position.getPieces(), pawnDefaultMask, side, pawnDefaultMove, false, Move.DEFAULT, moves, isNotLegalCheck);
            pawnMaskToMoves(position.getPieces(), pawnLongMask, side, pawnLongMove, false, Move.PAWN_LONG_MOVE, moves, isNotLegalCheck);
        }

        BitBoard allKnights = new BitBoard(position.getPieces().getPieceBitBoard()[side][Pieces.KNIGHT].getBoard());
        BitBoard allBishops = new BitBoard(position.getPieces().getPieceBitBoard()[side][Pieces.BISHOP].getBoard());
        BitBoard allRooks = new BitBoard(position.getPieces().getPieceBitBoard()[side][Pieces.ROOK].getBoard());
        BitBoard allQueens = new BitBoard(position.getPieces().getPieceBitBoard()[side][Pieces.QUEEN].getBoard());

        int attackerP;
        BitBoard mask;

        while (allKnights.getBoard() != 0) {
            attackerP = allKnights.bitScanForward();
            allKnights.clearBit(attackerP);
            mask = PsLegalMoveMaskGen.generateKnightMask(position.getPieces(), attackerP, side, onlyCaptures);
            pieceMaskToMoves(position.getPieces(), mask, attackerP, Pieces.KNIGHT, side, moves, isNotLegalCheck);
        }
        while (allBishops.getBoard() != 0) {
            attackerP = allBishops.bitScanForward();
            allBishops.clearBit(attackerP);
            mask = PsLegalMoveMaskGen.generateBishopMask(position.getPieces(), attackerP, side, onlyCaptures);
            pieceMaskToMoves(position.getPieces(), mask, attackerP, Pieces.BISHOP, side, moves, isNotLegalCheck);
        }
        while (allRooks.getBoard() != 0) {
            attackerP = allRooks.bitScanForward();
            allRooks.clearBit(attackerP);
            mask = PsLegalMoveMaskGen.generateRookMask(position.getPieces(), attackerP, side, onlyCaptures);
            pieceMaskToMoves(position.getPieces(), mask, attackerP, Pieces.ROOK, side, moves, isNotLegalCheck);
        }
        while (allQueens.getBoard() != 0) {
            attackerP = allQueens.bitScanForward();
            allQueens.clearBit(attackerP);
            mask = PsLegalMoveMaskGen.generateQueenMask(position.getPieces(), attackerP, side, onlyCaptures);
            pieceMaskToMoves(position.getPieces(), mask, attackerP, Pieces.QUEEN, side, moves, isNotLegalCheck);
        }

        attackerP = position.getPieces().getPieceBitBoard()[side][Pieces.KING].bitScanForward();
        mask = PsLegalMoveMaskGen.generateKingMask(position.getPieces(), attackerP, side, onlyCaptures);
        pieceMaskToMoves(position.getPieces(), mask, attackerP, Pieces.KING, side, moves, isNotLegalCheck);

        addEnPassantCaptures(position.getPieces(), side, position.getEnPassant(), moves);
        if (!onlyCaptures) {
            if (side == Pieces.WHITE) {
                addCastlingMoves(position.getPieces(), Pieces.WHITE, position.iswLCastling(), position.iswSCastling(), moves);
            } else {
                addCastlingMoves(position.getPieces(), Pieces.BLACK, position.isbLCastling(), position.isbSCastling(), moves);
            }
        }
        return moves;
    }

    private static void pawnMaskToMoves(Pieces pieces, BitBoard mask, int attackerSide, int attackerIndex, boolean lookForDefender, int flag, List<Move> moves, boolean isNotLegalCheck) {
        int defenderP;
        int defenderType = -1;
        int defenderSide = Pieces.inverse(attackerSide);

        while (mask.getBoard() != 0) {
            defenderP = mask.bitScanForward();
            mask.clearBit(defenderP);

            if (lookForDefender) {
                defenderType = -1;
                for (int i = 0; i < 6; i++) {
                    if (pieces.getPieceBitBoard()[defenderSide][i].getBit(defenderP)) {
                        defenderType = i;
                        break;
                    }
                }
            }

            Move move = new Move(defenderP + attackerIndex, defenderP, Pieces.PAWN, attackerSide, defenderType, defenderSide, flag);

            if (isNotLegalCheck || isLegal(pieces, move, false)) {
                if (defenderP < 8 || defenderP > 55) {
                    moves.add(new Move(defenderP + attackerIndex, defenderP, Pieces.PAWN, attackerSide, defenderType, defenderSide, Move.PROMOTE_TO_KNIGHT));
                    moves.add(new Move(defenderP + attackerIndex, defenderP, Pieces.PAWN, attackerSide, defenderType, defenderSide, Move.PROMOTE_TO_BISHOP));
                    moves.add(new Move(defenderP + attackerIndex, defenderP, Pieces.PAWN, attackerSide, defenderType, defenderSide, Move.PROMOTE_TO_ROOK));
                    moves.add(new Move(defenderP + attackerIndex, defenderP, Pieces.PAWN, attackerSide, defenderType, defenderSide, Move.PROMOTE_TO_QUEEN));
                } else {
                    moves.add(move);
                }
            }
        }
    }

    private static void pieceMaskToMoves(Pieces pieces, BitBoard mask, int attackerP, int attackerType, int attackerSide, List<Move> moves, boolean isNotLegalCheck) {
        int defenderP;
        int defenderType;

        while (mask.getBoard() != 0) {
            defenderP = mask.bitScanForward();
            mask.clearBit(defenderP);

            defenderType = -1;
            for (int i = 0; i < 6; i = i + 1) {
                if (pieces.getPieceBitBoard()[Pieces.inverse(attackerSide)][i].getBit(defenderP)) {
                    defenderType = i;
                    break;
                }
            }

            Move move = new Move(attackerP, defenderP, attackerType, attackerSide, defenderType, Pieces.inverse(attackerSide), Move.DEFAULT);

            if (isNotLegalCheck || isLegal(pieces, move, false)) {
                moves.add(move);
            }
        }
    }

    private static void addEnPassantCaptures(Pieces pieces, int side, int enPassant, List<Move> moves) {
        if (enPassant == -1) return;

        Move move;

        if (side == Pieces.WHITE) {
            if (enPassant % 8 != 7 && pieces.getPieceBitBoard()[Pieces.WHITE][Pieces.PAWN].getBit(enPassant - 7)) {
                move = new Move(enPassant - 7, enPassant, Pieces.PAWN, Pieces.WHITE, -1, -1, Move.EN_PASSANT_CAPTURE);
                if (isLegal(pieces, move, true)) {
                    moves.add(move);
                }
            }
            if (enPassant % 8 != 0 && pieces.getPieceBitBoard()[Pieces.WHITE][Pieces.PAWN].getBit(enPassant - 9)) {
                move = new Move(enPassant - 9, enPassant, Pieces.PAWN, Pieces.WHITE, -1, -1, Move.EN_PASSANT_CAPTURE);
                if (isLegal(pieces, move, true)) {
                    moves.add(move);
                }
            }
        } else {
            if (enPassant % 8 != 0 && pieces.getPieceBitBoard()[Pieces.BLACK][Pieces.PAWN].getBit(enPassant + 7)) {
                move = new Move(enPassant + 7, enPassant, Pieces.PAWN, Pieces.BLACK, -1, -1, Move.EN_PASSANT_CAPTURE);
                if (isLegal(pieces, move, true)) {
                    moves.add(move);
                }
            }
            if (enPassant % 8 != 7 && pieces.getPieceBitBoard()[Pieces.BLACK][Pieces.PAWN].getBit(enPassant + 9)) {
                move = new Move(enPassant + 9, enPassant, Pieces.PAWN, Pieces.BLACK, -1, -1, Move.EN_PASSANT_CAPTURE);
                if (isLegal(pieces, move, true)) {
                    moves.add(move);
                }
            }
        }
    }

    private static void addCastlingMoves(Pieces pieces, int side, boolean longCastling, boolean shortCastling, List<Move> moves) {
        int index;
        int longCastlingFlag;
        int shortCastlingFlag;
        if (side == Pieces.WHITE) {
            index = 0;
            longCastlingFlag = Move.WHITE_LONG_CASTLING;
            shortCastlingFlag = Move.WHITE_SHORT_CASTLING;
        } else {
            index = 56;
            longCastlingFlag = Move.BLACK_LONG_CASTLING;
            shortCastlingFlag = Move.BLACK_SHORT_CASTLING;
        }

        if (longCastling && pieces.getPieceBitBoard()[side][Pieces.ROOK].getBit(index) && pieces.getEmpty().getBit(index + 1)
                && pieces.getEmpty().getBit(index + 2) && pieces.getEmpty().getBit(index + 3)) {
            if (PsLegalMoveMaskGen.isSafe(pieces, pieces.getPieceBitBoard()[side][Pieces.KING].bitScanForward(), side)
                    && PsLegalMoveMaskGen.isSafe(pieces, index + 2, side) && PsLegalMoveMaskGen.isSafe(pieces, index + 3, side)) {
                moves.add(new Move(index + 4, index + 2, Pieces.KING, side, -1, -1, longCastlingFlag));
            }
        }
        if (shortCastling && pieces.getPieceBitBoard()[side][Pieces.ROOK].getBit(index + 7) && pieces.getEmpty().getBit(index + 5)
                && pieces.getEmpty().getBit(index + 6)) {
            if (PsLegalMoveMaskGen.isSafe(pieces, pieces.getPieceBitBoard()[side][Pieces.KING].bitScanForward(), side)
                    && PsLegalMoveMaskGen.isSafe(pieces, index + 5, side) && PsLegalMoveMaskGen.isSafe(pieces, index + 6, side)) {
                moves.add(new Move(index + 4, index + 6, Pieces.KING, side, -1, -1, shortCastlingFlag));
            }
        }
    }

    private static boolean isLegal(Pieces pieces, Move move, boolean enPassantCapture) {
        Pieces copyPiece = new Pieces(pieces);
        copyPiece.getPieceBitBoard()[move.getAttackerSide()][move.getAttackerType()].clearBit(move.getFrom());
        copyPiece.getPieceBitBoard()[move.getAttackerSide()][move.getAttackerType()].setBit(move.getTo());
        if (move.getDefenderType() != -1) {
            copyPiece.getPieceBitBoard()[move.getDefenderSide()][move.getDefenderType()].clearBit(move.getTo());
        } else if (enPassantCapture) {
            if (move.getAttackerSide() == Pieces.WHITE) {
                copyPiece.getPieceBitBoard()[Pieces.BLACK][Pieces.PAWN].clearBit(move.getTo() - 8);
            }
            copyPiece.getPieceBitBoard()[Pieces.WHITE][Pieces.PAWN].clearBit(move.getTo() + 8);
        }
        copyPiece.updateBitBoards();

        return PsLegalMoveMaskGen.isSafe(copyPiece, copyPiece.getPieceBitBoard()[move.getAttackerSide()][Pieces.KING].bitScanForward(), move.getAttackerSide());
    }
}
