package engine;

import util.*;

/**A siple entry class for a pawn table implementation that stores the evaluation scores for different pawn positions. Tne number of expected
 * hash table hits is high due to the pawn structure rarely changing while traversing the search tree.
 * 
 * @author Viktor
 *
 */
public class PTEntry implements HashTable.Entry<PTEntry> {

	long key;			// The Zobrist pawn key used to hash pawn positions.
	boolean whitesTurn;	// Whether the stored score is for white or black.
	short score;		// The pawn structure evaluation score.
	
	public PTEntry(long key, boolean whitesTurn, int score) {
		this.key = key;
		this.whitesTurn = whitesTurn;
		this.score = (short)score;
	}
	public boolean betterThan(PTEntry t) {
		// Hash table entries need not to be overwritten at this point;
		return false;
	}
	public boolean worseThan(PTEntry t) {
		// Hash table entries need not to be overwritten at this point;
		return false;
	}
	/**Returns the Zobrist pawn hash key. */
	public long hashKey() {
		return key;
	}

}