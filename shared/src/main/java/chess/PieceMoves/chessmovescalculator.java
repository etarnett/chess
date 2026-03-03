package chess.PieceMoves;
import chess.*;

import java.util.*;

public class chessmovescalculator {
    public static Collection<ChessMove> calculateMoves(
            ChessBoard board,
            ChessPosition position
    ) {
        Collection<ChessMove> moves = new ArrayList<>();

        //Null piece returns Null
        ChessPiece piece = board.getPiece(position);
        if (piece == null) {
            return moves;
        }

        //Bishop moves check
        if (piece.getPieceType() == ChessPiece.PieceType.BISHOP) {
            return bishopmoves.calculate(board,position,piece);
        }

        //Rook moves check
        if (piece.getPieceType() == ChessPiece.PieceType.ROOK) {
            return rookmoves.calculate(board,position,piece);
        }

        //Queen moves check
        if (piece.getPieceType() == ChessPiece.PieceType.QUEEN) {
            return queenmoves.calculate(board,position,piece);
        }

        //King moves check
        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            return kingmoves.calculate(board,position,piece);
        }

        //Knight moves check
        if (piece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
            return knightmoves.calculate(board,position,piece);
        }

        //Pawn moves check
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            return pawnmoves.calculate(board,position,piece);
        }

        return moves;
    }
}
