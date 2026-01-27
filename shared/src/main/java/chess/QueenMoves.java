package chess;

import java.util.*;


//class for calculating Queen's moves
public class QueenMoves {
    public static Collection<ChessMove> calculate(
            ChessBoard board,
            ChessPosition start,
            ChessPiece piece
    ) {
        //create a new array to put in the move positions
        Collection<ChessMove> moves = new ArrayList<>();

        //Queen is Rook and Bishop
        //Rook moves
        MoveHelper.move(board, start, piece, 1,0,moves);
        MoveHelper.move(board, start, piece, -1,0,moves);
        MoveHelper.move(board, start, piece, 0,1,moves);
        MoveHelper.move(board, start, piece, 0,-1,moves);

        //Bishop Moves
        MoveHelper.move(board, start, piece, 1,1,moves);
        MoveHelper.move(board, start, piece, -1,1,moves);
        MoveHelper.move(board, start, piece, 1,-1,moves);
        MoveHelper.move(board, start, piece, -1,-1,moves);


        return moves;
    }
}