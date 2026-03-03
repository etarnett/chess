package chess.PieceMoves;
import chess.*;

import java.util.*;
//Class to iterate through each possible move
public class movehelper {
    //function that takes in board, start postition, piece, the direction it moves, and the array
    public static void slide(
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
        while (inBounds(row, col)) {
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

    //King and Knight jump move (checks specific spot without looping)
    public static void jump(
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


    //promotion move to first check if pawn is in promotion zone then add the appropriate move to moves array
    public static void promotion(
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

    //check in bounds
    public static boolean inBounds(int row, int col) {
        return row >= 1 && row <= 8 && col >= 1 && col <= 8;
    }

}






