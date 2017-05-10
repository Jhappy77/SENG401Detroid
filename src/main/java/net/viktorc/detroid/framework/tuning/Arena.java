package net.viktorc.detroid.framework.tuning;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import net.viktorc.detroid.framework.uci.ScoreType;
import net.viktorc.detroid.framework.uci.SearchResults;
import net.viktorc.detroid.framework.uci.UCIEngine;
import net.viktorc.detroid.framework.validation.ControllerEngine;
import net.viktorc.detroid.framework.validation.GameState;

/**
 * A class for pitting two UCI compatible engines against each other, supervised by a controller engine.
 * 
 * @see UCIEngine
 * @see ControllerEngine
 * @author Viktor
 *
 */
class Arena implements AutoCloseable {

	/**
	 * The arena event's impromptu name.
	 */
	public static final String EVENT = "DETROID Computer Chess Arena";
	
	private ControllerEngine controller;
	private ExecutorService pool;
	private Random rand;
	private Logger resultLogger;
	private Logger fenLogger;
	private long id;
	
	/**
	 * Constructs an arena controlled by the specified engine. A logger can be provided that will be used to log game
	 * results. Furthermore, another logger can be provided as well for the logging of all positions in which no mate
	 * has been found in FEN format to create data sets to use for learning.
	 * 
	 * @param controller The engine to control the match.
	 * @param resultLogger The logger to log the game results and the complete game in PGN format.
	 * @param fenLogger A logger to log each position occurring during the games in which the engine that searched the
	 * position didn't find a mate in FEN format followed by semicolon separator and the result of the game as in 1 - 
	 * white wins, 0.5 - draw, 0 - black wins.
	 * @throws Exception If the controller engine cannot be initialised.
	 */
	public Arena(ControllerEngine controller, Logger resultLogger, Logger fenLogger) throws Exception {
		this.controller = controller;
		if (!this.controller.isInit())
			this.controller.init();
		this.controller.setControllerMode(true);
		pool = Executors.newCachedThreadPool();
		this.resultLogger = resultLogger;
		this.fenLogger = fenLogger;
		rand = new Random(System.nanoTime());
		id = rand.nextLong();
	}
	/**
	 * Constructs an arena controlled by the specified engine. A logger can be provided that will be used to log game
	 * results.
	 * 
	 * @param controller The engine to control the match.
	 * @param resultLogger The logger to log the game results and the complete game in PGN format.
	 * @throws Exception If the controller engine cannot be initialised.
	 */
	public Arena(ControllerEngine controller, Logger resultLogger) throws Exception {
		this(controller, resultLogger, null);
	}
	/**
	 * Constructs an arena controlled by the specified engine.
	 * 
	 * @param controller The engine to control the match.
	 * @throws Exception If the controller engine cannot be initialised.
	 */
	public Arena(ControllerEngine controller) throws Exception {
		this(controller, null, null);
	}
	/**
	 * Returns the Arena instance's id number.
	 * 
	 * @return
	 */
	public long getId() {
		return id;
	}
	/**
	 * Pits the two engines against each other playing the specified number of games with the number of milliseconds per game
	 * allotted for each engine to make their moves. The engines play alternating their colours after each game so it is
	 * recommended to specify an even number for the number of games to play.
	 * 
	 * @param engine1 Contender number one.
	 * @param engine2 Contender number two.
	 * @param games The number of games to play. Should be an even number.
	 * @param timePerGame The number of milliseconds each engine will have to make all their moves during the course of each game.
	 * If it is less than 500, it will default to 500.
	 * @param timeIncPerMove The number of milliseconds with which the remaining time of an engine is incremented after each legal
	 * move.
	 * @return The results of the match.
	 * @throws Exception If either of the engines is not initialised and an attempt at initialisation fails.
	 */
	public synchronized MatchResult match(UCIEngine engine1, UCIEngine engine2, int games, long timePerGame, long timeIncPerMove)
			throws Exception {
		Timer timer = null;
		int engine1Wins = 0;
		int engine2Wins = 0;
		int draws = 0;
		games = Math.max(0, games);
		timePerGame = Math.max(500, timePerGame);
		timeIncPerMove = Math.max(0, timeIncPerMove);
		List<String> fenLog = null;
		if (!engine1.isInit())
			engine1.init();
		if (!engine2.isInit())
			engine2.init();
		if (resultLogger != null) resultLogger.info("--------------------------------------MATCH STARTED--------------------------------------\n" +
				"Arena: " + id + "\n" +
				"Engine1: " + engine1.getName() + " - Engine2: " + engine2.getName() + "\n" +
				"Games: " + games + " TC: " + timePerGame + (timeIncPerMove != 0 ? " + " + timeIncPerMove : "") + "\n\n");
		boolean engine1White = rand.nextBoolean();
		Games: for (int i = 0; i < games; i++, engine1White = !engine1White) {
			if (fenLogger != null) fenLog = new ArrayList<>();
			SearchResults res = null;
			long engine1Time = timePerGame;
			long engine2Time = timePerGame;
			boolean engine1Turn = engine1White;
			if (timer != null)
				timer.cancel();
			timer = new Timer();
			engine1.newGame();
			engine2.newGame();
			controller.newGame();
			engine1.setPosition("startpos");
			engine2.setPosition("startpos");
			controller.setPosition("startpos");
			if (resultLogger != null) {
				controller.setPlayers(engine1White ? "Engine1" : "Engine2", engine1White ? "Engine2" : "Engine1");
				controller.setEvent(EVENT);
				controller.setSite("?");
			}
			while (controller.getGameState() == GameState.IN_PROGRESS) {
				TimerTask task;
				long start;
				res = null;
				if (engine1Turn) {
					try {
						task = new TimerTask() {
							
							@Override
							public void run() {
								engine1.stop();
							}
						};
						timer.schedule(task, engine1Time);
						start = System.nanoTime();
						res = engine1.search(null, null, engine1White ? engine1Time : engine2Time, engine1White ? engine2Time :
								engine1Time, timeIncPerMove, timeIncPerMove, null, null, null, null, null, null);
						task.cancel();
						engine1Time -= (System.nanoTime() - start)/1e6;
						if (engine1Time <= 0 || !controller.play(res.getBestMove())) {
							engine2Wins++;
							if (resultLogger != null) resultLogger.info("Arena: " + id + "\n" + "Engine2 WINS: " + (engine1Time <= 0 ?
									"Engine1 lost on time." : "Engine1 returned an illegal move: " + res.getBestMove() + ".") + "\n" +
									controller.toPGN() + "\nSTANDINGS: " + engine1Wins + " - " + engine2Wins + " - " + draws + "\n\n");
							continue Games;
						}
					} catch (Exception e) {
						timer.cancel();
						engine2Wins++;
						if (resultLogger != null) resultLogger.info("Arena: " + id + "\n" + "Engine2 WINS: Engine1 lost due to error.\n" +
								controller.toPGN() + "\nSTANDINGS: " + engine1Wins + " - " + engine2Wins + " - " + draws + "\n\n");
						continue Games;
					}
					engine1Time += timeIncPerMove;
					if (fenLogger != null && res.getScoreType().isPresent() && res.getScoreType().get() != ScoreType.MATE)
						fenLog.add(controller.toFEN());
				} else {
					try {
						task = new TimerTask() {
							
							@Override
							public void run() {
								engine2.stop();
							}
						};
						timer.schedule(task, engine2Time);
						start = System.nanoTime();
						res = engine2.search(null, null, engine1White ? engine1Time : engine2Time, engine1White ? engine2Time :
								engine1Time, timeIncPerMove, timeIncPerMove, null, null, null, null, null, null);
						task.cancel();
						engine2Time -= (System.nanoTime() - start)/1e6;
						if (engine2Time <= 0 || !controller.play(res.getBestMove())) {
							engine1Wins++;
							if (resultLogger != null) resultLogger.info("Arena: " + id + "\n" + "Engine1 WINS: " + (engine2Time <= 0 ?
									"Engine2 lost on time." : "Engine2 returned an illegal move: " + res.getBestMove() + ".") + "\n" +
									controller.toPGN() + "\nSTANDINGS: " + engine1Wins + " - " + engine2Wins + " - " + draws + "\n\n");
							continue Games;
						}
					} catch (Exception e) {
						timer.cancel();
						engine1Wins++;
						if (resultLogger != null) resultLogger.info("Arena: " + id + "\n" + "Engine1 WINS: Engine2 lost due to error.\n" +
								"STANDINGS: " + engine1Wins + " - " + engine2Wins + " - " + draws + "\n\n");
						continue Games;
					}
					engine2Time += timeIncPerMove;
					if (fenLogger != null && res.getScoreType().isPresent() && res.getScoreType().get() != ScoreType.MATE)
						fenLog.add(controller.toFEN());
				}
				engine1.play(res.getBestMove());
				engine2.play(res.getBestMove());
				engine1Turn = !engine1Turn;
			}
			GameState state = controller.getGameState();
			if (state == GameState.WHITE_MATES) {
				if (engine1White)
					engine1Wins++;
				else
					engine2Wins++;
				if (resultLogger != null) resultLogger.info("Arena: " + id + "\n" + (engine1White ? "Engine1 WINS: " : "Engine2 WINS: ") +
						"Check mate.\n" + controller.toPGN() + "\nSTANDINGS: " + engine1Wins + " - " + engine2Wins + " - " + draws + "\n\n");
				if (fenLogger != null) {
					for (int j = 0; j < fenLog.size(); j++)
						fenLog.set(j, fenLog.get(j) + ";1");
					for (String s : fenLog)
						fenLogger.info(s);
				}
			}
			else if (state == GameState.BLACK_MATES) {
				if (engine1White)
					engine2Wins++;
				else
					engine1Wins++;
				if (resultLogger != null) resultLogger.info("Arena: " + id + "\n" + (engine1White ? "Engine2 WINS: " : "Engine1 WINS: ") +
						"Check mate.\n" + controller.toPGN() + "\nSTANDINGS: " + engine1Wins + " - "  + engine2Wins + " - " + draws + "\n\n");
				if (fenLogger != null) {
					for (int j = 0; j < fenLog.size(); j++)
						fenLog.set(j, fenLog.get(j) + ";0");
					for (String s : fenLog)
						fenLogger.info(s);
				}
			}
			else {
				draws++;
				if (resultLogger != null) {
					String gameRes = "Arena: " + id + "\n" + "DRAW: ";
					String pgnAndStandings = controller.toPGN() + "\n" + "STANDINGS: " + engine1Wins + " - " +
							engine2Wins + " - " + draws + "\n\n";
					String reason = "";
					switch (state) {
						case STALE_MATE:
							reason = "Stale mate.\n";
							break;
						case DRAW_BY_INSUFFICIENT_MATERIAL:
							reason = "Insufficient material.\n";
							break;
						case DRAW_BY_3_FOLD_REPETITION:
							reason = "Three fold repetition.\n";
							break;
						case DRAW_BY_50_MOVE_RULE:
							reason = "Fifty move rule.\n";
							break;
						default:
							break;
					}
					resultLogger.info(gameRes + reason + pgnAndStandings);
				}
				if (fenLogger != null) {
					for (int j = 0; j < fenLog.size(); j++)
						fenLog.set(j, fenLog.get(j) + ";0.5");
					for (String s : fenLog)
						fenLogger.info(s);
				}
			}
		}
		if (timer != null)
			timer.cancel();
		return new MatchResult(engine1Wins, engine2Wins, draws);
	}
	@Override
	public void close() {
		controller.quit();
		pool.shutdown();
	}

}