package engine;

public final class Parameters {

	// Piece values.
	short KING_VALUE = 20000;
	short KING_PHASE_WEIGHT = 0;
	short QUEEN_VALUE = 900;
	short QUEEN_PHASE_WEIGHT = 4;
	short ROOK_VALUE = 500;
	short ROOK_PHASE_WEIGHT = 2;
	short BISHOP_VALUE = 330;
	short BISHOP_PHASE_WEIGHT = 1;
	short KNIGHT_VALUE = 320;
	short KNIGHT_PHASE_WEIGHT = 1;
	short PAWN_VALUE = 100;
	short PAWN_PHASE_WEIGHT = 0;
	
	// Evaluation weights.
	byte DEFENDED_PAWN_WEIGHT = 20;
	byte BLOCKED_PAWN_WEIGHT1 = 30;
	byte BLOCKED_PAWN_WEIGHT2 = 15;
	byte BLOCKED_PAWN_WEIGHT3 = 5;
	byte PASSED_PAWN_WEIGHT = 40;
	byte ISOLATED_PAWN_WEIGHT = 10;
	byte BACKWARD_PAWN_WEIGHT1 = 20;
	byte BACKWARD_PAWN_WEIGHT2 = 10;
	byte SHIELDING_PAWN_WEIGHT1 = 15;
	byte SHIELDING_PAWN_WEIGHT2 = 5;
	byte SHIELDING_PAWN_WEIGHT3 = 10;
	byte SHIELD_THREATENING_PAWN_WEIGHT1 = 15;
	byte SHIELD_THREATENING_PAWN_WEIGHT2 = 10;
	byte DEFEDED_KING_AREA_SQUARE_WEIGHT1 = 15;
	byte DEFEDED_KING_AREA_SQUARE_WEIGHT2 = 10;
	byte DEFEDED_KING_AREA_SQUARE_WEIGHT3 = 5;
	byte KING_PAWN_TROPISM_WEIGHT = 4;
	byte PINNED_QUEEN_WEIGHT = 10;
	byte PINNED_ROOK_WEIGHT = 6;
	byte PINNED_BISHOP_WEIGHT = 4;
	byte PINNED_KNIGHT_WEIGHT = 4;
	byte COVERED_SQUARE_WEIGHT = 1;
	byte COVERED_FRIENDLY_OCCUPIED_SQUARE_WEIGHT = 1;
	byte PAWN_DEFENDED_PIECE_WEIGHT = 10;
	byte PIECE_KING_TROPISM_WEIGHT = 4;
	byte STOPPED_PAWN_WEIGHT = 10;
	
	// Game phase intervals.
	short GAME_PHASE_OPENING_LOWER = 0;
	short GAME_PHASE_OPENING_UPPER = 22;
	short GAME_PHASE_MIDDLE_GAME_LOWER = 23;
	short GAME_PHASE_MIDDLE_GAME_UPPER = 170;
	short GAME_PHASE_END_GAME_LOWER = 171;
	short GAME_PHASE_END_GAME_UPPER = 256;
	
	// Search parameters.
	int NMR = 2;		// Null move pruning reduction.
	int LMR = 1;		// Late move reduction.
	int LMRMSM = 4;		// Min. number of searched moves for late move reduction
	int FMAR1 = 330;	// Futility margin.
	int FMAR2 = 500;	// Extended futility margin.
	int FMAR3 = 900;	// Razoring margin.
	int A_DELTA = 100;	// The aspiration delta within iterative deepening.
	int Q_DELTA = 270;	// The margin for delta-pruning in the quiescence search.
	
	// The relative history table's value depreciation factor.
	byte RHT_DECREMENT_FACTOR = 4;
	
	// The margin for lazy evaluation. The extended score should be very unlikely to differ by more than this amount from the core score.
	int LAZY_EVAL_MAR = 187;
	
