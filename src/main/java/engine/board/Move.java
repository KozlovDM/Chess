package engine.board;

public class Move {
    public static final int DEFAULT = 0;
    public static final int PAWN_LONG_MOVE = 1;
    public static final int EN_PASSANT_CAPTURE = 2;
    public static final int WHITE_LONG_CASTLING = 3;
    public static final int WHITE_SHORT_CASTLING = 4;
    public static final int BLACK_LONG_CASTLING = 5;
    public static final int BLACK_SHORT_CASTLING = 6;
    public static final int PROMOTE_TO_KNIGHT = 7;
    public static final int PROMOTE_TO_BISHOP = 8;
    public static final int PROMOTE_TO_ROOK = 9;
    public static final int PROMOTE_TO_QUEEN = 10;

    private int from;
    private int to;
    private int attackerType;
    private int attackerSide;
    private int defenderType;
    private int defenderSide;
    private int flag;

    public Move(int from, int to, int attackerType, int attackerSide, int defenderType, int defenderSide, int flag) {
        this.from = from;
        this.to = to;
        this.attackerType = attackerType;
        this.attackerSide = attackerSide;
        this.defenderType = defenderType;
        this.defenderSide = defenderSide;
        this.flag = flag;
    }

    public Move() {
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }

    public int getAttackerType() {
        return attackerType;
    }

    public int getAttackerSide() {
        return attackerSide;
    }

    public int getDefenderType() {
        return defenderType;
    }

    public int getDefenderSide() {
        return defenderSide;
    }

    public int getFlag() {
        return flag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Move move = (Move) o;

        if (from != move.from) return false;
        if (to != move.to) return false;
        if (attackerType != move.attackerType) return false;
        if (attackerSide != move.attackerSide) return false;
        if (defenderType != move.defenderType) return false;
        if (defenderSide != move.defenderSide) return false;
        return flag == move.flag;
    }

    @Override
    public int hashCode() {
        int result = from;
        result = 31 * result + to;
        result = 31 * result + attackerType;
        result = 31 * result + attackerSide;
        result = 31 * result + defenderType;
        result = 31 * result + defenderSide;
        result = 31 * result + flag;
        return result;
    }
}