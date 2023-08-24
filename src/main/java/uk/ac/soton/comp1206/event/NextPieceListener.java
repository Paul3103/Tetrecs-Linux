package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.game.GamePiece;

public interface NextPieceListener {

  /**
   * interface for detecting when the next piece needs to be displayed
   */
  public void nextPiece(GamePiece gamePiece);
}

