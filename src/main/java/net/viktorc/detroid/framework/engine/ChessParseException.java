package net.viktorc.detroid.framework.engine;

import java.text.ParseException;

import net.viktorc.detroid.framework.gui.standard.StandardStageManager;

/**
 * An exception for when a piece of chess notation text such as FEN, PGN, or SAN can not be parsed due to violations of the notation
 * standards.
 *
 * @author Viktor
 */
public class ChessParseException extends ParseException {

  /**
   * Generated serial version UID.
   */
  private static final long serialVersionUID = -2375327691687152573L;

  /**
   * @param desc A description of the error.
   */
  public ChessParseException(String desc) {
    super(desc, -1);
    StandardStageManager ssm = StandardStageManager.getStageManager();
    ssm.setTitle("ERROR! Reformat your chess notation");
  }

  /**
   * @param e The underlying exception.
   */
  public ChessParseException(Exception e) {
    this(e.getMessage());
  }

}
