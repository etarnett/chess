package chess;

import java.util.*;


//Class to iterate through each possible move
public class MoveHelper {
    //function that takes in board, start postition, piece, the direction it moves, and the array
    public static void move(
            ChessBoard board,
            ChessPosition start,
            ChessPiece piece,
            int rowChange,
            int colChange,
            Collection<ChessMove> moves
    ) {
        //update row and collumn to the new row or column
        int row = start.getRow() + rowChange;
        int col = start.getColumn() + colChange;

        //iteration loop to check if in bounds or if the piece captures another one
        while (row >= 1 && row <= 8 && col >=1 && col <= 8) {
            ChessPosition end = new ChessPosition(row, col);
            ChessPiece target = board.getPiece(end);

            if (target == null) {
                moves.add(new ChessMove(start, end, null));
            } else {
                if (target.getTeamColor() != piece.getTeamColor()) {
                    moves.add(new ChessMove(start,end,null));
                }
                break;
            }

            //update for nect iteration
            row += rowChange;
            col += colChange;
        }
    }

}






