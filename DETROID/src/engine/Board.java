package engine;

import util.*;
import java.util.Random;
import java.util.Scanner;

/**A bit board based class whose object holds information amongst others on the current board position, on all the previous moves and positions,
 * on castling and en passant rights, and on the player to move. It uses its own pre-calculated 'magic' move database to avoid the cost of
 * computing the possible move sets of sliding pieces on the fly. The functions include:
 * {@link #generateMoves() generateMoves}
 * {@link #makeMove(long) makeMove}
 * {@link #unmakeMove() unmakeMove}
 * {@link #perft(int) perft}
 * {@link #perftWithConsoleOutput(int, long, long, boolean) perftWithConsoleOutput}
 * 
 * It relies heavily on values hard-coded or computed on compile. These values are always different for each square on the board, thus most of them 
 * are stored in 64-fold enums with switch statements providing fast access (about one to five percent faster than array access) based on the index 
 * of the square.
 *  
 * @author Viktor
 *
 */
public class Board {
	
	/**An enum type for the 64 squares of the chess board. Each constant has a field that contains a long with only the bit on
	 * the respective square's index set.
	 * 
	 * @author Viktor
	 *
	 */
	public static enum Square {
		
		A1 (0b0000000000000000000000000000000000000000000000000000000000000001L),
		B1 (0b0000000000000000000000000000000000000000000000000000000000000010L),
		C1 (0b0000000000000000000000000000000000000000000000000000000000000100L),
		D1 (0b0000000000000000000000000000000000000000000000000000000000001000L),
		E1 (0b0000000000000000000000000000000000000000000000000000000000010000L),
		F1 (0b0000000000000000000000000000000000000000000000000000000000100000L),
		G1 (0b0000000000000000000000000000000000000000000000000000000001000000L),
		H1 (0b0000000000000000000000000000000000000000000000000000000010000000L),
		A2 (0b0000000000000000000000000000000000000000000000000000000100000000L),
		B2 (0b0000000000000000000000000000000000000000000000000000001000000000L),
		C2 (0b0000000000000000000000000000000000000000000000000000010000000000L),
		D2 (0b0000000000000000000000000000000000000000000000000000100000000000L),
		E2 (0b0000000000000000000000000000000000000000000000000001000000000000L),
		F2 (0b0000000000000000000000000000000000000000000000000010000000000000L),
		G2 (0b0000000000000000000000000000000000000000000000000100000000000000L),
		H2 (0b0000000000000000000000000000000000000000000000001000000000000000L),
		A3 (0b0000000000000000000000000000000000000000000000010000000000000000L),
		B3 (0b0000000000000000000000000000000000000000000000100000000000000000L),
		C3 (0b0000000000000000000000000000000000000000000001000000000000000000L),
		D3 (0b0000000000000000000000000000000000000000000010000000000000000000L),
		E3 (0b0000000000000000000000000000000000000000000100000000000000000000L),
		F3 (0b0000000000000000000000000000000000000000001000000000000000000000L),
		G3 (0b0000000000000000000000000000000000000000010000000000000000000000L),
		H3 (0b0000000000000000000000000000000000000000100000000000000000000000L),
		A4 (0b0000000000000000000000000000000000000001000000000000000000000000L),
		B4 (0b0000000000000000000000000000000000000010000000000000000000000000L),
		C4 (0b0000000000000000000000000000000000000100000000000000000000000000L),
		D4 (0b0000000000000000000000000000000000001000000000000000000000000000L),
		E4 (0b0000000000000000000000000000000000010000000000000000000000000000L),
		F4 (0b0000000000000000000000000000000000100000000000000000000000000000L),
		G4 (0b0000000000000000000000000000000001000000000000000000000000000000L),
		H4 (0b0000000000000000000000000000000010000000000000000000000000000000L),
		A5 (0b0000000000000000000000000000000100000000000000000000000000000000L),
		B5 (0b0000000000000000000000000000001000000000000000000000000000000000L),
		C5 (0b0000000000000000000000000000010000000000000000000000000000000000L),
		D5 (0b0000000000000000000000000000100000000000000000000000000000000000L),
		E5 (0b0000000000000000000000000001000000000000000000000000000000000000L),
		F5 (0b0000000000000000000000000010000000000000000000000000000000000000L),
		G5 (0b0000000000000000000000000100000000000000000000000000000000000000L),
		H5 (0b0000000000000000000000001000000000000000000000000000000000000000L),
		A6 (0b0000000000000000000000010000000000000000000000000000000000000000L),
		B6 (0b0000000000000000000000100000000000000000000000000000000000000000L),
		C6 (0b0000000000000000000001000000000000000000000000000000000000000000L),
		D6 (0b0000000000000000000010000000000000000000000000000000000000000000L),
		E6 (0b0000000000000000000100000000000000000000000000000000000000000000L),
		F6 (0b0000000000000000001000000000000000000000000000000000000000000000L),
		G6 (0b0000000000000000010000000000000000000000000000000000000000000000L),
		H6 (0b0000000000000000100000000000000000000000000000000000000000000000L),
		A7 (0b0000000000000001000000000000000000000000000000000000000000000000L),
		B7 (0b0000000000000010000000000000000000000000000000000000000000000000L),
		C7 (0b0000000000000100000000000000000000000000000000000000000000000000L),
		D7 (0b0000000000001000000000000000000000000000000000000000000000000000L),
		E7 (0b0000000000010000000000000000000000000000000000000000000000000000L),
		F7 (0b0000000000100000000000000000000000000000000000000000000000000000L),
		G7 (0b0000000001000000000000000000000000000000000000000000000000000000L),
		H7 (0b0000000010000000000000000000000000000000000000000000000000000000L),
		A8 (0b0000000100000000000000000000000000000000000000000000000000000000L),
		B8 (0b0000001000000000000000000000000000000000000000000000000000000000L),
		C8 (0b0000010000000000000000000000000000000000000000000000000000000000L),
		D8 (0b0000100000000000000000000000000000000000000000000000000000000000L),
		E8 (0b0001000000000000000000000000000000000000000000000000000000000000L),
		F8 (0b0010000000000000000000000000000000000000000000000000000000000000L),
		G8 (0b0100000000000000000000000000000000000000000000000000000000000000L),
		H8 (0b1000000000000000000000000000000000000000000000000000000000000000L);
		
		final long bitmap;
		
		private Square(long bitmap) {
			this.bitmap = bitmap;
		}
		/**Returns a bitmap with only the bit on the square's index set.*/
		public long getBitmap() {
			return this.bitmap;
		}
		/**Returns a String representation of the square.*/
		public String toString() {
			return toString(this.ordinal());
		}
		/**Returns a String representation of the square that is easily readable for humans.
		 * 
		 * @param sqrInd
		 * @return
		 */
		public static String toString(int sqrInd) {
			return "" + (char)('a' + sqrInd%8) + "" + (sqrInd/8 + 1);
		}
		/**Returns the index of a square specified by its file and rank.
		 * 
		 * @param square
		 * @return
		 */
		public static int toNumeric(String square) {
			if (square.length() == 2) {
				square = square.toLowerCase();
				int out = 8*(square.charAt(0) - 'a') + (square.charAt(1) - '1');
				if (out >= 0 && out < 64)
					return out;
			}
			throw new IllegalArgumentException();
		}
		/**@return a Square enum.*/
		public static Square getByIndex(int sqrInd) {
			switch(sqrInd) {
				case 0:  return A1; case 1:  return B1; case 2:  return C1; case 3:  return D1; case 4:  return E1; case 5:  return F1; case 6:  return G1; case 7:  return H1;
				case 8:  return A2; case 9:  return B2; case 10: return C2; case 11: return D2; case 12: return E2; case 13: return F2; case 14: return G2; case 15: return H2;
				case 16: return A3; case 17: return B3; case 18: return C3; case 19: return D3; case 20: return E3; case 21: return F3; case 22: return G3; case 23: return H3;
				case 24: return A4; case 25: return B4; case 26: return C4; case 27: return D4; case 28: return E4; case 29: return F4; case 30: return G4; case 31: return H4;
				case 32: return A5; case 33: return B5; case 34: return C5; case 35: return D5; case 36: return E5; case 37: return F5; case 38: return G5; case 39: return H5;
				case 40: return A6; case 41: return B6; case 42: return C6; case 43: return D6; case 44: return E6; case 45: return F6; case 46: return G6; case 47: return H6;
				case 48: return A7; case 49: return B7; case 50: return C7; case 51: return D7; case 52: return E7; case 53: return F7; case 54: return G7; case 55: return H7;
				case 56: return A8; case 57: return B8; case 58: return C8; case 59: return D8; case 60: return E8; case 61: return F8; case 62: return G8; case 63: return H8;
				default: throw new IllegalArgumentException("Invalid square index.");
			}
		}
		/**@return a long with only the selected square set.*/
		public static long getBitmapByIndex(int sqrInd) {
			switch(sqrInd) {
				case 0:  return 0b0000000000000000000000000000000000000000000000000000000000000001L;
				case 1:  return 0b0000000000000000000000000000000000000000000000000000000000000010L;
				case 2:  return 0b0000000000000000000000000000000000000000000000000000000000000100L;
				case 3:  return 0b0000000000000000000000000000000000000000000000000000000000001000L;
				case 4:  return 0b0000000000000000000000000000000000000000000000000000000000010000L;
				case 5:  return 0b0000000000000000000000000000000000000000000000000000000000100000L;
				case 6:  return 0b0000000000000000000000000000000000000000000000000000000001000000L;
				case 7:  return 0b0000000000000000000000000000000000000000000000000000000010000000L;
				case 8:  return 0b0000000000000000000000000000000000000000000000000000000100000000L;
				case 9:  return 0b0000000000000000000000000000000000000000000000000000001000000000L;
				case 10: return 0b0000000000000000000000000000000000000000000000000000010000000000L;
				case 11: return 0b0000000000000000000000000000000000000000000000000000100000000000L;
				case 12: return 0b0000000000000000000000000000000000000000000000000001000000000000L;
				case 13: return 0b0000000000000000000000000000000000000000000000000010000000000000L;
				case 14: return 0b0000000000000000000000000000000000000000000000000100000000000000L;
				case 15: return 0b0000000000000000000000000000000000000000000000001000000000000000L;
				case 16: return 0b0000000000000000000000000000000000000000000000010000000000000000L;
				case 17: return 0b0000000000000000000000000000000000000000000000100000000000000000L;
				case 18: return 0b0000000000000000000000000000000000000000000001000000000000000000L;
				case 19: return 0b0000000000000000000000000000000000000000000010000000000000000000L;
				case 20: return 0b0000000000000000000000000000000000000000000100000000000000000000L;
				case 21: return 0b0000000000000000000000000000000000000000001000000000000000000000L;
				case 22: return 0b0000000000000000000000000000000000000000010000000000000000000000L;
				case 23: return 0b0000000000000000000000000000000000000000100000000000000000000000L;
				case 24: return 0b0000000000000000000000000000000000000001000000000000000000000000L;
				case 25: return 0b0000000000000000000000000000000000000010000000000000000000000000L;
				case 26: return 0b0000000000000000000000000000000000000100000000000000000000000000L;
				case 27: return 0b0000000000000000000000000000000000001000000000000000000000000000L;
				case 28: return 0b0000000000000000000000000000000000010000000000000000000000000000L;
				case 29: return 0b0000000000000000000000000000000000100000000000000000000000000000L;
				case 30: return 0b0000000000000000000000000000000001000000000000000000000000000000L;
				case 31: return 0b0000000000000000000000000000000010000000000000000000000000000000L;
				case 32: return 0b0000000000000000000000000000000100000000000000000000000000000000L;
				case 33: return 0b0000000000000000000000000000001000000000000000000000000000000000L;
				case 34: return 0b0000000000000000000000000000010000000000000000000000000000000000L;
				case 35: return 0b0000000000000000000000000000100000000000000000000000000000000000L;
				case 36: return 0b0000000000000000000000000001000000000000000000000000000000000000L;
				case 37: return 0b0000000000000000000000000010000000000000000000000000000000000000L;
				case 38: return 0b0000000000000000000000000100000000000000000000000000000000000000L;
				case 39: return 0b0000000000000000000000001000000000000000000000000000000000000000L;
				case 40: return 0b0000000000000000000000010000000000000000000000000000000000000000L;
				case 41: return 0b0000000000000000000000100000000000000000000000000000000000000000L;
				case 42: return 0b0000000000000000000001000000000000000000000000000000000000000000L;
				case 43: return 0b0000000000000000000010000000000000000000000000000000000000000000L;
				case 44: return 0b0000000000000000000100000000000000000000000000000000000000000000L;
				case 45: return 0b0000000000000000001000000000000000000000000000000000000000000000L;
				case 46: return 0b0000000000000000010000000000000000000000000000000000000000000000L;
				case 47: return 0b0000000000000000100000000000000000000000000000000000000000000000L;
				case 48: return 0b0000000000000001000000000000000000000000000000000000000000000000L;
				case 49: return 0b0000000000000010000000000000000000000000000000000000000000000000L;
				case 50: return 0b0000000000000100000000000000000000000000000000000000000000000000L;
				case 51: return 0b0000000000001000000000000000000000000000000000000000000000000000L;
				case 52: return 0b0000000000010000000000000000000000000000000000000000000000000000L;
				case 53: return 0b0000000000100000000000000000000000000000000000000000000000000000L;
				case 54: return 0b0000000001000000000000000000000000000000000000000000000000000000L;
				case 55: return 0b0000000010000000000000000000000000000000000000000000000000000000L;
				case 56: return 0b0000000100000000000000000000000000000000000000000000000000000000L;
				case 57: return 0b0000001000000000000000000000000000000000000000000000000000000000L;
				case 58: return 0b0000010000000000000000000000000000000000000000000000000000000000L;
				case 59: return 0b0000100000000000000000000000000000000000000000000000000000000000L;
				case 60: return 0b0001000000000000000000000000000000000000000000000000000000000000L;
				case 61: return 0b0010000000000000000000000000000000000000000000000000000000000000L;
				case 62: return 0b0100000000000000000000000000000000000000000000000000000000000000L;
				case 63: return 0b1000000000000000000000000000000000000000000000000000000000000000L;
				default: throw new IllegalArgumentException("Invalid square index.");
			}
		}
	}
	
	/**An enum type for the 8 ranks/rows of a chess board. Each constant has a field that contains a long with only the byte on the rank's
	 * index set.
	 * 
	 * @author Viktor
	 *
	 */
	public static enum Rank {
		
		R1 (0b0000000000000000000000000000000000000000000000000000000011111111L),
		R2 (0b0000000000000000000000000000000000000000000000001111111100000000L),
		R3 (0b0000000000000000000000000000000000000000111111110000000000000000L),
		R4 (0b0000000000000000000000000000000011111111000000000000000000000000L),
		R5 (0b0000000000000000000000001111111100000000000000000000000000000000L),
		R6 (0b0000000000000000111111110000000000000000000000000000000000000000L),
		R7 (0b0000000011111111000000000000000000000000000000000000000000000000L),
		R8 (0b1111111100000000000000000000000000000000000000000000000000000000L);
		
		final long bitmap;
		
		private Rank(long bitmap) {
			this.bitmap = bitmap;
		}
		public long getBitmap() {
			return this.bitmap;
		}
		/**Returns a the numeric representation of a rank of the chess board with only the byte on the rank's index set.
		 * 
		 * @param rnkInd the index of the rank*/
		public static long getByIndex(int rnkInd) {
			switch(rnkInd) {
				case 0:  return 0b0000000000000000000000000000000000000000000000000000000011111111L;
				case 1:  return 0b0000000000000000000000000000000000000000000000001111111100000000L;
				case 2:  return 0b0000000000000000000000000000000000000000111111110000000000000000L;
				case 3:  return 0b0000000000000000000000000000000011111111000000000000000000000000L;
				case 4:  return 0b0000000000000000000000001111111100000000000000000000000000000000L;
				case 5:  return 0b0000000000000000111111110000000000000000000000000000000000000000L;
				case 6:  return 0b0000000011111111000000000000000000000000000000000000000000000000L;
				case 7:  return 0b1111111100000000000000000000000000000000000000000000000000000000L;
				default: throw new IllegalArgumentException("Invalid rank index.");
			}
		}
		/**Returns a the numeric representation of the rank of the chess board on which the input parameter square lies with only
		 * the byte on the rank's index set.
		 * 
		 * @param sqr a Square enum*/
		public static long getBySquare(Square sqr) {
			return getByIndex(sqr.ordinal() >>> 3);
		}
		/**Returns a the numeric representation of the rank of the chess board on which the input parameter square lies with only
		 * the byte on the rank's index set.
		 * 
		 * @param sqrInd the index of the square*/
		public static long getBySquareIndex(int sqrInd) {
			return getByIndex(sqrInd >>> 3);
		}
	}

	/**An enum type for the 8 files/columns of a chess board. Each constant has a field that contains a long with only the bits falling on the
	 * file set.
	 * 
	 * @author Viktor
	 *
	 */
	public static enum File {
		
		A (0b0000000100000001000000010000000100000001000000010000000100000001L),
		B (0b0000001000000010000000100000001000000010000000100000001000000010L),
		C (0b0000010000000100000001000000010000000100000001000000010000000100L),
		D (0b0000100000001000000010000000100000001000000010000000100000001000L),
		E (0b0001000000010000000100000001000000010000000100000001000000010000L),
		F (0b0010000000100000001000000010000000100000001000000010000000100000L),
		G (0b0100000001000000010000000100000001000000010000000100000001000000L),
		H (0b1000000010000000100000001000000010000000100000001000000010000000L);
		
		final long bitmap;
		
		private File(long bitmap) {
			this.bitmap = bitmap;
		}
		public long getBitmap() {
			return this.bitmap;
		}
		/**Returns a the numeric representation of a file of the chess board with only the bits falling on the specified file set.
		 * 
		 * @param fileInd the index of the file*/
		public static long getByIndex(int fileInd) {
			switch(fileInd) {
				case 0:  return 0b0000000100000001000000010000000100000001000000010000000100000001L;
				case 1:  return 0b0000001000000010000000100000001000000010000000100000001000000010L;
				case 2:  return 0b0000010000000100000001000000010000000100000001000000010000000100L;
				case 3:  return 0b0000100000001000000010000000100000001000000010000000100000001000L;
				case 4:  return 0b0001000000010000000100000001000000010000000100000001000000010000L;
				case 5:  return 0b0010000000100000001000000010000000100000001000000010000000100000L;
				case 6:  return 0b0100000001000000010000000100000001000000010000000100000001000000L;
				case 7:  return 0b1000000010000000100000001000000010000000100000001000000010000000L;
				default: throw new IllegalArgumentException("Invalid file index.");
			}
		}
		/**Returns a the numeric representation of the file of the chess board on which the input parameter square lies with only
		 * the relevant bits set.
		 * 
		 * @param sqr a Square enum*/
		public static long getBySquare(Square sqr) {
			return getByIndex(sqr.ordinal() & 7);
		}
		/**Returns a the numeric representation of the file of the chess board on which the input parameter square lies with only
		 * the relevant bits set.
		 * 
		 * @param sqrInd the index of the square*/
		public static long getBySquareIndex(int sqrInd) {
			return getByIndex(sqrInd & 7);
		}
	}
	
	/**An enum type for the 15 diagonals of a chess board. Each constant has a field that contains a long with only the bits on indexes
	 * of the squares falling on the diagonal set.
	 * 
	 * @author Viktor
	 *
	 */
	public static enum Diagonal {
		
		DG1  (0b0000000000000000000000000000000000000000000000000000000000000001L),
		DG2  (0b0000000000000000000000000000000000000000000000000000000100000010L),
		DG3  (0b0000000000000000000000000000000000000000000000010000001000000100L),
		DG4  (0b0000000000000000000000000000000000000001000000100000010000001000L),
		DG5  (0b0000000000000000000000000000000100000010000001000000100000010000L),
		DG6  (0b0000000000000000000000010000001000000100000010000001000000100000L),
		DG7  (0b0000000000000001000000100000010000001000000100000010000001000000L),
		DG8  (0b0000000100000010000001000000100000010000001000000100000010000000L),
		DG9  (0b0000001000000100000010000001000000100000010000001000000000000000L),
		DG10 (0b0000010000001000000100000010000001000000100000000000000000000000L),
		DG11 (0b0000100000010000001000000100000010000000000000000000000000000000L),
		DG12 (0b0001000000100000010000001000000000000000000000000000000000000000L),
		DG13 (0b0010000001000000100000000000000000000000000000000000000000000000L),
		DG14 (0b0100000010000000000000000000000000000000000000000000000000000000L),
		DG15 (0b1000000000000000000000000000000000000000000000000000000000000000L);
		
		final long bitmap;
		
		private Diagonal(long bitmap) {
			this.bitmap = bitmap;
		}
		public long getBitmap() {
			return this.bitmap;
		}
		/**Prints the binary literals for the enum constants.*/
		public void printBitmapLiterals() {
			long[] diag = new long[15];
			diag[0]		= Square.A1.getBitmap();
			diag[1]		= Square.A2.getBitmap() | Square.B1.getBitmap();
			diag[2]		= Square.A3.getBitmap() | Square.B2.getBitmap() | Square.C1.getBitmap();
			diag[3]		= Square.A4.getBitmap() | Square.B3.getBitmap() | Square.C2.getBitmap() | Square.D1.getBitmap();
			diag[4]		= Square.A5.getBitmap() | Square.B4.getBitmap() | Square.C3.getBitmap() | Square.D2.getBitmap() | Square.E1.getBitmap();
			diag[5]		= Square.A6.getBitmap() | Square.B5.getBitmap() | Square.C4.getBitmap() | Square.D3.getBitmap() | Square.E2.getBitmap() | Square.F1.getBitmap();
			diag[6]		= Square.A7.getBitmap() | Square.B6.getBitmap() | Square.C5.getBitmap() | Square.D4.getBitmap() | Square.E3.getBitmap() | Square.F2.getBitmap() | Square.G1.getBitmap();
			diag[7] 	= Square.A8.getBitmap() | Square.B7.getBitmap() | Square.C6.getBitmap() | Square.D5.getBitmap() | Square.E4.getBitmap() | Square.F3.getBitmap() | Square.G2.getBitmap() | Square.H1.getBitmap();
			diag[8] 	= Square.B8.getBitmap() | Square.C7.getBitmap() | Square.D6.getBitmap() | Square.E5.getBitmap() | Square.F4.getBitmap() | Square.G3.getBitmap() | Square.H2.getBitmap();
			diag[9]		= Square.C8.getBitmap() | Square.D7.getBitmap() | Square.E6.getBitmap() | Square.F5.getBitmap() | Square.G4.getBitmap() | Square.H3.getBitmap();
			diag[10] 	= Square.D8.getBitmap() | Square.E7.getBitmap() | Square.F6.getBitmap() | Square.G5.getBitmap() | Square.H4.getBitmap();
			diag[11] 	= Square.E8.getBitmap() | Square.F7.getBitmap() | Square.G6.getBitmap() | Square.H5.getBitmap();
			diag[12] 	= Square.F8.getBitmap() | Square.G7.getBitmap() | Square.H6.getBitmap();
			diag[13] 	= Square.G8.getBitmap() | Square.H7.getBitmap();
			diag[14]	= Square.H8.getBitmap();
			for (int i = 0; i < diag.length; i++) {
				System.out.println("Diagonal " + String.format("%2d", i+1) + ": " + BitOperations.toBinaryLiteral(diag[i]));
			}
		}
		/**Returns a the numeric representation of a diagonal of the chess board with only the bits falling on the specified diagonal set.
		 * 
		 * @param dgnInd the index of the diagonal*/
		public static long getByIndex(int dgnInd) {
			switch(dgnInd) {
				case 0:  return 0b0000000000000000000000000000000000000000000000000000000000000001L;
				case 1:  return 0b0000000000000000000000000000000000000000000000000000000100000010L;
				case 2:  return 0b0000000000000000000000000000000000000000000000010000001000000100L;
				case 3:  return 0b0000000000000000000000000000000000000001000000100000010000001000L;
				case 4:  return 0b0000000000000000000000000000000100000010000001000000100000010000L;
				case 5:  return 0b0000000000000000000000010000001000000100000010000001000000100000L;
				case 6:  return 0b0000000000000001000000100000010000001000000100000010000001000000L;
				case 7:  return 0b0000000100000010000001000000100000010000001000000100000010000000L;
				case 8:  return 0b0000001000000100000010000001000000100000010000001000000000000000L;
				case 9:  return 0b0000010000001000000100000010000001000000100000000000000000000000L;
				case 10: return 0b0000100000010000001000000100000010000000000000000000000000000000L;
				case 11: return 0b0001000000100000010000001000000000000000000000000000000000000000L;
				case 12: return 0b0010000001000000100000000000000000000000000000000000000000000000L;
				case 13: return 0b0100000010000000000000000000000000000000000000000000000000000000L;
				case 14: return 0b1000000000000000000000000000000000000000000000000000000000000000L;
				default: throw new IllegalArgumentException("Invalid diagonal index.");
			}
		}
		/**Returns a the numeric representation of a diagonal of the chess board on which the input parameter square lies with only
		 * the relevant bits set.
		 * 
		 * @param sqr a Square enum*/
		public static long getBySquare(Square sqr) {
			int sqrInd = sqr.ordinal();
			return getByIndex((sqrInd & 7) + (sqrInd >>> 3));
		}
		/**Returns a the numeric representation of a diagonal of the chess board on which the input parameter square lies with only
		 * the relevant bits set.
		 * 
		 * @param sqrInd the index of a square*/
		public static long getBySquareIndex(int sqrInd) {
			return getByIndex((sqrInd & 7) + (sqrInd >>> 3));
		}
	}
	
	/**An enum type for the 15 anti-diagonals of a chess board. Each constant has a field that contains a long with only the bits on indexes
	 * of the squares falling on the diagonal set.
	 * 
	 * @author Viktor
	 *
	 */
	public static enum AntiDiagonal {
		
		ADG1  (0b0000000100000000000000000000000000000000000000000000000000000000L),
		ADG2  (0b0000001000000001000000000000000000000000000000000000000000000000L),
		ADG3  (0b0000010000000010000000010000000000000000000000000000000000000000L),
		ADG4  (0b0000100000000100000000100000000100000000000000000000000000000000L),
		ADG5  (0b0001000000001000000001000000001000000001000000000000000000000000L),
		ADG6  (0b0010000000010000000010000000010000000010000000010000000000000000L),
		ADG7  (0b0100000000100000000100000000100000000100000000100000000100000000L),
		ADG8  (0b1000000001000000001000000001000000001000000001000000001000000001L),
		ADG9  (0b0000000010000000010000000010000000010000000010000000010000000010L),
		ADG10 (0b0000000000000000100000000100000000100000000100000000100000000100L),
		ADG11 (0b0000000000000000000000001000000001000000001000000001000000001000L),
		ADG12 (0b0000000000000000000000000000000010000000010000000010000000010000L),
		ADG13 (0b0000000000000000000000000000000000000000100000000100000000100000L),
		ADG14 (0b0000000000000000000000000000000000000000000000001000000001000000L),
		ADG15 (0b0000000000000000000000000000000000000000000000000000000010000000L);
		
		final long bitmap;
		
