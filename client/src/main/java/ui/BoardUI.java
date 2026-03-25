package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

public class BoardUI {

    private static final String RESET = "\u001B[0m";
    private static final String LIGHT_BG = "\u001B[47m";
    private static final String DARK_BG = "\u001B[40m";
    private static final String WHITE_PIECE = "\u001B[31m";
    private static final String BLACK_PIECE = "\u001B[34m";

    public static void drawBoard(ChessBoard board, String perspective) {
        boolean isWhite = perspective == null || perspective.equalsIgnoreCase("WHITE");

        int rowStart = isWhite ? 8 : 1;
        int rowEnd = isWhite ? 0 : 9;
        int rowStep = isWhite ? -1 : 1;
        int colStart = isWhite ? 1 : 8;
        int colEnd = isWhite ? 9 : 0;
        int colStep = isWhite ? 1 : -1;

        for (int row = rowStart; row != rowEnd; row += rowStep) {
            System.out.print(row + " ");

            for (int col = colStart; col != colEnd; col += colStep) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);

                System.out.print(getSquare(piece, row, col));
            }

            System.out.println();
        }

        System.out.print("  ");
        for (int col = colStart; col != colEnd; col += colStep) {
            char letter = (char) ('a' + col - 1);
            System.out.print(" " + letter + " ");
        }
        System.out.println();
    }

    private static String getSquare(ChessPiece piece, int row, int col) {
        boolean isLight = (row + col) % 2 == 0;

        String bg = isLight ? LIGHT_BG : DARK_BG;
        String fg = "";

        if (piece != null) {
            fg = piece.getTeamColor() == ChessGame.TeamColor.WHITE
                    ? WHITE_PIECE
                    : BLACK_PIECE;
        }

        String symbol = getPieceSymbol(piece);

        return bg + fg + " " + symbol + " " + RESET;
    }

    private static String getPieceSymbol(ChessPiece piece) {
        if (piece == null) {
            return " ";
        }

        return switch (piece.getPieceType()) {
            case KING -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? "K" : "k";
            case QUEEN -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? "Q" : "q";
            case ROOK -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? "R" : "r";
            case BISHOP -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? "B" : "b";
            case KNIGHT -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? "N" : "n";
            case PAWN -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? "P" : "p";
        };
    }
}