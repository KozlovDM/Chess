package engine.moveGeneration;

import engine.bitBoard.BitBoard;
import engine.bitBoard.BitboardColumns;
import engine.bitBoard.BitboardRows;
import engine.board.Pieces;

public class PsLegalMoveMaskGen {

    public static BitBoard generateKnightMask(Pieces pieces, int position, int side, boolean onlyCaptures) {
        if (onlyCaptures) {
            return new BitBoard(KnightMasks.MASKS[position].getBoard() & pieces.getSideBitBoard()[Pieces.inverse(side)].getBoard());
        }
        return new BitBoard(KnightMasks.MASKS[position].getBoard() & pieces.getInversionSideBitBoard()[side].getBoard());
    }

    public static BitBoard generateKingMask(Pieces pieces, int position, int side, boolean onlyCaptures) {
        if (onlyCaptures) {
            return new BitBoard(KingMasks.MASKS[position].getBoard() & pieces.getSideBitBoard()[Pieces.inverse(side)].getBoard());
        }
        return new BitBoard(KingMasks.MASKS[position].getBoard() & pieces.getInversionSideBitBoard()[side].getBoard());
    }

    public static BitBoard generatePawnDefaultMask(Pieces pieces, int side) {
        if (side == Pieces.WHITE) {
            return new BitBoard((pieces.getPieceBitBoard()[Pieces.WHITE][Pieces.PAWN].getBoard() << 8) & pieces.getEmpty().getBoard());
        }
        return new BitBoard((pieces.getPieceBitBoard()[Pieces.BLACK][Pieces.PAWN].getBoard() >>> 8) & pieces.getEmpty().getBoard());
    }

    public static BitBoard generatePawnLongMask(Pieces pieces, int side, BitBoard defaultMask) {
        if (side == Pieces.WHITE) {
            return new BitBoard(((defaultMask.getBoard() & BitboardRows.ROWS[2].getBoard()) << 8) & pieces.getEmpty().getBoard());
        }
        return new BitBoard(((defaultMask.getBoard() & BitboardRows.ROWS[5].getBoard()) >>> 8) & pieces.getEmpty().getBoard());
    }


    public static BitBoard generateBishopMask(Pieces pieces, int position, int side, boolean onlyCaptures) {
        BitBoard northWest = calculatedRay(pieces, position, side, onlyCaptures, SlidersMasks.NORTH_WEST, false);
        BitBoard northEast = calculatedRay(pieces, position, side, onlyCaptures, SlidersMasks.NORTH_EAST, false);
        BitBoard southWest = calculatedRay(pieces, position, side, onlyCaptures, SlidersMasks.SOUTH_WEST, true);
        BitBoard southEast = calculatedRay(pieces, position, side, onlyCaptures, SlidersMasks.SOUTH_EAST, true);
        return new BitBoard(northWest.getBoard() | northEast.getBoard() | southWest.getBoard() | southEast.getBoard());
    }


    public static BitBoard generateRookMask(Pieces pieces, int position, int side, boolean onlyCaptures) {
        BitBoard north = calculatedRay(pieces, position, side, onlyCaptures, SlidersMasks.NORTH, false);
        BitBoard south = calculatedRay(pieces, position, side, onlyCaptures, SlidersMasks.SOUTH, true);
        BitBoard west = calculatedRay(pieces, position, side, onlyCaptures, SlidersMasks.WEST, true);
        BitBoard east = calculatedRay(pieces, position, side, onlyCaptures, SlidersMasks.EAST, false);
        return new BitBoard(north.getBoard() | south.getBoard() | west.getBoard() | east.getBoard());
    }

    public static BitBoard generateQueenMask(Pieces pieces, int position, int side, boolean only_captures) {
        BitBoard bishopMask = generateBishopMask(pieces, position, side, only_captures);
        BitBoard rookMask = generateRookMask(pieces, position, side, only_captures);
        return new BitBoard(generateQueenMask(bishopMask, rookMask));
    }

    public static long generateQueenMask(BitBoard bishopMask, BitBoard rookMask) {
        return bishopMask.getBoard() | rookMask.getBoard();
    }

