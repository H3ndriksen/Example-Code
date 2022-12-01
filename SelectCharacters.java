package de.unisaarland.cs.se.sopra.commands;

import de.unisaarland.cs.se.sopra.Communicator;
import de.unisaarland.cs.se.sopra.GameBoard;
import de.unisaarland.cs.se.sopra.Player;
import de.unisaarland.cs.se.sopra.Survivor;
import de.unisaarland.cs.se.sopra.UtilClass;
import java.util.List;
import java.util.Optional;
import org.slf4j.LoggerFactory;

public class SelectCharacters extends Command {

  private final int charID0;
  private final int charID1;


  public SelectCharacters(final int commID, final int charID0, final int charID1) {
    super(commID);
    this.charID0 = charID0;
    this.charID1 = charID1;
  }


  @Override
  public void execute(final GameBoard gameBoard, final Communicator comm) {

    if (gameBoard.isRegisterPhase()) {
      comm.broadcastRegistrationAborted();
      gameBoard.startRegistrationAborted();
      return;
    } else if (gameBoard.isGamePhase()) {
      comm.sendCommandFailed(commID, "SC: Game already started");
      return;
    }

    final int playerID = comm.getPIDFromCID(commID);
    if (playerID < 0) {
      comm.sendCommandFailed(commID, "Player is not registered");
      return;
    }

    if (!gameBoard.isActivePlayerID(playerID)) {
      comm.sendCommandFailed(commID, "You're not the current player");
      return;
    }

    final List<Survivor> survStack = gameBoard.getSurvivorStack();

    final Optional<Survivor> surv0Opt = getSurvivorFromFirst4(survStack, charID0);
    if (surv0Opt.isEmpty()) {
      comm.sendCommandFailed(commID, "Select a character from the options");
      return;
    }

    final Optional<Survivor> surv1Opt = getSurvivorFromFirst4(survStack, charID1);
    if (surv1Opt.isEmpty()) {
      comm.sendCommandFailed(commID, "Select a character from the options");
      return;
    }

    // Add the selected characters to the player and spawn them
    final Player p = gameBoard.getPlayer(playerID).get();
    final UtilClass uc = new UtilClass(gameBoard, comm);
    if (surv0Opt.get().getID() < surv1Opt.get().getID()) {
      uc.spawnCharacter(p, surv0Opt.get(), true);
      uc.spawnCharacter(p, surv1Opt.get(), true);
    } else {
      uc.spawnCharacter(p, surv1Opt.get(), true);
      uc.spawnCharacter(p, surv0Opt.get(), true);
    }

    // Move the two remaining characters to the back of the stack
    survStack.add(survStack.remove(0));
    survStack.add(survStack.remove(0));

    LoggerFactory.getLogger(this.getClass()).error("Shuffle - " + survStack.size());
    gameBoard.getDiceShuffler().shuffle(survStack);

    gameBoard.nextPlayer();
  }

  private Optional<Survivor> getSurvivorFromFirst4(final List<Survivor> stack, final int id) {

    for (int i = 0; i < 4; i++) {
      if (stack.get(i).getID() == id) {
        return Optional.of(stack.get(i));
      }
    }
    return Optional.empty();
  }
}





