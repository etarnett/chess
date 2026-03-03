
package chess.PieceMoves;

import chess.*;

import java.util.*;


//class for calculating Pawn's moves
public class pawnmoves {
    public static Collection<ChessMove> calculate(
            ChessBoard board,
            ChessPosition start,
            ChessPiece piece
    ) {
        //create a new array to put in the move positions
        Collection<ChessMove> moves = new ArrayList<>();
        //create variables (row, col, depending on color[direction, starting row, and promotion row]
        int row = start.getRow();
        int col = start.getColumn();
        int direction;
        int startRow;
        int promotionRow;

        //check which color it is and adjust direction, start, and promotion
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            direction = 1;
            startRow = 2;
            promotionRow = 8;
        } else {
            direction = -1;
            startRow = 7;
            promotionRow = 1;
        }

        //advance by one
        ChessPosition advance = new ChessPosition(row + direction, col);
        if (movehelper.inBounds(row+direction, col) && board.getPiece(advance) == null) {
            movehelper.promotion(start, advance, promotionRow, moves);

            //if on startRow move up 2
            if (row == startRow) {
                ChessPosition twoForward = new ChessPosition(row + 2 * direction, col);
                if (board.getPiece(twoForward) == null) {
                    moves.add(new ChessMove(start, twoForward, null));
                }
            }
        }


        //check if captures diagonally left
        int oneRow = row + direction;
        if (movehelper.inBounds(oneRow, col - 1)) {
            ChessPosition leftAttack = new ChessPosition(oneRow, col - 1);
            ChessPiece target = board.getPiece(leftAttack);
            if (target != null && target.getTeamColor() != piece.getTeamColor()) {
                movehelper.promotion(start, leftAttack, promotionRow, moves);
            }
        }

        //check if captures diagonally right
        if (movehelper.inBounds(oneRow, col + 1)) {
            ChessPosition rightAttack = new ChessPosition(oneRow, col + 1);
            ChessPiece target = board.getPiece(rightAttack);
            if (target != null && target.getTeamColor() != piece.getTeamColor()) {
                movehelper.promotion(start, rightAttack, promotionRow, moves);
            }
        }
        return moves;
    }
}