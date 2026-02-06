package chess;
import java.util.*;


//class for calculating Knight's moves
public class KnightMoves {
    public static Collection<ChessMove> calculate(
            ChessBoard board,
            ChessPosition start,
            ChessPiece piece
    ) {
        //create a new array to put in the move positions
        Collection<ChessMove> moves = new ArrayList<>();

        //
        MoveHelper.jump(board, start, piece, 2, 1, moves);
        MoveHelper.jump(board, start, piece, 2, -1, moves);
        MoveHelper.jump(board, start, piece, -2, 1, moves);
        MoveHelper.jump(board, start, piece, -2, -1, moves);
        MoveHelper.jump(board, start, piece, 1, 2, moves);
        MoveHelper.jump(board, start, piece, -1, 2, moves);
        MoveHelper.jump(board, start, piece, 1, -2, moves);
        MoveHelper.jump(board, start, piece, -1, -2, moves);

        return moves;
    }


}