		private AntiDiagonal(long bitmap) {
			this.bitmap = bitmap;
		}
		public long getBitmap() {
			return this.bitmap;
		}
		/**Prints the binary literals for the enum constants.*/
		public void printBitmapLiterals() {
			long[] aDiag = new long[15];
			aDiag[0]	= Square.A8.getBitmap();
			aDiag[1]	= Square.A7.getBitmap() | Square.B8.getBitmap();
			aDiag[2]	= Square.A6.getBitmap() | Square.B7.getBitmap() | Square.C8.getBitmap();
			aDiag[3]	= Square.A5.getBitmap() | Square.B6.getBitmap() | Square.C7.getBitmap() | Square.D8.getBitmap();
			aDiag[4]	= Square.A4.getBitmap() | Square.B5.getBitmap() | Square.C6.getBitmap() | Square.D7.getBitmap() | Square.E8.getBitmap();
			aDiag[5]	= Square.A3.getBitmap() | Square.B4.getBitmap() | Square.C5.getBitmap() | Square.D6.getBitmap() | Square.E7.getBitmap() | Square.F8.getBitmap();
			aDiag[6]	= Square.A2.getBitmap() | Square.B3.getBitmap() | Square.C4.getBitmap() | Square.D5.getBitmap() | Square.E6.getBitmap() | Square.F7.getBitmap() | Square.G8.getBitmap();
			aDiag[7] 	= Square.A1.getBitmap() | Square.B2.getBitmap() | Square.C3.getBitmap() | Square.D4.getBitmap() | Square.E5.getBitmap() | Square.F6.getBitmap() | Square.G7.getBitmap() | Square.H8.getBitmap();
			aDiag[8] 	= Square.B1.getBitmap() | Square.C2.getBitmap() | Square.D3.getBitmap() | Square.E4.getBitmap() | Square.F5.getBitmap() | Square.G6.getBitmap() | Square.H7.getBitmap();
			aDiag[9]	= Square.C1.getBitmap() | Square.D2.getBitmap() | Square.E3.getBitmap() | Square.F4.getBitmap() | Square.G5.getBitmap() | Square.H6.getBitmap();
			aDiag[10] 	= Square.D1.getBitmap() | Square.E2.getBitmap() | Square.F3.getBitmap() | Square.G4.getBitmap() | Square.H5.getBitmap();
			aDiag[11] 	= Square.E1.getBitmap() | Square.F2.getBitmap() | Square.G3.getBitmap() | Square.H4.getBitmap();
			aDiag[12] 	= Square.F1.getBitmap() | Square.G2.getBitmap() | Square.H3.getBitmap();
			aDiag[13] 	= Square.G1.getBitmap() | Square.H2.getBitmap();
			aDiag[14]	= Square.H1.getBitmap();
			for (int i = 0; i < aDiag.length; i++) {
				System.out.println("Diagonal " + String.format("%2d", i+1) + ": " + BitOperations.toBinaryLiteral(aDiag[i]));
			}
		}
		/**Returns a the numeric representation of an anti-diagonal of the chess board with only the bits falling on the specified diagonal set.
		 * 
		 * @param adgnInd the index of the anti-diagonal*/
		public static long getByIndex(int adgnInd) {
			switch(adgnInd) {
				case 0:  return 0b0000000100000000000000000000000000000000000000000000000000000000L;
				case 1:  return 0b0000001000000001000000000000000000000000000000000000000000000000L;
				case 2:  return 0b0000010000000010000000010000000000000000000000000000000000000000L;
				case 3:  return 0b0000100000000100000000100000000100000000000000000000000000000000L;
				case 4:  return 0b0001000000001000000001000000001000000001000000000000000000000000L;
				case 5:  return 0b0010000000010000000010000000010000000010000000010000000000000000L;
				case 6:  return 0b0100000000100000000100000000100000000100000000100000000100000000L;
				case 7:  return 0b1000000001000000001000000001000000001000000001000000001000000001L;
				case 8:  return 0b0000000010000000010000000010000000010000000010000000010000000010L;
				case 9:  return 0b0000000000000000100000000100000000100000000100000000100000000100L;
				case 10: return 0b0000000000000000000000001000000001000000001000000001000000001000L;
				case 11: return 0b0000000000000000000000000000000010000000010000000010000000010000L;
				case 12: return 0b0000000000000000000000000000000000000000100000000100000000100000L;
				case 13: return 0b0000000000000000000000000000000000000000000000001000000001000000L;
				case 14: return 0b0000000000000000000000000000000000000000000000000000000010000000L;
				default: throw new IllegalArgumentException("Invalid anti-diagonal index.");
			}
		}
		/**Returns a the numeric representation of a diagonal of the chess board on which the input parameter square lies with only
		 * the relevant bits set.
		 * 
		 * @param sqr a Square enum*/
		public static long getBySquare(Square sqr) {
			int sqrInd = sqr.ordinal();
			return getByIndex((sqrInd & 7) + (7 - (sqrInd >>> 3)));
		}
		/**Returns a the numeric representation of a diagonal of the chess board on which the input parameter square lies with only
		 * the relevant bits set.
		 * 
		 * @param sqrInd the index of a square*/
		public static long getBySquareIndex(int sqrInd) {
			return getByIndex((sqrInd & 7) + (7 - (sqrInd >>> 3)));
		}
	}
	
	/**A static class that generates the basic move masks for each piece type. It does not include special moves or check considerations and it disregards occupancies.
	 * 
	 * @author Viktor
	 *
	 */
	public final static class MoveMask {
		
		/**Generates a bitmap of the basic king's move mask. Does not include target squares of castling; handles the wrap-around effect.*/
		public final static long generateKingsMoveMask(Square sqr) {
			long mask;
			int sqrInd = sqr.ordinal();
			long sqrBit = sqr.getBitmap();
			mask =	(sqrBit << 7)  | (sqrBit << 8)  | (sqrBit << 9)  |
					(sqrBit << 1)    			    | (sqrBit >>> 1) |
					(sqrBit >>> 9) | (sqrBit >>> 8) | (sqrBit >>> 7) ;
			if (sqrInd%8 == 0)
				mask &= ~File.H.getBitmap();
			else if ((sqrInd + 1)%8 == 0)
				mask &= ~File.A.getBitmap();
			return mask;
		}
		/**Generates a bitmap of the basic knight's move mask. Occupancies are disregarded. It handles the wrap-around effect.*/
		public final static long generateKnightMasks(Square sqr) {
			long mask;
			int sqrInd = sqr.ordinal();
			long sqrBit = sqr.getBitmap();
			mask =		 	(sqrBit << 15)	| (sqrBit << 17) |
					(sqrBit << 6)			| 		  (sqrBit << 10)   |
					(sqrBit >>> 10)			|		  (sqrBit >>> 6)   |
							(sqrBit >>> 17)	| (sqrBit >>> 15);
			if (sqrInd%8 == 0)
				mask &= ~(File.H.getBitmap() | File.G.getBitmap());
			else if ((sqrInd - 1)%8 == 0)
				mask &= ~File.H.getBitmap();
			else if ((sqrInd + 1)%8 == 0)
				mask &= ~(File.A.getBitmap() | File.B.getBitmap());
			else if ((sqrInd + 2)%8 == 0)
				mask &= ~File.A.getBitmap();
			return mask;
		}
		/**Generates a bitmap of the basic white pawn's capture mask. Occupancies are disregarded. It handles the wrap-around effect.*/
		public final static long generateWhitePawnsCaptureMasks(Square sqr) {
			long mask;
			int sqrInd = sqr.ordinal();
			if (sqrInd > 55)
					return 0;
			long sqrBit = sqr.getBitmap();
			mask =		(sqrBit << 7) | (sqrBit << 9);
			if (sqrInd%8 == 0)
				mask &= ~File.H.getBitmap();
			else if ((sqrInd + 1)%8 == 0)
				mask &= ~File.A.getBitmap();
			return mask;
		}
		/**Generates a bitmap of the basic black pawn's capture mask. Occupancies are disregarded. It handles the wrap-around effect.*/
		public final static long generateBlackPawnsCaptureMasks(Square sqr) {
			long mask;
			int sqrInd = sqr.ordinal();
			if (sqrInd < 8)
					return 0;
			long sqrBit = sqr.getBitmap();
			mask =		(sqrBit >>> 9) | (sqrBit >>> 7);
			if (sqrInd%8 == 0)
				mask &= ~File.H.getBitmap();
			else if ((sqrInd + 1)%8 == 0)
				mask &= ~File.A.getBitmap();
			return mask;
		}
		/**Generates a bitmap of the basic white pawn's advance mask. Double advance from initial square is included. Occupancies are disregarded. It handles the wrap-around effect.*/
		public final static long generateWhitePawnsAdvanceMasks(Square sqr) {
			long mask;
			int sqrInd = sqr.ordinal();
			if (sqrInd < 8 || sqrInd > 55)
				return 0;
			long sqrBit = sqr.getBitmap();
			mask = (sqrBit << 8);
			if (sqrInd < 16)
				mask |= (sqrBit << 16);
			return mask;
		}
		/**Generates a bitmap of the basic black pawn's advance mask. Double advance from initial square is included. Occupancies are disregarded. It handles the wrap-around effect.*/
		public final static long generateBlackPawnsAdvanceMasks(Square sqr) {
			long mask;
			int sqrInd = sqr.ordinal();
			if (sqrInd < 8 || sqrInd > 55)
				return 0;
			long sqrBit = sqr.getBitmap();
			mask = (sqrBit >>> 8);
			if (sqrInd >= 48)
				mask |= (sqrBit >>> 16);
			return mask;
		}
		/**Generates a bitmap of the basic rook's rank-wise/horizontal move mask. Occupancies are disregarded. Perimeter squares are included.*/
		public final static long generateRooksRankMoveMask(Square sqr) {
			long mask;
			mask =	(Rank.getBySquare(sqr)^sqr.getBitmap());
			return mask;
		}
		/**Generates a bitmap of the basic rook's file-wise/vertical move mask. Occupancies are disregarded. Perimeter squares are included.*/
		public final static long generateRooksFileMoveMask(Square sqr) {
			long mask;
			mask =	(File.getBySquare(sqr)^sqr.getBitmap());
			return mask;
		}
		/**Generates a bitmap of the basic rook's complete move mask. Occupancies are disregarded. Perimeter squares are included.*/
		public final static long generateRooksCompleteMoveMask(Square sqr) {
			long mask;
			mask = generateRooksRankMoveMask(sqr) | generateRooksFileMoveMask(sqr);
			return mask;
		}
		/**Generates a bitmap of the basic bishop's diagonal move mask. Occupancies are disregarded. Perimeter squares are included.*/
		public final static long generateBishopsDiagonalMoveMask(Square sqr) {
			long mask;
			mask = (Diagonal.getBySquare(sqr)^sqr.getBitmap());
			return mask;
		}
		/**Generates a bitmap of the basic bishop's anti-diagonal move mask. Occupancies are disregarded. Perimeter squares are included.*/
		public final static long generateBishopsAntiDiagonalMoveMask(Square sqr) {
			long mask;
			mask = (AntiDiagonal.getBySquare(sqr)^sqr.getBitmap());
			return mask;
		}
		/**Generates a bitmap of the basic bishop's complete move mask. Occupancies are disregarded. Perimeter squares are included.*/
		public final static long generateBishopsCompleteMoveMask(Square sqr) {
			long mask;
			mask = generateBishopsDiagonalMoveMask(sqr) | generateBishopsAntiDiagonalMoveMask(sqr);
			return mask;
		}
		/**Generates a bitmap of the basic queen's rank-wise/horizontal move mask. It is identical to the rook's rank-wise move mask. Occupancies are disregarded. Perimeter squares are included.*/
		public final static long generateQueensRankMoveMask(Square sqr) {
			return generateRooksRankMoveMask(sqr);
		}
		/**Generates a bitmap of the basic queen's file-wise/vertical move mask. It is identical to the rook's file-wise move mask. Occupancies are disregarded. Perimeter squares are included.*/
		public final static long generateQueensFileMoveMask(Square sqr) {
			return generateRooksFileMoveMask(sqr);
		}
		/**Generates a bitmap of the basic queen's diagonal move mask. It is identical to the bishop's diagonal move mask. Occupancies are disregarded. Perimeter squares are included.*/
		public final static long generateQueensDiagonalMoveMask(Square sqr) {
			return generateBishopsDiagonalMoveMask(sqr);
		}
		/**Generates a bitmap of the basic queen's anti-diagonal move mask. It is identical to the bishop's anti-diagonal move mask. Occupancies are disregarded. Perimeter squares are included.*/
		public final static long generateQueensAntiDiagonalMoveMask(Square sqr) {
			return generateBishopsAntiDiagonalMoveMask(sqr);
		}
		/**Generates a bitmap of the basic queen's complete move mask. Occupancies are disregarded. Perimeter squares are included.*/
		public final static long generateQueensCompleteMoveMask(Square sqr) {
			long mask;
			mask = generateRooksCompleteMoveMask(sqr) | generateBishopsCompleteMoveMask(sqr);
			return mask;
		}
	}
	
	/**An enum type that holds all the attack rays for each direction from each square on the board as bitmaps.
	 * 
	 * @author Viktor
	 *
	 */
	public static enum SliderAttackRayMask {
		
		A1, B1, C1, D1, E1, F1, G1, H1,
		A2, B2, C2, D2, E2, F2, G2, H2,
		A3, B3, C3, D3, E3, F3, G3, H3,
		A4, B4, C4, D4, E4, F4, G4, H4,
		A5, B5, C5, D5, E5, F5, G5, H5,
		A6, B6, C6, D6, E6, F6, G6, H6,
		A7, B7, C7, D7, E7, F7, G7, H7,
		A8, B8, C8, D8, E8, F8, G8, H8;
		
		final long rankPos;
		final long rankNeg;
		final long filePos;
		final long fileNeg;
		final long diagonalPos;
		final long diagonalNeg;
		final long antiDiagonalPos;
		final long antiDiagonalNeg;
		
		private SliderAttackRayMask( ) {
			int sqrInd = this.ordinal();
			long sqrBit = Square.getBitmapByIndex(sqrInd);
			long rank = Rank.getBySquareIndex(sqrInd);
			long file = File.getBySquareIndex(sqrInd);
			long diagonal = Diagonal.getBySquareIndex(sqrInd);
			long antiDiagonal = AntiDiagonal.getBySquareIndex(sqrInd);
			this.rankPos = rank & ~((sqrBit << 1) - 1);
			this.rankNeg = rank & (sqrBit - 1);
			this.filePos = file & ~((sqrBit << 1) - 1);
			this.fileNeg = file & (sqrBit - 1);
			this.diagonalPos = diagonal & ~((sqrBit << 1) - 1);
			this.diagonalNeg = diagonal & (sqrBit - 1);
			this.antiDiagonalPos = antiDiagonal & ~((sqrBit << 1) - 1);
			this.antiDiagonalNeg = antiDiagonal & (sqrBit - 1);
		}
		public long getRankPos() {
			return this.rankPos;
		}
		public long getRankNeg() {
			return this.rankNeg;
		}
		public long getFilePos() {
			return this.filePos;
		}
		public long getFileNeg() {
			return this.fileNeg;
		}
		public long getDiagonalPos() {
			return this.diagonalPos;
		}
		public long getDiagonalNeg() {
			return this.diagonalNeg;
		}
		public long getAntiDiagonalPos() {
			return this.antiDiagonalPos;
		}
		public long getAntiDiagonalNeg() {
			return this.antiDiagonalNeg;
		}
		public static SliderAttackRayMask getByIndex(int sqrInd) {
			switch(sqrInd) {
				case 0:  return A1; case 1:  return B1; case 2:  return C1; case 3:  return D1; case 4:  return E1; case 5:  return F1; case 6:  return G1; case 7:  return H1;
				case 8:  return A2; case 9:  return B2; case 10: return C2; case 11: return D2; case 12: return E2; case 13: return F2; case 14: return G2; case 15: return H2;
				case 16: return A3; case 17: return B3; case 18: return C3; case 19: return D3; case 20: return E3; case 21: return F3; case 22: return G3; case 23: return H3;
				case 24: return A4; case 25: return B4; case 26: return C4; case 27: return D4; case 28: return E4; case 29: return F4; case 30: return G4; case 31: return H4;
				case 32: return A5; case 33: return B5; case 34: return C5; case 35: return D5; case 36: return E5; case 37: return F5; case 38: return G5; case 39: return H5;
				case 40: return A6; case 41: return B6; case 42: return C6; case 43: return D6; case 44: return E6; case 45: return F6; case 46: return G6; case 47: return H6;
				case 48: return A7; case 49: return B7; case 50: return C7; case 51: return D7; case 52: return E7; case 53: return F7; case 54: return G7; case 55: return H7;
				case 56: return A8; case 57: return B8; case 58: return C8; case 59: return D8; case 60: return E8; case 61: return F8; case 62: return G8; case 63: return H8;
				default: throw new IllegalArgumentException("Invalid square index.");
			}
		}
	}
	
	/**An enum type that holds the rank, file, diagonal, and anti-diagonal that cross the square for each square as bitmaps. The square itself and the perimeter squares are excluded as their occupancies do not affect the attack set variations.
	 * 
	 * @author Viktor
	 *
	 */
	public static enum SliderOccupancyMask {
		
		A1(true),
			B1, C1, D1, E1, F1, G1, H1,
		A2, B2, C2, D2, E2, F2, G2, H2,
		A3, B3, C3, D3, E3, F3, G3, H3,
		A4, B4, C4, D4, E4, F4, G4, H4,
		A5, B5, C5, D5, E5, F5, G5, H5,
		A6, B6, C6, D6, E6, F6, G6, H6,
		A7, B7, C7, D7, E7, F7, G7, H7,
		A8, B8, C8, D8, E8, F8, G8, H8;
		
		final long rookOccupancyMask;
		final long bishopOccupancyMask;
		
		final byte rookOccupancyMaskBitCount;
		final byte bishopOccupancyMaskBitCount;
		
		private static long ANTIFRAME_VERTICAL;
		private static long ANTIFRAME_HORIZONTAL;
		private static long ANTIFRAME;
		
		private void initializeAntiFrames() {
			ANTIFRAME_VERTICAL 		= ~(File.A.getBitmap() 	| File.H.getBitmap());
			ANTIFRAME_HORIZONTAL	= ~(Rank.R1.getBitmap() | Rank.R8.getBitmap());
			ANTIFRAME				=  (ANTIFRAME_VERTICAL	& ANTIFRAME_HORIZONTAL);
		}
		private SliderOccupancyMask(boolean flag) {
			this.initializeAntiFrames();
			Square sqr = Square.getByIndex(this.ordinal());
			this.rookOccupancyMask = generateRooksCompleteOccupancyMask(sqr);
			this.bishopOccupancyMask = generateBishopsCompleteOccupancyMask(sqr);
			this.rookOccupancyMaskBitCount = (byte)BitOperations.getCardinality(this.rookOccupancyMask);
			this.bishopOccupancyMaskBitCount = (byte)BitOperations.getCardinality(this.bishopOccupancyMask);
		}
		private SliderOccupancyMask() {
			Square sqr = Square.getByIndex(this.ordinal());
			this.rookOccupancyMask = generateRooksCompleteOccupancyMask(sqr);
			this.bishopOccupancyMask = generateBishopsCompleteOccupancyMask(sqr);
			this.rookOccupancyMaskBitCount = (byte)BitOperations.getCardinality(this.rookOccupancyMask);
			this.bishopOccupancyMaskBitCount = (byte)BitOperations.getCardinality(this.bishopOccupancyMask);
		}
		public long getRookOccupancyMask() {
			return this.rookOccupancyMask;
		}
		public long getBishopOccupancyMask() {
			return this.bishopOccupancyMask;
		}
		public byte getRookOccupancyMaskBitCount() {
			return this.rookOccupancyMaskBitCount;
		}
		public byte getBishopOccupancyMaskBitCount() {
			return this.bishopOccupancyMaskBitCount;
		}
		private static long generateRooksRankOccupancyMask(Square sqr) {
			return (MoveMask.generateRooksRankMoveMask(sqr) & ANTIFRAME_VERTICAL);
		}
		private static long generateRooksFileOccupancyMask(Square sqr) {
			return (MoveMask.generateRooksFileMoveMask(sqr) & ANTIFRAME_HORIZONTAL);
		}
		private static long generateRooksCompleteOccupancyMask(Square sqr) {
			return (generateRooksRankOccupancyMask(sqr) | generateRooksFileOccupancyMask(sqr));
		}
		private static long generateBishopsDiagonalOccupancyMask(Square sqr) {
			return (MoveMask.generateBishopsDiagonalMoveMask(sqr) & ANTIFRAME);
		}
		private static long generateBishopsAntiDiagonalOccupancyMask(Square sqr) {
			return (MoveMask.generateBishopsAntiDiagonalMoveMask(sqr) & ANTIFRAME);
		}
		private static long generateBishopsCompleteOccupancyMask(Square sqr) {
			return (generateBishopsDiagonalOccupancyMask(sqr) | generateBishopsAntiDiagonalOccupancyMask(sqr));
		}
		public static SliderOccupancyMask getByIndex(int sqrInd) {
			switch(sqrInd) {
				case 0:  return A1; case 1:  return B1; case 2:  return C1; case 3:  return D1; case 4:  return E1; case 5:  return F1; case 6:  return G1; case 7:  return H1;
				case 8:  return A2; case 9:  return B2; case 10: return C2; case 11: return D2; case 12: return E2; case 13: return F2; case 14: return G2; case 15: return H2;
				case 16: return A3; case 17: return B3; case 18: return C3; case 19: return D3; case 20: return E3; case 21: return F3; case 22: return G3; case 23: return H3;
				case 24: return A4; case 25: return B4; case 26: return C4; case 27: return D4; case 28: return E4; case 29: return F4; case 30: return G4; case 31: return H4;
				case 32: return A5; case 33: return B5; case 34: return C5; case 35: return D5; case 36: return E5; case 37: return F5; case 38: return G5; case 39: return H5;
				case 40: return A6; case 41: return B6; case 42: return C6; case 43: return D6; case 44: return E6; case 45: return F6; case 46: return G6; case 47: return H6;
				case 48: return A7; case 49: return B7; case 50: return C7; case 51: return D7; case 52: return E7; case 53: return F7; case 54: return G7; case 55: return H7;
				case 56: return A8; case 57: return B8; case 58: return C8; case 59: return D8; case 60: return E8; case 61: return F8; case 62: return G8; case 63: return H8;
				default: throw new IllegalArgumentException("Invalid square index.");
			}
		}
	}
	
	/**A class with two static two-dimensional arrays for instance fields that hold all the different occupancy variations for each square's occupancy mask once generated.
	 * 
	 * @author Viktor
	 *
	 */
	public static class SliderOccupancyVariations {
		
		private final static long[][] rookOccupancyVariations	= initializeRookOccupancyVariations();
		private final static long[][] bishopOccupancyVariations = initializeBishopOccupancyVariations();
		
		public SliderOccupancyVariations() {
			
		}
		public long[][] getRookOccupancyVariations() {
			return rookOccupancyVariations;
		}
		public long[][] getBishopOccupancyVariations() {
			return bishopOccupancyVariations;
		}
		public static long[] generateRookOccupancyVariations(Square sqr) {
			int sqrInd = sqr.ordinal();
			SliderOccupancyMask occupancyMask = SliderOccupancyMask.getByIndex(sqrInd);
			long mask = occupancyMask.getRookOccupancyMask();
			byte numOfSetBitsInMask = occupancyMask.getRookOccupancyMaskBitCount();
			int[] setBitsInMask = BitOperations.serialize(mask, numOfSetBitsInMask);
			int totalNumOfVariations = (1 << numOfSetBitsInMask);
			long[] occVar = new long[totalNumOfVariations];
			for (int i = 0; i < totalNumOfVariations; i++) {
				int[] setBitsInVarInd = BitOperations.serialize(i, BitOperations.getCardinality(i));
				for (int j = 0; j < setBitsInVarInd.length; j++) {
					occVar[i] |= (1L << (setBitsInMask[setBitsInVarInd[j]]));
				}
			}
			return occVar;
		}
		public static long[] generateBishopOccupancyVariations(Square sqr) {
			int sqrInd = sqr.ordinal();
			SliderOccupancyMask occupancyMask = SliderOccupancyMask.getByIndex(sqrInd);
			long mask = occupancyMask.getBishopOccupancyMask();
			byte numOfSetBitsInMask = occupancyMask.getBishopOccupancyMaskBitCount();
			int[] setBitsInMask = BitOperations.serialize(mask, numOfSetBitsInMask);
			int totalNumOfVariations = (1 << numOfSetBitsInMask);
			long[] occVar = new long[totalNumOfVariations];
			for (int i = 0; i < totalNumOfVariations; i++) {
				int[] setBitsInVarInd = BitOperations.serialize(i, BitOperations.getCardinality(i));
				for (int j = 0; j < setBitsInVarInd.length; j++) {
					occVar[i] |= (1L << (setBitsInMask[setBitsInVarInd[j]]));
				}
			}
			return occVar;
		}
		private static long[][] initializeRookOccupancyVariations() {
			int sqrInd;
			long[][] rookOccVar = new long[64][];
			for (Square sqr : Square.values()) {
				sqrInd = sqr.ordinal();
				rookOccVar[sqrInd] = generateRookOccupancyVariations(sqr);
			}
			return rookOccVar;
		}
		private static long[][] initializeBishopOccupancyVariations() {
			int sqrInd;
			long[][] bishopOccVar = new long[64][];
			for (Square sqr : Square.values()) {
				sqrInd = sqr.ordinal();
				bishopOccVar[sqrInd] = generateBishopOccupancyVariations(sqr);
			}
			return bishopOccVar;
		}
	}
	
	/**A class with two static two-dimensional arrays for instance fields that hold the attack sets for the respective occupancy variations. .
	 * 
	 * @author Viktor
	 *
	 */
	public static class SliderAttackSetVariations {
		
		private final static long[][] rookAttackSetVariations	= initializeRookAttackSetVariations();
		private final static long[][] bishopAttackSetVariations = initializeBishopAttackSetVariations();
		
		public SliderAttackSetVariations() {
			
		}
		public long[][] getRookAttackSetVariations() {
			return rookAttackSetVariations;
		}
		public long[][] getBishopAttackSetVariations() {
			return bishopAttackSetVariations;
		}
		private static long rankAttacks(Square sqr, long occupancy) {
			long sqrBit = sqr.getBitmap();
			int sqrInd = sqr.ordinal();
			long rank = Rank.getBySquareIndex(sqrInd);
			long forward, reverse;
			forward  = rank & occupancy;
			reverse  = BitOperations.reverse(occupancy);
			forward -= 2*sqrBit;
			reverse -= 2*BitOperations.reverse(sqrBit);
			forward ^= BitOperations.reverse(reverse);
			return forward & rank;
		}
		private static long fileAttacks(Square sqr, long occupancy) {
			long sqrBit = sqr.getBitmap();
			int sqrInd = sqr.ordinal();
			long file = File.getBySquareIndex(sqrInd);
			long forward, reverse;
			forward  = file & occupancy;
			reverse  = BitOperations.reverseBytes(forward);
			forward -= sqrBit;
			reverse -= BitOperations.reverseBytes(sqrBit);
			forward ^= BitOperations.reverseBytes(reverse);
			return forward & file;
		}
		private static long diagonalAttacks(Square sqr, long occupancy) {
			long sqrBit = sqr.getBitmap();
			int sqrInd = sqr.ordinal();
			long diagonal = Diagonal.getBySquareIndex(sqrInd);
			long forward, reverse;
			forward  = diagonal & occupancy;
			reverse  = BitOperations.reverseBytes(forward);
			forward -= sqrBit;
			reverse -= BitOperations.reverseBytes(sqrBit);
			forward ^= BitOperations.reverseBytes(reverse);
			return forward & diagonal;
		}
		private static long antiDiagonalAttacks(Square sqr, long occupancy) {
			long sqrBit = sqr.getBitmap();
			int sqrInd = sqr.ordinal();
			long antiDiagonal = AntiDiagonal.getBySquareIndex(sqrInd);
			long forward, reverse;
			forward  = antiDiagonal & occupancy;
			reverse  = BitOperations.reverseBytes(forward);
			forward -= sqrBit;
			reverse -= BitOperations.reverseBytes(sqrBit);
			forward ^= BitOperations.reverseBytes(reverse);
			return forward & antiDiagonal;
		}
		public static long computeRookAttackSet(Square sqr, long occupancy) {
			return rankAttacks(sqr, occupancy) | fileAttacks(sqr, occupancy);
		}
		public static long computeBishopAttackSet(Square sqr, long occupancy) {
			return diagonalAttacks(sqr, occupancy) | antiDiagonalAttacks(sqr, occupancy);
		}
		private static long[][] initializeRookAttackSetVariations() {
			SliderOccupancyVariations occVar = new SliderOccupancyVariations();
			long[][] rookOccupancyVariations = occVar.getRookOccupancyVariations();
			long[][] rookAttVar = new long[64][];
			int variations;
			int sqrInd;
			for (Square sqr : Square.values()) {
				sqrInd = sqr.ordinal();
				variations = rookOccupancyVariations[sqrInd].length;
				rookAttVar[sqrInd] = new long[variations];
				for (int i = 0; i < variations; i++) {
					rookAttVar[sqrInd][i] = computeRookAttackSet(sqr, rookOccupancyVariations[sqrInd][i]);
				}
			}
			return rookAttVar;
		}
		private static long[][] initializeBishopAttackSetVariations() {
			SliderOccupancyVariations occVar = new SliderOccupancyVariations();
			long[][] bishopOccupancyVariations = occVar.getBishopOccupancyVariations();
			long[][] bishopAttVar = new long[64][];
			int variations;
			int sqrInd;
			for (Square sqr : Square.values()) {
				sqrInd = sqr.ordinal();
				variations = bishopOccupancyVariations[sqrInd].length;
				bishopAttVar[sqrInd] = new long[variations];
				for (int i = 0; i < variations; i++) {
					bishopAttVar[sqrInd][i] = computeBishopAttackSet(sqr, bishopOccupancyVariations[sqrInd][i]);
				}
			}
			return bishopAttVar;
		}
	}
	
