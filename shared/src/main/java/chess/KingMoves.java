
package chess;

import java.util.*;


//class for calculating King's moves
public class KingMoves {
    public static Collection<ChessMove> calculate(
            ChessBoard board,
            ChessPosition start,
            ChessPiece piece
    ) {
        //create a new array to put in the move positions
        Collection<ChessMove> moves = new ArrayList<>();

        oneMove(board, start, piece, 1, 0, moves);
        oneMove(board, start, piece, -1, 0, moves);
        oneMove(board, start, piece, 0, 1, moves);
        oneMove(board, start, piece, 0, -1, moves);
        oneMove(board, start, piece, 1, 1, moves);
        oneMove(board, start, piece, 1, -1, moves);
        oneMove(board, start, piece, -1, 1, moves);
        oneMove(board, start, piece, -1, -1, moves);

        return moves;
    }

    //unique moves for the King position
    private static void oneMove(
            ChessBoard board,
            ChessPosition start,
            ChessPiece piece,
            int rowDirection,
            int colDirection,
            Collection<ChessMove> moves
    ) {
        int row = start.getRow() + rowDirection;
        int col = start.getColumn() + colDirection;

        //check if in bounds
        if (row < 1 || row > 8 || col < 1 || col > 8) {
            return;
        }

        //find which is the next spot the King must check
        ChessPosition end = new ChessPosition(row, col);
        ChessPiece target = board.getPiece(end);

        //Check if there is a target and if it is your piece or the other teams
        if (target == null || target.getTeamColor() != piece.getTeamColor()) {
            moves.add(new ChessMove(start, end, null));
        }
    }
}