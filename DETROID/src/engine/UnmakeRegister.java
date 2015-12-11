package engine;

import util.*;
import engine.Board.Square;
import engine.Position.CastlingRights;
import engine.Position.EnPassantRights;

/**Some position and move information--such as castling and en passant rights, fifty-move rule clock, repetitions, a bitmap representing checkers, and the
 * moved and captured pieces--is stored in this unencapsulated class' instances so as to make reverting back to the previous position when unmaking a move faster.
 * 
 * @author Viktor
 *
 */
public class UnmakeRegister {

	final int whiteCastlingRights;
	final int blackCastlingRights;
	final int enPassantRights;
	final int fiftyMoveRuleClock;
	final int repetitions;
	final long checkers;
	
	public UnmakeRegister(int whiteCastlingRights, int blackCastlingRights, int enPassantRights,
	int fiftyMoveRuleClock, int repetitions, long checkers) {
		this.whiteCastlingRights = whiteCastlingRights;
		this.blackCastlingRights = blackCastlingRights;
		this.enPassantRights = enPassantRights;
		this.fiftyMoveRuleClock = fiftyMoveRuleClock;
		this.repetitions = repetitions;
		this.checkers = checkers;
	}
	/**Returns a human-readable String representation of the position information stored in the long.
	 * 
	 * @param positionInfo
	 * @return
	 */
	public String toString() {
		long checker;
		String rep = "";
		if ((checker = BitOperations.getLSBit(checkers)) != 0) {
			rep += String.format("%-23s " + Square.toString(BitOperations.indexOfBit(checker)), "Checker(s):");
			if ((checker = BitOperations.getLSBit(checkers^checker)) != 0)
				rep += ", " + Square.toString(BitOperations.indexOfBit(checker));
		}
		rep += String.format("%-23s ", "Castling rights:");
		rep += CastlingRights.toFEN(CastlingRights.getByIndex(whiteCastlingRights), CastlingRights.getByIndex(blackCastlingRights));
		rep += "\n";
		rep += String.format("%-23s ", "En passant rights:");
		rep += EnPassantRights.getByIndex(enPassantRights).toString() + "\n";
		rep += String.format("%-23s " + fiftyMoveRuleClock + "\n", "Fifty-move rule clock:");
		rep += String.format("%-23s " + repetitions + "\n", "Repetitions:");
		return rep;
	}
}