	public static class MagicNumberGenerator {
		
		private long[][] rookOccupancyVariations;
		private long[][] bishopOccupancyVariations;
		private long[][] rookAttackSetVariations;
		private long[][] bishopAttackSetVariations;
		
		private long[] rookMagicNumbers;
		private long[] bishopMagicNumbers;
		
		public MagicNumberGenerator() {
			SliderOccupancyVariations occVar = new SliderOccupancyVariations();
			rookOccupancyVariations = occVar.getRookOccupancyVariations();
			bishopOccupancyVariations = occVar.getBishopOccupancyVariations();
			SliderAttackSetVariations attSetVar = new SliderAttackSetVariations();
			rookAttackSetVariations = attSetVar.getRookAttackSetVariations();
			bishopAttackSetVariations = attSetVar.getBishopAttackSetVariations();
		}
		public long[] getRookMagicNumbers() {
			return this.rookMagicNumbers;
		}
		public long[] getBishopMagicNumbers() {
			return this.bishopMagicNumbers;
		}
		public void generateRookMagicNumbers() {
			rookMagicNumbers = new long[64];
			long[][] magicRookDatabase = new long[64][];
			Random random = new Random();
			long magicNumber;
			int index;
			boolean collision = false;
			for (int i = 0; i < 64; i++) {
				long[] occVar = this.rookOccupancyVariations[i];
				long[] attVar = this.rookAttackSetVariations[i];
				int variations = occVar.length;
				magicRookDatabase[i] = new long[variations];
				do {
					for (int j = 0; j < variations; j++)
						magicRookDatabase[i][j] = 0;
					collision = false;
					magicNumber = random.nextLong() & random.nextLong() & random.nextLong();
					rookMagicNumbers[i] = magicNumber;
					for (int j = 0; j < variations; j++) {
						index = (int)((occVar[j]*magicNumber) >>> (64 - SliderOccupancyMask.getByIndex(i).rookOccupancyMaskBitCount));
						if (magicRookDatabase[i][index] == 0)
							magicRookDatabase[i][index] = attVar[j];
						else if (magicRookDatabase[i][index] != attVar[j]) {
							collision = true;
							break;
						}
					}
				}
				while (collision);
			}
		}
		public void generateBishopMagicNumbers() {
			bishopMagicNumbers = new long[64];
			long[][] magicBishopDatabase = new long[64][];
			Random random = new Random();
			long magicNumber;
			int index;
			boolean collision = false;
			for (int i = 0; i < 64; i++) {
				long[] occVar = this.bishopOccupancyVariations[i];
				long[] attVar = this.bishopAttackSetVariations[i];
				int variations = occVar.length;
				magicBishopDatabase[i] = new long[variations];
				do {
					for (int j = 0; j < variations; j++)
						magicBishopDatabase[i][j] = 0;
					collision = false;
					magicNumber = random.nextLong() & random.nextLong() & random.nextLong();
					bishopMagicNumbers[i] = magicNumber;
					for (int j = 0; j < variations; j++) {
						index = (int)((occVar[j]*magicNumber) >>> (64 - SliderOccupancyMask.getByIndex(i).bishopOccupancyMaskBitCount));
						if (magicBishopDatabase[i][index] == 0)
							magicBishopDatabase[i][index] = attVar[j];
						else if (magicBishopDatabase[i][index] != attVar[j]) {
							collision = true;
							break;
						}
					}
				}
				while (collision);
			}
		}
		public void generateMagicNumbers() {
			this.generateRookMagicNumbers();
			this.generateBishopMagicNumbers();
		}
		public void printMagicNumbers() {
			if (this.rookMagicNumbers != null || this.bishopMagicNumbers != null) {
				if (this.rookMagicNumbers == null)
					this.rookMagicNumbers = new long[64];
				if (this.bishopMagicNumbers == null)
					this.bishopMagicNumbers = new long[64];
				System.out.format("%s %5s %70s\n\n", "SQ", "ROOK", "BISHOP");
				for (Square sqr : Square.values()) {
					int sqrInd = sqr.ordinal();
					System.out.println(sqr + " (" + BitOperations.toBinaryLiteral(this.rookMagicNumbers[sqrInd]) + ", " + BitOperations.toBinaryLiteral(this.bishopMagicNumbers[sqrInd]) + "),");
				}
			}
			else
				System.out.println("No magic numbers have been generated so far.");
		}
	}
	
	public static enum Magics {
		
		A1 (0b1001000010000000000000000001001000100000010000000000000010000001L, 0b0000000000000010000100000000000100001000000000001000000010000000L),
		B1 (0b0000000011000000000100000000001001100000000000000100000000000000L, 0b1010000000010001000100000000000110000010100100001000000001000100L),
		C1 (0b0000000010000000000010010010000000000000100000000101000000000100L, 0b0100000000000100000010000000000010000001000000010000000000010000L),
		D1 (0b0000000010000000000100000000000001000100100000000000100000000000L, 0b0000100000100100010000010000000000100000000100110000000001000011L),
		E1 (0b0000000010000000000000100000010000000000000010000000000010000000L, 0b0000000000000100000001010010000000000001001001001000000010000000L),
		F1 (0b0010000010000000000001000000000000100001000001100000000010000000L, 0b0000000000000010000000101000001000100000001000000100000000000000L),
		G1 (0b0110010000000000000011000000000010000001000000100011000000001000L, 0b1001000000000100000000010000010000000110101000000010000000000000L),
		H1 (0b0000001010000000000000100100010000100101000000000000000010000000L, 0b0000100000000001000001001000001000000000101000000000010001000000L),
		A2 (0b0000000001010000100000000000000010000000001000000100000000010100L, 0b1000011010000000000010000101100100001000000010000000000011001000L),
		B2 (0b1000000000010000010000000000000000100000000100000000000001000100L, 0b0000000000000010001000000000000100000010001000001000001100000000L),
		C2 (0b0000000000000110000000000100000001010110000000001000000001100000L, 0b0000000010000001100111100010001000000100000010010000000110000000L),
		D2 (0b0001100000000001000000000011000000000000001000110000001000001000L, 0b0110100000001101100001000100000001000000100010000000000001000000L),
		E2 (0b0000000000000000100000000000100000000011100000000000010000000000L, 0b0001001000100001000001000000010000100000110000100000000000000000L),
		F2 (0b0011000000000010000000000000100000000100000100000000101000000000L, 0b0000000000001010000000010011000000001000001000000000000000001000L),
		G2 (0b0001000100100001000000000000000100000000000001100000000010000100L, 0b0000010100100000001000100000100100000001001000000001001000100000L),
		H2 (0b0001000101000010000000000000010010000000010011000000001100010010L, 0b0010000000010000100000100000000100100000100010000000010000000000L),
		A3 (0b0101000100000011000000010000000000100010100000000000000001000000L, 0b0000000010000100000100000000100000010000000001000000100000000000L),
		B3 (0b0000000100110000000001000100000000000000010010001010000000000000L, 0b0100000010100110000010000010000001010100000000110000001000000000L),
		C3 (0b0000000001001010100000001000000001010000000000000010000000000001L, 0b0010000001001000000110100000010000001000000000001010000010011000L),
		D3 (0b0100000100000011100000011000000000001000000000000011000000000100L, 0b1000000000010100000000100000100000000100100100011001000000000000L),
		E3 (0b0000000100001110000000100000000000001000101000000001000000000100L, 0b0000001000001001000000010100100000100000000010000000010101000100L),
		F3 (0b0010010000100001000000010000000000000100000000001000100001000010L, 0b1011000000000010000000000000110000100000100101000010000000000000L),
		G3 (0b0000000010000000000001000000000000011000000100000000000110001010L, 0b0000000000000001010000000000001100011000000000100001000000000000L),
		H3 (0b0000100100000101000000100000000000010001000000001000000001010100L, 0b0001100001000001010000000100000100000001010010000000010000000000L),
		A4 (0b0000000001000000100000000010000010000000000000000100000000000000L, 0b1000010000010000010000000100000001000100000001000000010000000101L),
		B4 (0b1000000001000010100000010000000100000000001000000100000000001001L, 0b0011100000000100000011000001001000100000101010000000010010000000L),
		C4 (0b0000010000000100010000000011000100000000001000000000000100000001L, 0b0000000000010000111010000000100000100100000100000100010000000000L),
		D4 (0b0000000000000100011000010000000100000000000100000000000000001000L, 0b0100100000001100000000010000000010000000001000000000100010000000L),
		E4 (0b0001000001000000000110000000000010000000000001000000000010000000L, 0b0000001000001101000000000100000000101100000001000100000000000000L),
		F4 (0b1001000000001010000000000000101000000000010010001001000000000100L, 0b0100011010001000000010100000010000000000110000000100001000000010L),
		G4 (0b0000000000010000000000110000010000000000010100100100100000010000L, 0b0100000000001000000000100000000000000100100100100000000100000000L),
		H4 (0b1100010110000001100000000000110010000000000010000100100100000000L, 0b0001000000000000110000010000000000101000100001000001000100010100L),
		A5 (0b0000010000000000100000000100000000001100100000000000100000100000L, 0b0100000000010001001000000010000000100000000110000000100001001000L),
		B5 (0b0010000000010000000000000010000000010100110000000000000001000110L, 0b0000000000000000100100000001000000000000000101000000010000110000L),
		C5 (0b0000000000001100100000100001000000000000100000000010000000000000L, 0b0100000000000010000000100000101000000001000000010000100000000000L),
		D5 (0b0000000000001010000000000100000000010010000000000010000000101000L, 0b0000000100000001001000000010000000100000000010000000000010000000L),
		E5 (0b0000010000001000000000011000000000001000100000000000010000000000L, 0b0100000000010000000000100000000010000000000000000101000000000101L),
		F5 (0b1000000000001000000010100000000010000000100000000000010000000000L, 0b0000000000100010000000001000001000000100000000010000000001000000L),
		G5 (0b0000010100100001110000010000100000110100000000000001000000100010L, 0b0000000000001000000000001010010000001110010100010000000100000000L),
		H5 (0b0000000001001010011000000100010000001010000000000000001110100001L, 0b0000000000000100000000001000000000111000000001100000000100000000L),
		A6 (0b1000001010000000000000000100000100100000000001000100000000000011L, 0b0000010000000100000001000000010001000000000000000000010100000000L),
		B6 (0b0000000000110000000000000100110110100000000000010100000000000000L, 0b0000000000000010000000001001000000000100000001000000100010000000L),
		C6 (0b0000000000010000000000000010000000000001100000001000000000011000L, 0b0000001000000110000000001000001000100000100000000000010000001100L),
		D6 (0b0100010000010101000000001000101000110000000000010000000000100000L, 0b0000000010010000010000000100001000000000100000101000100000000000L),
		E6 (0b0000010000001000000011000000000000001000000000001000000010000000L, 0b0000000000000000010010000000000100000100000000010001000001000010L),
		F6 (0b0000000000000010000000010000010000001000001000100000000000110000L, 0b0000100000000100000100000000000010100010000000000001000100001000L),
		G6 (0b1010000100000100000100100010000100001000000001000000000000010000L, 0b1000000000100100010010000001111010000101000010000001010000000000L),
		H6 (0b0000010000000001000000000011000001000010100010010000000000000010L, 0b0000001000001001000010000000001000010000010101000010001010000000L),
		A7 (0b1000000000000000100010100000000100000100001000000100001000000000L, 0b0000000000010010000000010000000110001000010000000000100000001000L),
		B7 (0b0100000000000010010000000000000010000000001000000000010010000000L, 0b0100000001000010000000110000001100001001001100000000000010000000L),
		C7 (0b0100001000000001010000000011001000000101101000001000001000000000L, 0b0100000001000000001001100000000100010100000001100000100000100000L),
		D7 (0b1000000000010000000000010000000000010000011001001010100100000000L, 0b0000000000000000100000000001101001000110000010000000000001000000L),
		E7 (0b0000000000010100000010000000000010000000100001000000000110000000L, 0b0001011001000000011000000000110000000101000001000000110000100100L),
		F7 (0b1000000001101000000000100000000010000000100001000000000010000000L, 0b0000010000000000011000000000110000010000100000001000001000000000L),
		G7 (0b0000000100000001000000000100001000000000010001000000000100000000L, 0b0010000001100010101001000001000000000110100000010000000010010000L),
		H7 (0b0000010000010011000000000000011000000000010100001000000100000000L, 0b0001010001100000010010000000001000000000110000000100100000001000L),
		A8 (0b0100000000001110000000000010000010000000010000010101010100000010L, 0b0000000000000000010000100000000001000100001000000010010000000000L),
		B8 (0b0010000100010000100001000100000100000010000000000010010000010010L, 0b1000000000001001000000001100000001000110000100000001000000000010L),
		C8 (0b0000001010000010100000000100000100001010010100100000000000100010L, 0b0000100000000000000000001001101000000000100001000000010000000000L),
		D8 (0b0000010000000000000010000101000000000001000000000010000000110101L, 0b0000000100000000000000000000010001000000010000100000001000000100L),
		E8 (0b1000000000000010000000000010010100001000101000000001000000010010L, 0b0010000000000000000000001000000000010000000000100000001000000000L),
		F8 (0b0000000000010001000000000000001010000100000000000000100000000001L, 0b0000000000000000000000010010010010011000100100000000000100000010L),
		G8 (0b1000010000000000001000001000000100010000000010000000001000000100L, 0b0010001000000100000010010010000001011000001000001000100100000000L),
		H8 (0b1000001000000000000000010000010000001110001000001000010101000010L, 0b0100000000000100110010000000000010001000000100100000000001000100L);
		
		final byte rookShift;
		final byte bishopShift;
		
		final long rookMagicNumber;
		final long bishopMagicNumber;
		
		private Magics(long rookMagicNumber, long bishopMagicNumber) {
			SliderOccupancyMask sliderOccupancy = SliderOccupancyMask.getByIndex(this.ordinal());
			this.rookShift 			= (byte)(64 - sliderOccupancy.rookOccupancyMaskBitCount);
			this.bishopShift 		= (byte)(64 - sliderOccupancy.bishopOccupancyMaskBitCount);
			this.rookMagicNumber 	= rookMagicNumber;
			this.bishopMagicNumber 	= bishopMagicNumber;
		}
		public byte getRookShift() {
			return this.rookShift;
		}
		public byte getBishopShift() {
			return this.bishopShift;
		}
		public long getRookMagicNumber() {
			return this.rookMagicNumber;
		}
		public long getBishopMagicNumber() {
			return this.bishopMagicNumber;
		}
		public static Magics getByIndex(int sqrInd) {
			switch(sqrInd) {
				case 0:  return A1; case 1:  return B1; case 2:  return C1; case 3:  return D1; case 4:  return E1; case 5:  return F1; case 6:  return G1; case 7:  return H1;
				case 8:  return A2; case 9:  return B2; case 10: return C2; case 11: return D2; case 12: return E2; case 13: return F2; case 14: return G2; case 15: return H2;
				case 16: return A3; case 17: return B3; case 18: return C3; case 19: return D3; case 20: return E3; case 21: return F3; case 22: return G3; case 23: return H3;
				case 24: return A4; case 25: return B4; case 26: return C4; case 27: return D4; case 28: return E4; case 29: return F4; case 30: return G4; case 31: return H4;
				case 32: return A5; case 33: return B5; case 34: return C5; case 35: return D5; case 36: return E5; case 37: return F5; case 38: return G5; case 39: return H5;
				case 40: return A6; case 41: return B6; case 42: return C6; case 43: return D6; case 44: return E6; case 45: return F6; case 46: return G6; case 47: return H6;
				case 48: return A7; case 49: return B7; case 50: return C7; case 51: return D7; case 52: return E7; case 53: return F7; case 54: return G7; case 55: return H7;
				case 56: return A8; case 57: return B8; case 58: return C8; case 59: return D8; case 60: return E8; case 61: return F8; case 62: return G8; case 63: return H8;
				default: throw new IllegalArgumentException("Invalid square index.");
			}
		}
	}
	
	public static enum MoveDatabase {
		
		A1, B1, C1, D1, E1, F1, G1, H1,
		A2, B2, C2, D2, E2, F2, G2, H2,
		A3, B3, C3, D3, E3, F3, G3, H3,
		A4, B4, C4, D4, E4, F4, G4, H4,
		A5, B5, C5, D5, E5, F5, G5, H5,
		A6, B6, C6, D6, E6, F6, G6, H6,
		A7, B7, C7, D7, E7, F7, G7, H7,
		A8, B8, C8, D8, E8, F8, G8, H8;
		
		final byte sqrInd;
		
		SliderOccupancyMask occupancy;
		Magics magics;
		
		private final long[] rook;
		private final long[] bishop;
		final long king;
		final long knight;
		final long pawnWhiteAdvance;
		final long pawnWhiteCapture;
		final long pawnBlackAdvance;
		final long pawnBlackCapture;
		
		private MoveDatabase() {
			this.sqrInd = (byte)this.ordinal();
			Square sqr = Square.getByIndex(this.sqrInd);
			this.magics = Magics.getByIndex(this.sqrInd);
			this.occupancy = SliderOccupancyMask.getByIndex(this.sqrInd);
			SliderOccupancyVariations occVar = new SliderOccupancyVariations();
			long[] rookOccVar 	= occVar.getRookOccupancyVariations()[this.sqrInd];
			long[] bishopOccVar = occVar.getBishopOccupancyVariations()[this.sqrInd];
			int rookNumOfVar 	= rookOccVar.length;
			int bishopNumOfVar 	= bishopOccVar.length;
			this.rook 	= new long[rookNumOfVar];
			this.bishop = new long[bishopNumOfVar];
			SliderAttackSetVariations attVar = new SliderAttackSetVariations();
			long[] rookAttVar 	= attVar.getRookAttackSetVariations()[this.sqrInd];
			long[] bishopAttVar = attVar.getBishopAttackSetVariations()[this.sqrInd];
			int index;
			for (int i = 0; i < rookNumOfVar; i++) {
				index = (int)((rookOccVar[i]*this.magics.rookMagicNumber) >>> this.magics.rookShift);
				this.rook[index] = rookAttVar[i];
			}
			for (int i = 0; i < bishopNumOfVar; i++) {
				index = (int)((bishopOccVar[i]*this.magics.bishopMagicNumber) >>> this.magics.bishopShift);
				this.bishop[index] = bishopAttVar[i];
			}
			this.king 				= MoveMask.generateKingsMoveMask(sqr);
			this.knight 			= MoveMask.generateKnightMasks(sqr);
			this.pawnWhiteAdvance 	= MoveMask.generateWhitePawnsAdvanceMasks(sqr);
			this.pawnWhiteCapture 	= MoveMask.generateWhitePawnsCaptureMasks(sqr);
			this.pawnBlackAdvance 	= MoveMask.generateBlackPawnsAdvanceMasks(sqr);
			this.pawnBlackCapture 	= MoveMask.generateBlackPawnsCaptureMasks(sqr);
		}
		public long getCrudeKingMoves() {
			return this.king;
		}
		public long getCrudeKnightMoves() {
			return this.knight;
		}
		public long getCrudeWhitePawnCaptures() {
			return this.pawnWhiteCapture;
		}
		public long getCrudeBlackPawnCaptures() {
			return this.pawnBlackCapture;
		}
		public long getCrudeRookMoves() {
			return this.rook[0];
		}
		public long getCrudeBishopMoves() {
			return this.bishop[0];
		}
		public long getCrudeQueenMoves() {
			return this.rook[0] | this.bishop[0];
		}
		public long getWhiteKingMoves(long allNonWhiteOccupied) {
			return this.king & allNonWhiteOccupied;
		}
		public long getBlackKingMoves(long allNonBlackOccupied) {
			return this.king & allNonBlackOccupied;
		}
		public long getWhiteKnightMoves(long allNonWhiteOccupied) {
			return this.knight & allNonWhiteOccupied;
		}
		public long getBlackKnightMoves(long allNonBlackOccupied) {
			return this.knight & allNonBlackOccupied;
		}
		public long getWhitePawnCaptures(long allBlackPieces) {
			return this.pawnWhiteCapture & allBlackPieces;
		}
		public long getBlackPawnCaptures(long allWhitePieces) {
			return this.pawnBlackCapture & allWhitePieces;
		}
		public long getWhitePawnAdvances(long allEmpty) {
			if (this.sqrInd > 15)
				return this.pawnWhiteAdvance & allEmpty;
			long firstBlocker = BitOperations.getLSBit(this.pawnWhiteAdvance & ~allEmpty);
			return this.pawnWhiteAdvance & (-1 + firstBlocker);
		}
		public long getBlackPawnAdvances(long allEmpty) {
			if (this.sqrInd < 48)
				return this.pawnBlackAdvance & allEmpty;
			long attackSet = (1L << (this.sqrInd - 8)) & allEmpty;
			return attackSet | ((attackSet >>> 8) & allEmpty);
		}
		public long getWhitePawnMoves(long allBlackPieces, long allEmpty) {
			return this.getWhitePawnAdvances(allEmpty) | this.getWhitePawnCaptures(allBlackPieces);
		}
		public long getBlackPawnMoves(long allWhitePieces, long allEmpty) {
			return this.getBlackPawnAdvances(allEmpty) | this.getBlackPawnCaptures(allWhitePieces);
		}
		public long getWhiteRookMoves(long allNonWhiteOccupied, long allOccupied) {
			return this.rook[(int)(((this.occupancy.rookOccupancyMask & allOccupied)*magics.rookMagicNumber) >>> magics.rookShift)] & allNonWhiteOccupied;
		}
		public long getBlackRookMoves(long allNonBlackOccupied, long allOccupied) {
			return this.rook[(int)(((this.occupancy.rookOccupancyMask & allOccupied)*magics.rookMagicNumber) >>> magics.rookShift)] & allNonBlackOccupied;
		}
		public long getWhiteBishopMoves(long allNonWhiteOccupied, long allOccupied) {
			return this.bishop[(int)(((this.occupancy.bishopOccupancyMask & allOccupied)*magics.bishopMagicNumber) >>> magics.bishopShift)] & allNonWhiteOccupied;
		}
		public long getBlackBishopMoves(long allNonBlackOccupied, long allOccupied) {
			return this.bishop[(int)(((this.occupancy.bishopOccupancyMask & allOccupied)*magics.bishopMagicNumber) >>> magics.bishopShift)] & allNonBlackOccupied;
		}
		public long getWhiteQueenMoves(long allNonWhiteOccupied, long allOccupied) {
			return (this.rook[(int)(((this.occupancy.rookOccupancyMask & allOccupied)*magics.rookMagicNumber) >>> magics.rookShift)] |
				    this.bishop[(int)(((this.occupancy.bishopOccupancyMask & allOccupied)*magics.bishopMagicNumber) >>> magics.bishopShift)]) & allNonWhiteOccupied;
		}
		public long getBlackQueenMoves(long allNonBlackOccupied, long allOccupied) {
			return (this.rook[(int)(((this.occupancy.rookOccupancyMask & allOccupied)*magics.rookMagicNumber) >>> magics.rookShift)] |
				    this.bishop[(int)(((this.occupancy.bishopOccupancyMask & allOccupied)*magics.bishopMagicNumber) >>> magics.bishopShift)]) & allNonBlackOccupied;
		}
		public static MoveDatabase getByIndex(int sqrInd) {
			switch(sqrInd) {
				case 0:  return A1; case 1:  return B1; case 2:  return C1; case 3:  return D1; case 4:  return E1; case 5:  return F1; case 6:  return G1; case 7:  return H1;
				case 8:  return A2; case 9:  return B2; case 10: return C2; case 11: return D2; case 12: return E2; case 13: return F2; case 14: return G2; case 15: return H2;
				case 16: return A3; case 17: return B3; case 18: return C3; case 19: return D3; case 20: return E3; case 21: return F3; case 22: return G3; case 23: return H3;
				case 24: return A4; case 25: return B4; case 26: return C4; case 27: return D4; case 28: return E4; case 29: return F4; case 30: return G4; case 31: return H4;
				case 32: return A5; case 33: return B5; case 34: return C5; case 35: return D5; case 36: return E5; case 37: return F5; case 38: return G5; case 39: return H5;
				case 40: return A6; case 41: return B6; case 42: return C6; case 43: return D6; case 44: return E6; case 45: return F6; case 46: return G6; case 47: return H6;
				case 48: return A7; case 49: return B7; case 50: return C7; case 51: return D7; case 52: return E7; case 53: return F7; case 54: return G7; case 55: return H7;
				case 56: return A8; case 57: return B8; case 58: return C8; case 59: return D8; case 60: return E8; case 61: return F8; case 62: return G8; case 63: return H8;
				default: throw new IllegalArgumentException("Invalid square index.");
			}
		}
	}
	
	//the bitboards for each piece
	private long whiteKing;
	private long whiteQueens;
	private long whiteRooks;
	private long whiteBishops;
	private long whiteKnights;
	private long whitePawns;
	
	private long blackKing;
	private long blackQueens;
	private long blackRooks;
	private long blackBishops;
	private long blackKnights;
	private long blackPawns;
	
	//bitboard unions maintained for faster processing of the position
	private long allWhitePieces;
	private long allBlackPieces;
	
	private long allNonWhiteOccupied;
	private long allNonBlackOccupied;
	
	private long allOccupied;
	private long allEmpty;
	
	private int[] offsetBoard;									//a complimentary board data-structure to the bitboards to efficiently detect pieces on specific squares
	
	private boolean whitesTurn = true;
	
	private long checkers = 0;									//a bitboard of all the pieces that attack the color to move's king
	private boolean check = false;
	
	private LongStack moveList = new LongStack();				//a stack of all the moves made so far
	
	private int plyIndex = 0;									//the count of the current ply/half-move
	private long fiftyMoveRuleClock = 0;						//the number of moves made since the last pawn move or capture; the choice of type fell on long due to data loss when int is shifted beyond the 32nd bit in the move integer
	
	private int enPassantRights = 8;							//denotes the file on which en passant is possible; 8 means no en passant rights
	
	private int whiteCastlingRights = 3;						//denotes to what extent it would still be possible to castle regardless of whether it is actually legally executable in the current position
	private int blackCastlingRights = 3;						//0 - no castling rights, 1 - king-side castling only, 2 - queen-side castling only, 3 - all castling rights
	
	private ZobristGenerator keyGen = new ZobristGenerator();	//a Zobrist key generator for hashing the board
	
	private long zobristKey;									//the Zobrist key that is fairly close to a unique representation of the state of the Board instance in one number
	private long[] zobristKeyHistory = new long[2*237];			/*All the positions that have occured so far represented in Zobrist keys.
														 		 "The longest decisive tournament game is Fressinet-Kosteniuk, Villandry 2007, which Kosteniuk won in 237 moves."*/
	private long repetitions = 0;								//the number of times the current position has occured before; the choice of type fell on long due to data loss when int is shifted beyond the 32nd bit in the move integer
	
