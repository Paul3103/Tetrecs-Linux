package uk.ac.soton.comp1206.event;

import java.util.Set;
import uk.ac.soton.comp1206.component.GameBlockCoordinate;

public interface LineClearedListener {

  void lineCleared(final Set<GameBlockCoordinate> fadeBlocks);
}
