package AI.utils;

import engine.board.Move;
import engine.board.Pieces;
import engine.board.Position;
import engine.moveGeneration.LegalMoveGen;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class OpeningBook {
    private static final List<List<Move>> moves;
    private static Random random;

    public static Move getMoveFromOpeningBook(Position position) {
        Position buff;

        List<Move> possible_moves = new ArrayList<>();
        boolean move_exist;

        for (List<Move> moveList : moves) {
            buff = new Position("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");

            if (buff.getPieces().equals(position.getPieces())) {
                move_exist = false;
                for (var added_move : possible_moves) {
                    if (added_move == moveList.get(0)) {
                        move_exist = true;
                        break;
                    }
                }

                if (!move_exist) {
                    possible_moves.add(moveList.get(0));
                }
                continue;
            }

            for (int move = 0; move < moveList.size() - 1; move = move + 1) {
                buff.move(moveList.get(move));

                if (buff.getPieces().equals(position.getPieces())) {
                    move_exist = false;
                    for (var added_move : possible_moves) {
                        if (added_move == moveList.get(move)) {
                            move_exist = true;
                            break;
                        }
                    }

                    if (!move_exist) {
                        possible_moves.add(moveList.get(move + 1));
                    }
                }
            }
        }

        if (possible_moves.isEmpty()) {
            return null;
        }
        return possible_moves.get(random.nextInt(possible_moves.size()));
    }

    static {
        random = new Random();
        moves = new ArrayList<>();
        InputStream inputStream =  OpeningBook.class.getResourceAsStream("/opening.txt");
        InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        try (BufferedReader reader = new BufferedReader(streamReader)) {
            int from;
            int to;
            List<Move> possibleMoves;
            Position buff;
            String line;
            List<Move> tmp = null;

            while ((line = reader.readLine()) != null) {
                tmp = new ArrayList<>();
                String[] moveStrings = line.split(" ");
                int side = Pieces.WHITE;
                buff = new Position("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
                for (String moveString : moveStrings) {
                    from = (moveString.charAt(1) - '1') * 8 + moveString.charAt(0) - 'a';
                    to = (moveString.charAt(3) - '1') * 8 + moveString.charAt(2) - 'a';
                    possibleMoves = LegalMoveGen.generate(buff, side, false, false);
                    for (Move move : possibleMoves) {
                        if (move.getFrom() == from && move.getTo() == to) {
                            tmp.add(move);
                            buff.move(move);
                            break;
                        }
                    }
                    side = Pieces.inverse(side);
                }
                moves.add(tmp);
            }
        } catch (Exception e) {
            System.out.println("Все плохо");
        }
    }
}
