package communication;

import util.KeyValuePair;

/**
 * An interface supporting the UCI protocol as specified by Stefan-Meyer Kahlen.
 * 
 * @author Viktor
 *
 */
public interface UCI {

	/**
	 * The possible parameters in a 'go' command.
	 * 
	 * @author Viktor
	 *
	 */
	public enum SearchAttributes {
		searchmoves,
		ponder,
		wtime,
		btime,
		winc,
		binc,
		movestogo,
		depth,
		nodes,
		mate,
		movetime,
		infinite
	}
	/**
	 * The possible parameters sent to the GUI in info strings when in debug mode.
	 * 
	 * @author Viktor
	 *
	 */
	public enum InfoAttributes {
		depth,
		seldepth,
		time,
		nodes,
		pv,
		multipv,
		score,
		currmove,
		currmovenumber,
		hashfull,
		nps,
		tbhits,
		cpuload,
		string,
		refutation,
		currline
	}
	/**
	 * The possible types of the score attribute from the info attributes.
	 * 
	 * @author Viktor
	 *
	 */
	public enum InfoScoreTypes {
		cp,
		mate,
		lowerbound,
		upperbound
	}
	/**
	 * The attributes that can and have to be specified (according to the type of the option) in the 'option' command.
	 * 
	 * @author Viktor
	 *
	 */
	public enum OptionAttributes {
		name,
		type,
		default_,
		min,
		max,
		var
	}
	/**
	 * The different values the 'type' attribute from the option attributes can take on.
	 * 
	 * @author Viktor
	 *
	 */
	public enum OptionTypes {
		check,
		spin,
		combo,
		button,
		string
	}
	
	/**
	 * Tells the engine to switch to UCI mode.
	 * 
	 * @return Whether the engine successfully switched to UCI mode.
	 */
	boolean uci();
	/**
	 * The name of the engine.
	 * 
	 * @return
	 */
	String getName();
	/**
	 * The name of the author of the engine.
	 * 
	 * @return
	 */
	String getAuthor();
	/**
	 * Sets whether the engine should run in debug mode. In debug mode, the engine can (and is expected to) send detailed info strings
	 * such as search statistics to the GUI.
	 * 
	 * @param on
	 */
	void debug(boolean on);
	/**
	 * Asks the engine whether it is done doing what the GUI commanded it to do and waits for the response.
	 * 
	 * @return
	 */
	boolean isReady();
	/**
	 * Returns the options the engine offers. Each option is defined by an array of KeyValuePairs of varying numbers of elements specifying the
	 * name, type, default value, etc. of the option.
	 * 
	 * @return
	 */
	KeyValuePair<String, ?>[][] options();
	/**
	 * Sets an option defined by the engine to the specified value.
	 * 
	 * @param optionName
	 * @param value
	 */
	void setOption(String optionName, Object value);
	/**
	 * Resets the game to a new instance.
	 */
	void uciNewGame();
	/**
	 * Sends the current position to the engine.
	 * 
	 * @param fen
	 */
	void position(String fen);
	/**
	 * Asks the engine to start searching the current position according to the specified parameters.
	 * 
	 * @param params
	 * @return The best move found in long algebraic notation.
	 */
	String go(KeyValuePair<String, ?>[] params);
	/**
	 * Asks the engine to stop searching and return the best move found up until that point.
	 * 
	 * @return The best move found in long algebraic notation.
	 */
	String stop();
	/**
	 * Tells the engine that ponder move fed to the search was actually made, so the engine should search further but not in ponder mode
	 * anymore.
	 */
	void ponderHit();
	/**
	 * Asks the engine to shut down.
	 */
	void quit();
}
