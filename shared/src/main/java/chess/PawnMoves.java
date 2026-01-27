
package chess;

import java.util.*;


//class for calculating Pawn's moves
public class PawnMoves {
    public static Collection<ChessMove> calculate(
            ChessBoard board,
            ChessPosition start,
            ChessPiece piece
    ) {
        //create a new array to put in the move positions
        Collection<ChessMove> moves = new ArrayList<>();

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
        if (inBounds(row+direction, col) && board.getPiece(advance) == null) {
            PMove(start, advance, promotionRow, moves);

            //if on startRow move up 2
            if (row == startRow) {
                ChessPosition twoForward = new ChessPosition(row + 2 * direction, col);
                if (board.getPiece(twoForward) == null) {
                    moves.add(new ChessMove(start, twoForward, null));
                }
            }
        }


        //check if captures diagonally
        int oneRow = row + direction;
        if (inBounds(oneRow, col - 1)) {
            ChessPosition leftAttack = new ChessPosition(oneRow, col - 1);
            ChessPiece target = board.getPiece(leftAttack);

            if (target != null && target.getTeamColor() != piece.getTeamColor()) {
                PMove(start, leftAttack, promotionRow, moves);
            }
        }

        if (inBounds(oneRow, col + 1)) {
            ChessPosition rightAttack = new ChessPosition(oneRow, col + 1);
            ChessPiece target = board.getPiece(rightAttack);
            if (target != null && target.getTeamColor() != piece.getTeamColor()) {
                PMove(start, rightAttack, promotionRow, moves);
            }
        }


        return moves;
    }

    private static boolean inBounds(int row, int col) {
        return row >= 1 && row <= 8 && col >= 1 && col <= 8;
    }

    //unique moves for the Pawn position
    private static void PMove(
            ChessPosition start,
            ChessPosition end,
            int promotionRow,
            Collection<ChessMove> moves
    ) {
        if (end.getRow() == promotionRow) {
            moves.add(new ChessMove(start, end, ChessPiece.PieceType.QUEEN));
            moves.add(new ChessMove(start, end, ChessPiece.PieceType.ROOK));
            moves.add(new ChessMove(start, end, ChessPiece.PieceType.BISHOP));
            moves.add(new ChessMove(start, end, ChessPiece.PieceType.KNIGHT));
        } else {
            moves.add(new ChessMove(start,end,null));
        }
    }
}