	// Piece-square tables based on and extending Tomasz Michniewski's "Unified Evaluation" tables.
	byte[] PST_W_PAWN_OPENING =
		{  0,  0,  0,  0,  0,  0,  0,  0,
		  50, 50, 50, 50, 50, 50, 50, 50,
		  10, 20, 25, 30, 30, 25, 20, 10,
		   5, 15, 20, 20, 20, 20, 15,  5,
		   0, 10, 15, 15, 15, 15, 10,  0,
		   0,  8, 10, 10, 10, 10,  8,  0,
		   5,  5,  5,  0,  0,  5,  5,  5,
		   0,  0,  0,  0,  0,  0,  0,  0};
	byte[] PST_W_PAWN_ENDGAME =
		{  0,  0,  0,  0,  0,  0,  0,  0,
		  70, 75, 80, 80, 80, 80, 75, 70,
		  20, 30, 40, 45, 45, 40, 30, 20,
		  10, 20, 25, 30, 30, 25, 20, 10,
		   5, 10, 10, 15, 15, 10, 10,  5,
		   0,  0, -5, -5, -5, -5,  0,  0,
		  -5,-10,-10,-20,-20,-10,-10, -5,
		   0,  0,  0,  0,  0,  0,  0,  0};
	byte[] PST_W_KNIGHT_OPENING =
		{-15,-10, -5, -5, -5, -5,-10,-15,
		 -10,-10,  0,  0,  0,  0,-10,-10,
		  -5,  0,  5, 10, 10,  5,  0, -5,
		  -5,  0, 10, 15, 15, 10,  0, -5,
		  -5,  0, 10, 15, 15, 10,  0, -5,
		  -5,  0,  5, 10, 10,  5,  0, -5,
		 -10, -5,  0,  0,  0,  0, -5,-10,
		 -15,-10, -5, -5, -5, -5,-10,-15};
	byte[] PST_W_KNIGHT_ENDGAME =
		{-50,-40,-30,-30,-30,-30,-40,-50,
		 -40,-20, -5,  0,  0, -5,-20,-40,
		 -30,  0,  0,  0,  0,  0, -5,-30,
		 -30,  0,  5,  5,  5,  5,  0,-30,
		 -30, -5,  5,  5,  5,  5, -5,-30,
		 -30,  0,  0,  0,  0,  0,  0,-30,
		 -40,-20, -5,  0,  0, -5,-20,-40,
		 -50,-40,-30,-30,-30,-30,-40,-50};
	byte[] PST_W_BISHOP =
		{-20,-10,-10,-10,-10,-10,-10,-20,
		 -10,  0,  0,  0,  0,  0,  0,-10,
		 -10,  0,  5, 10, 10,  5,  0,-10,
		 -10,  5,  5, 15, 15,  5,  5,-10,
		 -10,  0, 10, 15, 15, 10,  0,-10,
		 -10, 10, 10, 10, 10, 10, 10,-10,
		 -10,  5,  0,  0,  0,  0,  5,-10,
		 -20,-10,-10,-10,-10,-10,-10,-20};
	byte[] PST_W_ROOK_OPENING =
		{  0,  0,  0,  0,  0,  0,  0,  0,
		   5, 10, 10, 10, 10, 10, 10,  5,
		  -5, -5, -5, -5, -5, -5, -5, -5,
		  -5, -5, -5, -5, -5, -5, -5, -5,
		  -5, -5, -5, -5, -5, -5, -5, -5,
		  -5, -5, -5, -5, -5, -5, -5, -5,
		  -5, -5, -5, -5, -5, -5, -5, -5,
		  10, -5,  0, 20,  5, 20, -5, 10};
	byte[] PST_W_ROOK_ENDGAME =
		{  0,  0,  0,  0,  0,  0,  0,  0,
		   5, 10, 10, 10, 10, 10, 10,  5,
		  -5,  0,  0,  0,  0,  0,  0, -5,
		  -5,  0,  0,  0,  0,  0,  0, -5,
		  -5,  0,  0,  0,  0,  0,  0, -5,
		  -5,  0,  0,  0,  0,  0,  0, -5,
		  -5,  0,  0,  0,  0,  0,  0, -5,
		   0,  0,  5,  5,  5,  5,  0,  0};
	byte[] PST_W_QUEEN =
		{-20,-10,-10, -5, -5,-10,-10,-20,
		 -10,  0,  0,  0,  0,  0,  0,-10,
		 -10,  0,  5,  5,  5,  5,  0,-10,
		  -5,  0,  5,  5,  5,  5,  0, -5,
		   0,  0,  5,  5,  5,  5,  0, -5,
		 -10,  0,  5,  5,  5,  5,  0,-10,
		 -10,  0,  0,  0,  0,  0,  0,-10,
		 -20,-10,-10, -5, -5,-10,-10,-20};
	byte[] PST_W_KING_OPENING =
		{-30,-40,-40,-50,-50,-40,-40,-30,
		 -30,-40,-40,-50,-50,-40,-40,-30,
		 -30,-40,-40,-50,-50,-40,-40,-30,
		 -30,-40,-40,-50,-50,-40,-40,-30,
		 -20,-30,-30,-40,-40,-30,-30,-20,
		 -10,-20,-20,-20,-20,-20,-20,-10,
		  10, 10,-10,-10,-10,-10, 10, 10,
		  20, 30, 30,-20,  0,-20, 40, 30};
	byte[] PST_W_KING_ENDGAME =
		{-50,-40,-30,-20,-20,-30,-40,-50,
		 -30,-20,-10,  0,  0,-10,-20,-30,
		 -30,-10, 20, 30, 30, 20,-10,-30,
		 -30,-10, 30, 40, 40, 30,-10,-30,
		 -30,-10, 30, 40, 40, 30,-10,-30,
		 -30,-10, 20, 30, 30, 20,-10,-30,
		 -30,-30,  0,  0,  0,  0,-30,-30,
		 -50,-30,-30,-30,-30,-30,-30,-50};
	