    public static long generatePawnLeftCapturesMask(Pieces pieces, int side, boolean includeAllPossibleCaptures) {
        long mask;
        if (side == Pieces.WHITE) {
            mask = (pieces.getPieceBitBoard()[Pieces.WHITE][Pieces.PAWN].getBoard() << 7) & BitboardColumns.INVERSION_COLUMNS[7].getBoard();
            if (!includeAllPossibleCaptures) {
                mask &= pieces.getSideBitBoard()[Pieces.BLACK].getBoard();
            }
        } else {
            mask = (pieces.getPieceBitBoard()[Pieces.BLACK][Pieces.PAWN].getBoard() >>> 9) & BitboardColumns.INVERSION_COLUMNS[7].getBoard();
            if (!includeAllPossibleCaptures) {
                mask &= pieces.getSideBitBoard()[Pieces.WHITE].getBoard();
            }
        }
        return mask;
    }

    public static long generatePawnRightCapturesMask(Pieces pieces, int side, boolean includeAllPossibleCaptures) {
        long mask;
        if (side == Pieces.WHITE) {
            mask = (pieces.getPieceBitBoard()[Pieces.WHITE][Pieces.PAWN].getBoard() << 9) & BitboardColumns.INVERSION_COLUMNS[0].getBoard();
            if (!includeAllPossibleCaptures) {
                mask = mask & pieces.getSideBitBoard()[Pieces.BLACK].getBoard();
            }
        } else {
            mask = (pieces.getPieceBitBoard()[Pieces.BLACK][Pieces.PAWN].getBoard() >>> 7) & BitboardColumns.INVERSION_COLUMNS[0].getBoard();
            if (!includeAllPossibleCaptures) {
                mask= mask & pieces.getSideBitBoard()[Pieces.WHITE].getBoard();
            }
        }
        return mask;
    }

    public static boolean isSafe(Pieces pieces, int position, int side) {
        int defendSide = Pieces.inverse(side);

        BitBoard bishopMask = generateBishopMask(pieces, position, side, true);
        if ((bishopMask.getBoard() & pieces.getPieceBitBoard()[defendSide][Pieces.BISHOP].getBoard()) != 0) {
            return false;
        }
        BitBoard rookMask = generateRookMask(pieces, position, side, true);
        if ((rookMask.getBoard() & pieces.getPieceBitBoard()[defendSide][Pieces.ROOK].getBoard()) != 0) {
            return false;
        }
        if ((generateQueenMask(bishopMask, rookMask) & pieces.getPieceBitBoard()[defendSide][Pieces.QUEEN].getBoard()) != 0) {
            return false;
        }
        if ((generateKnightMask(pieces, position, side, true).getBoard() & pieces.getPieceBitBoard()[defendSide][Pieces.KNIGHT].getBoard()) != 0) {
            return false;
        }

        long oppositePawnsLeftCaptures = generatePawnLeftCapturesMask(pieces, defendSide, true);
        long oppositePawnsRightCaptures = generatePawnRightCapturesMask(pieces, defendSide, true);
        BitBoard oppositePawnsCaptures = new BitBoard(oppositePawnsLeftCaptures | oppositePawnsRightCaptures);
        if (oppositePawnsCaptures.getBit(position)) {
            return false;
        }

        return (generateKingMask(pieces, position, side, true).getBoard() & pieces.getPieceBitBoard()[defendSide][Pieces.KING].getBoard()) == 0;
    }

    private static BitBoard calculatedRay(Pieces pieces, int position, int side, boolean onlyCaptures, int direction, boolean bsr) {
        BitBoard blockers = new BitBoard(SlidersMasks.MASKS[position][direction].getBoard() & pieces.getAll().getBoard());
        if (blockers.getBoard() == 0) {
            if (onlyCaptures) {
                return new BitBoard();
            }
            return SlidersMasks.MASKS[position][direction];
        }

        int blockingSquare;
        if (bsr) {
            blockingSquare = blockers.bitScanReverse();
        } else {
            blockingSquare = blockers.bitScanForward();
        }

        BitBoard moves = new BitBoard();
        if (onlyCaptures) {
            moves.setBit(blockingSquare);
        } else {
            moves.setBoard(SlidersMasks.MASKS[position][direction].getBoard() ^ SlidersMasks.MASKS[blockingSquare][direction].getBoard());
            if (pieces.getSideBitBoard()[side].getBit(blockingSquare)) {
                moves.clearBit(blockingSquare);
            }
        }
        return moves;
    }
}
