package main.java.engine;

import main.java.util.LossyHashTable;

/**
 * A simple entry class for a pawn table implementation that stores the evaluation scores for different pawn positions. The number of expected
 * hash table hits is high due to the pawn structure rarely changing while traversing the search tree.
 * 
 * @author Viktor
 *
 */
class PTEntry implements LossyHashTable.Entry<PTEntry> {

	/**
	 * The Zobrist pawn key used to hash pawn positions.
	 */
	final long key;
	/**
	 * The pawn structure evaluation score.
	 */
	final short score;
	/**
	 * The age of the entry.
	 */
	byte generation;
	
	PTEntry(long key, short score, byte generation) {
		this.key = key;
		this.score = score;
		this.generation = generation;
	}
	@Override
	public int compareTo(PTEntry e) {
		return generation - e.generation;
	}
	/**Returns the Zobrist pawn hash key. */
	@Override
	public long hashKey() {
		return key;
	}
	
}
