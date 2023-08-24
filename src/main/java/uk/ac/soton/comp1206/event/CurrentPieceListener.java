package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.game.GamePiece;

public interface CurrentPieceListener {

  /**
   * interface for detecting when the next piece needs to be displayed
   */
  public void currentPiece(GamePiece gamePiece);
}