	/**Initializes an instance of Board and sets up the pieces in their initial position.*/
	public Board() {
		this.initializeBitBoards();
		this.initializeOffsetBoard();
		this.initializeZobristKeys();
	}
	/**It parses a FEN-String and sets the instance fields accordingly.
	 * Beside standard six-field FEN-Strings, it also accepts four-field Strings without the fifty-move rule clock and the move index.
	 * 
	 * @param fen
	 */
	public Board(String fen) {
		String[] fenFields = fen.split(" "), ranks;
		String board, turn, castling, enPassant, rank;
		char piece;
		int pieceNum, index = 0, fiftyMoveRuleClock, moveIndex;
		if (fenFields.length == 6) {
			try {
				fiftyMoveRuleClock = Integer.parseInt(fenFields[4]);
				if (fiftyMoveRuleClock >= 0)
					this.fiftyMoveRuleClock = fiftyMoveRuleClock;
				else
					this.fiftyMoveRuleClock = 0;
			}
			catch (NumberFormatException e) {
				throw new IllegalArgumentException("The fifty-move rule clock field of the FEN-string does not conform to the standards. Parsing not possible.");
			}
			try {
				moveIndex = (Integer.parseInt(fenFields[5]) - 1)*2;
				if (!this.whitesTurn)
					moveIndex++;
				if (moveIndex >= 0)
					this.plyIndex = moveIndex;
				else
					this.plyIndex = 0;
			}
			catch (NumberFormatException e) {
				throw new IllegalArgumentException("The move index field does not conform to the standards. Parsing not possible.");
			}
		}
		else if (fenFields.length != 4)
			throw new IllegalArgumentException("The FEN-String has an unallowed number of fields.");
		board 			= fenFields[0];
		turn 			= fenFields[1];
		castling 		= fenFields[2];
		enPassant 		= fenFields[3];
		ranks = board.split("/");
		if (ranks.length != 8)
			throw new IllegalArgumentException("The board position representation does not have eight ranks.");
		this.offsetBoard = new int[64];
		for (int i = 0; i < 64; i++)
			this.offsetBoard[i] = 0;
		for (int i = 7; i >= 0; i--) {
			rank = ranks[i];
			for (int j = 0; j < rank.length(); j++) {
				piece = rank.charAt(j);
				pieceNum = piece - '0';
				if (pieceNum >= 0 && pieceNum <= 8)
					index += pieceNum;
				else {
					switch (piece) {
						case 'K': {
							this.offsetBoard[index] = 1;
							this.whiteKing		 = Square.getBitmapByIndex(index);
						}
						break;
						case 'Q': {
							this.offsetBoard[index] = 2;
							this.whiteQueens	|= Square.getBitmapByIndex(index);
						}
						break;
						case 'R': {
							this.offsetBoard[index] = 3;
							this.whiteRooks		|= Square.getBitmapByIndex(index);
						}
						break;
						case 'B': {
							this.offsetBoard[index] = 4;
							this.whiteBishops	|= Square.getBitmapByIndex(index);
						}
						break;
						case 'N': {
							this.offsetBoard[index] = 5;
							this.whiteKnights	|= Square.getBitmapByIndex(index);
						}
						break;
						case 'P': {
							this.offsetBoard[index] = 6;
							this.whitePawns		|= Square.getBitmapByIndex(index);
						}
						break;
						case 'k': {
							this.offsetBoard[index] = 7;
							this.blackKing		 = Square.getBitmapByIndex(index);
						}
						break;
						case 'q': {
							this.offsetBoard[index] = 8;
							this.blackQueens	|= Square.getBitmapByIndex(index);
						}
						break;
						case 'r': {
							this.offsetBoard[index] = 9;
							this.blackRooks		|= Square.getBitmapByIndex(index);
						}
						break;
						case 'b': {
							this.offsetBoard[index] = 10;
							this.blackBishops	|= Square.getBitmapByIndex(index);
						}
						break;
						case 'n': {
							this.offsetBoard[index] = 11;
							this.blackKnights	|= Square.getBitmapByIndex(index);
						}
						break;
						case 'p': {
							this.offsetBoard[index] = 12;
							this.blackPawns		|= Square.getBitmapByIndex(index);
						}
					}
					index++;
				}
			}
		}
		this.initializeCollections();
		if (turn.toLowerCase().compareTo("w") == 0)
			this.whitesTurn = true;
		else
			this.whitesTurn = false;
		this.whiteCastlingRights = 0;
		if (castling.contains("K"))
			this.whiteCastlingRights += 1;
		if (castling.contains("Q"))
			this.whiteCastlingRights += 2;
		this.blackCastlingRights = 0;
		if (castling.contains("k"))
			this.blackCastlingRights += 1;
		if (castling.contains("q"))
			this.blackCastlingRights += 2;
		if (enPassant.compareTo("-") == 0)
			this.enPassantRights = 8;
		else
			this.enPassantRights = enPassant.toLowerCase().charAt(0) - 'a';
		this.setCheck();
		this.initializeZobristKeys();
	}
	private void initializeCollections() {
		this.allWhitePieces		 =  this.whiteKing | this.whiteQueens | this.whiteRooks | this.whiteBishops | this.whiteKnights | this.whitePawns;
		this.allBlackPieces		 =  this.blackKing | this.blackQueens | this.blackRooks | this.blackBishops | this.blackKnights | this.blackPawns;
		this.allNonWhiteOccupied = ~this.allWhitePieces;
		this.allNonBlackOccupied = ~this.allBlackPieces;
		this.allOccupied		 =  this.allWhitePieces | this.allBlackPieces;
		this.allEmpty			 = ~this.allOccupied;
	}
	private void initializeBitBoards() {
		this.whiteKing		=  Piece.WHITE_KING.initPosBitmap;
		this.whiteQueens	=  Piece.WHITE_QUEEN.initPosBitmap;
		this.whiteRooks		=  Piece.WHITE_ROOK.initPosBitmap;
		this.whiteBishops	=  Piece.WHITE_BISHOP.initPosBitmap;
		this.whiteKnights	=  Piece.WHITE_KNIGHT.initPosBitmap;
		this.whitePawns		=  Piece.WHITE_PAWN.initPosBitmap;
		
		this.blackKing		=  Piece.BLACK_KING.initPosBitmap;
		this.blackQueens	=  Piece.BLACK_QUEEN.initPosBitmap;
		this.blackRooks		=  Piece.BLACK_ROOK.initPosBitmap;
		this.blackBishops	=  Piece.BLACK_BISHOP.initPosBitmap;
		this.blackKnights	=  Piece.BLACK_KNIGHT.initPosBitmap;
		this.blackPawns		=  Piece.BLACK_PAWN.initPosBitmap;
		this.initializeCollections();
	}
	private void initializeOffsetBoard() {
		this.offsetBoard = new int[64];
		this.offsetBoard[0] =  Piece.WHITE_ROOK.numericNotation;
		this.offsetBoard[1] =  Piece.WHITE_KNIGHT.numericNotation;
		this.offsetBoard[2] =  Piece.WHITE_BISHOP.numericNotation;
		this.offsetBoard[3] =  Piece.WHITE_QUEEN.numericNotation;
		this.offsetBoard[4] =  Piece.WHITE_KING.numericNotation;
		this.offsetBoard[5] =  Piece.WHITE_BISHOP.numericNotation;
		this.offsetBoard[6] =  Piece.WHITE_KNIGHT.numericNotation;
		this.offsetBoard[7] =  Piece.WHITE_ROOK.numericNotation;
		for (int i = 8; i < 16; i++)
			this.offsetBoard[i] = Piece.WHITE_PAWN.numericNotation;
		
		for (int i = 48; i < 56; i++)
			this.offsetBoard[i] = Piece.BLACK_PAWN.numericNotation;
		this.offsetBoard[56] = Piece.BLACK_ROOK.numericNotation;
		this.offsetBoard[57] = Piece.BLACK_KNIGHT.numericNotation;
		this.offsetBoard[58] = Piece.BLACK_BISHOP.numericNotation;
		this.offsetBoard[59] = Piece.BLACK_QUEEN.numericNotation;
		this.offsetBoard[60] = Piece.BLACK_KING.numericNotation;
		this.offsetBoard[61] = Piece.BLACK_BISHOP.numericNotation;
		this.offsetBoard[62] = Piece.BLACK_KNIGHT.numericNotation;
		this.offsetBoard[63] = Piece.BLACK_ROOK.numericNotation;
	}
	private void initializeZobristKeys() {
		this.zobristKey = keyGen.hash(this);
		this.zobristKeyHistory[0] = this.zobristKey;
	}
	/**Returns a bitmap representing the white king's position.*/
	public long getWhiteKing() {
		return this.whiteKing;
	}
	/**Returns a bitmap representing the white queens' position.*/
	public long getWhiteQueens() {
		return this.whiteQueens;
	}
	/**Returns a bitmap representing the white rooks' position.*/
	public long getWhiteRooks() {
		return this.whiteRooks;
	}
	/**Returns a bitmap representing the white bishops' position.*/
	public long getWhiteBishops() {
		return this.whiteBishops;
	}
	/**Returns a bitmap representing the white knights' position.*/
	public long getWhiteKnights() {
		return this.whiteKnights;
	}
	/**Returns a bitmap representing the white pawns' position.*/
	public long getWhitePawns() {
		return this.whitePawns;
	}
	/**Returns a bitmap representing the black king's position.*/
	public long getBlackKing() {
		return this.blackKing;
	}
	/**Returns a bitmap representing the black queens' position.*/
	public long getBlackQueens() {
		return this.blackQueens;
	}
	/**Returns a bitmap representing the black rooks' position.*/
	public long getBlackRooks() {
		return this.blackRooks;
	}
	/**Returns a bitmap representing the black bishops' position.*/
	public long getBlackBishops() {
		return this.blackBishops;
	}
	/**Returns a bitmap representing the black knights' position.*/
	public long getBlackKnights() {
		return this.blackKnights;
	}
	/**Returns a bitmap representing the black pawns' position.*/
	public long getBlackPawns() {
		return this.blackPawns;
	}
	/**Returns an array of longs representing the current position with each array element denoting a square and the value in the element denoting the piece on the square.*/
	public int[] getOffsetBoard() {
		return this.offsetBoard;
	}
	/**Returns whether it is white's turn or not.*/
	public boolean getTurn() {
		return this.whitesTurn;
	}
	/**Returns whether the color to move's king is in check.*/
	public boolean getCheck() {
		return this.check;
	}
	/**Returns the current ply/half-move index.*/
	public int getPlyIndex() {
		return this.plyIndex;
	}
	/**Returns the number of half-moves made since the last pawn-move or capture.*/
	public long getFiftyMoveRuleClock() {
		return this.fiftyMoveRuleClock;
	}
	/**Returns a number denoting white's castling rights.
	 * 0 - no castling rights
	 * 1 - king-side castling only
	 * 2 - queen-side castling only
	 * 3 - all castling rights
	 */
	public int getWhiteCastlingRights() {
		return this.whiteCastlingRights;
	}
	/**Returns a number denoting black's castling rights.
	 * 0 - no castling rights
	 * 1 - king-side castling only
	 * 2 - queen-side castling only
	 * 3 - all castling rights
	 */
	public int getBlackCastlingRights() {
		return this.blackCastlingRights;
	}
	/**Returns a number denoting the file on which in the current position en passant is possible.
	 * 0 - a; 1 - b; ...; 7 - h; 8 - no en passant rights
	 */
	public int getEnPassantRights() {
		return this.enPassantRights;
	}
	/**Returns the number of times the current position has previously occured since the initialization of the object.*/
	public long getRepetitions() {
		return this.repetitions;
	}
	/**Returns the 64-bit Zobrist key of the current position. A Zobrist key is used to almost uniquely hash a chess position to an integer.*/
	public long getZobristKey() {
		return this.zobristKey;
	}
	/**Returns a long containing all relevant information about the last move made according to the Move enum. if the move history list is empty, it returns 0.*/
	public long getLastMove() {
		return this.moveList.getHead();
	}
	private void setBitboards(int moved, int captured, long fromBit, long toBit) {
		if (this.whitesTurn) {
			switch (moved) {
				case 1: {
					this.whiteKing 			 ^=  fromBit;
					this.whiteKing 			 ^=  toBit;
					this.allWhitePieces 	 ^=  fromBit;
					this.allWhitePieces 	 ^=  toBit;
					this.allNonWhiteOccupied  = ~this.allWhitePieces;
				}
				break;
				case 2: {
					this.whiteQueens		 ^=  fromBit;
					this.whiteQueens 		 ^=  toBit;
					this.allWhitePieces 	 ^=  fromBit;
					this.allWhitePieces 	 ^=  toBit;
					this.allNonWhiteOccupied  = ~this.allWhitePieces;
				}
				break;
				case 3: {
					this.whiteRooks 		 ^=  fromBit;
					this.whiteRooks 		 ^=  toBit;
					this.allWhitePieces 	 ^=  fromBit;
					this.allWhitePieces 	 ^=  toBit;
					this.allNonWhiteOccupied  = ~this.allWhitePieces;
				}
				break;
				case 4: {
					this.whiteBishops 		 ^=  fromBit;
					this.whiteBishops 		 ^=  toBit;
					this.allWhitePieces 	 ^=  fromBit;
					this.allWhitePieces 	 ^=  toBit;
					this.allNonWhiteOccupied  = ~this.allWhitePieces;
				}
				break;
				case 5: {
					this.whiteKnights 		 ^=  fromBit;
					this.whiteKnights 		 ^=  toBit;
					this.allWhitePieces 	 ^=  fromBit;
					this.allWhitePieces 	 ^=  toBit;
					this.allNonWhiteOccupied  = ~this.allWhitePieces;
				}
				break;
				case 6: {
					this.whitePawns 		 ^=  fromBit;
					this.whitePawns 		 ^=  toBit;
					this.allWhitePieces 	 ^=  fromBit;
					this.allWhitePieces 	 ^=  toBit;
					this.allNonWhiteOccupied  = ~this.allWhitePieces;
				}
			}
			switch (captured) {
				case 0:
				break;
				case 8: {
					this.blackQueens 		 ^=  toBit;
					this.allBlackPieces 	 ^=  toBit;
					this.allNonBlackOccupied  = ~this.allBlackPieces;
				}
				break;
				case 9: {
					this.blackRooks 		 ^=  toBit;
					this.allBlackPieces 	 ^=  toBit;
					this.allNonBlackOccupied  = ~this.allBlackPieces;
				}
				break;
				case 10: {
					this.blackBishops 		 ^=  toBit;
					this.allBlackPieces 	 ^=  toBit;
					this.allNonBlackOccupied  = ~this.allBlackPieces;
				}
				break;
				case 11: {
					this.blackKnights 		 ^=  toBit;
					this.allBlackPieces 	 ^=  toBit;
					this.allNonBlackOccupied  = ~this.allBlackPieces;
				}
				break;
				case 12: {
					this.blackPawns 		 ^=  toBit;
					this.allBlackPieces 	 ^=  toBit;
					this.allNonBlackOccupied  = ~this.allBlackPieces;
				}
			}
			this.allOccupied 		  =  this.allWhitePieces | this.allBlackPieces;
			this.allEmpty			  = ~this.allOccupied;
		}
		else {
			switch (moved) {
				case 7: {
					this.blackKing 			 ^=  fromBit;
					this.blackKing 			 ^=  toBit;
					this.allBlackPieces 	 ^=  fromBit;
					this.allBlackPieces 	 ^=  toBit;
					this.allNonBlackOccupied  = ~this.allBlackPieces;
				}
				break;
				case 8: {
					this.blackQueens		 ^=  fromBit;
					this.blackQueens 		 ^=  toBit;
					this.allBlackPieces 	 ^=  fromBit;
					this.allBlackPieces 	 ^=  toBit;
					this.allNonBlackOccupied  = ~this.allBlackPieces;
				}
				break;
				case 9: {
					this.blackRooks 		 ^=  fromBit;
					this.blackRooks 		 ^=  toBit;
					this.allBlackPieces 	 ^=  fromBit;
					this.allBlackPieces 	 ^=  toBit;
					this.allNonBlackOccupied  = ~this.allBlackPieces;
				}
				break;
				case 10: {
					this.blackBishops 		 ^=  fromBit;
					this.blackBishops 		 ^=  toBit;
					this.allBlackPieces 	 ^=  fromBit;
					this.allBlackPieces 	 ^=  toBit;
					this.allNonBlackOccupied  = ~this.allBlackPieces;
				}
				break;
				case 11: {
					this.blackKnights 		 ^=  fromBit;
					this.blackKnights 		 ^=  toBit;
					this.allBlackPieces 	 ^=  fromBit;
					this.allBlackPieces 	 ^=  toBit;
					this.allNonBlackOccupied  = ~this.allBlackPieces;
				}
				break;
				case 12: {
					this.blackPawns 		 ^=  fromBit;
					this.blackPawns 		 ^=  toBit;
					this.allBlackPieces 	 ^=  fromBit;
					this.allBlackPieces 	 ^=  toBit;
					this.allNonBlackOccupied  = ~this.allBlackPieces;
				}
			}
			switch (captured) {
				case 0:
				break;
				case 2: {
					this.whiteQueens 		 ^=  toBit;
					this.allWhitePieces 	 ^=  toBit;
					this.allNonWhiteOccupied  = ~this.allWhitePieces;
				}
				break;
				case 3: {
					this.whiteRooks 		 ^=  toBit;
					this.allWhitePieces 	 ^=  toBit;
					this.allNonWhiteOccupied  = ~this.allWhitePieces;
				}
				break;
				case 4: {
					this.whiteBishops 		 ^=  toBit;
					this.allWhitePieces 	 ^=  toBit;
					this.allNonWhiteOccupied  = ~this.allWhitePieces;
				}
				break;
				case 5: {
					this.whiteKnights 		 ^=  toBit;
					this.allWhitePieces 	 ^=  toBit;
					this.allNonWhiteOccupied  = ~this.allWhitePieces;
				}
				break;
				case 6: {
					this.whitePawns 		 ^=  toBit;
					this.allWhitePieces 	 ^=  toBit;
					this.allNonWhiteOccupied  = ~this.allWhitePieces;
				}
			}
			this.allOccupied 		  =  this.allWhitePieces | this.allBlackPieces;
			this.allEmpty			  = ~this.allOccupied;
		}
	}
	private void setTurn() {
		this.whitesTurn = !this.whitesTurn;
	}
	private void setMoveIndices(int moved, int captured) {
		this.plyIndex++;
		if (captured != 0 || moved == 6 || moved == 12)
			this.fiftyMoveRuleClock = 0;
		else
			this.fiftyMoveRuleClock++;
	}
	private void setEnPassantRights(int from, int to, int movedPiece) {
		if (movedPiece == 6) {
			if (to - from == 16) {
				this.enPassantRights = to%8;
				return;
			}
		}
		else if (movedPiece == 12) {
			if (from - to == 16) {
				this.enPassantRights = to%8;
				return;
			}
		}
		this.enPassantRights = 8;
	}
	private void setCastlingRights() {
		if (this.whitesTurn) {
			switch (this.whiteCastlingRights) {
				case 0: return;
				case 1: {
					if (this.offsetBoard[4] != 1 || this.offsetBoard[7] != 3)
						this.whiteCastlingRights = 0;
				}
				break;
				case 2: {
					if (this.offsetBoard[4] != 1 || this.offsetBoard[0] != 3)
						this.whiteCastlingRights = 0;
				}
				break;
				case 3: {
					if (this.offsetBoard[4] == 1) {
						if (this.offsetBoard[7] != 3)
							this.whiteCastlingRights -= 1;
						if (this.offsetBoard[0] != 3)
							this.whiteCastlingRights -= 2;
					}
					else
						this.whiteCastlingRights = 0;
				}
			}
		}
		else {
			switch (this.blackCastlingRights) {
				case 0:
					return;
				case 1: {
					if (this.offsetBoard[60] != 7 || this.offsetBoard[63] != 9)
						this.blackCastlingRights = 0;
				}
				break;
				case 2: {
					if (this.offsetBoard[60] != 7 || this.offsetBoard[56] != 9)
						this.blackCastlingRights = 0;
				}
				break;
				case 3: {
					if (this.offsetBoard[60] == 7) {
						if (this.offsetBoard[63] != 9)
							this.blackCastlingRights -= 1;
						if (this.offsetBoard[56] != 9)
							this.blackCastlingRights -= 2;
					}
					else
						this.blackCastlingRights = 0;
				}
			}
		}
	}
	private void setCheck() {
		if (this.whitesTurn) {
			this.checkers = getAttackers(BitOperations.indexOfLSBit(this.whiteKing), false);
			if (this.checkers == 0)
				this.check = false;
			else
				this.check = true;
		}
		else {
			this.checkers = getAttackers(BitOperations.indexOfLSBit(this.blackKing), true);
			if (this.checkers == 0)
				this.check = false;
			else
				this.check = true;
		}
	}
	private void setKeys() {
		this.zobristKey = keyGen.updateKey(this);
		this.zobristKeyHistory[this.plyIndex] = this.zobristKey;
	}
	/**Should be used before resetMoveIndices().*/
	private void setRepetitions() {
		if (this.fiftyMoveRuleClock >= 4) {
			for (int i = this.plyIndex; i >= (this.plyIndex - this.fiftyMoveRuleClock); i -= 2) {
				if (this.zobristKeyHistory[i] == this.zobristKey)
					this.repetitions++;
			}
		}
		else
			this.repetitions = 0;
	}
	/**Returns whether there are any pieces of the color defined by byWhite that could be, in the current position, legally moved to the supposedly enemy occupied square specified by sqrInd.*/
	public boolean isAttacked(int sqrInd, boolean byWhite) {
		MoveDatabase dB = MoveDatabase.getByIndex(sqrInd);
		if (byWhite) {
			if ((this.whiteKing		& dB.getCrudeKingMoves()) != 0)
				return true;
			if ((this.whiteKnights 	& dB.getCrudeKnightMoves()) != 0)
				return true;
			if ((this.whitePawns 	& dB.getCrudeBlackPawnCaptures()) != 0)
				return true;
			if (((this.whiteQueens | this.whiteRooks) 	& dB.getBlackRookMoves(this.allNonBlackOccupied, this.allOccupied)) != 0)
				return true;
			if (((this.whiteQueens | this.whiteBishops) & dB.getBlackBishopMoves(this.allNonBlackOccupied, this.allOccupied)) != 0)
				return true;
			if (this.enPassantRights != 8 && sqrInd == 32 + this.enPassantRights) {
				if ((this.whitePawns & dB.getCrudeKingMoves() & Rank.getByIndex(4)) != 0)
					return true;
			}
		}
		else {
			if ((this.blackKing		& dB.getCrudeKingMoves()) != 0)
				return true;
			if ((this.blackKnights 	& dB.getCrudeKnightMoves()) != 0)
				return true;
			if ((this.blackPawns 	& dB.getCrudeWhitePawnCaptures()) != 0)
				return true;
			if (((this.blackQueens | this.blackRooks) 	& dB.getWhiteRookMoves(this.allNonWhiteOccupied, this.allOccupied)) != 0)
				return true;
			if (((this.blackQueens | this.blackBishops) & dB.getWhiteBishopMoves(this.allNonWhiteOccupied, this.allOccupied)) != 0)
				return true;
			if (this.enPassantRights != 8 && sqrInd == 24 + this.enPassantRights) {
				if ((this.blackPawns & dB.getCrudeKingMoves() & Rank.getByIndex(3)) != 0)
					return true;
			}
		}
		return false;
	}
	/**Returns a long representing all the squares on which the pieces are of the color defined by byWhite and in the current position could legally be moved to the supposedly enemy occupied square specified by sqrInd.*/
	public long getAttackers(int sqrInd, boolean byWhite) {
		long attackers = 0;
		MoveDatabase dB = MoveDatabase.getByIndex(sqrInd);
		if (byWhite) {
			attackers  =  this.whiteKing						& dB.getCrudeKingMoves();
			attackers |=  this.whiteKnights						& dB.getCrudeKnightMoves();
			attackers |=  this.whitePawns 						& dB.getCrudeBlackPawnCaptures();
			attackers |= (this.whiteQueens | this.whiteRooks)	& dB.getBlackRookMoves(this.allNonBlackOccupied, this.allOccupied);
			attackers |= (this.whiteQueens | this.whiteBishops) & dB.getBlackBishopMoves(this.allNonBlackOccupied, this.allOccupied);
			if (this.enPassantRights != 8 && sqrInd == 32 + this.enPassantRights)
				attackers |=  this.whitePawns & dB.getCrudeKingMoves() & Rank.getByIndex(4);
		}
		else {
			attackers  =  this.blackKing						& dB.getCrudeKingMoves();
			attackers |=  this.blackKnights						& dB.getCrudeKnightMoves();
			attackers |=  this.blackPawns 						& dB.getCrudeWhitePawnCaptures();
			attackers |= (this.blackQueens | this.blackRooks)	& dB.getWhiteRookMoves(this.allNonWhiteOccupied, this.allOccupied);
			attackers |= (this.blackQueens | this.blackBishops) & dB.getWhiteBishopMoves(this.allNonWhiteOccupied, this.allOccupied);
			if (this.enPassantRights != 8 && sqrInd == 24 + this.enPassantRights)
				attackers |=  this.blackPawns & dB.getCrudeKingMoves() & Rank.getByIndex(3);
		}
		return attackers;
	}
	/**Returns a long representing all the squares on which the pieces are of the color defined by byWhite and in the current position could legally be moved to the supposedly empty square specified by sqrInd.*/
	public long getBlockerCandidates(int sqrInd, boolean byWhite) {
		long blockerCandidates = 0;
		long sqrBit = Square.getBitmapByIndex(sqrInd);
		long blackPawnAdvance = (sqrBit >>> 8), whitePawnAdvance = (sqrBit << 8);
		if (byWhite) {
			MoveDatabase dB = MoveDatabase.getByIndex(sqrInd);
			blockerCandidates |=  this.whiteKnights						& dB.getCrudeKnightMoves();
			blockerCandidates |=  this.whitePawns 						& blackPawnAdvance;
			if ((sqrBit & Rank.getByIndex(3)) != 0 && (this.allEmpty & blackPawnAdvance) != 0)
				blockerCandidates |=  this.whitePawns 					& (blackPawnAdvance >>> 8);
			blockerCandidates |= (this.whiteQueens | this.whiteRooks)	& dB.getBlackRookMoves(this.allNonBlackOccupied, this.allOccupied);
			blockerCandidates |= (this.whiteQueens | this.whiteBishops) & dB.getBlackBishopMoves(this.allNonBlackOccupied, this.allOccupied);
			if (this.enPassantRights == sqrInd%8 && (sqrBit & Rank.getByIndex(5)) != 0)
				blockerCandidates |=  this.whitePawns & dB.getCrudeBlackPawnCaptures();
		}
		else {
			MoveDatabase dB = MoveDatabase.getByIndex(sqrInd);
			blockerCandidates |=  this.blackKnights						& dB.getCrudeKnightMoves();
			blockerCandidates |=  this.blackPawns 						& whitePawnAdvance;
			if ((sqrBit & Rank.getByIndex(4)) != 0 && (this.allEmpty & whitePawnAdvance) != 0)
				blockerCandidates |=  this.blackPawns 					& (whitePawnAdvance << 8);
			blockerCandidates |= (this.blackQueens | this.blackRooks)	& dB.getWhiteRookMoves(this.allNonWhiteOccupied, this.allOccupied);
			blockerCandidates |= (this.blackQueens | this.blackBishops) & dB.getWhiteBishopMoves(this.allNonWhiteOccupied, this.allOccupied);
			if (this.enPassantRights == sqrInd%8 && (sqrBit & Rank.getByIndex(2)) != 0)
				blockerCandidates |=  this.blackPawns & dB.getCrudeWhitePawnCaptures();
		}
		return blockerCandidates;
	}
	/**Returns a long representing all the squares on which there are pinned pieces of the color defined by forWhite in the current position. A pinned piece is one that when moved would expose its king to a check.*/
	public long getPinnedPieces(boolean forWhite) {
		long rankPos, rankNeg, filePos, fileNeg, diagonalPos, diagonalNeg, antiDiagonalPos, antiDiagonalNeg;
		long straightSliders, diagonalSliders;
		SliderAttackRayMask attRayMask;
		int sqrInd;
		long pinnedPiece;
		long pinnedPieces = 0;
		if (forWhite) {
			sqrInd = BitOperations.indexOfBit(this.whiteKing);
			straightSliders = this.blackQueens | this.blackRooks;
			diagonalSliders = this.blackQueens | this.blackBishops;
			attRayMask 		= SliderAttackRayMask.getByIndex(sqrInd);
			rankPos 		= attRayMask.rankPos 			& this.allOccupied;
			rankNeg 		= attRayMask.rankNeg 			& this.allOccupied;
			filePos 		= attRayMask.filePos 			& this.allOccupied;
			fileNeg 		= attRayMask.fileNeg 			& this.allOccupied;
			diagonalPos 	= attRayMask.diagonalPos 		& this.allOccupied;
			diagonalNeg 	= attRayMask.diagonalNeg 		& this.allOccupied;
			antiDiagonalPos = attRayMask.antiDiagonalPos 	& this.allOccupied;
			antiDiagonalNeg = attRayMask.antiDiagonalNeg 	& this.allOccupied;
			if ((pinnedPiece = BitOperations.getLSBit(rankPos)			 & this.allWhitePieces) != 0) {
				if ((BitOperations.getLSBit(rankPos^pinnedPiece) 		 & straightSliders) != 0)
					pinnedPieces |= pinnedPiece;
			}
			if ((pinnedPiece = BitOperations.getLSBit(filePos)			 & this.allWhitePieces) != 0) {
				if ((BitOperations.getLSBit(filePos^pinnedPiece) 		 & straightSliders) != 0)
					pinnedPieces |= pinnedPiece;
			}
			if ((pinnedPiece = BitOperations.getLSBit(diagonalPos)	 	 & this.allWhitePieces) != 0) {
				if ((BitOperations.getLSBit(diagonalPos^pinnedPiece) 	 & diagonalSliders) != 0)
					pinnedPieces |= pinnedPiece;
			}
			if ((pinnedPiece = BitOperations.getLSBit(antiDiagonalPos) 	 & this.allWhitePieces) != 0) {
				if ((BitOperations.getLSBit(antiDiagonalPos^pinnedPiece) & diagonalSliders) != 0)
					pinnedPieces |= pinnedPiece;
			}
			if ((pinnedPiece = BitOperations.getMSBit(rankNeg)		 	 & this.allWhitePieces) != 0) {
				if ((BitOperations.getMSBit(rankNeg^pinnedPiece) 		 & straightSliders) != 0)
					pinnedPieces |= pinnedPiece;
			}
			if ((pinnedPiece = BitOperations.getMSBit(fileNeg)			 & this.allWhitePieces) != 0) {
				if ((BitOperations.getMSBit(fileNeg^pinnedPiece) 		 & straightSliders) != 0)
					pinnedPieces |= pinnedPiece;
			}
			if ((pinnedPiece = BitOperations.getMSBit(diagonalNeg)	 	 & this.allWhitePieces) != 0) {
				if ((BitOperations.getMSBit(diagonalNeg^pinnedPiece) 	 & diagonalSliders) != 0)
					pinnedPieces |= pinnedPiece;
			}
			if ((pinnedPiece = BitOperations.getMSBit(antiDiagonalNeg) 	 & this.allWhitePieces) != 0) {
				if ((BitOperations.getMSBit(antiDiagonalNeg^pinnedPiece) & diagonalSliders) != 0)
					pinnedPieces |= pinnedPiece;
			}
		}
		else {
			sqrInd = BitOperations.indexOfBit(this.blackKing);
			straightSliders = this.whiteQueens | this.whiteRooks;
			diagonalSliders = this.whiteQueens | this.whiteBishops;
			attRayMask		= SliderAttackRayMask.getByIndex(sqrInd);
			rankPos 		= attRayMask.rankPos 			& this.allOccupied;
			rankNeg 		= attRayMask.rankNeg 			& this.allOccupied;
			filePos 		= attRayMask.filePos 			& this.allOccupied;
			fileNeg 		= attRayMask.fileNeg 			& this.allOccupied;
			diagonalPos 	= attRayMask.diagonalPos 		& this.allOccupied;
			diagonalNeg 	= attRayMask.diagonalNeg 		& this.allOccupied;
			antiDiagonalPos = attRayMask.antiDiagonalPos 	& this.allOccupied;
			antiDiagonalNeg = attRayMask.antiDiagonalNeg 	& this.allOccupied;
			if ((pinnedPiece = BitOperations.getLSBit(rankPos)			 & this.allBlackPieces) != 0) {
				if ((BitOperations.getLSBit(rankPos^pinnedPiece) 		 & straightSliders) != 0)
					pinnedPieces |= pinnedPiece;
			}
			if ((pinnedPiece = BitOperations.getLSBit(filePos)			 & this.allBlackPieces) != 0) {
				if ((BitOperations.getLSBit(filePos^pinnedPiece) 		 & straightSliders) != 0)
					pinnedPieces |= pinnedPiece;
			}
			if ((pinnedPiece = BitOperations.getLSBit(diagonalPos)	 	 & this.allBlackPieces) != 0) {
				if ((BitOperations.getLSBit(diagonalPos^pinnedPiece) 	 & diagonalSliders) != 0)
					pinnedPieces |= pinnedPiece;
			}
			if ((pinnedPiece = BitOperations.getLSBit(antiDiagonalPos) 	 & this.allBlackPieces) != 0) {
				if ((BitOperations.getLSBit(antiDiagonalPos^pinnedPiece) & diagonalSliders) != 0)
					pinnedPieces |= pinnedPiece;
			}
			if ((pinnedPiece = BitOperations.getMSBit(rankNeg)		 	 & this.allBlackPieces) != 0) {
				if ((BitOperations.getMSBit(rankNeg^pinnedPiece) 		 & straightSliders) != 0)
					pinnedPieces |= pinnedPiece;
			}
			if ((pinnedPiece = BitOperations.getMSBit(fileNeg)			 & this.allBlackPieces) != 0) {
				if ((BitOperations.getMSBit(fileNeg^pinnedPiece) 		 & straightSliders) != 0)
					pinnedPieces |= pinnedPiece;
			}
			if ((pinnedPiece = BitOperations.getMSBit(diagonalNeg)	 	 & this.allBlackPieces) != 0) {
				if ((BitOperations.getMSBit(diagonalNeg^pinnedPiece) 	 & diagonalSliders) != 0)
					pinnedPieces |= pinnedPiece;
			}
			if ((pinnedPiece = BitOperations.getMSBit(antiDiagonalNeg) 	 & this.allBlackPieces) != 0) {
				if ((BitOperations.getMSBit(antiDiagonalNeg^pinnedPiece) & diagonalSliders) != 0)
					pinnedPieces |= pinnedPiece;
			}
		}
		return pinnedPieces;
	}
	private LongQueue generateNormalMoves() {
		SliderAttackRayMask attRayMask;
		long rankPos, rankNeg, filePos, fileNeg, diagonalPos, diagonalNeg, antiDiagonalPos, antiDiagonalNeg;
		long straightSliders, diagonalSliders, pinnedPieceBit, pinnedPieces = 0, movablePieces, pinnerBit;
		long pieceSet, moveSet;
		int pinnedPieceInd, pinnedPiece, king, queen, rook, bishop, knight, pawn, to, enPassantDestination = 0;
		IntStack queens, rooks, bishops, knights, pawns;
		long pinnedPieceMove = 0, kingMove = 0, queenMove = 0, rookMove = 0, bishopMove = 0, knightMove = 0, pawnMove = 0, move = 0;
		IntStack kingMoves, queenMoves, rookMoves, bishopMoves, knightMoves, pawnMoves, pinnedPieceMoves;
		LongQueue moves = new LongQueue();
		move |= (this.whiteCastlingRights		<< Move.PREVIOUS_WHITE_CASTLING_RIGHTS.shift);
		move |= (this.blackCastlingRights		<< Move.PREVIOUS_BLACK_CASTLING_RIGHTS.shift);
		move |= (this.enPassantRights	 		<< Move.PREVIOUS_ENPASSANT_RIGHTS.shift);
		move |= (this.fiftyMoveRuleClock		<< Move.PREVIOUS_FIFTY_MOVE_RULE_CLOCK.shift);
		move |= (this.repetitions				<< Move.PREVIOUS_REPETITIONS.shift);
		if (this.whitesTurn) {
			king = BitOperations.indexOfBit(this.whiteKing);
			kingMove  =  move;
			kingMove |= (king << Move.FROM.shift);
			kingMove |= (1L << Move.MOVED_PIECE.shift);
			moveSet	  = MoveDatabase.getByIndex(king).getWhiteKingMoves(this.allNonWhiteOccupied);
			if (moveSet != 0) {
				kingMoves = BitOperations.serialize(moveSet);
				while (kingMoves.hasNext()) {
					to = kingMoves.next();
					if (!isAttacked(to, false))
						moves.add(kingMove | (to << Move.TO.getShift()) | (this.offsetBoard[to] << Move.CAPTURED_PIECE.shift));
				}
				if ((this.whiteCastlingRights & 2) != 0) {
					if (((Square.getBitmapByIndex(1) | Square.getBitmapByIndex(2) | Square.getBitmapByIndex(3)) & this.allOccupied) == 0) {
						if (((moves.getTail() >>> Move.TO.shift) & Move.TO.mask) == 3 && !isAttacked(2, false))
							moves.add(kingMove | (2L << Move.TO.shift) | (2L << Move.TYPE.shift));
					}
				}
				if ((this.whiteCastlingRights & 1) != 0) {
					if (((Square.getBitmapByIndex(5) | Square.getBitmapByIndex(6)) & this.allOccupied) == 0) {
						if (!isAttacked(5, false) && !isAttacked(6, false))
							moves.add(kingMove | (6L << Move.TO.shift) | (1L << Move.TYPE.shift));
					}
				}
			}
			straightSliders = this.blackQueens | this.blackRooks;
			diagonalSliders = this.blackQueens | this.blackBishops;
			attRayMask 		= SliderAttackRayMask.getByIndex(king);
			rankPos 		= attRayMask.getRankPos();
			rankNeg 		= attRayMask.getRankNeg();
			filePos 		= attRayMask.getFilePos();
			fileNeg 		= attRayMask.getFileNeg();
			diagonalPos 	= attRayMask.getDiagonalPos();
			diagonalNeg 	= attRayMask.getDiagonalNeg();
			antiDiagonalPos = attRayMask.getAntiDiagonalPos();
			antiDiagonalNeg = attRayMask.getAntiDiagonalNeg();
			if ((pinnedPieceBit = BitOperations.getLSBit(rankPos & this.allOccupied) & this.allWhitePieces) != 0) {
				if ((pinnerBit = BitOperations.getLSBit((rankPos & this.allOccupied)^pinnedPieceBit) & straightSliders) != 0) {
					pinnedPieces	|= pinnedPieceBit;
					pinnedPieceInd	 = BitOperations.indexOfBit(pinnedPieceBit);
					pinnedPiece		 = this.offsetBoard[pinnedPieceInd];
					if (pinnedPiece == 2 || pinnedPiece == 3) {
						pinnedPieceMove  = (pinnedPieceInd << Move.FROM.shift);
						pinnedPieceMove |=  move;
						pinnedPieceMove |= (pinnedPiece << Move.MOVED_PIECE.shift);
						pinnedPieceMoves = BitOperations.serialize(((pinnerBit - this.whiteKing) << 1)^pinnedPieceBit);
						while (pinnedPieceMoves.hasNext()) {
							to = pinnedPieceMoves.next();
							moves.add(pinnedPieceMove | (to << Move.TO.shift) | (this.offsetBoard[to] << Move.CAPTURED_PIECE.shift));
						}
					}
				}
			}
			if ((pinnedPieceBit = BitOperations.getLSBit(filePos & this.allOccupied) & this.allWhitePieces) != 0) {
				if ((pinnerBit = BitOperations.getLSBit((filePos & this.allOccupied)^pinnedPieceBit)  & straightSliders) != 0) {
					pinnedPieces	|= pinnedPieceBit;
					pinnedPieceInd	 = BitOperations.indexOfBit(pinnedPieceBit);
					pinnedPiece		 = this.offsetBoard[pinnedPieceInd];
					if (pinnedPiece == 2 || pinnedPiece == 3) {
						pinnedPieceMove  =  move;
						pinnedPieceMove |= (pinnedPieceInd << Move.FROM.shift);
						pinnedPieceMove |= (pinnedPiece << Move.MOVED_PIECE.shift);
						pinnedPieceMoves = BitOperations.serialize((((pinnerBit - this.whiteKing) << 1) & filePos)^pinnedPieceBit);
						while (pinnedPieceMoves.hasNext()) {
							to = pinnedPieceMoves.next();
							moves.add(pinnedPieceMove | (to << Move.TO.shift) | (this.offsetBoard[to] << Move.CAPTURED_PIECE.shift));
						}
					}
					else if (pinnedPiece == 6) {
						pinnedPieceMove  =  move;
						pinnedPieceMove |= (pinnedPieceInd << Move.FROM.shift);
						pinnedPieceMove |= (pinnedPiece << Move.MOVED_PIECE.shift);
						pinnedPieceMoves = BitOperations.serialize(MoveDatabase.getByIndex(pinnedPieceInd).getWhitePawnAdvances(this.allEmpty));
						while (pinnedPieceMoves.hasNext()) {
							to = pinnedPieceMoves.next();
							moves.add(pinnedPieceMove | (to << Move.TO.shift));
						}
					}
				}
			}
			if ((pinnedPieceBit = BitOperations.getLSBit(diagonalPos & this.allOccupied) & this.allWhitePieces) != 0) {
				if ((pinnerBit = BitOperations.getLSBit((diagonalPos & this.allOccupied)^pinnedPieceBit) & diagonalSliders) != 0) {
					pinnedPieces	|= pinnedPieceBit;
					pinnedPieceInd	 = BitOperations.indexOfBit(pinnedPieceBit);
					pinnedPiece		 = this.offsetBoard[pinnedPieceInd];
					if (pinnedPiece == 2 || pinnedPiece == 4) {
						pinnedPieceMove  =  move;
						pinnedPieceMove |= (pinnedPieceInd << Move.FROM.shift);
						pinnedPieceMove |= (pinnedPiece << Move.MOVED_PIECE.shift);
						pinnedPieceMoves = BitOperations.serialize((((pinnerBit - this.whiteKing) << 1) & diagonalPos)^pinnedPieceBit);
						while (pinnedPieceMoves.hasNext()) {
							to = pinnedPieceMoves.next();
							moves.add(pinnedPieceMove | (to << Move.TO.shift) | (this.offsetBoard[to] << Move.CAPTURED_PIECE.shift));
						}
					}
					else if (pinnedPiece == 6) {
						pinnedPieceMove  =  move;
						pinnedPieceMove |= (pinnedPieceInd << Move.FROM.shift);
						pinnedPieceMove |= (pinnedPiece << Move.MOVED_PIECE.shift);
						if (this.enPassantRights != 0)
							pinnedPieceMoves = BitOperations.serialize(MoveDatabase.getByIndex(pinnedPieceInd).getWhitePawnCaptures(this.allBlackPieces | (1L << (enPassantDestination = 40 + this.enPassantRights))) & diagonalPos);
						else
							pinnedPieceMoves = BitOperations.serialize(MoveDatabase.getByIndex(pinnedPieceInd).getWhitePawnCaptures(this.allBlackPieces) & diagonalPos);
						while (pinnedPieceMoves.hasNext()) {
							to = pinnedPieceMoves.next();
							if (to == enPassantDestination)
								moves.add(pinnedPieceMove | (to << Move.TO.shift) | (12L << Move.CAPTURED_PIECE.shift) | (3L << Move.TYPE.shift));
							else if (to >= 56) {
								moves.add(pinnedPieceMove | (to << Move.TO.shift) | (this.offsetBoard[to] << Move.CAPTURED_PIECE.shift) | (4L << Move.TYPE.shift));
								moves.add(pinnedPieceMove | (to << Move.TO.shift) | (this.offsetBoard[to] << Move.CAPTURED_PIECE.shift) | (5L << Move.TYPE.shift));
								moves.add(pinnedPieceMove | (to << Move.TO.shift) | (this.offsetBoard[to] << Move.CAPTURED_PIECE.shift) | (6L << Move.TYPE.shift));
								moves.add(pinnedPieceMove | (to << Move.TO.shift) | (this.offsetBoard[to] << Move.CAPTURED_PIECE.shift) | (7L << Move.TYPE.shift));
							}
							else
								moves.add(pinnedPieceMove | (to << Move.TO.shift) | (this.offsetBoard[to] << Move.CAPTURED_PIECE.shift));
						}
					}
				}
			}
			if ((pinnedPieceBit = BitOperations.getLSBit(antiDiagonalPos & this.allOccupied) & this.allWhitePieces) != 0) {
				if ((pinnerBit = BitOperations.getLSBit((antiDiagonalPos & this.allOccupied)^pinnedPieceBit) & diagonalSliders) != 0) {
					pinnedPieces	|= pinnedPieceBit;
					pinnedPieceInd	 = BitOperations.indexOfBit(pinnedPieceBit);
					pinnedPiece		 = this.offsetBoard[pinnedPieceInd];
					if (pinnedPiece == 2 || pinnedPiece == 4) {
						pinnedPieceMove  =  move;
						pinnedPieceMove |= (pinnedPieceInd << Move.FROM.shift);
						pinnedPieceMove |= (pinnedPiece << Move.MOVED_PIECE.shift);
						pinnedPieceMoves = BitOperations.serialize((((pinnerBit - this.whiteKing) << 1) & antiDiagonalPos)^pinnedPieceBit);
						while (pinnedPieceMoves.hasNext()) {
							to = pinnedPieceMoves.next();
							moves.add(pinnedPieceMove | (to << Move.TO.shift) | (this.offsetBoard[to] << Move.CAPTURED_PIECE.shift));
						}
					}
					else if (pinnedPiece == 6) {
						pinnedPieceMove  =  move;
						pinnedPieceMove |= (pinnedPieceInd << Move.FROM.shift);
						pinnedPieceMove |= (pinnedPiece << Move.MOVED_PIECE.shift);
						if (this.enPassantRights != 0)
							pinnedPieceMoves = BitOperations.serialize(MoveDatabase.getByIndex(pinnedPieceInd).getWhitePawnCaptures(this.allBlackPieces | (1L << (enPassantDestination = 40 + this.enPassantRights))) & antiDiagonalPos);
						else
							pinnedPieceMoves = BitOperations.serialize(MoveDatabase.getByIndex(pinnedPieceInd).getWhitePawnCaptures(this.allBlackPieces) & antiDiagonalPos);
						while (pinnedPieceMoves.hasNext()) {
							to = pinnedPieceMoves.next();
							if (to == enPassantDestination)
								moves.add(pinnedPieceMove | (to << Move.TO.shift) | (12L << Move.CAPTURED_PIECE.shift) | (3L << Move.TYPE.shift));
							else if (to >= 56) {
								moves.add(pinnedPieceMove | (to << Move.TO.shift) | (this.offsetBoard[to] << Move.CAPTURED_PIECE.shift) | (4L << Move.TYPE.shift));
								moves.add(pinnedPieceMove | (to << Move.TO.shift) | (this.offsetBoard[to] << Move.CAPTURED_PIECE.shift) | (5L << Move.TYPE.shift));
								moves.add(pinnedPieceMove | (to << Move.TO.shift) | (this.offsetBoard[to] << Move.CAPTURED_PIECE.shift) | (6L << Move.TYPE.shift));
								moves.add(pinnedPieceMove | (to << Move.TO.shift) | (this.offsetBoard[to] << Move.CAPTURED_PIECE.shift) | (7L << Move.TYPE.shift));
							}
							else
								moves.add(pinnedPieceMove | (to << Move.TO.shift) | (this.offsetBoard[to] << Move.CAPTURED_PIECE.shift));
						}
					}
				}
			}
			if ((pinnedPieceBit = BitOperations.getMSBit(rankNeg & this.allOccupied) & this.allWhitePieces) != 0) {
				if ((pinnerBit = BitOperations.getMSBit((rankNeg & this.allOccupied)^pinnedPieceBit) & straightSliders) != 0) {
					pinnedPieces	|= pinnedPieceBit;
					pinnedPieceInd	 = BitOperations.indexOfBit(pinnedPieceBit);
					pinnedPiece		 = this.offsetBoard[pinnedPieceInd];
					if (pinnedPiece == 2 || pinnedPiece == 3) {
						pinnedPieceMove  =  move;
						pinnedPieceMove |= (pinnedPieceInd << Move.FROM.shift);
						pinnedPieceMove |= (pinnedPiece << Move.MOVED_PIECE.shift);
						pinnedPieceMoves = BitOperations.serialize((this.whiteKing - pinnerBit)^pinnedPieceBit);
						while (pinnedPieceMoves.hasNext()) {
							to = pinnedPieceMoves.next();
							moves.add(pinnedPieceMove | (to << Move.TO.shift) | (this.offsetBoard[to] << Move.CAPTURED_PIECE.shift));
						}
					}
				}
			}
			if ((pinnedPieceBit = BitOperations.getMSBit(fileNeg & this.allOccupied) & this.allWhitePieces) != 0) {
				if ((pinnerBit = BitOperations.getMSBit((fileNeg & this.allOccupied)^pinnedPieceBit)  & straightSliders) != 0) {
					pinnedPieces	|= pinnedPieceBit;
					pinnedPieceInd	 = BitOperations.indexOfBit(pinnedPieceBit);
					pinnedPiece		 = this.offsetBoard[pinnedPieceInd];
					if (pinnedPiece == 2 || pinnedPiece == 3) {
						pinnedPieceMove  =  move;
						pinnedPieceMove |= (pinnedPieceInd << Move.FROM.shift);
						pinnedPieceMove |= (pinnedPiece << Move.MOVED_PIECE.shift);
						pinnedPieceMoves = BitOperations.serialize(((this.whiteKing - pinnerBit) & fileNeg)^pinnedPieceBit);
						while (pinnedPieceMoves.hasNext()) {
							to = pinnedPieceMoves.next();
							moves.add(pinnedPieceMove | (to << Move.TO.shift) | (this.offsetBoard[to] << Move.CAPTURED_PIECE.shift));
						}
					}
					else if (pinnedPiece == 6) {
						pinnedPieceMove  =  move;
						pinnedPieceMove |= (pinnedPieceInd << Move.FROM.shift);
						pinnedPieceMove |= (pinnedPiece << Move.MOVED_PIECE.shift);
						pinnedPieceMoves = BitOperations.serialize(MoveDatabase.getByIndex(pinnedPieceInd).getWhitePawnAdvances(this.allEmpty));
						while (pinnedPieceMoves.hasNext()) {
							to = pinnedPieceMoves.next();
							moves.add(pinnedPieceMove | (to << Move.TO.shift));
						}
					}
				}
			}
			if ((pinnedPieceBit = BitOperations.getMSBit(diagonalNeg & this.allOccupied) & this.allWhitePieces) != 0) {
				if ((pinnerBit = BitOperations.getMSBit((diagonalNeg & this.allOccupied)^pinnedPieceBit)  & diagonalSliders) != 0) {
					pinnedPieces	|= pinnedPieceBit;
					pinnedPieceInd	 = BitOperations.indexOfBit(pinnedPieceBit);
					pinnedPiece		 = this.offsetBoard[pinnedPieceInd];
					if (pinnedPiece == 2 || pinnedPiece == 4) {
						pinnedPieceMove  =  move;
						pinnedPieceMove |= (pinnedPieceInd << Move.FROM.shift);
						pinnedPieceMove |= (pinnedPiece << Move.MOVED_PIECE.shift);
						pinnedPieceMoves = BitOperations.serialize(((this.whiteKing - pinnerBit) & diagonalNeg)^pinnedPieceBit);
						while (pinnedPieceMoves.hasNext()) {
							to = pinnedPieceMoves.next();
							moves.add(pinnedPieceMove | (to << Move.TO.shift) | (this.offsetBoard[to] << Move.CAPTURED_PIECE.shift));
						}
					}
				}
			}
			if ((pinnedPieceBit = BitOperations.getMSBit(antiDiagonalNeg & this.allOccupied) & this.allWhitePieces) != 0) {
				if ((pinnerBit = BitOperations.getMSBit((antiDiagonalNeg & this.allOccupied)^pinnedPieceBit)  & diagonalSliders) != 0) {
					pinnedPieces	|= pinnedPieceBit;
					pinnedPieceInd	 = BitOperations.indexOfBit(pinnedPieceBit);
					pinnedPiece		 = this.offsetBoard[pinnedPieceInd];
					if (pinnedPiece == 2 || pinnedPiece == 4) {
						pinnedPieceMove  =  move;
						pinnedPieceMove |= (pinnedPieceInd << Move.FROM.shift);
						pinnedPieceMove |= (pinnedPiece << Move.MOVED_PIECE.shift);
						pinnedPieceMoves = BitOperations.serialize(((this.whiteKing - pinnerBit) & antiDiagonalNeg)^pinnedPieceBit);
						while (pinnedPieceMoves.hasNext()) {
							to = pinnedPieceMoves.next();
							moves.add(pinnedPieceMove | (to << Move.TO.shift) | (this.offsetBoard[to] << Move.CAPTURED_PIECE.shift));
						}
					}
				}
			}
			movablePieces = ~pinnedPieces;
			pieceSet = this.whiteQueens & movablePieces;
			if (pieceSet != 0) {
				queens = BitOperations.serialize(pieceSet);
				while (queens.hasNext()) {
					queen = queens.next();
					queenMove  =  move;
					queenMove |= (queen << Move.FROM.shift);
					queenMove |= (2L << Move.MOVED_PIECE.shift);
					moveSet	   = MoveDatabase.getByIndex(queen).getWhiteQueenMoves(this.allNonWhiteOccupied, this.allOccupied);
					if (moveSet != 0) {
						queenMoves = BitOperations.serialize(moveSet);
						while (queenMoves.hasNext()) {
							to = queenMoves.next();
							moves.add(queenMove | (to << Move.TO.shift) | (this.offsetBoard[to] << Move.CAPTURED_PIECE.shift));
						}
					}
				}
			}
			pieceSet = this.whiteRooks & movablePieces;
			if (pieceSet != 0) {
				rooks	  = BitOperations.serialize(pieceSet);
				while (rooks.hasNext()) {
					rook = rooks.next();
					rookMove  =  move;
					rookMove |= (rook << Move.FROM.shift);
					rookMove |= (3L << Move.MOVED_PIECE.shift);
					moveSet	  = MoveDatabase.getByIndex(rook).getWhiteRookMoves(this.allNonWhiteOccupied, this.allOccupied);
					if (moveSet != 0) {
						rookMoves = BitOperations.serialize(moveSet);
						while (rookMoves.hasNext()) {
							to = rookMoves.next();
							moves.add(rookMove | (to << Move.TO.shift) | (this.offsetBoard[to] << Move.CAPTURED_PIECE.shift));
						}
					}
				}
			}
			pieceSet = this.whiteBishops & movablePieces;
			if (pieceSet != 0) {
				bishops = BitOperations.serialize(pieceSet);
				while (bishops.hasNext()) {
					bishop = bishops.next();
					bishopMove  =  move;
					bishopMove |= (bishop << Move.FROM.shift);
					bishopMove |= (4L << Move.MOVED_PIECE.shift);
					moveSet		= MoveDatabase.getByIndex(bishop).getWhiteBishopMoves(this.allNonWhiteOccupied, this.allOccupied);
					if (moveSet != 0) {
						bishopMoves = BitOperations.serialize(moveSet);
						while (bishopMoves.hasNext()) {
							to = bishopMoves.next();
							moves.add(bishopMove | (to << Move.TO.shift) | (this.offsetBoard[to] << Move.CAPTURED_PIECE.shift));
						}
					}
				}
			}
			pieceSet = this.whiteKnights & movablePieces;
			if (pieceSet != 0) {
				knights = BitOperations.serialize(pieceSet);
				while (knights.hasNext()) {
					knight = knights.next();
					knightMove  =  move;
					knightMove |= (knight << Move.FROM.shift);
					knightMove |= (5L << Move.MOVED_PIECE.shift);
					moveSet		= MoveDatabase.getByIndex(knight).getWhiteKnightMoves(this.allNonWhiteOccupied);
					if (moveSet != 0) {
						knightMoves = BitOperations.serialize(moveSet);
						while (knightMoves.hasNext()) {
							to = knightMoves.next();
							moves.add(knightMove | (to << Move.TO.shift) | (this.offsetBoard[to] << Move.CAPTURED_PIECE.shift));
						}
					}
				}
			}
			pieceSet = this.whitePawns & movablePieces;
			if (pieceSet != 0) {
				pawns = BitOperations.serialize(pieceSet);
				while (pawns.hasNext()) {
					pawn = pawns.next();
					pawnMove  =  move;
					pawnMove |= (pawn << Move.FROM.shift);
					pawnMove |= (6L << Move.MOVED_PIECE.shift);
					moveSet = MoveDatabase.getByIndex(pawn).getWhitePawnMoves(this.allBlackPieces, this.allEmpty);
					if (moveSet != 0) {
						pawnMoves = BitOperations.serialize(moveSet);
						while (pawnMoves.hasNext()) {
							to = pawnMoves.next();
							if (to > 55) {
								moves.add(pawnMove | (to << Move.TO.shift) | (this.offsetBoard[to] << Move.CAPTURED_PIECE.shift) | (4L << Move.TYPE.shift));
								moves.add(pawnMove | (to << Move.TO.shift) | (this.offsetBoard[to] << Move.CAPTURED_PIECE.shift) | (5L << Move.TYPE.shift));
								moves.add(pawnMove | (to << Move.TO.shift) | (this.offsetBoard[to] << Move.CAPTURED_PIECE.shift) | (6L << Move.TYPE.shift));
								moves.add(pawnMove | (to << Move.TO.shift) | (this.offsetBoard[to] << Move.CAPTURED_PIECE.shift) | (7L << Move.TYPE.shift));
							}
							else
								moves.add(pawnMove | (to << Move.TO.shift) | (this.offsetBoard[to] << Move.CAPTURED_PIECE.shift));
						}
					}
					if (this.enPassantRights != 8) {
						to = 40 + this.enPassantRights;
						if ((MoveDatabase.getByIndex(to).getCrudeBlackPawnCaptures() & (1L << pawn)) != 0) {
							pawnMove |= (to << Move.TO.shift) | (12L << Move.CAPTURED_PIECE.shift) | (3L << Move.TYPE.shift);
							this.makeMoveOnBoard(pawnMove);
							if (!this.isAttacked(king, false))
								moves.add(pawnMove);
							this.unMakeMoveOnBoard(pawnMove);
						}
					}
				}
			}
		}
		else {
			king = BitOperations.indexOfBit(this.blackKing);
			kingMove  =  move;
			kingMove |= (king >> Move.FROM.shift);
			kingMove |= (7L << Move.MOVED_PIECE.shift);
			moveSet	  = MoveDatabase.getByIndex(king).getBlackKingMoves(this.allNonBlackOccupied);
			if (moveSet != 0) {
				kingMoves = BitOperations.serialize(moveSet);
				while (kingMoves.hasNext()) {
					to = kingMoves.next();
					if (!isAttacked(to, true))
						moves.add(kingMove | (to << Move.TO.shift) | (this.offsetBoard[to] << Move.CAPTURED_PIECE.shift));
				}
				if ((this.blackCastlingRights & 1) != 0) {
					if (((Square.getBitmapByIndex(61) | Square.getBitmapByIndex(62)) & this.allOccupied) == 0) {
						if (((moves.getHead() >>> Move.TO.shift) & Move.TO.mask) == 61 && !isAttacked(62, true))
							moves.add(kingMove | (62L << Move.TO.shift) | (1L << Move.TYPE.shift));
					}
				}
				if ((this.blackCastlingRights & 2) != 0) {
					if (((Square.getBitmapByIndex(57) | Square.getBitmapByIndex(58) | Square.getBitmapByIndex(59)) & this.allOccupied) == 0) {
						if (!isAttacked(58, true) && !isAttacked(59, true))
							moves.add(kingMove | (58L << Move.TO.shift) | (2L << Move.TYPE.shift));
					}
				}
			}
			straightSliders = this.whiteQueens | this.whiteRooks;
			diagonalSliders = this.whiteQueens | this.whiteBishops;
			attRayMask 		= SliderAttackRayMask.getByIndex(king);
			rankPos 		= attRayMask.getRankPos();
			rankNeg 		= attRayMask.getRankNeg();
			filePos 		= attRayMask.getFilePos();
			fileNeg 		= attRayMask.getFileNeg();
			diagonalPos 	= attRayMask.getDiagonalPos();
			diagonalNeg 	= attRayMask.getDiagonalNeg();
			antiDiagonalPos = attRayMask.getAntiDiagonalPos();
			antiDiagonalNeg = attRayMask.getAntiDiagonalNeg();
			if ((pinnedPieceBit = BitOperations.getLSBit(rankPos & this.allOccupied) & this.allBlackPieces) != 0) {
				if ((pinnerBit = BitOperations.getLSBit((rankPos & this.allOccupied)^pinnedPieceBit) & straightSliders) != 0) {
					pinnedPieces	|= pinnedPieceBit;
					pinnedPieceInd	 = BitOperations.indexOfBit(pinnedPieceBit);
					pinnedPiece		 = this.offsetBoard[pinnedPieceInd];
					if (pinnedPiece == 8 || pinnedPiece == 9) {
						pinnedPieceMove  =  move;
						pinnedPieceMove |= (pinnedPieceInd << Move.FROM.shift);
						pinnedPieceMove |= (pinnedPiece << Move.MOVED_PIECE.shift);
						pinnedPieceMoves = BitOperations.serialize(((pinnerBit - this.blackKing) << 1)^pinnedPieceBit);
						while (pinnedPieceMoves.hasNext()) {
							to = pinnedPieceMoves.next();
							moves.add(pinnedPieceMove | (to << Move.TO.shift) | (this.offsetBoard[to] << Move.CAPTURED_PIECE.shift));
						}
					}
				}
			}
			if ((pinnedPieceBit = BitOperations.getLSBit(filePos & this.allOccupied) & this.allBlackPieces) != 0) {
				if ((pinnerBit = BitOperations.getLSBit((filePos & this.allOccupied)^pinnedPieceBit)  & straightSliders) != 0) {
					pinnedPieces	|= pinnedPieceBit;
					pinnedPieceInd	 = BitOperations.indexOfBit(pinnedPieceBit);
					pinnedPiece		 = this.offsetBoard[pinnedPieceInd];
					if (pinnedPiece == 8 || pinnedPiece == 9) {
						pinnedPieceMove  =  move;
						pinnedPieceMove |= (pinnedPieceInd << Move.FROM.shift);
						pinnedPieceMove |= (pinnedPiece << Move.MOVED_PIECE.shift);
						pinnedPieceMoves = BitOperations.serialize((((pinnerBit - this.blackKing) << 1) & filePos)^pinnedPieceBit);
						while (pinnedPieceMoves.hasNext()) {
							to = pinnedPieceMoves.next();
							moves.add(pinnedPieceMove | (to << Move.TO.shift) | (this.offsetBoard[to] << Move.CAPTURED_PIECE.shift));
						}
					}
					else if (pinnedPiece == 12) {
						pinnedPieceMove  =  move;
						pinnedPieceMove |= (pinnedPieceInd << Move.FROM.shift);
						pinnedPieceMove |= (pinnedPiece << Move.MOVED_PIECE.shift);
						pinnedPieceMoves = BitOperations.serialize(MoveDatabase.getByIndex(pinnedPieceInd).getBlackPawnAdvances(this.allEmpty));
						while (pinnedPieceMoves.hasNext()) {
							to = pinnedPieceMoves.next();
							moves.add(pinnedPieceMove | (to << Move.TO.shift));
						}
					}
				}
			}
			if ((pinnedPieceBit = BitOperations.getLSBit(diagonalPos & this.allOccupied) & this.allBlackPieces) != 0) {
				if ((pinnerBit = BitOperations.getLSBit((diagonalPos & this.allOccupied)^pinnedPieceBit) & diagonalSliders) != 0) {
					pinnedPieces	|= pinnedPieceBit;
					pinnedPieceInd	 = BitOperations.indexOfBit(pinnedPieceBit);
					pinnedPiece		 = this.offsetBoard[pinnedPieceInd];
					if (pinnedPiece == 8 || pinnedPiece == 10) {
						pinnedPieceMove  =  move;
						pinnedPieceMove |= (pinnedPieceInd << Move.FROM.shift);
						pinnedPieceMove |= (pinnedPiece << Move.MOVED_PIECE.shift);
						pinnedPieceMoves = BitOperations.serialize((((pinnerBit - this.blackKing) << 1) & diagonalPos)^pinnedPieceBit);
						while (pinnedPieceMoves.hasNext()) {
							to = pinnedPieceMoves.next();
							moves.add(pinnedPieceMove | (to << Move.TO.shift) | (this.offsetBoard[to] << Move.CAPTURED_PIECE.shift));
						}
					}
				}
			}
			if ((pinnedPieceBit = BitOperations.getLSBit(antiDiagonalPos & this.allOccupied) & this.allBlackPieces) != 0) {
				if ((pinnerBit = BitOperations.getLSBit((antiDiagonalPos & this.allOccupied)^pinnedPieceBit) & diagonalSliders) != 0) {
					pinnedPieces	|= pinnedPieceBit;
					pinnedPieceInd	 = BitOperations.indexOfBit(pinnedPieceBit);
					pinnedPiece		 = this.offsetBoard[pinnedPieceInd];
					if (pinnedPiece == 8 || pinnedPiece == 10) {
						pinnedPieceMove  =  move;
						pinnedPieceMove |= (pinnedPieceInd << Move.FROM.shift);
						pinnedPieceMove |= (pinnedPiece << Move.MOVED_PIECE.shift);
						pinnedPieceMoves = BitOperations.serialize((((pinnerBit - this.blackKing) << 1) & antiDiagonalPos)^pinnedPieceBit);
						while (pinnedPieceMoves.hasNext()) {
							to = pinnedPieceMoves.next();
							moves.add(pinnedPieceMove | (to << Move.TO.shift) | (this.offsetBoard[to] << Move.CAPTURED_PIECE.shift));
						}
					}
				}
			}
			if ((pinnedPieceBit = BitOperations.getMSBit(rankNeg & this.allOccupied) & this.allBlackPieces) != 0) {
				if ((pinnerBit = BitOperations.getMSBit((rankNeg & this.allOccupied)^pinnedPieceBit) & straightSliders) != 0) {
					pinnedPieces	|= pinnedPieceBit;
					pinnedPieceInd	 = BitOperations.indexOfBit(pinnedPieceBit);
					pinnedPiece		 = this.offsetBoard[pinnedPieceInd];
					if (pinnedPiece == 8 || pinnedPiece == 9) {
						pinnedPieceMove  =  move;
						pinnedPieceMove |= (pinnedPieceInd << Move.FROM.shift);
						pinnedPieceMove |= (pinnedPiece << Move.MOVED_PIECE.shift);
						pinnedPieceMoves = BitOperations.serialize((this.blackKing - pinnerBit)^pinnedPieceBit);
						while (pinnedPieceMoves.hasNext()) {
							to = pinnedPieceMoves.next();
							moves.add(pinnedPieceMove | (to << Move.TO.shift) | (this.offsetBoard[to] << Move.CAPTURED_PIECE.shift));
						}
					}
				}
			}
			if ((pinnedPieceBit = BitOperations.getMSBit(fileNeg & this.allOccupied) & this.allBlackPieces) != 0) {
				if ((pinnerBit = BitOperations.getMSBit((fileNeg & this.allOccupied)^pinnedPieceBit)  & straightSliders) != 0) {
					pinnedPieces	|= pinnedPieceBit;
					pinnedPieceInd	 = BitOperations.indexOfBit(pinnedPieceBit);
					pinnedPiece		 = this.offsetBoard[pinnedPieceInd];
					if (pinnedPiece == 8 || pinnedPiece == 9) {
						pinnedPieceMove  =  move;
						pinnedPieceMove |= (pinnedPieceInd << Move.FROM.shift);
						pinnedPieceMove |= (pinnedPiece << Move.MOVED_PIECE.shift);
						pinnedPieceMoves = BitOperations.serialize(((this.blackKing - pinnerBit) & fileNeg)^pinnedPieceBit);
						while (pinnedPieceMoves.hasNext()) {
							to = pinnedPieceMoves.next();
							moves.add(pinnedPieceMove | (to << Move.TO.shift) | (this.offsetBoard[to] << Move.CAPTURED_PIECE.shift));
						}
					}
					else if (pinnedPiece == 12) {
						pinnedPieceMove  =  move;
						pinnedPieceMove |= (pinnedPieceInd << Move.FROM.shift);
						pinnedPieceMove |= (pinnedPiece << Move.MOVED_PIECE.shift);
						pinnedPieceMoves = BitOperations.serialize(MoveDatabase.getByIndex(pinnedPieceInd).getBlackPawnAdvances(this.allEmpty));
						while (pinnedPieceMoves.hasNext()) {
							to = pinnedPieceMoves.next();
							moves.add(pinnedPieceMove | (to << Move.TO.shift));
						}
					}
				}
			}
			if ((pinnedPieceBit = BitOperations.getMSBit(diagonalNeg & this.allOccupied) & this.allBlackPieces) != 0) {
				if ((pinnerBit = BitOperations.getMSBit((diagonalNeg & this.allOccupied)^pinnedPieceBit)  & diagonalSliders) != 0) {
					pinnedPieces	|= pinnedPieceBit;
					pinnedPieceInd	 = BitOperations.indexOfBit(pinnedPieceBit);
					pinnedPiece		 = this.offsetBoard[pinnedPieceInd];
					if (pinnedPiece == 8 || pinnedPiece == 10) {
						pinnedPieceMove  =  move;
						pinnedPieceMove |= (pinnedPieceInd << Move.FROM.shift);
						pinnedPieceMove |= (pinnedPiece << Move.MOVED_PIECE.shift);
						pinnedPieceMoves = BitOperations.serialize(((this.blackKing - pinnerBit) & diagonalNeg)^pinnedPieceBit);
						while (pinnedPieceMoves.hasNext()) {
							to = pinnedPieceMoves.next();
							moves.add(pinnedPieceMove | (to << Move.TO.shift) | (this.offsetBoard[to] << Move.CAPTURED_PIECE.shift));
						}
					}
					else if (pinnedPiece == 12) {
						pinnedPieceMove  =  move;
						pinnedPieceMove |= (pinnedPieceInd << Move.FROM.shift);
						pinnedPieceMove |= (pinnedPiece << Move.MOVED_PIECE.shift);
						if (this.enPassantRights != 0)
							pinnedPieceMoves = BitOperations.serialize(MoveDatabase.getByIndex(pinnedPieceInd).getBlackPawnCaptures(this.allWhitePieces | (1L << (enPassantDestination = 16 + this.enPassantRights))) & diagonalNeg);
						else
							pinnedPieceMoves = BitOperations.serialize(MoveDatabase.getByIndex(pinnedPieceInd).getBlackPawnCaptures(this.allWhitePieces) & diagonalNeg);
						while (pinnedPieceMoves.hasNext()) {
							to = pinnedPieceMoves.next();
							if (to == enPassantDestination)
								moves.add(pinnedPieceMove | (to << Move.TO.shift) | (6L << Move.CAPTURED_PIECE.shift) | (3L << Move.TYPE.shift));
							else if (to < 8) {
								moves.add(pinnedPieceMove | (to << Move.TO.shift) | (this.offsetBoard[to] << Move.CAPTURED_PIECE.shift) | (4L << Move.TYPE.shift));
								moves.add(pinnedPieceMove | (to << Move.TO.shift) | (this.offsetBoard[to] << Move.CAPTURED_PIECE.shift) | (5L << Move.TYPE.shift));
								moves.add(pinnedPieceMove | (to << Move.TO.shift) | (this.offsetBoard[to] << Move.CAPTURED_PIECE.shift) | (6L << Move.TYPE.shift));
								moves.add(pinnedPieceMove | (to << Move.TO.shift) | (this.offsetBoard[to] << Move.CAPTURED_PIECE.shift) | (7L << Move.TYPE.shift));
							}
							else
								moves.add(pinnedPieceMove | (to << Move.TO.shift) | (this.offsetBoard[to] << Move.CAPTURED_PIECE.shift));
						}
					}
				}
			}
			if ((pinnedPieceBit = BitOperations.getMSBit(antiDiagonalNeg & this.allOccupied) & this.allBlackPieces) != 0) {
				if ((pinnerBit = BitOperations.getMSBit((antiDiagonalNeg & this.allOccupied)^pinnedPieceBit) & diagonalSliders) != 0) {
					pinnedPieces	|= pinnedPieceBit;
					pinnedPieceInd	 = BitOperations.indexOfBit(pinnedPieceBit);
					pinnedPiece		 = this.offsetBoard[pinnedPieceInd];
					if (pinnedPiece == 8 || pinnedPiece == 10) {
						pinnedPieceMove  =  move;
						pinnedPieceMove |= (pinnedPieceInd << Move.FROM.shift);
						pinnedPieceMove |= (pinnedPiece << Move.MOVED_PIECE.shift);
						pinnedPieceMoves = BitOperations.serialize(((this.blackKing - pinnerBit) & antiDiagonalNeg)^pinnedPieceBit);
						while (pinnedPieceMoves.hasNext()) {
							to = pinnedPieceMoves.next();
							moves.add(pinnedPieceMove | (to << Move.TO.shift) | (this.offsetBoard[to] << Move.CAPTURED_PIECE.shift));
						}
					}
					else if (pinnedPiece == 12) {
						pinnedPieceMove  =  move;
						pinnedPieceMove |= (pinnedPieceInd << Move.FROM.shift);
						pinnedPieceMove |= (pinnedPiece << Move.MOVED_PIECE.shift);
						if (this.enPassantRights != 0)
							pinnedPieceMoves = BitOperations.serialize(MoveDatabase.getByIndex(pinnedPieceInd).getBlackPawnCaptures(this.allWhitePieces | (1L << (enPassantDestination = 16 + this.enPassantRights))) & antiDiagonalNeg);
						else
							pinnedPieceMoves = BitOperations.serialize(MoveDatabase.getByIndex(pinnedPieceInd).getBlackPawnCaptures(this.allWhitePieces) & antiDiagonalNeg);
						while (pinnedPieceMoves.hasNext()) {
							to = pinnedPieceMoves.next();
							if (to == enPassantDestination)
								moves.add(pinnedPieceMove | (to << Move.TO.shift) | (6L << Move.CAPTURED_PIECE.shift) | (3L << Move.TYPE.shift));
							else if (to < 8) {
								moves.add(pinnedPieceMove | (to << Move.TO.shift) | (this.offsetBoard[to] << Move.CAPTURED_PIECE.shift) | (4L << Move.TYPE.shift));
								moves.add(pinnedPieceMove | (to << Move.TO.shift) | (this.offsetBoard[to] << Move.CAPTURED_PIECE.shift) | (5L << Move.TYPE.shift));
								moves.add(pinnedPieceMove | (to << Move.TO.shift) | (this.offsetBoard[to] << Move.CAPTURED_PIECE.shift) | (6L << Move.TYPE.shift));
								moves.add(pinnedPieceMove | (to << Move.TO.shift) | (this.offsetBoard[to] << Move.CAPTURED_PIECE.shift) | (7L << Move.TYPE.shift));
							}
							else
								moves.add(pinnedPieceMove | (to << Move.TO.shift) | (this.offsetBoard[to] << Move.CAPTURED_PIECE.shift));
						}
					}
				}
			}
			movablePieces = ~pinnedPieces;
			pieceSet = this.blackQueens & movablePieces;
			if (pieceSet != 0) {
				queens = BitOperations.serialize(pieceSet);
				while (queens.hasNext()) {
					queen = queens.next();
					queenMove  =  move;
					queenMove |= (queen << Move.FROM.shift);
					queenMove |= (8L << Move.MOVED_PIECE.shift);
					moveSet	   = MoveDatabase.getByIndex(queen).getBlackQueenMoves(this.allNonBlackOccupied, this.allOccupied);
					if (moveSet != 0) {
						queenMoves = BitOperations.serialize(moveSet);
						while (queenMoves.hasNext()) {
							to = queenMoves.next();
							moves.add(queenMove | (to << Move.TO.shift) | (this.offsetBoard[to] << Move.CAPTURED_PIECE.shift));
						}
					}
				}
			}
			pieceSet = this.blackRooks & movablePieces;
			if (pieceSet != 0) {
				rooks = BitOperations.serialize(pieceSet);
				while (rooks.hasNext()) {
					rook = rooks.next();
					rookMove  =  move;
					rookMove |= (rook << Move.FROM.shift);
					rookMove |= (9L << Move.MOVED_PIECE.shift);
					moveSet	  = MoveDatabase.getByIndex(rook).getBlackRookMoves(this.allNonBlackOccupied, this.allOccupied);
					if (moveSet != 0) {
						rookMoves = BitOperations.serialize(moveSet);
						while (rookMoves.hasNext()) {
							to = rookMoves.next();
							moves.add(rookMove | (to << Move.TO.shift) | (this.offsetBoard[to] << Move.CAPTURED_PIECE.shift));
						}
					}
				}
			}
			pieceSet = this.blackBishops & movablePieces;
			if (pieceSet != 0) {
				bishops = BitOperations.serialize(pieceSet);
				while (bishops.hasNext()) {
					bishop = bishops.next();
					bishopMove  =  move;
					bishopMove |= (bishop << Move.FROM.shift);
					bishopMove |= (10L << Move.MOVED_PIECE.shift);
					moveSet		= MoveDatabase.getByIndex(bishop).getBlackBishopMoves(this.allNonBlackOccupied, this.allOccupied);
					if (moveSet != 0) {
						bishopMoves = BitOperations.serialize(moveSet);
						while (bishopMoves.hasNext()) {
							to = bishopMoves.next();
							moves.add(bishopMove | (to << Move.TO.shift) | (this.offsetBoard[to] << Move.CAPTURED_PIECE.shift));
						}
					}
				}
			}
			pieceSet = this.blackKnights & movablePieces;
			if (pieceSet != 0) {
				knights = BitOperations.serialize(pieceSet);
				while (knights.hasNext()) {
					knight = knights.next();
					knightMove  =  move;
					knightMove |= (knight << Move.FROM.shift);
					knightMove |= (11L << Move.MOVED_PIECE.shift);
					moveSet		= MoveDatabase.getByIndex(knight).getBlackKnightMoves(this.allNonBlackOccupied);
					if (moveSet != 0) {
						knightMoves = BitOperations.serialize(moveSet);
						while (knightMoves.hasNext()) {
							to = knightMoves.next();
							moves.add(knightMove | (to << Move.TO.shift) | (this.offsetBoard[to] << Move.CAPTURED_PIECE.shift));
						}
					}
				}
			}
			pieceSet = this.blackPawns & movablePieces;
			if (pieceSet != 0) {
				pawns = BitOperations.serialize(pieceSet);
				while (pawns.hasNext()) {
					pawn = pawns.next();
					pawnMove  =  move;
					pawnMove |= (pawn << Move.FROM.shift);
					pawnMove |= (12L << Move.MOVED_PIECE.shift);
					moveSet = MoveDatabase.getByIndex(pawn).getBlackPawnMoves(this.allWhitePieces, this.allEmpty);
					if (moveSet != 0) {
						pawnMoves = BitOperations.serialize(moveSet);
						while (pawnMoves.hasNext()) {
							to = pawnMoves.next();
							if (to < 8) {
								moves.add(pawnMove | (to << Move.TO.shift) | (this.offsetBoard[to] << Move.CAPTURED_PIECE.shift) | (4L << Move.TYPE.shift));
								moves.add(pawnMove | (to << Move.TO.shift) | (this.offsetBoard[to] << Move.CAPTURED_PIECE.shift) | (5L << Move.TYPE.shift));
								moves.add(pawnMove | (to << Move.TO.shift) | (this.offsetBoard[to] << Move.CAPTURED_PIECE.shift) | (6L << Move.TYPE.shift));
								moves.add(pawnMove | (to << Move.TO.shift) | (this.offsetBoard[to] << Move.CAPTURED_PIECE.shift) | (7L << Move.TYPE.shift));
							}
							else
								moves.add(pawnMove | (to << Move.TO.shift) | (this.offsetBoard[to] << Move.CAPTURED_PIECE.shift));
						}
					}
					if (this.enPassantRights != 8) {
						to = 16 + this.enPassantRights;
						if ((MoveDatabase.getByIndex(to).getCrudeWhitePawnCaptures() & (1L << pawn)) != 0) {
							pawnMove |= (to << Move.TO.shift) | (6L << Move.CAPTURED_PIECE.shift) | (3L << Move.TYPE.shift);
							this.makeMoveOnBoard(pawnMove);
							if (!this.isAttacked(king, true))
								moves.add(pawnMove);
							this.unMakeMoveOnBoard(pawnMove);
						}
					}
				}
			}
		}
		return moves;
	}
	private LongQueue generateCheckEvasionMoves() {
		long move = 0, kingMove = 0, kingMoveSet, pinnedPieces, movablePieces, squaresOfInterventionSet, checkerAttackerSet, checkerBlockerSet, checkerAttackerMove, checkerBlockerMove;
		int checker1, checker2, checkerPiece1, checkerPiece2, squareOfIntervention, checkerAttackerSquare, checkerBlockerSquare, king, to;
		IntStack kingMoves, squaresOfIntervention, checkerAttackers, checkerBlockers;
		LongQueue moves = new LongQueue();
		MoveDatabase dB, kingDb;
		boolean promotionOnAttackPossible = false, promotionOnBlockPossible = false;
		move |= (this.whiteCastlingRights		<< Move.PREVIOUS_WHITE_CASTLING_RIGHTS.shift);
		move |= (this.blackCastlingRights		<< Move.PREVIOUS_BLACK_CASTLING_RIGHTS.shift);
		move |= (this.enPassantRights	 		<< Move.PREVIOUS_ENPASSANT_RIGHTS.shift);
		move |= (1L 							<< Move.PREVIOUS_CHECK.shift);
		move |= (this.fiftyMoveRuleClock		<< Move.PREVIOUS_FIFTY_MOVE_RULE_CLOCK.shift);
		move |= (this.repetitions				<< Move.PREVIOUS_REPETITIONS.shift);
		if (this.whitesTurn) {
			if (BitOperations.resetLSBit(this.checkers) == 0) {
				pinnedPieces 	 =  getPinnedPieces(true);
				movablePieces	 = ~pinnedPieces;
				checker1  		 = BitOperations.indexOfBit(this.checkers);
				checkerPiece1 	 = this.offsetBoard[checker1];
				king 	  		 = BitOperations.indexOfBit(this.whiteKing);
				kingMove 		 =  move;
				kingMove		|= (king << Move.FROM.shift);
				kingMove		|= (1L << Move.MOVED_PIECE.shift);
				kingDb			 = MoveDatabase.getByIndex(king);
				kingMoveSet		 = kingDb.getWhiteKingMoves(this.allNonWhiteOccupied);
				dB = MoveDatabase.getByIndex(checker1);
				if ((this.checkers & Rank.getByIndex(7)) != 0)
					promotionOnAttackPossible = true;
				checkerAttackerSet = getAttackers(checker1, true) & movablePieces & ~this.whiteKing;
				checkerAttackers = BitOperations.serialize(checkerAttackerSet);
				while (checkerAttackers.hasNext()) {
					checkerAttackerSquare = checkerAttackers.next();
					checkerAttackerMove = move | (checkerAttackerSquare << Move.FROM.shift) | (this.offsetBoard[checkerAttackerSquare] << Move.MOVED_PIECE.shift) | (checkerPiece1 << Move.CAPTURED_PIECE.shift);
					if ((checkerAttackerMove >>> Move.MOVED_PIECE.shift & Move.MOVED_PIECE.mask) == 6) {
						if (promotionOnAttackPossible) {
							checkerAttackerMove |= (checker1 << Move.TO.shift);
							moves.add(checkerAttackerMove | (4L << Move.TYPE.shift));
							moves.add(checkerAttackerMove | (5L << Move.TYPE.shift));
							moves.add(checkerAttackerMove | (6L << Move.TYPE.shift));
							moves.add(checkerAttackerMove | (7L << Move.TYPE.shift));
						}
						else if (this.enPassantRights == checker1%8 && (Rank.getByIndex(4) & this.checkers) != 0 && (MoveDatabase.getByIndex(to = checker1 + 8).getCrudeBlackPawnCaptures() & (1L << checkerAttackerSquare)) != 0)
							moves.add(checkerAttackerMove | (to << Move.TO.shift) | (3L << Move.TYPE.shift));
						else
							moves.add(checkerAttackerMove | (checker1 << Move.TO.shift));
					}
					else
						moves.add(checkerAttackerMove | (checker1 << Move.TO.shift));
				}
				switch (checkerPiece1) {
					case 8: {
						if ((File.getBySquareIndex(king) & this.checkers) != 0 || (Rank.getBySquareIndex(king) & this.checkers) != 0) {
							squaresOfInterventionSet = (dB.getBlackRookMoves(this.allNonBlackOccupied, this.allOccupied) & kingDb.getWhiteRookMoves(this.allNonWhiteOccupied, this.allOccupied));
							if (promotionOnAttackPossible && (this.whiteKing & Rank.getByIndex(7)) != 0)
								promotionOnBlockPossible = true;
						}
						else
							squaresOfInterventionSet = (dB.getBlackBishopMoves(this.allNonBlackOccupied, this.allOccupied) & kingDb.getWhiteBishopMoves(this.allNonWhiteOccupied, this.allOccupied));
						squaresOfIntervention = BitOperations.serialize(squaresOfInterventionSet);
						while (squaresOfIntervention.hasNext()) {
							squareOfIntervention = squaresOfIntervention.next();
							checkerBlockerSet = getBlockerCandidates(squareOfIntervention, true) & movablePieces;
							if (checkerBlockerSet != 0) {
								checkerBlockers = BitOperations.serialize(checkerBlockerSet);
								while (checkerBlockers.hasNext()) {
									checkerBlockerSquare = checkerBlockers.next();
									checkerBlockerMove = move | (checkerBlockerSquare << Move.FROM.shift) | (squareOfIntervention << Move.TO.shift) | (this.offsetBoard[checkerBlockerSquare] << Move.MOVED_PIECE.shift) | (this.offsetBoard[squareOfIntervention] << Move.CAPTURED_PIECE.shift);
									if (promotionOnBlockPossible && (checkerBlockerMove >>> Move.MOVED_PIECE.shift & Move.MOVED_PIECE.mask) == 6) {
										moves.add(checkerBlockerMove | (4L << Move.TYPE.shift));
										moves.add(checkerBlockerMove | (5L << Move.TYPE.shift));
										moves.add(checkerBlockerMove | (6L << Move.TYPE.shift));
										moves.add(checkerBlockerMove | (7L << Move.TYPE.shift));
									}
									else
										moves.add(checkerBlockerMove);
								}
							}
						}
						kingMoveSet &= ~dB.getWhiteQueenMoves(this.allNonWhiteOccupied, (this.allOccupied^this.whiteKing));
					}
					break;
					case 9: {
						if (promotionOnAttackPossible && (this.whiteKing & Rank.getByIndex(7)) != 0)
							promotionOnBlockPossible = true;
						squaresOfInterventionSet = (dB.getBlackRookMoves(this.allNonBlackOccupied, this.allOccupied) & kingDb.getWhiteRookMoves(this.allNonWhiteOccupied, this.allOccupied));
						squaresOfIntervention = BitOperations.serialize(squaresOfInterventionSet);
						while (squaresOfIntervention.hasNext()) {
							squareOfIntervention = squaresOfIntervention.next();
							checkerBlockerSet = getBlockerCandidates(squareOfIntervention, true) & movablePieces;
							if (checkerBlockerSet != 0) {
								checkerBlockers = BitOperations.serialize(checkerBlockerSet);
								while (checkerBlockers.hasNext()) {
									checkerBlockerSquare = checkerBlockers.next();
									checkerBlockerMove = move | (checkerBlockerSquare << Move.FROM.shift) | (squareOfIntervention << Move.TO.shift) | (this.offsetBoard[checkerBlockerSquare] << Move.MOVED_PIECE.shift) | (this.offsetBoard[squareOfIntervention] << Move.CAPTURED_PIECE.shift);
									if (promotionOnBlockPossible && (checkerBlockerMove >>> Move.MOVED_PIECE.shift & Move.MOVED_PIECE.mask) == 6) {
										moves.add(checkerBlockerMove | (4L << Move.TYPE.shift));
										moves.add(checkerBlockerMove | (5L << Move.TYPE.shift));
										moves.add(checkerBlockerMove | (6L << Move.TYPE.shift));
										moves.add(checkerBlockerMove | (7L << Move.TYPE.shift));
									}
									else
										moves.add(checkerBlockerMove);
								}
							}
						}
						kingMoveSet &= ~dB.getCrudeRookMoves();
					}
					break;
					case 10: {
						squaresOfInterventionSet = (dB.getBlackBishopMoves(this.allNonBlackOccupied, this.allOccupied) & kingDb.getWhiteBishopMoves(this.allNonWhiteOccupied, this.allOccupied));
						squaresOfIntervention = BitOperations.serialize(squaresOfInterventionSet);
						while (squaresOfIntervention.hasNext()) {
							squareOfIntervention = squaresOfIntervention.next();
							checkerBlockerSet = getBlockerCandidates(squareOfIntervention, true) & movablePieces;
							if (checkerBlockerSet != 0) {
								checkerBlockers = BitOperations.serialize(checkerBlockerSet);
								while (checkerBlockers.hasNext()) {
									checkerBlockerSquare = checkerBlockers.next();
									checkerBlockerMove = move | (checkerBlockerSquare << Move.FROM.shift) | (squareOfIntervention << Move.TO.shift) | (this.offsetBoard[checkerBlockerSquare] << Move.MOVED_PIECE.shift) | (this.offsetBoard[squareOfIntervention] << Move.CAPTURED_PIECE.shift);
									if (promotionOnBlockPossible && (checkerBlockerMove >>> Move.MOVED_PIECE.shift & Move.MOVED_PIECE.mask) == 6) {
										moves.add(checkerBlockerMove | (4L << Move.TYPE.shift));
										moves.add(checkerBlockerMove | (5L << Move.TYPE.shift));
										moves.add(checkerBlockerMove | (6L << Move.TYPE.shift));
										moves.add(checkerBlockerMove | (7L << Move.TYPE.shift));
									}
									else
										moves.add(checkerBlockerMove);
								}
							}
						}
						kingMoveSet &= ~dB.getCrudeBishopMoves();
					}
				}
				if (kingMoveSet != 0) {
					kingMoves = BitOperations.serialize(kingMoveSet);
					while (kingMoves.hasNext()) {
						to = kingMoves.next();
						if (!isAttacked(to, false))
							moves.add(kingMove | (to << Move.TO.shift) | (this.offsetBoard[to] << Move.CAPTURED_PIECE.shift));
					}
				}
			}
			else {
				king 	  		= BitOperations.indexOfBit(this.whiteKing);
				kingMove	   |= move;
				kingMove 	   |= (king << Move.FROM.shift);
				kingMove 	   |= (1L << Move.MOVED_PIECE.shift);
				kingMoveSet		= MoveDatabase.getByIndex(king).getWhiteKingMoves(this.allNonWhiteOccupied);
				checker1 		= BitOperations.indexOfLSBit(this.checkers);
				checkerPiece1 	= this.offsetBoard[checker1];
				checker2		= BitOperations.indexOfBit(BitOperations.resetLSBit(this.checkers));
				checkerPiece2 	= this.offsetBoard[checker2];
				dB = MoveDatabase.getByIndex(checker1);
				switch (checkerPiece1) {
					case 8:
						kingMoveSet &= ~dB.getWhiteQueenMoves(this.allNonWhiteOccupied, (this.allOccupied^this.whiteKing));
					break;
					case 9:
						kingMoveSet &= ~dB.getCrudeRookMoves();
					break;
					case 10:
						kingMoveSet &= ~dB.getCrudeBishopMoves();
					break;
					case 11:
						kingMoveSet &= ~dB.getCrudeKnightMoves();
				}
				dB = MoveDatabase.getByIndex(checker2);
				switch (checkerPiece2) {
					case 8:
						kingMoveSet &= ~dB.getWhiteQueenMoves(this.allNonWhiteOccupied, (this.allOccupied^this.whiteKing));
					break;
					case 9:
						kingMoveSet &= ~dB.getCrudeRookMoves();
					break;
					case 10:
						kingMoveSet &= ~dB.getCrudeBishopMoves();
					break;
					case 11:
						kingMoveSet &= ~dB.getCrudeKnightMoves();
				}
				if (kingMoveSet != 0) {
					kingMoves = BitOperations.serialize(kingMoveSet);
					while (kingMoves.hasNext()) {
						to = kingMoves.next();
						if (!isAttacked(to, false))
							moves.add(kingMove | (to << Move.TO.getShift()) | (this.offsetBoard[to] << Move.CAPTURED_PIECE.shift));
					}
				}
			}
		}
		else {
			if (BitOperations.resetLSBit(this.checkers) == 0) {
				pinnedPieces  	=  getPinnedPieces(false);
				movablePieces 	= ~pinnedPieces;
				checker1  		= BitOperations.indexOfBit(this.checkers);
				checkerPiece1	= this.offsetBoard[checker1];
				king 	  		= BitOperations.indexOfBit(this.blackKing);
				kingMove 	    = move;
				kingMove 	   |= (king << Move.FROM.shift);
				kingMove 	   |= (7L << Move.MOVED_PIECE.shift);
				kingDb	 		= MoveDatabase.getByIndex(king);
				kingMoveSet		= kingDb.getBlackKingMoves(this.allNonBlackOccupied);
				dB				= MoveDatabase.getByIndex(checker1);
				if ((this.checkers & Rank.getByIndex(0)) != 0)
					promotionOnAttackPossible = true;
				checkerAttackerSet = getAttackers(checker1, false) & movablePieces & ~this.blackKing;
				checkerAttackers = BitOperations.serialize(checkerAttackerSet);
				while (checkerAttackers.hasNext()) {
					checkerAttackerSquare = checkerAttackers.next();
					checkerAttackerMove = move | (checkerAttackerSquare << Move.FROM.shift) | (this.offsetBoard[checkerAttackerSquare] << Move.MOVED_PIECE.shift) | (checkerPiece1 << Move.CAPTURED_PIECE.shift);
					if ((checkerAttackerMove >>> Move.MOVED_PIECE.shift & Move.MOVED_PIECE.mask) == 12) {
						if (promotionOnAttackPossible) {
							checkerAttackerMove |= (checker1 << Move.TO.shift);
							moves.add(checkerAttackerMove | (4L << Move.TYPE.shift));
							moves.add(checkerAttackerMove | (5L << Move.TYPE.shift));
							moves.add(checkerAttackerMove | (6L << Move.TYPE.shift));
							moves.add(checkerAttackerMove | (7L << Move.TYPE.shift));
						}
						else if (this.enPassantRights == checker1%8 && (Rank.getByIndex(3) & this.checkers) != 0 && (MoveDatabase.getByIndex(to = checker1 - 8).getCrudeWhitePawnCaptures() & (1L << checkerAttackerSquare)) != 0)
							moves.add(checkerAttackerMove | (to << Move.TO.shift) | (3L << Move.TYPE.shift));
						else
							moves.add(checkerAttackerMove | (checker1 << Move.TO.shift));
					}
					else
						moves.add(checkerAttackerMove | (checker1 << Move.TO.shift));
				}
				switch (checkerPiece1) {
					case 2: {
						if ((File.getBySquareIndex(king) & this.checkers) != 0 || (Rank.getBySquareIndex(king) & this.checkers) != 0) {
							squaresOfInterventionSet = (dB.getWhiteRookMoves(this.allNonWhiteOccupied, this.allOccupied) & kingDb.getBlackRookMoves(this.allNonBlackOccupied, this.allOccupied));
							if (promotionOnAttackPossible && (this.blackKing & Rank.getByIndex(0)) != 0)
								promotionOnBlockPossible = true;
						}
						else
							squaresOfInterventionSet = (dB.getWhiteBishopMoves(this.allNonWhiteOccupied, this.allOccupied) & kingDb.getBlackBishopMoves(this.allNonBlackOccupied, this.allOccupied));
						squaresOfIntervention = BitOperations.serialize(squaresOfInterventionSet);
						while (squaresOfIntervention.hasNext()) {
							squareOfIntervention = squaresOfIntervention.next();
							checkerBlockerSet = getBlockerCandidates(squareOfIntervention, false) & movablePieces;
							if (checkerBlockerSet != 0) {
								checkerBlockers = BitOperations.serialize(checkerBlockerSet);
								while (checkerBlockers.hasNext()) {
									checkerBlockerSquare = checkerBlockers.next();
									checkerBlockerMove = move | (checkerBlockerSquare << Move.FROM.shift) | (squareOfIntervention << Move.TO.shift) | (this.offsetBoard[checkerBlockerSquare] << Move.MOVED_PIECE.shift) | (this.offsetBoard[squareOfIntervention] << Move.CAPTURED_PIECE.shift);
									if (promotionOnBlockPossible && (checkerBlockerMove >>> Move.MOVED_PIECE.shift & Move.MOVED_PIECE.mask) == 12) {
										moves.add(checkerBlockerMove | (4L << Move.TYPE.shift));
										moves.add(checkerBlockerMove | (5L << Move.TYPE.shift));
										moves.add(checkerBlockerMove | (6L << Move.TYPE.shift));
										moves.add(checkerBlockerMove | (7L << Move.TYPE.shift));
									}
									else
										moves.add(checkerBlockerMove);
								}
							}
						}
						kingMoveSet &= ~dB.getBlackQueenMoves(this.allNonBlackOccupied, (this.allOccupied^this.blackKing));
					}
					break;
					case 3: {
						if (promotionOnAttackPossible && (this.blackKing & Rank.getByIndex(0)) != 0)
							promotionOnBlockPossible = true;
						squaresOfInterventionSet = (dB.getWhiteRookMoves(this.allNonWhiteOccupied, this.allOccupied) & kingDb.getBlackRookMoves(this.allNonBlackOccupied, this.allOccupied));
						squaresOfIntervention = BitOperations.serialize(squaresOfInterventionSet);
						while (squaresOfIntervention.hasNext()) {
							squareOfIntervention = squaresOfIntervention.next();
							checkerBlockerSet = getBlockerCandidates(squareOfIntervention, false) & movablePieces;
							if (checkerBlockerSet != 0) {
								checkerBlockers = BitOperations.serialize(checkerBlockerSet);
								while (checkerBlockers.hasNext()) {
									checkerBlockerSquare = checkerBlockers.next();
									checkerBlockerMove = move | (checkerBlockerSquare << Move.FROM.shift) | (squareOfIntervention << Move.TO.shift) | (this.offsetBoard[checkerBlockerSquare] << Move.MOVED_PIECE.shift) | (this.offsetBoard[squareOfIntervention] << Move.CAPTURED_PIECE.shift);
									if (promotionOnBlockPossible && (checkerBlockerMove >>> Move.MOVED_PIECE.shift & Move.MOVED_PIECE.mask) == 12) {
										moves.add(checkerBlockerMove | (4L << Move.TYPE.shift));
										moves.add(checkerBlockerMove | (5L << Move.TYPE.shift));
										moves.add(checkerBlockerMove | (6L << Move.TYPE.shift));
										moves.add(checkerBlockerMove | (7L << Move.TYPE.shift));
									}
									else
										moves.add(checkerBlockerMove);
								}
							}
						}
						kingMoveSet &= ~dB.getCrudeRookMoves();
					}
					break;
					case 4: {
						squaresOfInterventionSet = (dB.getWhiteBishopMoves(this.allNonWhiteOccupied, this.allOccupied) & kingDb.getBlackBishopMoves(this.allNonBlackOccupied, this.allOccupied));
						squaresOfIntervention = BitOperations.serialize(squaresOfInterventionSet);
						while (squaresOfIntervention.hasNext()) {
							squareOfIntervention = squaresOfIntervention.next();
							checkerBlockerSet = getBlockerCandidates(squareOfIntervention, false) & movablePieces;
							if (checkerBlockerSet != 0) {
								checkerBlockers = BitOperations.serialize(checkerBlockerSet);
								while (checkerBlockers.hasNext()) {
									checkerBlockerSquare = checkerBlockers.next();
									checkerBlockerMove = move | (checkerBlockerSquare << Move.FROM.shift) | (squareOfIntervention << Move.TO.shift) | (this.offsetBoard[checkerBlockerSquare] << Move.MOVED_PIECE.shift) | (this.offsetBoard[squareOfIntervention] << Move.CAPTURED_PIECE.shift);
									if (promotionOnBlockPossible && (checkerBlockerMove >>> Move.MOVED_PIECE.shift & Move.MOVED_PIECE.mask) == 12) {
										moves.add(checkerBlockerMove | (4L << Move.TYPE.shift));
										moves.add(checkerBlockerMove | (5L << Move.TYPE.shift));
										moves.add(checkerBlockerMove | (6L << Move.TYPE.shift));
										moves.add(checkerBlockerMove | (7L << Move.TYPE.shift));
									}
									else
										moves.add(checkerBlockerMove);
								}
							}
						}
						kingMoveSet &= ~dB.getCrudeBishopMoves();
					}
				}
				if (kingMoveSet != 0) {
					kingMoves = BitOperations.serialize(kingMoveSet);
					while (kingMoves.hasNext()) {
						to = kingMoves.next();
						if (!isAttacked(to, true))
							moves.add(kingMove | (to << Move.TO.shift) | (this.offsetBoard[to] << Move.CAPTURED_PIECE.shift));
					}
				}
			}
			else {
				king			= BitOperations.indexOfBit(this.blackKing);
				kingMove 	   |= move;
				kingMove 	   |= (king << Move.FROM.shift);
				kingMove 	   |= (7L << Move.MOVED_PIECE.shift);
				kingMoveSet 	= MoveDatabase.getByIndex(king).getBlackKingMoves(this.allNonBlackOccupied);
				checker1 		= BitOperations.indexOfLSBit(this.checkers);
				checkerPiece1 	= this.offsetBoard[checker1];
				checker2		= BitOperations.indexOfBit(BitOperations.resetLSBit(this.checkers));
				checkerPiece2 	= this.offsetBoard[checker2];
				dB				= MoveDatabase.getByIndex(checker1);
				switch (checkerPiece1) {
					case 2:
						kingMoveSet &= ~dB.getBlackQueenMoves(this.allNonBlackOccupied, (this.allOccupied^this.blackKing));
					break;
					case 3:
						kingMoveSet &= ~dB.getCrudeRookMoves();
					break;
					case 4:
						kingMoveSet &= ~dB.getCrudeBishopMoves();
					break;
					case 5:
						kingMoveSet &= ~dB.getCrudeKnightMoves();
				}
				dB = MoveDatabase.getByIndex(checker2);
				switch (checkerPiece2) {
					case 2:
						kingMoveSet &= ~dB.getBlackQueenMoves(this.allNonBlackOccupied, (this.allOccupied^this.blackKing));
					break;
					case 3:
						kingMoveSet &= ~dB.getCrudeRookMoves();
					break;
					case 4:
						kingMoveSet &= ~dB.getCrudeBishopMoves();
					break;
					case 5:
						kingMoveSet &= ~dB.getCrudeKnightMoves();
				}
				if (kingMoveSet != 0) {
					kingMoves = BitOperations.serialize(kingMoveSet);
					while (kingMoves.hasNext()) {
						to = kingMoves.next();
						if (!isAttacked(to, true))
							moves.add(kingMove | (to << Move.TO.shift) | (this.offsetBoard[to] << Move.CAPTURED_PIECE.shift));
					}
				}
			}
		}
		return moves;
	}
	/**Generates a queue of longs that represent all the legal moves from the current position.
	 * 
	 * @return
	 */
	public LongQueue generateMoves() {
		if (this.check)
			return this.generateCheckEvasionMoves();
		else
			return this.generateNormalMoves();
	}
	/**Makes a move on the bitboards and the offset board. Only the board positions is changed.
	 * Used also for testing for discovered checks in combination with {@link #unMakeMoveOnBoard(long) unMakeMoveOnBoard} as it is more efficient than wholly making and un-making moves.
	 * 
	 * @param move
	 */
	private void makeMoveOnBoard(long move) {
		int from 			= (int)((move >>> Move.FROM.shift)		 	  & Move.FROM.mask);
		int to	 			= (int)((move >>> Move.TO.shift) 			  & Move.TO.mask);
		int moved			= (int)((move >>> Move.MOVED_PIECE.shift) 	  & Move.MOVED_PIECE.mask);
		int captured	 	= (int)((move >>> Move.CAPTURED_PIECE.shift)  & Move.CAPTURED_PIECE.mask);
		int type			= (int)((move >>> Move.TYPE.shift)  		  & Move.TYPE.mask);
		long fromBit = Square.getBitmapByIndex(from);
		long toBit	 = Square.getBitmapByIndex(to);
		int enPassantVictimSquare;
		long enPassantVictimSquareBit;
		switch (type) {
			case 0: {
				this.offsetBoard[from]  = 0;
				this.offsetBoard[to]	= moved;
				this.setBitboards(moved, captured, fromBit, toBit);
			}
			break;
			case 1: {
				this.offsetBoard[from]   = 0;
				this.offsetBoard[to]	 = moved;
				this.setBitboards(moved, 0, fromBit, toBit);
				if (this.whitesTurn) {
					this.offsetBoard[7]	 = 0;
					this.offsetBoard[5]	 = 3;
					this.setBitboards(3, 0, Square.getBitmapByIndex(7), Square.getBitmapByIndex(5));
				}
				else {
					this.offsetBoard[63] = 0;
					this.offsetBoard[61] = 9;
					this.setBitboards(9, 0, Square.getBitmapByIndex(63), Square.getBitmapByIndex(61));
				}
			}
			break;
			case 2: {
				this.offsetBoard[from]   = 0;
				this.offsetBoard[to]	 = moved;
				this.setBitboards(moved, 0, fromBit, toBit);
				if (this.whitesTurn) {
					this.offsetBoard[0]	 = 0;
					this.offsetBoard[3]	 = 3;
					this.setBitboards(3, 0, Square.getBitmapByIndex(0), Square.getBitmapByIndex(3));
				}
				else {
					this.offsetBoard[56] = 0;
					this.offsetBoard[59] = 9;
					this.setBitboards(9, 0, Square.getBitmapByIndex(56), Square.getBitmapByIndex(59));
				}
			}
			break;
			case 3: {
				this.offsetBoard[from]   = 0;
				this.offsetBoard[to]	 = moved;
				if (this.whitesTurn)
					enPassantVictimSquare = to - 8;
				else
					enPassantVictimSquare = to + 8;
				this.offsetBoard[enPassantVictimSquare] = 0;
				enPassantVictimSquareBit = Square.getBitmapByIndex(enPassantVictimSquare);
				this.setBitboards(moved, captured, fromBit, enPassantVictimSquareBit);
				this.setBitboards(moved, 0, enPassantVictimSquareBit, toBit);
			}
			break;
			case 4: {
				this.offsetBoard[from]   = 0;
				this.setBitboards(moved, 0, fromBit, 0);
				if (this.whitesTurn) {
					this.offsetBoard[to] = 2;
					this.setBitboards(2, captured, 0, toBit);
				}
				else {
					this.offsetBoard[to] = 8;
					this.setBitboards(8, captured, 0, toBit);
				}
			}
			break;
			case 5: {
				this.offsetBoard[from]   = 0;
				this.setBitboards(moved, 0, fromBit, 0);
				if (this.whitesTurn) {
					this.offsetBoard[to] = 3;
					this.setBitboards(3, captured, 0, toBit);
				}
				else {
					this.offsetBoard[to] = 9;
					this.setBitboards(9, captured, 0, toBit);
				}
			}
			break;
			case 6: {
				this.offsetBoard[from]   = 0;
				this.setBitboards(moved, 0, fromBit, 0);
				if (this.whitesTurn) {
					this.offsetBoard[to] = 4;
					this.setBitboards(4, captured, 0, toBit);
				}
				else {
					this.offsetBoard[to] = 10;
					this.setBitboards(10, captured, 0, toBit);
				}
			}
			break;
			case 7: {
				this.offsetBoard[from]   = 0;
				this.setBitboards(moved, 0, fromBit, 0);
				if (this.whitesTurn) {
					this.offsetBoard[to] = 5;
					this.setBitboards(5, captured, 0, toBit);
				}
				else {
					this.offsetBoard[to] = 11;
					this.setBitboards(11, captured, 0, toBit);
				}
			}
		}
	}
	/**Un-makes a move on the bitboards and the offset board. Only the board positions is changed.
	 * Used also for testing for discovered checks in combination with {@link #makeMoveOnBoard(long) makeMoveOnBoard} as it is more efficient than wholly making and un-making moves.
	 * 
	 * @param move
	 */
	private void unMakeMoveOnBoard(long move) {
		int from 			= (int)((move >>> Move.FROM.shift)		 	  & Move.FROM.mask);
		int to	 			= (int)((move >>> Move.TO.shift) 			  & Move.TO.mask);
		int moved			= (int)((move >>> Move.MOVED_PIECE.shift) 	  & Move.MOVED_PIECE.mask);
		int captured	 	= (int)((move >>> Move.CAPTURED_PIECE.shift)  & Move.CAPTURED_PIECE.mask);
		int type			= (int)((move >>> Move.TYPE.shift)  		  & Move.TYPE.mask);
		int enPassantVictimSquare;
		long fromBit = Square.getBitmapByIndex(from);
		long toBit	 = Square.getBitmapByIndex(to);
		switch (type) {
			case 0: {
				this.offsetBoard[from]  = moved;
				this.offsetBoard[to]	= captured;
				this.setBitboards(moved, captured, fromBit, toBit);
			}
			break;
			case 1: {
				this.offsetBoard[from]  = moved;
				this.offsetBoard[to]	= 0;
				this.setBitboards(moved, 0, fromBit, toBit);
				if (this.whitesTurn) {
					this.offsetBoard[7]	 = 3;
					this.offsetBoard[5]	 = 0;
					this.setBitboards(3, 0, Square.getBitmapByIndex(5), Square.getBitmapByIndex(7));
				}
				else {
					this.offsetBoard[63] = 9;
					this.offsetBoard[61] = 0;
					this.setBitboards(9, 0, Square.getBitmapByIndex(61), Square.getBitmapByIndex(63));
				}
			}
			break;
			case 2: {
				this.offsetBoard[from]  = moved;
				this.offsetBoard[to]	= 0;
				this.setBitboards(moved, 0, fromBit, toBit);
				if (this.whitesTurn) {
					this.offsetBoard[0]	 = 3;
					this.offsetBoard[3]	 = 0;
					this.setBitboards(3, 0, Square.getBitmapByIndex(3), Square.getBitmapByIndex(0));
				}
				else {
					this.offsetBoard[56] = 9;
					this.offsetBoard[59] = 0;
					this.setBitboards(9, 0, Square.getBitmapByIndex(59), Square.getBitmapByIndex(56));
				}
			}
			break;
			case 3: {
				this.offsetBoard[from]   = moved;
				this.offsetBoard[to]	 = 0;
				this.setBitboards(moved, 0, fromBit, toBit);
				if (this.whitesTurn)
					enPassantVictimSquare = to - 8;
				else
					enPassantVictimSquare = to + 8;
				this.offsetBoard[enPassantVictimSquare] = captured;
				this.setBitboards(0, captured, 0, Square.getBitmapByIndex(enPassantVictimSquare));
			}
			break;
			case 4: {
				this.offsetBoard[from]  = moved;
				this.offsetBoard[to] 	= captured;
				this.setBitboards(moved, 0, fromBit, 0);
				if (this.whitesTurn)
					this.setBitboards(2, 0, toBit, 0);
				else
					this.setBitboards(8, 0, toBit, 0);
				this.setBitboards(0, captured, 0, toBit);
			}
			break;
			case 5: {
				this.offsetBoard[from]  = moved;
				this.offsetBoard[to] 	= captured;
				this.setBitboards(moved, 0, fromBit, 0);
				if (this.whitesTurn)
					this.setBitboards(3, 0, toBit, 0);
				else
					this.setBitboards(9, 0, toBit, 0);
				this.setBitboards(0, captured, 0, toBit);
			}
			break;
			case 6: {
				this.offsetBoard[from]  = moved;
				this.offsetBoard[to] 	= captured;
				this.setBitboards(moved, 0, fromBit, 0);
				if (this.whitesTurn)
					this.setBitboards(4, 0, toBit, 0);
				else
					this.setBitboards(10, 0, toBit, 0);
				this.setBitboards(0, captured, 0, toBit);
			}
			break;
			case 7: {
				this.offsetBoard[from]  = moved;
				this.offsetBoard[to] 	= captured;
				this.setBitboards(moved, 0, fromBit, 0);
				if (this.whitesTurn)
					this.setBitboards(5, 0, toBit, 0);
				else
					this.setBitboards(11, 0, toBit, 0);
				this.setBitboards(0, captured, 0, toBit);
			}
		}
	}
	/**Makes a single move from a LongQueue generated by generateMoves.
	 * 
	 * @param move
	 */
	protected void makeMove(long move) {
		int from 			= (int)((move >>> Move.FROM.shift)		 	  & Move.FROM.mask);
		int to	 			= (int)((move >>> Move.TO.shift) 			  & Move.TO.mask);
		int moved			= (int)((move >>> Move.MOVED_PIECE.shift) 	  & Move.MOVED_PIECE.mask);
		int captured	 	= (int)((move >>> Move.CAPTURED_PIECE.shift)  & Move.CAPTURED_PIECE.mask);
		int type			= (int)((move >>> Move.TYPE.shift)  		  & Move.TYPE.mask);
		long fromBit = Square.getBitmapByIndex(from);
		long toBit	 = Square.getBitmapByIndex(to);
		int enPassantVictimSquare;
		long enPassantVictimSquareBit;
		switch (type) {
			case 0: {
				this.offsetBoard[from]  = 0;
				this.offsetBoard[to]	= moved;
				this.setBitboards(moved, captured, fromBit, toBit);
			}
			break;
			case 1: {
				this.offsetBoard[from]   = 0;
				this.offsetBoard[to]	 = moved;
				this.setBitboards(moved, 0, fromBit, toBit);
				if (this.whitesTurn) {
					this.offsetBoard[7]	 = 0;
					this.offsetBoard[5]	 = 3;
					this.setBitboards(3, 0, Square.getBitmapByIndex(7), Square.getBitmapByIndex(5));
				}
				else {
					this.offsetBoard[63] = 0;
					this.offsetBoard[61] = 9;
					this.setBitboards(9, 0, Square.getBitmapByIndex(63), Square.getBitmapByIndex(61));
				}
			}
			break;
			case 2: {
				this.offsetBoard[from]   = 0;
				this.offsetBoard[to]	 = moved;
				this.setBitboards(moved, 0, fromBit, toBit);
				if (this.whitesTurn) {
					this.offsetBoard[0]	 = 0;
					this.offsetBoard[3]	 = 3;
					this.setBitboards(3, 0, Square.getBitmapByIndex(0), Square.getBitmapByIndex(3));
				}
				else {
					this.offsetBoard[56] = 0;
					this.offsetBoard[59] = 9;
					this.setBitboards(9, 0, Square.getBitmapByIndex(56), Square.getBitmapByIndex(59));
				}
			}
			break;
			case 3: {
				this.offsetBoard[from]   = 0;
				this.offsetBoard[to]	 = moved;
				if (this.whitesTurn)
					enPassantVictimSquare = to - 8;
				else
					enPassantVictimSquare = to + 8;
				this.offsetBoard[enPassantVictimSquare] = 0;
				enPassantVictimSquareBit = Square.getBitmapByIndex(enPassantVictimSquare);
				this.setBitboards(moved, captured, fromBit, enPassantVictimSquareBit);
				this.setBitboards(moved, 0, enPassantVictimSquareBit, toBit);
			}
			break;
			case 4: {
				this.offsetBoard[from]   = 0;
				this.setBitboards(moved, 0, fromBit, 0);
				if (this.whitesTurn) {
					this.offsetBoard[to] = 2;
					this.setBitboards(2, captured, 0, toBit);
				}
				else {
					this.offsetBoard[to] = 8;
					this.setBitboards(8, captured, 0, toBit);
				}
			}
			break;
			case 5: {
				this.offsetBoard[from]   = 0;
				this.setBitboards(moved, 0, fromBit, 0);
				if (this.whitesTurn) {
					this.offsetBoard[to] = 3;
					this.setBitboards(3, captured, 0, toBit);
				}
				else {
					this.offsetBoard[to] = 9;
					this.setBitboards(9, captured, 0, toBit);
				}
			}
			break;
			case 6: {
				this.offsetBoard[from]   = 0;
				this.setBitboards(moved, 0, fromBit, 0);
				if (this.whitesTurn) {
					this.offsetBoard[to] = 4;
					this.setBitboards(4, captured, 0, toBit);
				}
				else {
					this.offsetBoard[to] = 10;
					this.setBitboards(10, captured, 0, toBit);
				}
			}
			break;
			case 7: {
				this.offsetBoard[from]   = 0;
				this.setBitboards(moved, 0, fromBit, 0);
				if (this.whitesTurn) {
					this.offsetBoard[to] = 5;
					this.setBitboards(5, captured, 0, toBit);
				}
				else {
					this.offsetBoard[to] = 11;
					this.setBitboards(11, captured, 0, toBit);
				}
			}
		}
		this.moveList.add(move);
		this.setTurn();
		this.setCheck();
		this.setCastlingRights();
		this.setEnPassantRights(from, to, moved);
		this.setMoveIndices(moved, captured);
		this.setKeys();
		this.setRepetitions();
	}
	/**Reverts the state of the instance to that before the last move made in every aspect necessary for the traversal of the game tree. Used within the engine.*/
	protected void unMakeMove() {
		this.setTurn();
		long move = this.moveList.pop();
		int from					= (int)((move >>> Move.FROM.shift)		 	  					& Move.FROM.mask);
		int to						= (int)((move >>> Move.TO.shift) 			  					& Move.TO.mask);
		int moved					= (int)((move >>> Move.MOVED_PIECE.shift) 	 					& Move.MOVED_PIECE.mask);
		int captured				= (int)((move >>> Move.CAPTURED_PIECE.shift)  					& Move.CAPTURED_PIECE.mask);
		int type					= (int)((move >>> Move.TYPE.shift)  							& Move.TYPE.mask);
		int prevWhiteCastlingRights	= (int)((move >>> Move.PREVIOUS_WHITE_CASTLING_RIGHTS.shift)	& Move.PREVIOUS_WHITE_CASTLING_RIGHTS.mask);
		int prevBlackCastlingRights	= (int)((move >>> Move.PREVIOUS_BLACK_CASTLING_RIGHTS.shift)	& Move.PREVIOUS_BLACK_CASTLING_RIGHTS.mask);
		int prevEnPassantRights		= (int)((move >>> Move.PREVIOUS_ENPASSANT_RIGHTS.shift)			& Move.PREVIOUS_ENPASSANT_RIGHTS.mask);
		int prevCheck				= (int)((move >>> Move.PREVIOUS_CHECK.shift)  		  			& Move.PREVIOUS_CHECK.mask);
		long prevFiftyMoveRuleClock = 		(move >>> Move.PREVIOUS_FIFTY_MOVE_RULE_CLOCK.shift)	& Move.PREVIOUS_FIFTY_MOVE_RULE_CLOCK.mask;
		long prevRepetitions 		= 		(move >>> Move.PREVIOUS_REPETITIONS.shift)				& Move.PREVIOUS_REPETITIONS.mask;
		int enPassantVictimSquare;
		long fromBit = Square.getBitmapByIndex(from);
		long toBit	 = Square.getBitmapByIndex(to);
		switch (type) {
			case 0: {
				this.offsetBoard[from]  = moved;
				this.offsetBoard[to]	= captured;
				this.setBitboards(moved, captured, fromBit, toBit);
			}
			break;
			case 1: {
				this.offsetBoard[from]  = moved;
				this.offsetBoard[to]	= 0;
				this.setBitboards(moved, 0, fromBit, toBit);
				if (this.whitesTurn) {
					this.offsetBoard[7]	 = 3;
					this.offsetBoard[5]	 = 0;
					this.setBitboards(3, 0, Square.getBitmapByIndex(5), Square.getBitmapByIndex(7));
				}
				else {
					this.offsetBoard[63] = 9;
					this.offsetBoard[61] = 0;
					this.setBitboards(9, 0, Square.getBitmapByIndex(61), Square.getBitmapByIndex(63));
				}
			}
			break;
			case 2: {
				this.offsetBoard[from]  = moved;
				this.offsetBoard[to]	= 0;
				this.setBitboards(moved, 0, fromBit, toBit);
				if (this.whitesTurn) {
					this.offsetBoard[0]	 = 3;
					this.offsetBoard[3]	 = 0;
					this.setBitboards(3, 0, Square.getBitmapByIndex(3), Square.getBitmapByIndex(0));
				}
				else {
					this.offsetBoard[56] = 9;
					this.offsetBoard[59] = 0;
					this.setBitboards(9, 0, Square.getBitmapByIndex(59), Square.getBitmapByIndex(56));
				}
			}
			break;
			case 3: {
				this.offsetBoard[from]   = moved;
				this.offsetBoard[to]	 = 0;
				this.setBitboards(moved, 0, fromBit, toBit);
				if (this.whitesTurn)
					enPassantVictimSquare = to - 8;
				else
					enPassantVictimSquare = to + 8;
				this.offsetBoard[enPassantVictimSquare] = captured;
				this.setBitboards(0, captured, 0, Square.getBitmapByIndex(enPassantVictimSquare));
			}
			break;
			case 4: {
				this.offsetBoard[from]  = moved;
				this.offsetBoard[to] 	= captured;
				this.setBitboards(moved, 0, fromBit, 0);
				if (this.whitesTurn)
					this.setBitboards(2, 0, toBit, 0);
				else
					this.setBitboards(8, 0, toBit, 0);
				this.setBitboards(0, captured, 0, toBit);
			}
			break;
			case 5: {
				this.offsetBoard[from]  = moved;
				this.offsetBoard[to] 	= captured;
				this.setBitboards(moved, 0, fromBit, 0);
				if (this.whitesTurn)
					this.setBitboards(3, 0, toBit, 0);
				else
					this.setBitboards(9, 0, toBit, 0);
				this.setBitboards(0, captured, 0, toBit);
			}
			break;
			case 6: {
				this.offsetBoard[from]  = moved;
				this.offsetBoard[to] 	= captured;
				this.setBitboards(moved, 0, fromBit, 0);
				if (this.whitesTurn)
					this.setBitboards(4, 0, toBit, 0);
				else
					this.setBitboards(10, 0, toBit, 0);
				this.setBitboards(0, captured, 0, toBit);
			}
			break;
			case 7: {
				this.offsetBoard[from]  = moved;
				this.offsetBoard[to] 	= captured;
				this.setBitboards(moved, 0, fromBit, 0);
				if (this.whitesTurn)
					this.setBitboards(5, 0, toBit, 0);
				else
					this.setBitboards(11, 0, toBit, 0);
				this.setBitboards(0, captured, 0, toBit);
			}
		}
		this.repetitions = prevRepetitions;
		this.zobristKeyHistory[this.plyIndex] = 0;
		this.zobristKey = this.zobristKeyHistory[this.plyIndex - 1];
		this.plyIndex--;
		this.fiftyMoveRuleClock	= prevFiftyMoveRuleClock;
		this.enPassantRights = prevEnPassantRights;
		this.whiteCastlingRights = prevWhiteCastlingRights;
		this.blackCastlingRights = prevBlackCastlingRights;
		this.check = prevCheck == 1 ? true : false;
	}
	/**Makes a move specified by user input. If it is legal and the command is valid ([origin square + destination square] as e.g.: b1a3 without any spaces; in case of promotion,
	 * the FEN notation of the piece the pawn is wished to be promoted to should be appended to the command as in c7c8q; the parser is not case sensitive), it returns true, else
	 * false.
	 * 
	 * @return
	 */
	public boolean makeMove(String input) {
		char zero, one, two, three, four;
		int from, to, type = 0;
		long move;
		LongQueue moves;
		String command = "";
		for (int i = 0; i < input.length(); i++) {
			if (Character.toString(input.charAt(i)).matches("\\p{Graph}"))
				command += input.charAt(i);
		}
		if (command.length() >= 4 && command.length() <= 5) {
			command = command.toLowerCase();
			if (Character.toString(zero = command.charAt(0)).matches("[a-h]") && Character.toString(two = command.charAt(2)).matches("[a-h]") && Character.toString(one = command.charAt(1)).matches("[1-8]") && Character.toString(three = command.charAt(3)).matches("[1-8]")) {
				from = (zero - 'a') + (one - '1')*8;
				to 	 = (two - 'a') + (three - '1')*8;
				if (command.length() == 5) {
					four = command.charAt(4);
					if (three == 1 || three == 8) {
						switch (four) {
							case 'q':
								type = 4;
							break;
							case 'r':
								type = 5;
							break;
							case 'b':
								type = 6;
							break;
							case 'n':
								type = 7;
							break;
							default:
								return false;
						}
					}
					else
						return false;
				}
				moves = this.generateMoves();
				while (moves.hasNext()) {
					move = moves.next();
					if (((move >>> Move.FROM.shift) & Move.FROM.mask) == from && ((move >>> Move.TO.shift) & Move.TO.mask) == to) {
						if (type != 0) {
							if (((move >>> Move.TYPE.shift) & Move.TYPE.mask) == type) {
								this.makeMove(move);
								return true;
							}
						}
						this.makeMove(move);
						return true;
					}
				}
			}
		}
		return false;
	}
	/**Reverts the state of the instance to that before the last move made. Used within the interface to allow the human player to take steps back during the course of a game.
	 * If a move has already been made, the body is executed and the method returns true, else false.
	 * 
	 * @return
	 */
	public boolean revert() {
		if (this.moveList.getHead() != 0) {
			this.unMakeMove();
			if (this.check)
				this.setCheck();
			return true;
		}
		return false;
	}
	/**Returns the current state of a Board object as a one-line String in FEN-notation. The FEN-notation consists of six fields separated by spaces.
	 * The six fields are as follows:
	 * 		1. board position
	 * 		2. color to move
	 * 		3. castling rights
	 * 		4. en passant rights
	 * 		5. fifty-move rule clock
	 * 		6. fullmove number
	 */
	public String toString() {
		String fen = "";
		int piece, emptyCount;
		for (int i = 7; i >= 0; i--) {
			emptyCount = 0;
			for (int j = 0; j < 8; j++) {
				piece = this.offsetBoard[i*8 + j];
				if (piece == 0)
					emptyCount++;
				else {
					if (emptyCount != 0)
						fen += emptyCount;
					emptyCount = 0;
					fen += Piece.fenNotation(piece);
				}
			}
			if (emptyCount != 0)
				fen += emptyCount;
			if (i != 0)
				fen += '/';
		}
		fen += ' ';
		if (this.whitesTurn)
			fen += 'w';
		else
			fen += 'b';
		fen += ' ';
		if (this.whiteCastlingRights != 0) {
			if ((this.whiteCastlingRights & 1) != 0)
				fen += 'K';
			if ((this.whiteCastlingRights & 2) != 0)
				fen += 'Q';
		}
		if (this.blackCastlingRights != 0) {
			if ((this.blackCastlingRights & 1) != 0)
				fen += 'k';
			if ((this.blackCastlingRights & 2) != 0)
				fen += 'q';
		}
		if (this.whiteCastlingRights == 0 && this.blackCastlingRights == 0)
			fen += '-';
		fen += ' ';
		if (this.enPassantRights == 8)
			fen += '-';
		else {
			fen += (char)(this.enPassantRights + 'a');
			if (this.whitesTurn)
				fen += 6;
			else
				fen += 3;
		}
		fen += ' ';
		fen += this.fiftyMoveRuleClock;
		fen += ' ';
		fen += 1 + this.plyIndex/2;
		return fen;
	}
	/**Prints the object's move history to the console in chronological order in pseudo-algebraic chess notation.*/
	public void printMoveHistoryToConsole() {
		LongList chronMoves = new LongStack();
		int i = 0;
		while (this.moveList.hasNext())
			chronMoves.add(this.moveList.next());
		while (chronMoves.hasNext()) {
			i++;
			System.out.printf(i + ". %-8s ", Move.pseudoAlgebraicNotation(chronMoves.next()));
		}
		System.out.println();
	}
	/**Prints a bitboard representing all the occupied squares of the object's board position to the console in a human-readable form,
	 * aligned like a chess board.*/
	public void printBitboardToConsole() {
		BitOperations.printBitboardToConsole(this.allOccupied);
	}
	/**Prints an array representing the object's board position to the console in a human-readable form, aligned like a chess board with 
	 * integers denoting the pieces. 0 means an empty square, 1 is the white king, 2 is the white queen, ..., 7 is the black king, etc.*/
	public void printOffsetBoardToConsole() {
		for (int i = 7; i >= 0; i--) {
			for (int j = 0; j < 8; j++)
				System.out.format("%3d", this.offsetBoard[i*8 + j]);
			System.out.println();
		}
		System.out.println();
	}
	/**Prints the chess board to the console. Pieces are represented according to the FEN notation.*/
	public void printFancyBoardToConsole() {
		for (int i = 16; i >= 0; i--) {
			if (i%2 == 0) {
				System.out.print("  ");
				for (int j = 0; j < 17; j++) {
					if (j%2 == 0)
						System.out.print("+");
					else
						System.out.print("---");
				}
			}
			else {
				System.out.print((i + 1)/2 + " ");
				for (int j = 0; j < 17; j++) {
					if (j%2 == 0)
						System.out.print("|");
					else
						System.out.print(" " + Piece.fenNotation(this.offsetBoard[(i - 1)*4 + j/2]) + " ");
				}
			}
			System.out.println();
		}
		System.out.print("  ");
		for (int i = 0; i < 8; i++) {
			System.out.print("  " + (char)('A' + i) + " ");
		}
		System.out.println();
	}
	/**Prints information that constitutes the Board instance's state to the console.*/
	public void printStateToConsole() {
		IntStack checkers;
		this.printFancyBoardToConsole();
		System.out.println();
		System.out.printf("%-23s ", "To move:");
		if (this.whitesTurn)
			System.out.println("white");
		else
			System.out.println("black");
		if (this.check) {
			System.out.printf("%-23s ", "Checker(s):");
			checkers = BitOperations.serialize(this.checkers);
			while (checkers.hasNext())
				System.out.print(Square.toString(checkers.next()) + " ");
			System.out.println();
		}
		System.out.printf("%-23s ", "Castling rights:");
		if ((this.whiteCastlingRights & 1) != 0)
			System.out.print("K");
		if ((this.whiteCastlingRights & 2) != 0)
			System.out.print("Q");
		if ((this.blackCastlingRights & 1) != 0)
			System.out.print("k");
		if ((this.blackCastlingRights & 2) != 0)
			System.out.print("q");
		if (this.whiteCastlingRights == 0 && this.blackCastlingRights == 0)
			System.out.print("-");
		System.out.println();
		System.out.printf("%-23s ", "En passant rights:");
		if (this.enPassantRights == 8)
			System.out.println("-");
		else
			System.out.println((char)('a' + this.enPassantRights));
		System.out.printf("%-23s " + this.plyIndex + "\n", "Half-move index:");
		System.out.printf("%-23s " + this.fiftyMoveRuleClock + "\n", "Fifty-move rule clock:");
		System.out.printf("%-23s " + Long.toHexString(this.zobristKey) + "\n", "Hash key:");
		System.out.printf("%-23s ", "Move history:");
		this.printMoveHistoryToConsole();
		System.out.println();
	}
	/**Runs a perft test to the given depth and returns the number of leaf nodes the traversed game tree had. It is used mainly for move generation and move
	 * making speed benchmarking; and bug detection by comparing the returned values to validated results.
	 * 
	 * @param depth
	 * @return
	 */
	public long perft(int depth) {
		LongQueue moves;
		long move, leafNodes = 0;
		if (depth == 0)
			return 1;
		moves = this.generateMoves();
		while (moves.hasNext()) {
			move = moves.next();
			this.makeMove(move);
			leafNodes += this.perft(depth - 1);
			this.unMakeMove();
		}
		return leafNodes;
	}
	/**Runs a perft test faster than the standard method. Instead of making and unmaking the moves leading to the leafnodes, it simply
	 * returns the number of generated moves from the nodes at depth 1. More suitable for benchmarking move generation.
	 * 
	 * @param depth
	 * @return
	 */
	public long quickPerft(int depth) {
		LongQueue moves;
		long move, leafNodes = 0;
		moves = this.generateMoves();
		if (depth == 1)
			return moves.length();
		while (moves.hasNext()) {
			move = moves.next();
			this.makeMove(move);
			leafNodes += this.quickPerft(depth - 1);
			this.unMakeMove();
		}
		return leafNodes;
	}
	/**Runs a perft test to the given depth and returns the number of leafnodes resulting from moves of the type specified by moveType.
	 * Expected moveType values:
	 * default:	all kinds of moves;
	 * 1:		ordinary moves;
	 * 2:		castling;
	 * 3:		en passant;
	 * 4:		promotion.
	 * It can also print to the console either the move list or one of two kinds of representations of the board position in the leafnodes according to consoleOutputType.
	 * Expected consoleOutputType values:
	 * default:	no output;
	 * 1:		the whole move list in chronological order;
	 * 2:		a bitboard representing all the occupied squares using {@link #printBitboardToConsole() printBitboardToConsole};
	 * 3:		a matrix of integers denoting chess pieces according to {@link #Board.Piece Piece} using {@link #printOffsetBoardToConsole() printOffsetBoardToConsole}.
	 * 
	 * @param depth
	 * @param moveType
	 * @param consoleOutputType
	 * @return
	 */
	public long detailedPerft(int depth, int moveType, int consoleOutputType) {
		LongList moves;
		long move, type, leafNodes = 0;
		if (depth == 0) {
			switch (moveType) {
				case 1: {
					if (((this.moveList.getHead() >>> Move.TYPE.shift) & Move.TYPE.mask) == 0) {
						switch (consoleOutputType) {
							case 1: 
								this.printMoveHistoryToConsole();
							break;
							case 2:
								this.printBitboardToConsole();
							break;
							case 3:
								this.printOffsetBoardToConsole();
						}
						return 1;
					}
				}
				break;
				case 2: {
					if ((type = (this.moveList.getHead() >>> Move.TYPE.shift) & Move.TYPE.mask) == 1 || type == 2) {
						switch (consoleOutputType) {
							case 1: 
								this.printMoveHistoryToConsole();
							break;
							case 2:
								this.printBitboardToConsole();
							break;
							case 3:
								this.printOffsetBoardToConsole();
						}
						return 1;
					}
				}
				break;
				case 3: {
					if (((this.moveList.getHead() >>> Move.TYPE.shift) & Move.TYPE.mask) == 3) {
						switch (consoleOutputType) {
							case 1: 
								this.printMoveHistoryToConsole();
							break;
							case 2:
								this.printBitboardToConsole();
							break;
							case 3:
								this.printOffsetBoardToConsole();
						}
						return 1;
					}
				}
				break;
				case 4: {
					if (((this.moveList.getHead() >>> Move.TYPE.shift) & Move.TYPE.mask) > 3) {
						switch (consoleOutputType) {
							case 1: 
								this.printMoveHistoryToConsole();
							break;
							case 2:
								this.printBitboardToConsole();
							break;
							case 3:
								this.printOffsetBoardToConsole();
						}
						return 1;
					}
				}
				break;
				default: {
					switch (consoleOutputType) {
						case 1: 
							this.printMoveHistoryToConsole();
						break;
						case 2:
							this.printBitboardToConsole();
						break;
						case 3:
							this.printOffsetBoardToConsole();
					}
					return 1;
				}
			}
			return 0;
		}
		moves = this.generateMoves();
		while (moves.hasNext()) {
			move = moves.next();
			this.makeMove(move);
			leafNodes += this.detailedPerft(depth - 1, moveType, consoleOutputType);
			this.unMakeMove();
		}
		return leafNodes;
	}
	/**An interactive perft/divide function that runs a perft test and prints out all the legal moves at the root including the number of leafnodes in the subtrees.
	 * Selecting the index of a move results in the move made and the divide function run again for that subtree. If an invalid index is entered or depth 0 is reached, the method exits.
	 * A debugging tool based on comparison to correct move generators' results.
	 * 
	 * @param depth
	 */
	public void dividePerft(int depth) {
		System.out.println("DIVIDE_START");
		Scanner in = new Scanner(System.in);
		int moveIndex, i;
		IntQueue moveIndices = new IntQueue();
		long move, nodes, total;
		LongQueue moves;
		LongStack chronoHistory;
		boolean found = true;
		while (depth > 0 && found) {
			depth--;
			this.printStateToConsole();
			total = 0;
			found = false;
			i = 0;
			chronoHistory = new LongStack();
			while (this.moveList.hasNext())
				chronoHistory.add(this.moveList.next());
			moves = this.generateMoves();
			while (moves.hasNext()) {
				while (chronoHistory.hasNext()) {
					move = chronoHistory.next();
					System.out.printf("%3d. %-8s ", moveIndices.next(), Move.pseudoAlgebraicNotation(move));
				}
				moveIndices.reset();
				i++;
				move = moves.next();
				this.makeMove(move);
				if (depth > 0)
					nodes = this.quickPerft(depth);
				else
					nodes = 1;
				System.out.printf("%3d. %-8s nodes: %d\n", i, Move.pseudoAlgebraicNotation(move), nodes);
				total += nodes;
				this.unMakeMove();
			}
			System.out.println("\nMoves: " + moves.length());
			System.out.println("Total nodes: " + total);
			System.out.print("Enter the index of the move to divide: ");
			moveIndex = in.nextInt();
			if (moveIndex >= 0 && moveIndex <= i) {
				i = 0;
				while (moves.hasNext()) {
					i++;
					move = moves.next();
					if (i == moveIndex) {
						this.makeMove(move);
						moveIndices.add(moveIndex);
						found = true;
					}
				}
			}
		}
		in.close();
		System.out.println("DIVIDE_END");
	}
	/**Breaks up the number perft would return into the root's subtrees. It prints to the console all the legal moves from the root position and the number of leafnodes in
	 * the subtrees to which the moves lead.
	 * 
	 * @param depth
	 */
	public void divide(int depth) {
		long move, total = 0;
		LongQueue moves = this.generateMoves();
		while (moves.hasNext()) {
			move = moves.next();
			System.out.printf("%-10s ", Move.pseudoAlgebraicNotation(move) + ":");
			this.makeMove(move);
			System.out.println(total += this.quickPerft(depth - 1));
			this.unMakeMove();
		}
		System.out.println("Moves: " + moves.length());
		System.out.println("Total nodes: " + total);
	}
}
