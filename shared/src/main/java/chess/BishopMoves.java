package chess;

import java.util.*;

//class for calculating Bishop's moves
public class BishopMoves {
    public static Collection<ChessMove> calculate(
        ChessBoard board,
        ChessPosition start,
        ChessPiece piece
    ) {
        //create a new array to put in the move positions
        Collection<ChessMove> moves = new ArrayList<>();

        //call teh move helper function to go in each of the diagonal directions
        MoveHelper.move(board, start, piece, 1,1,moves);
        MoveHelper.move(board, start, piece, 1,-1,moves);
        MoveHelper.move(board, start, piece, -1,1,moves);
        MoveHelper.move(board, start, piece, -1,-1,moves);

        return moves;
    }
}
