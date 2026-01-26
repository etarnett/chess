package chess;
import java.util.*;

public class ChessMovesCalculator {
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
            return BishopMoves.calculate(board,position,piece);
        }

        //other pieces here

        return moves;
    }
}
