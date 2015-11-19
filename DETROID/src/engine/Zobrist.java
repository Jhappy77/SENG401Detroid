package engine;

import java.util.Random;

import engine.Board.Square;
import engine.Move.MoveType;
import engine.Position.CastlingRights;
import engine.Position.EnPassantRights;

/**A class whose object encodes the most important pieces of information stored in a Board class into a long by XOR-operations.
 * Two Board objects with identical states will always have the same Zobrist keys within one runtime and two Board objects with
 * different values for the concerned instance fields will almost always have different Zobrist keys.
 * The relevant fields are:
 * 		1. color to move
 * 		2. board position
 * 		3. white's castling rights
 * 		4. black's castling rights
 * 		5. en passant rights
 * 
 * The class creates its own random values it then uses for XOR-ing on compile thus Board objects with identical states are 
 * likely to have different keys for each runtime.
 * 
 * @author Viktor
 *
 */
public class Zobrist {
	
	private static long turn;
	private static long[][] board = new long[Piece.values().length][64];
	private static long[] whiteCastlingRights = new long[CastlingRights.values().length];
	private static long[] blackCastlingRights = new long[CastlingRights.values().length];
	private static long[] enPassantRights = new long[EnPassantRights.values().length];
	
	static {
		pseudorandNumGen();
	}
	/**Generates the 'random' values for the instance fields. For the board, there is a value for any piece on any square.*/
	private static void pseudorandNumGen() {
		Random random = new Random();
		turn = random.nextLong();
		for (int i = 0; i < board[0].length; i++)
			board[0][i] = 0;
		for (int i = 1; i < board.length; i++) {
			for (int j = 0; j < board[0].length; j++)
				board[i][j] = random.nextLong();
		}
		for (int i = 0; i < whiteCastlingRights.length; i++)
			whiteCastlingRights[i] = random.nextLong();
		for (int i = 0; i < blackCastlingRights.length; i++)
			blackCastlingRights[i] = random.nextLong();
		for (int i = 0; i < enPassantRights.length; i++)
			enPassantRights[i] = random.nextLong();
	}
	/**Encodes a Position object's pawn and main hash keys.
	 * 
	 * @param board
	 */
	public static void setHashKeys(Position p) {
		int[] board64 = p.getOffsetBoard();
		long key = 0, pawnKey = 0;
		if (!p.whitesTurn)
			key ^= turn;
		for (int i = 0; i < board64.length; i++) {
			key ^= board[board64[i]][i];
			if (board64[i] == Piece.W_PAWN.ind)
				pawnKey ^= board[Piece.W_PAWN.ind][i];
			else if (board64[i] == Piece.B_PAWN.ind)
				pawnKey ^= board[Piece.B_PAWN.ind][i];
		}
		key ^= whiteCastlingRights[p.whiteCastlingRights];
		key ^= blackCastlingRights[p.blackCastlingRights];
		key ^= enPassantRights[p.enPassantRights];
		p.key = key;
		p.pawnKey = pawnKey;
	}
	/**Modifies a Position object's hash keys according to the changes made by
	 * the last move.
	 * 
	 * @param p
	 */
	public static void updateKeys(Position p) {
		int enPassVictSqr;
		long movedFrom, movedTo;
		long key = p.key, pawnKey = p.pawnKey;
		Move move = p.getLastMove();
		UnmakeRegister unmakeReg = p.getUnmakeRegister();
		key ^= turn;
		key ^= whiteCastlingRights[unmakeReg.whiteCastlingRights];
		key ^= blackCastlingRights[unmakeReg.blackCastlingRights];
		key ^= enPassantRights[unmakeReg.enPassantRights];
		key ^= whiteCastlingRights[p.whiteCastlingRights];
		key ^= blackCastlingRights[p.blackCastlingRights];
		key ^= enPassantRights[p.enPassantRights];
		if (move != null) {
			movedFrom = board[move.movedPiece][move.from];
			movedTo = board[move.movedPiece][move.to];
			if (move.type == MoveType.NORMAL.ind) {
				key ^= movedFrom;
				key ^= board[move.capturedPiece][move.to];
				key ^= movedTo;
				if (move.movedPiece == Piece.W_PAWN.ind || move.movedPiece == Piece.B_PAWN.ind) {
					pawnKey ^= movedFrom;
					pawnKey ^= movedTo;
				}
				if (move.capturedPiece == Piece.B_PAWN.ind || move.movedPiece == Piece.W_PAWN.ind)
					pawnKey ^= board[move.capturedPiece][move.to];
			}
			else if (move.type == MoveType.SHORT_CASTLING.ind) {
				key ^= movedFrom;
				key ^= board[move.capturedPiece][move.to];
				key ^= movedTo;
				if (move.movedPiece == Piece.W_KING.ind) {
					key ^= board[Piece.W_ROOK.ind][Square.H1.ind];
					key ^= board[Piece.W_ROOK.ind][Square.F1.ind];
				}
				else {
					key ^= board[Piece.B_ROOK.ind][Square.H8.ind];
					key ^= board[Piece.B_ROOK.ind][Square.F8.ind];
				}
			}
			else if (move.type == MoveType.LONG_CASTLING.ind) {
				key ^= movedFrom;
				key ^= board[move.capturedPiece][move.to];
				key ^= movedTo;
				if (move.movedPiece == Piece.W_KING.ind) {
					key ^= board[Piece.W_ROOK.ind][Square.A1.ind];
					key ^= board[Piece.W_ROOK.ind][Square.D1.ind];
				}
				else {
					key ^= board[Piece.B_ROOK.ind][Square.A8.ind];
					key ^= board[Piece.B_ROOK.ind][Square.D8.ind];
				}
			}
			else if (move.type == MoveType.EN_PASSANT.ind) {
				key ^= movedFrom;
				key ^= movedTo;
				pawnKey ^= movedFrom;
				pawnKey ^= movedTo;
				if (move.movedPiece == Piece.W_PAWN.ind)
					enPassVictSqr = move.to - 8;
				else
					enPassVictSqr = move.to + 8;
				key ^= board[move.capturedPiece][enPassVictSqr];
				pawnKey ^= board[move.capturedPiece][enPassVictSqr];
			}
			else if (move.type == MoveType.PROMOTION_TO_QUEEN.ind) {
				key ^= movedFrom;
				key ^= board[move.capturedPiece][move.to];
				pawnKey ^= movedFrom;
				if (move.movedPiece == Piece.W_PAWN.ind) {
					key ^= board[Piece.W_QUEEN.ind][move.to];
					if (move.capturedPiece == Piece.B_PAWN.ind)
						pawnKey ^= board[move.capturedPiece][move.to];
				}
				else {
					key ^= board[Piece.B_QUEEN.ind][move.to];
					if (move.capturedPiece == Piece.W_PAWN.ind)
						pawnKey ^= board[move.capturedPiece][move.to];
				}
			}
			else if (move.type == MoveType.PROMOTION_TO_ROOK.ind) {
				key ^= movedFrom;
				key ^= board[move.capturedPiece][move.to];
				pawnKey ^= movedFrom;
				if (move.movedPiece == Piece.W_PAWN.ind) {
					key ^= board[Piece.W_ROOK.ind][move.to];
					if (move.capturedPiece == Piece.B_PAWN.ind)
						pawnKey ^= board[move.capturedPiece][move.to];
				}
				else {
					key ^= board[Piece.B_ROOK.ind][move.to];
					if (move.capturedPiece == Piece.W_PAWN.ind)
						pawnKey ^= board[move.capturedPiece][move.to];
				}
			}
			else if (move.type == MoveType.PROMOTION_TO_BISHOP.ind) {
				key ^= movedFrom;
				key ^= board[move.capturedPiece][move.to];
				pawnKey ^= movedFrom;
				if (move.movedPiece == Piece.W_PAWN.ind) {
					key ^= board[Piece.W_BISHOP.ind][move.to];
					if (move.capturedPiece == Piece.B_PAWN.ind)
						pawnKey ^= board[move.capturedPiece][move.to];
				}
				else {
					key ^= board[Piece.B_BISHOP.ind][move.to];
					if (move.capturedPiece == Piece.W_PAWN.ind)
						pawnKey ^= board[move.capturedPiece][move.to];
				}
			}
			else if (move.type == MoveType.PROMOTION_TO_KNIGHT.ind) {
				key ^= movedFrom;
				key ^= board[move.capturedPiece][move.to];
				pawnKey ^= movedFrom;
				if (move.movedPiece == Piece.W_PAWN.ind) {
					key ^= board[Piece.W_KNIGHT.ind][move.to];
					if (move.capturedPiece == Piece.B_PAWN.ind)
						pawnKey ^= board[move.capturedPiece][move.to];
				}
				else {
					key ^= board[Piece.B_KNIGHT.ind][move.to];
					if (move.capturedPiece == Piece.W_PAWN.ind)
						pawnKey ^= board[move.capturedPiece][move.to];
				}
			}
		}
		p.key = key;
		p.pawnKey = pawnKey;
	}
}
