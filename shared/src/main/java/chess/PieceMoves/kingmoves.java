
package chess.PieceMoves;

import chess.*;

import java.util.*;


//class for calculating King's moves
public class kingmoves {
    public static Collection<ChessMove> calculate(
            ChessBoard board,
            ChessPosition start,
            ChessPiece piece
    ) {
        //create a new array to put in the move positions
        Collection<ChessMove> moves = new ArrayList<>();

        movehelper.jump(board, start, piece, 1, 0, moves);
        movehelper.jump(board, start, piece, -1, 0, moves);
        movehelper.jump(board, start, piece, 0, 1, moves);
        movehelper.jump(board, start, piece, 0, -1, moves);
        movehelper.jump(board, start, piece, 1, 1, moves);
        movehelper.jump(board, start, piece, 1, -1, moves);
        movehelper.jump(board, start, piece, -1, 1, moves);
        movehelper.jump(board, start, piece, -1, -1, moves);

        return moves;
    }


}