	byte[] PST_B_PAWN_OPENING = new byte[64];
	byte[] PST_B_PAWN_ENDGAME = new byte[64];
	byte[] PST_B_KNIGHT_OPENING = new byte[64];
	byte[] PST_B_KNIGHT_ENDGAME = new byte[64];
	byte[] PST_B_BISHOP = new byte[64];
	byte[] PST_B_ROOK_OPENING = new byte[64];
	byte[] PST_B_ROOK_ENDGAME = new byte[64];
	byte[] PST_B_QUEEN = new byte[64];
	byte[] PST_B_KING_OPENING = new byte[64];
	byte[] PST_B_KING_ENDGAME = new byte[64];
	
	{
		int c1, c2;
		// Due to the reversed order of the rows in the definition of the white piece-square tables,
		// they are just right for black with negated values.
		for (int i = 0; i < 64; i++) {
			PST_B_PAWN_OPENING[i] = (byte)-PST_W_PAWN_OPENING[i];
			PST_B_PAWN_ENDGAME[i] = (byte)-PST_W_PAWN_ENDGAME[i];
			PST_B_KNIGHT_OPENING[i] = (byte)-PST_W_KNIGHT_OPENING[i];
			PST_B_KNIGHT_ENDGAME[i] = (byte)-PST_W_KNIGHT_ENDGAME[i];
			PST_B_BISHOP[i] = (byte)-PST_W_BISHOP[i];
			PST_B_ROOK_OPENING[i] = (byte)-PST_W_ROOK_OPENING[i];
			PST_B_ROOK_ENDGAME[i] = (byte)-PST_W_ROOK_ENDGAME[i];
			PST_B_QUEEN[i] = (byte)-PST_W_QUEEN[i];
			PST_B_KING_OPENING[i] = (byte)-PST_W_KING_OPENING[i];
			PST_B_KING_ENDGAME[i] = (byte)-PST_W_KING_ENDGAME[i];
		}
		// To get the right values for the white piece-square tables, we vertically mirror and negate the ones for black
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				c1 = i*8 + j;
				c2 = ((7 - i)*8) + j;
				PST_W_PAWN_OPENING[c1] = (byte)-PST_B_PAWN_OPENING[c2];
				PST_W_PAWN_ENDGAME[c1] = (byte)-PST_B_PAWN_ENDGAME[c2];
				PST_W_KNIGHT_OPENING[c1] = (byte)-PST_B_KNIGHT_OPENING[c2];
				PST_W_KNIGHT_ENDGAME[c1] = (byte)-PST_B_KNIGHT_ENDGAME[c2];
				PST_W_BISHOP[c1] = (byte)-PST_B_BISHOP[c2];
				PST_W_ROOK_OPENING[c1] = (byte)-PST_B_ROOK_OPENING[c2];
				PST_W_ROOK_ENDGAME[c1] = (byte)-PST_B_ROOK_ENDGAME[c2];
				PST_W_QUEEN[c1] = (byte)-PST_B_QUEEN[c2];
				PST_W_KING_OPENING[c1] = (byte)-PST_B_KING_OPENING[c2];
				PST_W_KING_ENDGAME[c1] = (byte)-PST_B_KING_ENDGAME[c2];
			}
		}
	}
}
