package net.viktorc.detroid.framework.engine;

/**
 * A simple class representing a chess move.
 * 
 * @author Viktor
 *
 */
class Move implements Comparable<Move> {

	/**
	 * A null move instance.
	 */
	static final Move NULL_MOVE = new Move();

	// Mask and shift values for encoding contents of a Move object into an int; and vica versa.
	private static final byte SHIFT_TO = 6;
	private static final byte SHIFT_MOVED = 12;
	private static final byte SHIFT_CAPTURED = 16;
	private static final byte SHIFT_TYPE = 20;
	private static final byte MASK_FROM = 63;
	private static final byte MASK_TO = 63;
	private static final byte MASK_MOVED = 15;
	private static final byte MASK_CAPTURED = 15;

	private final byte from;
	private final byte to;
	private final byte movedPiece;
	private final byte capturedPiece;
	private final byte type;
	private short value;

	/**
	 * Parses a move encoded in a 32 bitboard integer.
	 *
	 * @param move The encoded move.
	 */
	static Move toMove(int move) {
		byte from, to, movedPiece, capturedPiece, type;
		from = (byte)(move & MASK_FROM);
		to = (byte)((move >>> SHIFT_TO) & MASK_TO);
		movedPiece = (byte)((move >>> SHIFT_MOVED) & MASK_MOVED);
		capturedPiece = (byte)((move >>> SHIFT_CAPTURED) & MASK_CAPTURED);
		type = (byte)(move >>> SHIFT_TYPE);
		return new Move(from, to, movedPiece, capturedPiece, type);
	}
	/**
	 * Default constructor.
	 */
	Move() {
		from = 0;
		to = 0;
		movedPiece = 0;
		capturedPiece = 0;
		type = 0;
	}
	/**
	 * @param from The index of the origin square.
	 * @param to The index of the destination square.
	 * @param movedPiece The numeric notation of the type of the moved piece.
	 * @param capturedPiece The numeric notation of the type of the captured piece.
	 * @param type The type of the move.
	 */
	Move(byte from, byte to, byte movedPiece, byte capturedPiece, byte type) {
		this.from = from;
		this.to = to;
		this.movedPiece = movedPiece;
		this.capturedPiece = capturedPiece;
		this.type = type;
	}
	/**
	 * @return The index of the origin square.
	 */
	byte getFrom() {
		return from;
	}
	/**
	 * @return The index of the destination square.
	 */
	byte getTo() {
		return to;
	}
	/**
	 * @return The numeric notation of the type of the moved piece.
	 */
	byte getMovedPiece() {
		return movedPiece;
	}
	/**
	 * @return The numeric notation of the type of the captured piece.
	 */
	byte getCapturedPiece() {
		return capturedPiece;
	}
	/**
	 * @return The type of the move.
	 */
	byte getType() {
		return type;
	}
	/**
	 * @return The value assigned to the move (for whatever purpose).
	 */
	short getValue() {
		return value;
	}
	/**
	 * @param value The value assigned to the move (for whatever purpose).
	 */
	void setValue(short value) {
		this.value = value;
	}
	/**
	 * Returns a move as a 32 bitboard integer with information on the state of the object stored in designated
	 * bitboard, except for the score. Useful in memory sensitive applications like the transposition table as it
	 * identifies a 30 byte Move object in merely 4 bytes.
	 *
	 * @return
	 */
	int toInt() {
		return (from | (to << SHIFT_TO) | (movedPiece << SHIFT_MOVED) |
			   (capturedPiece << SHIFT_CAPTURED) | (type << SHIFT_TYPE));
	}
	/**
	 * Returns whether the move is a material move or not.
	 *
	 * @return
	 */
	boolean isMaterial() {
		return capturedPiece != Piece.NULL.ind || type >= MoveType.PROMOTION_TO_QUEEN.ind;
	}
	/**
	 * Returns whether this move is equal to the input parameter move. The assigned theoretical value is disregarded as
	 * it is highly context dependent.
	 * 
	 * @param m
	 * @return
	 */
	boolean equals(Move m) {
		return m != null && from == m.from && to == m.to && movedPiece == m.movedPiece &&
				capturedPiece == m.capturedPiece && type == m.type;
	}
	/**
	 * Returns whether this move is equal to the input parameter 'compressed' move.
	 * 
	 * @param m
	 * @return
	 */
	boolean equals(int m) {
		return (from == (m & MASK_FROM) && to == ((m >>> SHIFT_TO) & MASK_FROM) &&
			movedPiece == ((m >>> SHIFT_MOVED) & MASK_MOVED) &&
			capturedPiece == ((m >>> SHIFT_CAPTURED) & MASK_CAPTURED) &&
			type == (m >>> SHIFT_TYPE));
	}
	@Override
	public boolean equals(Object o) {
		return o instanceof Move ? equals((Move) o) : o instanceof Integer && equals(((Integer) o).intValue());
	}
	@Override
	public int hashCode() {
		return toInt();
	}
	@Override
	public int compareTo(Move m) throws NullPointerException {
		return value - m.value;
	}
	@Override
	public String toString() {
		String pacn, originFile, originRank, destFile, destRank;
		originRank = Integer.toString(from/8 + 1);
		originFile = Character.toString((char)(from%8 + 'a'));
		destRank = Integer.toString(to/8 + 1);
		destFile = Character.toString((char)(to%8 + 'a'));
		pacn = originFile + originRank + destFile + destRank;
		switch (type) {
			case 4:
				return pacn + "q";
			case 5:
				return pacn + "r";
			case 6:
				return pacn + "b";
			case 7:
				return pacn + "n";
			default:
				return pacn;
		}
	}
	
}
