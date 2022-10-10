package AI;

import AI.model.Entry;
import AI.utils.MoveSorter;
import AI.utils.OpeningBook;
import AI.utils.StaticEvaluator;
import AI.utils.TranspositionTable;
import engine.board.Move;
import engine.board.Pieces;
import engine.board.Position;
import engine.moveGeneration.LegalMoveGen;
import engine.moveGeneration.PsLegalMoveMaskGen;

import java.util.AbstractMap;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class AI {

    static final long NEGATIVE = -1000000000;
    static final long POSITIVE = 1000000000;

    static AtomicBoolean isStopSearch;

    static long evaluated;
    static int maximalDepth;
    static int ttCutoffs;

    public Move bestMove(Position position, long max_ms) throws InterruptedException, ExecutionException {

        if (position.getMoveCount() < 6) {
            Move moveFromOpeningBook = OpeningBook.getMoveFromOpeningBook(position);
            if (moveFromOpeningBook != null) {
                return moveFromOpeningBook;
            }
        }

        isStopSearch = new AtomicBoolean(false);
        TranspositionTable tt = new TranspositionTable();
        long bestMoveEvaluation;
        Move bestMove = null;
        Future<AbstractMap.SimpleEntry<Long, Move>> bestMoveThread;
        boolean updateBestMove;

        for (int i = 1; i < 1000; i++) {
            evaluated = 0;
            maximalDepth = 0;
            ttCutoffs = 0;

            Date date = new Date();
            int finalI = i;
            bestMoveThread = Executors.newSingleThreadExecutor().submit(() -> bestMove(position, position.getSide(), finalI, tt));

            updateBestMove = true;
            AbstractMap.SimpleEntry<Long, Move> longMoveSimpleEntry = null;
            try {
                longMoveSimpleEntry = bestMoveThread.get(max_ms, TimeUnit.MILLISECONDS);
            } catch (TimeoutException e) {
                updateBestMove = false;
            }
            max_ms -= new Date().getTime() - date.getTime();


            if (updateBestMove || i == 1) {
                bestMoveEvaluation = longMoveSimpleEntry.getKey();
                bestMove = longMoveSimpleEntry.getValue();
            } else {
                isStopSearch.set(true);
                break;
            }

            System.out.println("Base depth: " + i + ". Maximal depth: " + maximalDepth);

            if (bestMoveEvaluation > POSITIVE - 2000 || bestMoveEvaluation < NEGATIVE + 2000) {
                break;
            }
        }

        return bestMove;
    }

    private AbstractMap.SimpleEntry<Long, Move> bestMove(Position position, int side, int depth, TranspositionTable tt) {
        if (side == Pieces.WHITE) {
            return alphaBetaMax(position, NEGATIVE, POSITIVE, depth, 0, tt);
        } else {
            return alphaBetaMin(position, NEGATIVE, POSITIVE, depth, 0, tt);
        }
    }

    private AbstractMap.SimpleEntry<Long, Move> alphaBetaMin(Position position, long alpha, long beta, int depthLeft, int depthCurrent, TranspositionTable tt) {
        if (isStopSearch.get()) {
            return new AbstractMap.SimpleEntry<>(0L, new Move());
        }
        if (depthCurrent > maximalDepth) {
            maximalDepth = depthCurrent;
        }
        if (depthLeft == 0) {
            return new AbstractMap.SimpleEntry<>(alphaBetaMinOnlyCaptures(position, alpha, beta, depthCurrent), new Move());
        }
        if (position.getFiftyMoveCounter() >= 50 || position.getRepetitionHistory().getRepetitionNumber(position.getHash()) >= 3) {
            return new AbstractMap.SimpleEntry<>(0L, new Move());
        }

        List<Move> moves = LegalMoveGen.generate(position, Pieces.BLACK, false, false);
        MoveSorter.sort(position.getPieces(), moves);

        boolean in_check = !PsLegalMoveMaskGen.isSafe(position.getPieces(), position.getPieces().getPieceBitBoard()[Pieces.BLACK][Pieces.KING].bitScanForward(), Pieces.BLACK);

        if (moves.size() == 0) {
            if (in_check) {
                return new AbstractMap.SimpleEntry<>(POSITIVE - depthCurrent, new Move());
            }
            return new AbstractMap.SimpleEntry<>(0L, new Move());
        }

        Move move;
        Move bestMove = new Move();
        int bestMoveIndex = 0;
        long evaluation;
        Position copy;
        int tt_result = tt.tryToFindBestMoveIndex(position.getHash());

        for (int i = 0; i < moves.size(); i++) {
            if (tt_result >= moves.size()) {
                move = moves.get(i);
            } else {
                if (i == 0) {
                    move = moves.get(tt_result);
                } else {
                    if (i == tt_result) {
                        move = moves.get(0);
                    } else {
                        move = moves.get(i);
                    }
                }
            }

            copy = new Position(position);
            copy.move(move);
            evaluation = alphaBetaMax(copy, alpha, beta, depthLeft - (!in_check ? 1 : 0), depthCurrent + 1, tt).getKey();

            if (evaluation <= alpha) {
                if (tt_result >= moves.size() || i != 0) {
                    tt.addEntry(new Entry(position.getHash(), depthLeft, bestMoveIndex));
                } else {
                    ttCutoffs++;
                }
                return new AbstractMap.SimpleEntry<>(alpha, bestMove);
            }
            if (evaluation < beta) {
                bestMove = move;
                bestMoveIndex = i;
                beta = evaluation;
            }
        }

        tt.addEntry(new Entry(position.getHash(), depthLeft, bestMoveIndex));
        return new AbstractMap.SimpleEntry<>(beta, bestMove);
    }

    private AbstractMap.SimpleEntry<Long, Move> alphaBetaMax(Position position, long alpha, long beta, int depthLeft, int depthCurrent, TranspositionTable tt) {
        if (isStopSearch.get()) {
            return new AbstractMap.SimpleEntry<>(0L, new Move());
        }
        if (depthCurrent > maximalDepth) {
            maximalDepth = depthCurrent;
        }

        if (depthLeft == 0) {
            return new AbstractMap.SimpleEntry<>(alphaBetaMaxOnlyCaptures(position, alpha, beta, depthCurrent), new Move());
        }

        if (position.getFiftyMoveCounter() >= 50 || position.getRepetitionHistory().getRepetitionNumber(position.getHash()) >= 3) {
            return new AbstractMap.SimpleEntry<>(0L, new Move());
        }

        List<Move> moves = LegalMoveGen.generate(position, Pieces.WHITE, false, false);
        MoveSorter.sort(position.getPieces(), moves);

        boolean isCheck = !PsLegalMoveMaskGen.isSafe(position.getPieces(), position.getPieces().getPieceBitBoard()[Pieces.WHITE][Pieces.KING].bitScanForward(), Pieces.WHITE);

        if (moves.size() == 0) {
            if (isCheck) {
                return new AbstractMap.SimpleEntry<>(NEGATIVE + depthCurrent, new Move());
            }
            return new AbstractMap.SimpleEntry<>(0L, new Move());
        }

        Move move;
        Move bestMove = new Move();
        int bestMoveIndex = 0;
        long evaluation;
        Position copy;
        int tt_result = tt.tryToFindBestMoveIndex(position.getHash());

        for (int i = 0; i < moves.size(); i = i + 1) {
            if (tt_result >= moves.size()) {
                move = moves.get(i);
            } else {
                if (i == 0) {
                    move = moves.get(tt_result);
                } else {
                    if (i == tt_result) {
                        move = moves.get(0);
                    } else {
                        move = moves.get(i);
                    }
                }
            }

            copy = new Position(position);
            copy.move(move);
            evaluation = alphaBetaMin(copy, alpha, beta, depthLeft - (!isCheck ? 1 : 0), depthCurrent + 1, tt).getKey();

            if (evaluation >= beta) {
                if (tt_result >= moves.size() || i != 0) {
                    tt.addEntry(new Entry(position.getHash(), depthLeft, bestMoveIndex));
                } else {
                    ttCutoffs++;
                }
                return new AbstractMap.SimpleEntry<>(beta, bestMove);
            }
            if (evaluation > alpha) {
                bestMove = move;
                bestMoveIndex = i;
                alpha = evaluation;
            }
        }

        tt.addEntry(new Entry(position.getHash(), depthLeft, bestMoveIndex));
        return new AbstractMap.SimpleEntry<>(alpha, bestMove);
    }

    private long alphaBetaMinOnlyCaptures(Position position, long alpha, long beta, int depth_current) {
        if (isStopSearch.get()) {
            return 0;
        }
        if (depth_current > maximalDepth) {
            maximalDepth = depth_current;
        }

        long evaluation = StaticEvaluator.evaluate(position.getPieces(), position.iswLCastling(), position.iswSCastling(),
                position.isbLCastling(), position.isbSCastling(), position.isWhiteCastlingHappened(), position.isBlackCastlingHappened());
        evaluated++;

        if (evaluation <= alpha) {
            return alpha;
        }
        if (evaluation < beta) {
            beta = evaluation;
        }

        List<Move> moves = LegalMoveGen.generate(position, Pieces.BLACK, true, false);
        MoveSorter.sort(position.getPieces(), moves);

        Position copy;
        for (Move move : moves) {
            copy = new Position(position);
            copy.move(move);
            evaluation = alphaBetaMaxOnlyCaptures(copy, alpha, beta, depth_current + 1);
            if (evaluation <= alpha) {
                return alpha;
            }
            if (evaluation < beta) {
                beta = evaluation;
            }
        }
        return beta;
    }

    private long alphaBetaMaxOnlyCaptures(Position position, long alpha, long beta, int depthCurrent) {
        if (isStopSearch.get()) {
            return 0;
        }
        if (depthCurrent > maximalDepth) {
            maximalDepth = depthCurrent;
        }

        long evaluation = StaticEvaluator.evaluate(position.getPieces(), position.iswLCastling(), position.iswSCastling(),
                position.isbLCastling(), position.isbSCastling(), position.isWhiteCastlingHappened(), position.isBlackCastlingHappened());
        evaluated = evaluated + 1;

        if (evaluation >= beta) {
            return beta;
        }
        if (evaluation > alpha) {
            alpha = evaluation;
        }

        List<Move> moves = LegalMoveGen.generate(position, Pieces.WHITE, true, false);
        MoveSorter.sort(position.getPieces(), moves);

        Position copy;
        for (Move move : moves) {
            copy = new Position(position);
            copy.move(move);
            evaluation = alphaBetaMinOnlyCaptures(copy, alpha, beta, depthCurrent + 1);

            if (evaluation >= beta) {
                return beta;
            }
            if (evaluation > alpha) {
                alpha = evaluation;
            }
        }

        return alpha;
    }
}
